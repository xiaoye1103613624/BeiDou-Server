package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import lombok.AllArgsConstructor;
import org.gms.constants.string.CategoryType;
import org.gms.dao.entity.ModifiedCashItemDO;
import org.gms.dao.mapper.ModifiedCashItemMapper;
import org.gms.exception.BizException;
import org.gms.model.dto.CashShopBatchOnSaleReqDTO;
import org.gms.model.dto.CashShopSearchRtnDTO;
import org.gms.model.pojo.CashCategory;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.server.CashShop;
import org.gms.server.ItemInformationProvider;
import org.gms.util.BasePageUtil;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 现金商城服务类
 * 管理商城商品的上架、分类查询以及购买记录，支持WZ数据与数据库数据合并
 */
@Service
@AllArgsConstructor
public class CashShopService {
    /** 自定义商城商品数据访问对象 */
    private final ModifiedCashItemMapper modifiedCashItemMapper;

    /**
     * 加载所有自定义商城商品数据
     *
     * @return 自定义商城商品列表
     */
    public List<ModifiedCashItemDO> loadAllModifiedCashItems() {
        return modifiedCashItemMapper.selectAll();
    }

    /**
     * 获取所有商城分类列表
     * 从WZ的Category.img中读取分类数据
     *
     * @return 商城分类列表
     */
    public List<CashCategory> getAllCategoryList() {
        DataProvider etc = DataProviderFactory.getDataProvider(WZFiles.ETC);
        List<CashCategory> cashCategoryList = new ArrayList<>();
        for (Data item : etc.getData("Category.img").getChildren()) {
            int id = DataTool.getIntConvert("Category", item);
            int subId = DataTool.getIntConvert("CategorySub", item);
            String subName = DataTool.getString("Name", item);
            String name = CategoryType.toName(id);
            cashCategoryList.add(CashCategory.builder().id(id).name(name).subId(subId).subName(subName).build());
        }
        return cashCategoryList;
    }

    /**
     * 按分类查询商城商品
     * 合并WZ数据与数据库数据，以数据库为准覆盖可更新字段
     *
     * @param data 分类查询条件，包含分类ID、子分类ID、上架状态、物品ID、物品名称等筛选条件
     * @return 分页的商城商品列表
     */
    public Page<CashShopSearchRtnDTO> getCommodityByCategory(CashCategory data) {
        RequireUtil.requireNotNull(data.getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        RequireUtil.requireNotNull(data.getSubId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "subId"));

        CashCategory cashCategory = getCategory(data.getId(), data.getSubId());
        // 与客户端保持一致，固定每页10条
        data.setPageSize(10);

        final String prefix = data.getId() + String.format("%02d", data.getSubId());
        // wz中的物品
        List<CashShopSearchRtnDTO> wzCashItems = CashShop.CashItemFactory.getItems().values().stream()
                // 按分类过滤
                .filter(cashItem -> String.valueOf(cashItem.getSn()).startsWith(prefix))
                .map(cashItem -> fromCashItem(cashCategory, cashItem))
                .toList();
        // 数据库中的物品
        List<ModifiedCashItemDO> dbCashItems = CashShop.CashItemFactory.getModifiedCashItems().values().stream()
                // 按分类过滤
                .filter(modifiedCashItemDO -> String.valueOf(modifiedCashItemDO.getSn()).startsWith(prefix))
                .toList();
        // 以数据库为准更新可能更新的字段
        wzCashItems.forEach(wzCashItem -> dbCashItems.stream()
                .filter(dbCashItem -> Objects.equals(wzCashItem.getSn(), dbCashItem.getSn()))
                .findFirst()
                .ifPresent(dbCashItem -> setDbItemValue(wzCashItem, dbCashItem)));

        // 按其他条件过滤
        wzCashItems = wzCashItems.stream().filter(item ->
                // 上架状态
                (data.getOnSale() == null || Objects.equals(data.getOnSale(), item.getOnSale() != null && item.getOnSale() == 1))
                        // 物品id
                        && (data.getItemId() == null || data.getItemId().equals(item.getItemId()))
        ).toList();

        // 现在需要批量去set wzCashItems中的itemName值
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        wzCashItems.forEach(wzCashItem -> {
            wzCashItem.setItemName(ii.getName(wzCashItem.getItemId()));
        });


        // 排序是否正确？ 猜测按照Priority降序 ItemId升序排列
        return BasePageUtil.create(wzCashItems, data)
                .sorted(Comparator.comparing(CashShopSearchRtnDTO::getPriority).reversed().thenComparing(CashShopSearchRtnDTO::getItemId))
                .page();
    }

    /**
     * 根据序列号查询单个商城商品
     * 合并WZ商品数据与数据库中的修改数据
     *
     * @param sn 商品序列号
     * @return 商城商品详细信息
     */
    public CashShopSearchRtnDTO getCommodityBySn(Integer sn) {
        RequireUtil.requireNotNull(sn, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "sn"));
        String snStr = String.valueOf(sn);
        int id = Integer.parseInt(snStr.substring(0, 1));
        int subId = Integer.parseInt(snStr.substring(1, 3));
        CashCategory cashCategory = getCategory(id, subId);
        ModifiedCashItemDO cashItem = CashShop.CashItemFactory.getWzItem(sn);
        RequireUtil.requireNotNull(cashItem, I18nUtil.getExceptionMessage("UNKNOWN_PARAMETER_VALUE", "sn", sn));
        CashShopSearchRtnDTO rtnDTO = fromCashItem(cashCategory, cashItem);
        CashShop.CashItemFactory.getModifiedCashItems().values().stream()
                .filter(dbCashItem -> Objects.equals(dbCashItem.getSn(), sn))
                .findFirst()
                .ifPresent(dbCashItem -> setDbItemValue(rtnDTO, dbCashItem));
        return rtnDTO;
    }

    /**
     * 修改商品上架状态
     * 如果上架：只记录与WZ默认值不同的字段到数据库；如果下架：仅记录onSale=0
     *
     * @param data 商城商品数据，包含序列号和需要修改的字段值
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeOnSale(ModifiedCashItemDO data) {
        RequireUtil.requireNotNull(data.getSn(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "sn"));
        ModifiedCashItemDO cashItem = CashShop.CashItemFactory.getWzItem(data.getSn());
        modifiedCashItemMapper.deleteById(data.getSn());

        // 如果是下架，直接插入或更新除状态外所有值为null
        if (data.getOnSale() != null && data.getOnSale() != 1) {
            if (cashItem.isSelling()) {
                modifiedCashItemMapper.insertSelective(ModifiedCashItemDO.builder().sn(data.getSn()).onSale(0).build());
            }
            CashShop.CashItemFactory.loadAllModifiedCashItems();
            return;
        }
        if (Objects.equals(cashItem.getItemId(), data.getItemId())) {
            data.setItemId(null);
        }
        if (Objects.equals(cashItem.getPrice(), data.getPrice())) {
            data.setPrice(null);
        }
        if (Objects.equals(cashItem.getPeriod(), data.getPeriod())) {
            data.setPeriod(null);
        }
        if (Objects.equals(cashItem.getPriority(), data.getPriority())) {
            data.setPriority(null);
        }
        if (Objects.equals(cashItem.getCount(), data.getCount())) {
            data.setCount(null);
        }
        if (Objects.equals(cashItem.getOnSale(), data.getOnSale())) {
            data.setOnSale(null);
        }
        modifiedCashItemMapper.insertSelective(data);
        CashShop.CashItemFactory.loadAllModifiedCashItems();
    }

    /**
     * 从缓存中查找分类
     *
     * @param id    分类ID
     * @param subId 子分类ID
     * @return 匹配的商城分类
     * @throws BizException 未找到分类时抛出
     */
    private CashCategory getCategory(Integer id, Integer subId) {
        return CashShop.CashItemFactory.getCashCategories().stream()
                .filter(cc -> Objects.equals(cc.getId(), id) && Objects.equals(cc.getSubId(), subId))
                .findFirst()
                .orElseThrow(() -> new BizException(I18nUtil.getExceptionMessage("CashShopService.getByCategory.exception1")));
    }

    /**
     * 将WZ商品实体转换为DTO并附加分类信息
     *
     * @param cashCategory 商城分类
     * @param cashItem     WZ商品实体
     * @return 商城商品DTO，包含分类信息和默认值
     */
    private CashShopSearchRtnDTO fromCashItem(CashCategory cashCategory, ModifiedCashItemDO cashItem) {
        return CashShopSearchRtnDTO.builder()
                .categoryId(cashCategory.getId())
                .categoryName(cashCategory.getName())
                .subcategoryId(cashCategory.getSubId())
                .subcategoryName(cashCategory.getSubName())
                .sn(cashItem.getSn())
                .itemId(cashItem.getItemId())
                .price(cashItem.getPrice())
                .defaultPrice(cashItem.getPrice())
                .period(cashItem.getPeriod())
                .defaultPeriod(cashItem.getPeriod())
                .priority(cashItem.getPriority())
                .defaultPriority(cashItem.getPriority())
                .count(cashItem.getCount())
                .defaultCount(cashItem.getCount())
                .onSale(cashItem.getOnSale())
                .defaultOnSale(cashItem.getOnSale())
                .bonus(cashItem.getBonus())
                .defaultBonus(cashItem.getBonus())
                .maplePoint(cashItem.getMaplePoint())
                .defaultMaplePoint(cashItem.getMaplePoint())
                .meso(cashItem.getMeso())
                .defaultMeso(cashItem.getMeso())
                .forPremiumUser(cashItem.getForPremiumUser())
                .defaultForPremiumUser(cashItem.getForPremiumUser())
                .gender(cashItem.getCommodityGender())
                .defaultGender(cashItem.getCommodityGender())
                .clz(cashItem.getClz())
                .defaultClz(cashItem.getClz())
                .limit(cashItem.getLimit())
                .defaultLimit(cashItem.getLimit())
                .pbCash(cashItem.getPbCash())
                .defaultPBCash(cashItem.getPbCash())
                .pbPoint(cashItem.getPbPoint())
                .defaultPBPoint(cashItem.getPbPoint())
                .pbGift(cashItem.getPbGift())
                .defaultPBGift(cashItem.getPbGift())
                .packageSn(cashItem.getPackageSn())
                .defaultPackageSn(cashItem.getPackageSn())
                .build();
    }

    /**
     * 用数据库中的商品数据覆盖DTO中的对应字段
     * 如果数据库字段为null，则保留DTO原值（WZ默认值）
     *
     * @param rtnDTO     WZ商品DTO，字段值会被数据库值覆盖
     * @param dbCashItem 数据库中的商品修改记录
     */
    private void setDbItemValue(CashShopSearchRtnDTO rtnDTO, ModifiedCashItemDO dbCashItem) {
        rtnDTO.setItemId(Optional.ofNullable(dbCashItem.getItemId()).orElse(rtnDTO.getItemId()));
        rtnDTO.setPrice(Optional.ofNullable(dbCashItem.getPrice()).orElse(rtnDTO.getPrice()));
        rtnDTO.setPeriod(Optional.ofNullable(dbCashItem.getPeriod()).orElse(rtnDTO.getPeriod()));
        rtnDTO.setPriority(Optional.ofNullable(dbCashItem.getPriority()).orElse(rtnDTO.getPriority()));
        rtnDTO.setCount(Optional.ofNullable(dbCashItem.getCount()).orElse(rtnDTO.getCount()));
        rtnDTO.setOnSale(Optional.ofNullable(dbCashItem.getOnSale()).orElse(rtnDTO.getOnSale()));
        rtnDTO.setBonus(Optional.ofNullable(dbCashItem.getBonus()).orElse(rtnDTO.getBonus()));
        rtnDTO.setMaplePoint(Optional.ofNullable(dbCashItem.getMaplePoint()).orElse(rtnDTO.getMaplePoint()));
        rtnDTO.setMeso(Optional.ofNullable(dbCashItem.getMeso()).orElse(rtnDTO.getMeso()));
        rtnDTO.setForPremiumUser(Optional.ofNullable(dbCashItem.getForPremiumUser()).orElse(rtnDTO.getForPremiumUser()));
        rtnDTO.setGender(Optional.ofNullable(dbCashItem.getCommodityGender()).orElse(rtnDTO.getGender()));
        rtnDTO.setClz(Optional.ofNullable(dbCashItem.getClz()).orElse(rtnDTO.getClz()));
        rtnDTO.setLimit(Optional.ofNullable(dbCashItem.getLimit()).orElse(rtnDTO.getLimit()));
        rtnDTO.setPbCash(Optional.ofNullable(dbCashItem.getPbCash()).orElse(rtnDTO.getPbCash()));
        rtnDTO.setPbPoint(Optional.ofNullable(dbCashItem.getPbPoint()).orElse(rtnDTO.getPbPoint()));
        rtnDTO.setPbGift(Optional.ofNullable(dbCashItem.getPbGift()).orElse(rtnDTO.getPbGift()));
        rtnDTO.setPackageSn(Optional.ofNullable(dbCashItem.getPackageSn()).orElse(rtnDTO.getPackageSn()));
    }

    /**
     * 批量修改商品上架属性
     * 遍历提交数据，根据操作类型（价格/数量/有效期）设置对应字段后逐一上架
     *
     * @param submit 批量上架请求DTO，包含商品列表、操作类型和修改值
     */
    @Transactional
    public void batchChangeOnSale(CashShopBatchOnSaleReqDTO submit) {
        for (ModifiedCashItemDO data : submit.getData()) {
            data.setOnSale(1);
            switch (submit.getType()) {
                case "价格":
                    data.setPrice(submit.getValue());
                    break;
                case "数量":
                    data.setCount(submit.getValue().shortValue());
                    break;
                case "有效期":
                    data.setPeriod(submit.getValue().longValue());
                    break;
            }
            changeOnSale(data);
        }
    }
}
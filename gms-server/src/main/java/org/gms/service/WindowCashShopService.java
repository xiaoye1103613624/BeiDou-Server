package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.XyCashShopCategoryDO;
import org.gms.dao.entity.XyCashShopCategoryItemDO;
import org.gms.dao.entity.XyCashShopItemDO;
import org.gms.dao.mapper.XyCashShopCategoryItemMapper;
import org.gms.dao.mapper.XyCashShopCategoryMapper;
import org.gms.dao.mapper.XyCashShopItemMapper;
import org.gms.exception.BizException;
import org.gms.model.dto.WindowCashShopClientSyncReqDTO;
import org.gms.model.dto.WindowCashShopClientSyncRtnDTO;
import org.gms.model.dto.WindowCashShopIconSyncReqDTO;
import org.gms.model.dto.WindowCashShopIconSyncRtnDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.cashshop.CashShopAssetCheck;
import org.gms.server.cashshop.CashShopCatalog;
import org.gms.server.cashshop.CashShopClickType;
import org.gms.server.cashshop.CashShopItemNames;
import org.gms.server.cashshop.CashShopTaxonomy;
import org.gms.server.cashshop.CashShopWindowPackets;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.server.cashshop.DamageSkinCashItems;
import org.gms.server.cashshop.InventorySlotCashItems;
import org.gms.server.cashshop.ItemIconFiles;
import org.gms.server.cashshop.XyPlayCashItems;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service("windowCashShopService")
@RequiredArgsConstructor
public class WindowCashShopService {
    private final XyCashShopCategoryMapper categoryMapper;
    private final XyCashShopItemMapper itemMapper;
    private final XyCashShopCategoryItemMapper categoryItemMapper;
    private final GameIconService gameIconService;

    public Map<String, Object> getClientDataPathInfo() {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("configured", ClientDataPath.configuredRaw());
        m.put("resolved", ClientDataPath.resolve().map(Path::toString).orElse(""));
        m.put("jvmProperty", ClientDataPath.SYS_PROP);
        m.put("configCode", ClientDataPath.CONFIG_CODE);
        final var v = ClientDataPath.validateConfigured();
        m.put("ok", v.ok());
        m.put("skipped", v.skipped());
        m.put("warning", v.warning());
        m.put("message", v.message());
        return m;
    }

    public Map<String, Object> setClientDataPath(String absolutePath) {
        if (StringUtils.hasText(absolutePath)) {
            final Path p = Path.of(absolutePath.trim()).toAbsolutePath().normalize();
            final var v = ClientDataPath.validate(p);
            if (!v.ok()) {
                throw new BizException(v.message());
            }
            ClientDataPath.saveToGameConfig(p.toString());
        } else {
            ClientDataPath.saveToGameConfig("");
        }
        return getClientDataPathInfo();
    }

    public Map<String, Object> validateClientDataPath(String absolutePath) {
        final Path p = StringUtils.hasText(absolutePath)
                ? Path.of(absolutePath.trim()).toAbsolutePath().normalize()
                : ClientDataPath.resolve().orElse(null);
        final var v = p == null
                ? ClientDataPath.ValidationResult.skip("path empty")
                : ClientDataPath.validate(p);
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("ok", v.ok());
        m.put("skipped", v.skipped());
        m.put("warning", v.warning());
        m.put("path", v.path());
        m.put("message", v.message());
        return m;
    }

    public List<Map<String, Object>> listDirectories(String absolutePath) {
        RequireUtil.requireNotEmpty(absolutePath, "path");
        final Path root = Path.of(absolutePath.trim()).toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            throw new BizException("not a directory: " + root);
        }
        try (var stream = Files.list(root)) {
            return stream.filter(Files::isDirectory)
                    .sorted()
                    .map(p -> {
                        final Map<String, Object> n = new LinkedHashMap<>();
                        n.put("name", p.getFileName() != null ? p.getFileName().toString() : p.toString());
                        n.put("path", p.toString());
                        return n;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new BizException("listDirectories failed: " + e.getMessage());
        }
    }

    public List<XyCashShopCategoryDO> listCategories() {
        return categoryMapper.selectAll().stream()
                .sorted(Comparator.comparing((XyCashShopCategoryDO c) -> c.getSort() == null ? 0 : c.getSort())
                        .thenComparing(c -> c.getId() == null ? 0 : c.getId()))
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public XyCashShopCategoryDO saveCategory(XyCashShopCategoryDO body) {
        RequireUtil.requireNotEmpty(body.getName(), "name");
        if (!StringUtils.hasText(body.getClickType())) {
            body.setClickType(CashShopClickType.SHOW_ITEMS.name());
        } else {
            body.setClickType(CashShopClickType.from(body.getClickType()).name());
        }
        if (body.getSort() == null) {
            body.setSort(0);
        }
        if (body.getEnabled() == null) {
            body.setEnabled(1);
        }
        if (body.getIsHot() == null) {
            body.setIsHot(0);
        }
        if (body.getId() == null) {
            categoryMapper.insertSelective(body);
        } else {
            categoryMapper.update(body);
        }
        return body;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Integer id) {
        RequireUtil.requireNotNull(id, "id");
        categoryMapper.deleteById(id);
    }

    public List<XyCashShopItemDO> listItems() {
        return itemMapper.selectAll();
    }

    public List<Map<String, Object>> listItemsGrouped() {
        final List<XyCashShopCategoryDO> cats = listCategories();
        final Map<Integer, XyCashShopItemDO> items = itemMapper.selectAll().stream()
                .collect(Collectors.toMap(XyCashShopItemDO::getItemId, i -> i, (a, b) -> a, LinkedHashMap::new));
        final List<XyCashShopCategoryItemDO> links = categoryItemMapper.selectAll();
        final Map<Integer, List<XyCashShopCategoryItemDO>> byCat = links.stream()
                .collect(Collectors.groupingBy(XyCashShopCategoryItemDO::getCategoryId, LinkedHashMap::new, Collectors.toList()));

        final List<Map<String, Object>> out = new ArrayList<>();
        for (XyCashShopCategoryDO cat : cats) {
            final Map<String, Object> node = new LinkedHashMap<>();
            node.put("category", cat);
            final List<Map<String, Object>> rows = new ArrayList<>();
            final List<XyCashShopCategoryItemDO> catLinks = byCat.getOrDefault(cat.getId(), List.of()).stream()
                    .sorted(Comparator.comparing(l -> l.getSort() == null ? 0 : l.getSort()))
                    .toList();
            for (XyCashShopCategoryItemDO link : catLinks) {
                final XyCashShopItemDO item = items.get(link.getItemId());
                if (item == null) {
                    continue;
                }
                final Map<String, Object> row = new LinkedHashMap<>();
                row.put("link", link);
                row.put("item", item);
                rows.add(row);
            }
            node.put("items", rows);
            out.add(node);
        }
        return out;
    }

    @Transactional(rollbackFor = Exception.class)
    public XyCashShopItemDO saveItem(XyCashShopItemDO body, boolean requireClientAsset) {
        RequireUtil.requireNotNull(body.getItemId(), "itemId");
        final int itemId = body.getItemId();
        // 扩展背包栏券 / 伤害皮肤本体无 WZ 实物，仅目录+购买时服务端直接履约
        final boolean slotCoupon = InventorySlotCashItems.isSlotCoupon(itemId);
        final boolean damageSkinSku = DamageSkinCashItems.isCashSku(itemId);
        if (!slotCoupon && !damageSkinSku) {
            final CashShopAssetCheck.Result check = CashShopAssetCheck.check(itemId);
            if (!check.serverOk()) {
                throw new BizException(String.join("; ", check.messages()));
            }
            if (requireClientAsset && !check.clientSkipped() && !check.clientOk()) {
                throw new BizException("client asset check failed: " + String.join("; ", check.messages()));
            }
        }
        if (body.getPrice() == null) {
            body.setPrice(0);
        }
        if (body.getCount() == null) {
            body.setCount(1);
        }
        if (body.getPeriod() == null) {
            body.setPeriod(0);
        }
        if (body.getGender() == null) {
            body.setGender(0);
        }
        if (body.getEnabled() == null) {
            body.setEnabled(1);
        }
        String name = CashShopItemNames.resolve(body.getItemId(), body.getName());
        if (!StringUtils.hasText(name)) {
            name = String.valueOf(body.getItemId());
        }
        body.setName(name);
        if (itemMapper.selectOneById(body.getItemId()) == null) {
            itemMapper.insertSelective(body);
        } else {
            itemMapper.update(body);
        }
        return body;
    }

    @Transactional(rollbackFor = Exception.class)
    public void linkItem(Integer categoryId, Integer itemId, Integer sort, Integer enabled) {
        RequireUtil.requireNotNull(categoryId, "categoryId");
        RequireUtil.requireNotNull(itemId, "itemId");
        if (categoryMapper.selectOneById(categoryId) == null) {
            throw new BizException("category not found: " + categoryId);
        }
        if (itemMapper.selectOneById(itemId) == null) {
            throw new BizException("item not found, save item first: " + itemId);
        }
        final XyCashShopCategoryItemDO exist = categoryItemMapper.selectOneByQuery(
                QueryWrapper.create().eq("category_id", categoryId).eq("item_id", itemId));
        if (exist != null) {
            exist.setSort(sort == null ? exist.getSort() : sort);
            exist.setEnabled(enabled == null ? exist.getEnabled() : enabled);
            categoryItemMapper.update(exist);
            return;
        }
        categoryItemMapper.insertSelective(XyCashShopCategoryItemDO.builder()
                .categoryId(categoryId)
                .itemId(itemId)
                .sort(sort == null ? 0 : sort)
                .enabled(enabled == null ? 1 : enabled)
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public void unlinkItem(Integer categoryId, Integer itemId) {
        categoryItemMapper.deleteByQuery(
                QueryWrapper.create().eq("category_id", categoryId).eq("item_id", itemId));
    }

    public CashShopAssetCheck.Result checkAsset(int itemId) {
        return CashShopAssetCheck.check(itemId);
    }

    public Map<String, Object> reloadAll() {
        if (!loadFromDbIntoMemory()) {
            CashShopCatalog.replaceAll(List.of(), "empty");
        }
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("source", CashShopCatalog.source());
        m.put("size", CashShopCatalog.size());
        m.put("buckets", CashShopCatalog.index().size());
        return m;
    }

    public Map<String, Object> reloadCategory(Integer categoryId) {
        RequireUtil.requireNotNull(categoryId, "categoryId");
        final XyCashShopCategoryDO cat = categoryMapper.selectOneById(categoryId);
        if (cat == null) {
            throw new BizException("category not found: " + categoryId);
        }
        if (cat.getLegacyTab() == null || cat.getLegacyCategory() == null) {
            throw new BizException("category missing legacy_tab/legacy_category; needed until taxonomy protocol");
        }
        final List<CashShopCatalog.Row> rows = buildRowsForCategory(cat);
        CashShopCatalog.replaceBucket(cat.getLegacyTab(), cat.getLegacyCategory(), rows);
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("categoryId", categoryId);
        m.put("tab", cat.getLegacyTab());
        m.put("category", cat.getLegacyCategory());
        m.put("itemCount", rows.size());
        m.put("source", CashShopCatalog.source());
        return m;
    }

    public boolean loadFromDbIntoMemory() {
        final List<XyCashShopCategoryDO> cats = categoryMapper.selectAll().stream()
                .filter(c -> c.getEnabled() != null && c.getEnabled() == 1)
                .filter(c -> c.getLegacyTab() != null && c.getLegacyCategory() != null)
                .filter(c -> CashShopClickType.from(c.getClickType()) == CashShopClickType.SHOW_ITEMS
                        || (c.getIsHot() != null && c.getIsHot() == 1))
                .toList();
        if (cats.isEmpty()) {
            return false;
        }

        final List<XyCashShopCategoryItemDO> links = categoryItemMapper.selectAll().stream()
                .filter(l -> l.getEnabled() == null || l.getEnabled() == 1)
                .toList();
        if (links.isEmpty()) {
            return false;
        }

        final Map<Integer, XyCashShopItemDO> items = itemMapper.selectAll().stream()
                .filter(i -> i.getEnabled() == null || i.getEnabled() == 1)
                .collect(Collectors.toMap(XyCashShopItemDO::getItemId, i -> i, (a, b) -> a));
        if (items.isEmpty()) {
            return false;
        }

        final Map<Integer, List<XyCashShopCategoryItemDO>> byCat = links.stream()
                .collect(Collectors.groupingBy(XyCashShopCategoryItemDO::getCategoryId));

        final List<CashShopCatalog.Row> rows = new ArrayList<>();
        final Map<Integer, CashShopCatalog.Row> purchase = new HashMap<>();
        for (XyCashShopCategoryDO cat : cats) {
            final List<XyCashShopCategoryItemDO> catLinks = byCat.getOrDefault(cat.getId(), List.of()).stream()
                    .sorted(Comparator.comparing(l -> l.getSort() == null ? 0 : l.getSort()))
                    .toList();
            for (XyCashShopCategoryItemDO link : catLinks) {
                final XyCashShopItemDO item = items.get(link.getItemId());
                if (item == null) {
                    continue;
                }
                final String name = CashShopItemNames.resolve(item.getItemId(), item.getName());
                if (!StringUtils.hasText(name)) {
                    continue;
                }
                final CashShopCatalog.Row row = new CashShopCatalog.Row(
                        item.getItemId(),
                        Math.max(0, item.getPrice() == null ? 0 : item.getPrice()),
                        Math.max(1, item.getCount() == null ? 1 : item.getCount()),
                        cat.getLegacyTab(),
                        cat.getLegacyCategory(),
                        Math.max(0, item.getPeriod() == null ? 0 : item.getPeriod()),
                        item.getGender() == null ? 0 : item.getGender(),
                        name);
                rows.add(row);
                purchase.putIfAbsent(item.getItemId(), row);
            }
        }
        if (rows.isEmpty()) {
            return false;
        }
        CashShopCatalog.replaceAll(rows, "db:" + cats.size() + "cats/" + purchase.size() + "items");
        return true;
    }

    private List<CashShopCatalog.Row> buildRowsForCategory(XyCashShopCategoryDO cat) {
        if (cat.getEnabled() != null && cat.getEnabled() == 0) {
            return List.of();
        }
        final List<XyCashShopCategoryItemDO> links = categoryItemMapper.selectListByQuery(
                        QueryWrapper.create().eq("category_id", cat.getId()))
                .stream()
                .filter(l -> l.getEnabled() == null || l.getEnabled() == 1)
                .sorted(Comparator.comparing(l -> l.getSort() == null ? 0 : l.getSort()))
                .toList();
        final List<CashShopCatalog.Row> rows = new ArrayList<>();
        for (XyCashShopCategoryItemDO link : links) {
            final XyCashShopItemDO item = itemMapper.selectOneById(link.getItemId());
            if (item == null || (item.getEnabled() != null && item.getEnabled() == 0)) {
                continue;
            }
            final String name = CashShopItemNames.resolve(item.getItemId(), item.getName());
            if (!StringUtils.hasText(name)) {
                continue;
            }
            rows.add(new CashShopCatalog.Row(
                    item.getItemId(),
                    Math.max(0, item.getPrice() == null ? 0 : item.getPrice()),
                    Math.max(1, item.getCount() == null ? 1 : item.getCount()),
                    Objects.requireNonNull(cat.getLegacyTab()),
                    Objects.requireNonNull(cat.getLegacyCategory()),
                    Math.max(0, item.getPeriod() == null ? 0 : item.getPeriod()),
                    item.getGender() == null ? 0 : item.getGender(),
                    name));
        }
        return rows;
    }

    /**
     * 用 WZ 名称覆盖 xy_cashshop_item.name（忽略 DB 中的旧英文名）。
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importFromTsv(boolean onlyIfEmpty) {
        if (onlyIfEmpty && categoryMapper.selectCountByQuery(QueryWrapper.create()) > 0) {
            throw new BizException("categories not empty; pass onlyIfEmpty=false to backfill");
        }
        final Path path = Path.of("cashshop", "catalog.tsv");
        if (!Files.isRegularFile(path)) {
            throw new BizException("TSV not found: " + path.toAbsolutePath());
        }
        int cats = 0, items = 0, links = 0;
        final Map<String, Integer> catKeyToId = new HashMap<>();
        try {
            for (String line : Files.readAllLines(path, java.nio.charset.StandardCharsets.UTF_8)) {
                final String s = line.strip();
                if (s.isEmpty() || s.charAt(0) == '#') {
                    continue;
                }
                final String[] f = s.split("\t");
                if (f.length < 7) {
                    continue;
                }
                final int itemId = Integer.parseInt(f[0].strip());
                final int price = Integer.parseInt(f[1].strip());
                final int count = Integer.parseInt(f[2].strip());
                final int tab = Integer.parseInt(f[3].strip());
                final int cat = Integer.parseInt(f[4].strip());
                final int period = Integer.parseInt(f[5].strip());
                final int gender = Integer.parseInt(f[6].strip());
                final String tsvName = f.length > 7 ? f[7].strip() : "";
                final String name = CashShopItemNames.resolve(itemId, tsvName);
                if (!StringUtils.hasText(name)) {
                    continue;
                }

                final String key = tab + ":" + cat;
                Integer categoryId = catKeyToId.get(key);
                if (categoryId == null) {
                    final String tabKey = "tab:" + tab;
                    Integer parentId = catKeyToId.get(tabKey);
                    if (parentId == null) {
                        XyCashShopCategoryDO parent = categoryMapper.selectOneByQuery(
                                QueryWrapper.create().eq("name", "Tab " + tab));
                        if (parent == null) {
                            parent = XyCashShopCategoryDO.builder()
                                    .name("Tab " + tab)
                                    .parentId(null)
                                    .sort(tab)
                                    .enabled(1)
                                    .clickType(CashShopClickType.SHOW_ITEMS.name())
                                    .isHot(0)
                                    .legacyTab(tab)
                                    .legacyCategory(0)
                                    .remark("auto-tab")
                                    .build();
                            categoryMapper.insertSelective(parent);
                            cats++;
                        }
                        parentId = parent.getId();
                        catKeyToId.put(tabKey, parentId);
                    }

                    XyCashShopCategoryDO existing = categoryMapper.selectOneByQuery(
                            QueryWrapper.create().eq("legacy_tab", tab).eq("legacy_category", cat));
                    if (existing == null) {
                        existing = XyCashShopCategoryDO.builder()
                                .name("Cat " + tab + "-" + cat)
                                .parentId(parentId)
                                .sort(tab * 100 + cat)
                                .enabled(1)
                                .clickType(CashShopClickType.SHOW_ITEMS.name())
                                .isHot(0)
                                .legacyTab(tab)
                                .legacyCategory(cat)
                                .build();
                        categoryMapper.insertSelective(existing);
                        cats++;
                    }
                    categoryId = existing.getId();
                    catKeyToId.put(key, categoryId);
                }

                if (itemMapper.selectOneById(itemId) == null) {
                    itemMapper.insertSelective(XyCashShopItemDO.builder()
                            .itemId(itemId)
                            .price(price)
                            .count(count)
                            .period(period)
                            .gender(gender)
                            .name(name)
                            .enabled(1)
                            .build());
                    items++;
                }

                final Integer finalCatId = categoryId;
                final long linkCount = categoryItemMapper.selectCountByQuery(
                        QueryWrapper.create().eq("category_id", finalCatId).eq("item_id", itemId));
                if (linkCount == 0) {
                    categoryItemMapper.insertSelective(XyCashShopCategoryItemDO.builder()
                            .categoryId(finalCatId)
                            .itemId(itemId)
                            .sort(links)
                            .enabled(1)
                            .build());
                    links++;
                }
            }
        } catch (IOException e) {
            throw new BizException("read TSV failed: " + e.getMessage());
        }

        final boolean reloaded = loadFromDbIntoMemory();
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("path", path.toAbsolutePath().toString());
        m.put("categoriesCreated", cats);
        m.put("itemsCreated", items);
        m.put("linksCreated", links);
        m.put("catalogReloaded", reloaded);
        m.put("catalogSource", CashShopCatalog.source());
        m.put("catalogSize", CashShopCatalog.size());
        return m;
    }

    /**
     * 用 WZ 名称覆盖 xy_cashshop_item.name（忽略 DB 中的旧英文名）。
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> refreshNamesFromWz() {
        final ItemInformationProvider ii = ItemInformationProvider.getInstance();
        int updated = 0;
        int skipped = 0;
        for (XyCashShopItemDO item : itemMapper.selectAll()) {
            final String wzName = ii.getName(item.getItemId());
            if (!CashShopItemNames.isUsableWzName(wzName)) {
                skipped++;
                continue;
            }
            final String name = CashShopItemNames.truncate(wzName);
            if (Objects.equals(name, item.getName())) {
                skipped++;
                continue;
            }
            item.setName(name);
            itemMapper.update(item);
            updated++;
        }
        final boolean reloaded = loadFromDbIntoMemory();
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("updated", updated);
        m.put("skipped", skipped);
        m.put("catalogReloaded", reloaded);
        m.put("catalogSource", CashShopCatalog.source());
        m.put("catalogSize", CashShopCatalog.size());
        return m;
    }

    // ---------- taxonomy / gate / browse / seed ----------

    public List<CashShopWindowPackets.TaxonomyNode> buildTaxonomy(org.gms.client.Character chr) {
        final List<XyCashShopCategoryDO> cats = listCategories().stream()
                .filter(c -> c.getEnabled() == null || c.getEnabled() == 1)
                .toList();
        final Map<Integer, Long> linkCounts = categoryItemMapper.selectAll().stream()
                .filter(l -> l.getEnabled() == null || l.getEnabled() == 1)
                .collect(Collectors.groupingBy(XyCashShopCategoryItemDO::getCategoryId, Collectors.counting()));

        final List<CashShopWindowPackets.TaxonomyNode> out = new ArrayList<>();
        for (XyCashShopCategoryDO c : cats) {
            if (c.getGateItemId() != null && c.getGateItemId() > 0) {
                if (chr == null || !chr.haveItem(c.getGateItemId())) {
                    continue;
                }
            }
            final CashShopClickType ct = CashShopClickType.from(c.getClickType());
            final int clickByte = switch (ct) {
                case SHOW_ITEMS -> CashShopWindowPackets.CLICK_SHOW_ITEMS;
                case OPEN_WINDOW -> CashShopWindowPackets.CLICK_OPEN_WINDOW;
                case SEND_PACKET -> CashShopWindowPackets.CLICK_SEND_PACKET;
                case RUN_NPC -> CashShopWindowPackets.CLICK_RUN_NPC;
                case WARP -> CashShopWindowPackets.CLICK_WARP;
            };
            final int parent = c.getParentId() == null ? 0 : c.getParentId();
            final int tab = c.getLegacyTab() == null ? 0 : c.getLegacyTab();
            final int cat = c.getLegacyCategory() == null ? 0 : c.getLegacyCategory();
            final int count = linkCounts.getOrDefault(c.getId(), 0L).intValue();
            out.add(new CashShopWindowPackets.TaxonomyNode(
                    c.getId(), parent, clickByte,
                    c.getClickParam() == null ? "" : c.getClickParam(),
                    tab, cat, count, c.getName()));
        }
        return out;
    }

    public boolean canAccessLegacyBucket(org.gms.client.Character chr, int tab, int cat) {
        final List<XyCashShopCategoryDO> matched = categoryMapper.selectListByQuery(
                QueryWrapper.create().eq("legacy_tab", tab).eq("legacy_category", cat));
        if (matched.isEmpty()) {
            return false;
        }
        for (XyCashShopCategoryDO c : matched) {
            if (c.getEnabled() != null && c.getEnabled() == 0) {
                continue;
            }
            if (c.getGateItemId() != null && c.getGateItemId() > 0) {
                if (chr == null || !chr.haveItem(c.getGateItemId())) {
                    continue;
                }
            }
            return true;
        }
        return false;
    }

    public CashShopClickType resolveClickType(org.gms.client.Character chr, int categoryId) {
        final XyCashShopCategoryDO c = categoryMapper.selectOneById(categoryId);
        if (c == null || (c.getEnabled() != null && c.getEnabled() == 0)) {
            return CashShopClickType.SHOW_ITEMS;
        }
        if (c.getGateItemId() != null && c.getGateItemId() > 0
                && (chr == null || !chr.haveItem(c.getGateItemId()))) {
            return CashShopClickType.SHOW_ITEMS;
        }
        return CashShopClickType.from(c.getClickType());
    }

    public List<Map<String, Object>> browseItems(Integer minId, Integer maxId, String keyword) {
        final int lo = minId == null ? 1000000 : minId;
        final int hi = maxId == null ? Math.min(lo + 5000, 1999999) : maxId;
        if (hi < lo || hi - lo > 20000) {
            throw new BizException("id range too large (max 20000)");
        }
        final ItemInformationProvider ii = ItemInformationProvider.getInstance();
        final String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        final List<Map<String, Object>> out = new ArrayList<>();
        for (int id = lo; id <= hi; id++) {
            if (!ii.itemExists(id)) {
                continue;
            }
            String name = ii.getName(id);
            if (name == null) {
                name = String.valueOf(id);
            }
            if (!kw.isEmpty() && !name.toLowerCase().contains(kw) && !String.valueOf(id).contains(kw)) {
                continue;
            }
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put("itemId", id);
            row.put("name", name);
            out.add(row);
            if (out.size() >= 500) {
                break;
            }
        }
        return out;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importItems(Integer categoryId, List<Integer> itemIds,
                                           Integer price, boolean requireClient) {
        RequireUtil.requireNotNull(categoryId, "categoryId");
        if (itemIds == null || itemIds.isEmpty()) {
            throw new BizException("itemIds empty");
        }
        if (categoryMapper.selectOneById(categoryId) == null) {
            throw new BizException("category not found: " + categoryId);
        }
        int saved = 0, linked = 0, skipped = 0;
        for (Integer itemId : itemIds) {
            if (itemId == null) {
                continue;
            }
            try {
                final XyCashShopItemDO body = XyCashShopItemDO.builder()
                        .itemId(itemId)
                        .price(price == null ? 1000 : price)
                        .count(1)
                        .period(0)
                        .gender(0)
                        .enabled(1)
                        .build();
                saveItem(body, requireClient);
                saved++;
                linkItem(categoryId, itemId, linked, 1);
                linked++;
            } catch (Exception e) {
                skipped++;
                log.warn("importItems skip {}: {}", itemId, e.toString());
            }
        }
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("saved", saved);
        m.put("linked", linked);
        m.put("skipped", skipped);
        return m;
    }

    @Transactional(rollbackFor = Exception.class)
    public void reorderCategories(List<Integer> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return;
        }
        int sort = 0;
        for (Integer id : orderedIds) {
            if (id == null) {
                continue;
            }
            final XyCashShopCategoryDO c = categoryMapper.selectOneById(id);
            if (c == null) {
                continue;
            }
            c.setSort(sort++);
            categoryMapper.update(c);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> seedDefaults() {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("hot", ensureCategory("热门", null, 0, 1, CashShopClickType.SHOW_ITEMS.name(),
                null, null, 1, 8, 0));
        // 皮肤大类：阿尔泰帽子 Cap 1008900-1009999，售价 10 万点券（客户端 kTabs id=9）
        m.put("skin", ensureCategory("皮肤", null, 10, 1, CashShopClickType.SHOW_ITEMS.name(),
                null, null, 0, 9, 0));
        // XY玩法：自定义入口道具（伤害皮肤栏/幻化/棱镜/美容院/扩容）；不含皮肤本体虚拟 SKU
        m.put("xyPlay", ensureCategory(XyPlayCashItems.CATEGORY_NAME, null, 20, 1,
                CashShopClickType.SHOW_ITEMS.name(), null, null, 0,
                XyPlayCashItems.TAB, XyPlayCashItems.CATEGORY));
        final Integer skinId = (Integer) ((Map<?, ?>) m.get("skin")).get("id");
        if (skinId != null) {
            final long cnt = categoryItemMapper.selectCountByQuery(
                    QueryWrapper.create().eq("category_id", skinId));
            if (cnt == 0) {
                final List<Integer> ids = new ArrayList<>();
                final ItemInformationProvider ii = ItemInformationProvider.getInstance();
                for (int id = 1008900; id <= 1009999; id++) {
                    if (ii.itemExists(id)) {
                        ids.add(id);
                    }
                }
                m.put("skinImport", importItems(skinId, ids, 100000, false));
            }
        }
        final Integer xyCatId = (Integer) ((Map<?, ?>) m.get("xyPlay")).get("id");
        if (xyCatId != null) {
            m.put("xyPlayItems", seedXyPlayItems(xyCatId));
        }
        m.put("catalogReloaded", loadFromDbIntoMemory());
        return m;
    }

    /**
     * 上架 {@link XyPlayCashItems} 入口道具；卸下并禁用伤害皮肤本体虚拟 SKU。
     */
    private Map<String, Object> seedXyPlayItems(int categoryId) {
        final Map<String, Object> out = new LinkedHashMap<>();
        int saved = 0;
        int linked = 0;
        int unlinkedBodies = 0;
        int disabledBodies = 0;

        // 卸下本分类下的皮肤本体虚拟 SKU
        final List<XyCashShopCategoryItemDO> links = categoryItemMapper.selectListByQuery(
                QueryWrapper.create().eq("category_id", categoryId));
        for (XyCashShopCategoryItemDO link : links) {
            if (link.getItemId() != null && XyPlayCashItems.isDamageSkinBodySku(link.getItemId())) {
                categoryItemMapper.deleteByQuery(QueryWrapper.create()
                        .eq("category_id", categoryId)
                        .eq("item_id", link.getItemId()));
                unlinkedBodies++;
            }
        }
        for (XyCashShopItemDO body : itemMapper.selectListByQuery(
                QueryWrapper.create().gt("item_id", DamageSkinCashItems.BASE)
                        .le("item_id", DamageSkinCashItems.MAX_ITEM_ID))) {
            if (body.getEnabled() == null || body.getEnabled() != 0) {
                body.setEnabled(0);
                itemMapper.update(body);
                disabledBodies++;
            }
        }

        // 扩展券从「游戏」分类移除，避免重复
        final XyCashShopCategoryDO gameCat = categoryMapper.selectOneByQuery(
                QueryWrapper.create().eq("legacy_tab", 5).eq("legacy_category", 2));
        if (gameCat != null && gameCat.getId() != null) {
            for (InventorySlotCashItems.Spec s : InventorySlotCashItems.all()) {
                categoryItemMapper.deleteByQuery(QueryWrapper.create()
                        .eq("category_id", gameCat.getId())
                        .eq("item_id", s.itemId()));
            }
        }

        for (XyPlayCashItems.Entry e : XyPlayCashItems.ENTRIES) {
            try {
                saveItem(XyCashShopItemDO.builder()
                        .itemId(e.itemId())
                        .price(e.price())
                        .count(1)
                        .period(0)
                        .gender(2)
                        .name(e.name())
                        .enabled(1)
                        .remark(e.remark())
                        .build(), false);
                saved++;
                linkItem(categoryId, e.itemId(), e.sort(), 1);
                linked++;
            } catch (Exception ex) {
                log.warn("seed xy-play item {} skip: {}", e.itemId(), ex.toString());
            }
        }
        out.put("saved", saved);
        out.put("linked", linked);
        out.put("unlinkedBodies", unlinkedBodies);
        out.put("disabledBodies", disabledBodies);
        return out;
    }

    private Map<String, Object> ensureCategory(String name, Integer parentId, int sort, int enabled,
                                               String clickType, String clickParam, Integer gateItemId,
                                               int isHot, int legacyTab, int legacyCat) {
        XyCashShopCategoryDO existing = categoryMapper.selectOneByQuery(
                QueryWrapper.create().eq("name", name));
        if (existing == null) {
            existing = categoryMapper.selectOneByQuery(
                    QueryWrapper.create().eq("legacy_tab", legacyTab).eq("legacy_category", legacyCat));
        }
        boolean created = false;
        boolean updated = false;
        if (existing == null) {
            existing = XyCashShopCategoryDO.builder()
                    .name(name)
                    .parentId(parentId)
                    .sort(sort)
                    .enabled(enabled)
                    .clickType(clickType)
                    .clickParam(clickParam)
                    .gateItemId(gateItemId)
                    .isHot(isHot)
                    .legacyTab(legacyTab)
                    .legacyCategory(legacyCat)
                    .updatedAt(new Date())
                    .build();
            categoryMapper.insertSelective(existing);
            created = true;
        } else if (!Objects.equals(existing.getName(), name)
                || !Objects.equals(existing.getLegacyTab(), legacyTab)
                || !Objects.equals(existing.getLegacyCategory(), legacyCat)
                || !Objects.equals(existing.getSort(), sort)) {
            existing.setName(name);
            existing.setSort(sort);
            existing.setEnabled(enabled);
            existing.setClickType(clickType);
            existing.setLegacyTab(legacyTab);
            existing.setLegacyCategory(legacyCat);
            existing.setUpdatedAt(new Date());
            categoryMapper.update(existing);
            updated = true;
        }
        final Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", existing.getId());
        info.put("created", created);
        info.put("updated", updated);
        info.put("name", existing.getName());
        return info;
    }

    // ---------- icon sync / client Data sync ----------

    @Transactional(rollbackFor = Exception.class)
    public WindowCashShopIconSyncRtnDTO syncIcons(WindowCashShopIconSyncReqDTO req) {
        final WindowCashShopIconSyncReqDTO body = req == null ? new WindowCashShopIconSyncReqDTO() : req;
        final String modeRaw = body.getMode() == null ? "fillEmpty" : body.getMode().trim();
        final boolean force = "force".equalsIgnoreCase(modeRaw);
        if (!force && !"fillEmpty".equalsIgnoreCase(modeRaw)) {
            throw new BizException("mode must be fillEmpty or force");
        }
        final String mode = force ? "force" : "fillEmpty";
        final Path iconDir = ItemIconFiles.resolveOrCreateIconDir();
        final List<XyCashShopItemDO> targets = resolveIconSyncTargets(body);

        int updated = 0;
        int skipped = 0;
        int filesWritten = 0;
        int failed = 0;
        for (XyCashShopItemDO item : targets) {
            if (item == null || item.getItemId() == null || item.getItemId() <= 0) {
                skipped++;
                continue;
            }
            final boolean hasLocalUrl = StringUtils.hasText(item.getIconUrl()) && !isCdnIconUrl(item.getIconUrl());
            if (!force && hasLocalUrl) {
                skipped++;
                continue;
            }
            try {
                if (ensureLocalItemPng(item.getItemId(), force)) {
                    filesWritten++;
                }
                final String url = ItemIconFiles.webUrl(item.getItemId());
                if (!force && Objects.equals(url, item.getIconUrl())) {
                    skipped++;
                    continue;
                }
                item.setIconUrl(url);
                itemMapper.update(item);
                updated++;
            } catch (Exception e) {
                failed++;
                log.warn("syncIcons failed itemId={}: {}", item.getItemId(), e.toString());
            }
        }

        return WindowCashShopIconSyncRtnDTO.builder()
                .mode(mode)
                .iconDir(iconDir.toString())
                .requested(targets.size())
                .updated(updated)
                .skipped(skipped)
                .filesWritten(filesWritten)
                .failed(failed)
                .message(String.format(Locale.ROOT,
                        "mode=%s requested=%d updated=%d skipped=%d filesWritten=%d failed=%d dir=%s",
                        mode, targets.size(), updated, skipped, filesWritten, failed, iconDir))
                .build();
    }

    private List<XyCashShopItemDO> resolveIconSyncTargets(WindowCashShopIconSyncReqDTO body) {
        if (body.getItemIds() != null && !body.getItemIds().isEmpty()) {
            final List<XyCashShopItemDO> out = new ArrayList<>();
            final Set<Integer> seen = new LinkedHashSet<>();
            for (Integer id : body.getItemIds()) {
                if (id == null || id <= 0 || !seen.add(id)) {
                    continue;
                }
                XyCashShopItemDO item = itemMapper.selectOneById(id);
                if (item != null) {
                    out.add(item);
                }
            }
            return out;
        }
        if (body.getCategoryId() != null) {
            final List<XyCashShopCategoryItemDO> links = categoryItemMapper.selectListByQuery(
                    QueryWrapper.create().eq("category_id", body.getCategoryId()));
            final List<XyCashShopItemDO> out = new ArrayList<>();
            final Set<Integer> seen = new LinkedHashSet<>();
            for (XyCashShopCategoryItemDO link : links) {
                if (link.getItemId() == null || !seen.add(link.getItemId())) {
                    continue;
                }
                XyCashShopItemDO item = itemMapper.selectOneById(link.getItemId());
                if (item != null) {
                    out.add(item);
                }
            }
            return out;
        }
        return itemMapper.selectAll();
    }

    /**
     * Ensure {@code item-icons/{id}.png} exists: reuse local/legacy cache, else CDN ? {@code xy_game_icon} ? disk.
     * Client Data path is checked for asset presence only (no WZ?PNG extract in-process).
     */
    private boolean ensureLocalItemPng(int itemId, boolean force) {
        final Path dir = ItemIconFiles.resolveOrCreateIconDir();
        final Path png = ItemIconFiles.pngPath(dir, itemId);
        if (!force && Files.isRegularFile(png)) {
            return false;
        }
        if (ItemIconFiles.copyFromLegacyCacheIfPresent(itemId) && Files.isRegularFile(png) && !force) {
            return true;
        }
        final Optional<byte[]> bytes = gameIconService.ensureItemIconBytes(itemId, force);
        if (bytes.isPresent() && ItemIconFiles.writePng(itemId, bytes.get())) {
            return true;
        }
        return ItemIconFiles.copyFromLegacyCacheIfPresent(itemId);
    }

    /**
     * Scan client Data and upsert categories/items. Idempotent; not one giant DB transaction
     * (avoids long locks). Icon fill only uses local/legacy PNG — no CDN during bulk sync.
     */
    public WindowCashShopClientSyncRtnDTO syncFromClientData(WindowCashShopClientSyncReqDTO req) {
        final long started = System.currentTimeMillis();
        final WindowCashShopClientSyncReqDTO body = req == null ? new WindowCashShopClientSyncReqDTO() : req;
        final Path root = ClientDataPath.resolve()
                .orElseThrow(() -> new BizException(
                        "未配置客户端 Data 路径，请先填写并保存（例如 F:\\MXD_dev\\BeiDou-Client\\Data）"));
        final var validated = ClientDataPath.validate(root);
        if (!validated.ok()) {
            throw new BizException("客户端 Data 路径无效: " + validated.message());
        }
        if (!Files.isDirectory(root.resolve("Character")) && !Files.isDirectory(root.resolve("Item"))) {
            throw new BizException(
                    "路径看起来不是客户端 Data 根目录（缺少 Character/ 与 Item/）。请指向 …\\BeiDou-Client\\Data");
        }

        final boolean fillIcons = body.getFillIcons() == null || Boolean.TRUE.equals(body.getFillIcons());
        final boolean cashOnly = body.getCashOnly() == null || Boolean.TRUE.equals(body.getCashOnly());
        final int defaultPrice = body.getDefaultPrice() == null ? 0 : Math.max(0, body.getDefaultPrice());

        log.info("[windowCashShop] syncFromClientData start path={} cashOnly={} fillIcons={}",
                root, cashOnly, fillIcons);
        final Set<Integer> scannedIds = scanClientItemIds(root, cashOnly);
        log.info("[windowCashShop] scan done: {} candidate ids", scannedIds.size());

        int categoriesCreated = 0;
        int categoriesUpdated = 0;
        int itemsUpserted = 0;
        int linksUpserted = 0;
        int iconsFilled = 0;
        int skipped = 0;

        final Map<String, Integer> bucketToCategoryId = new HashMap<>();
        final Map<Integer, Integer> itemToCanonicalCat = new HashMap<>();
        final ItemInformationProvider ii = ItemInformationProvider.getInstance();

        // Preload to avoid N+1 selects
        final Map<Integer, XyCashShopItemDO> existingItems = new HashMap<>();
        for (XyCashShopItemDO row : itemMapper.selectAll()) {
            if (row != null && row.getItemId() != null) {
                existingItems.put(row.getItemId(), row);
            }
        }
        final Set<String> existingLinks = new HashSet<>();
        for (XyCashShopCategoryItemDO link : categoryItemMapper.selectAll()) {
            if (link != null && link.getCategoryId() != null && link.getItemId() != null) {
                existingLinks.add(link.getCategoryId() + ":" + link.getItemId());
            }
        }

        final List<XyCashShopItemDO> pendingInsertItems = new ArrayList<>();
        final List<XyCashShopItemDO> pendingUpdateItems = new ArrayList<>();
        final List<XyCashShopCategoryItemDO> pendingLinks = new ArrayList<>();
        final Path iconDir = fillIcons ? ItemIconFiles.resolveOrCreateIconDir() : null;
        int processed = 0;

        for (Integer itemId : scannedIds) {
            processed++;
            if (processed % 2000 == 0) {
                log.info("[windowCashShop] sync progress {}/{} upserted={} links={} skipped={}",
                        processed, scannedIds.size(), itemsUpserted, linksUpserted, skipped);
            }
            if (itemId == null || itemId <= 0) {
                skipped++;
                continue;
            }
            // Equips come from Character/*.img scan (already validated cash flag when cashOnly).
            // Non-equip ids must exist in server Item.wz.
            if (itemId / 1000000 != 1 && !ii.itemExists(itemId)) {
                skipped++;
                continue;
            }

            final String name = CashShopItemNames.resolve(itemId, null);
            if (!StringUtils.hasText(name)) {
                skipped++;
                continue;
            }

            // Only create a kCats bucket after the item is actually sellable (has a name).
            final CashShopTaxonomy.Bucket bucket = CashShopTaxonomy.forItemId(itemId);
            Integer categoryId = bucketToCategoryId.get(bucket.key());
            if (categoryId == null) {
                final Map<String, Object> ensured = ensureKCatsCategory(bucket);
                categoryId = (Integer) ensured.get("id");
                bucketToCategoryId.put(bucket.key(), categoryId);
                if (Boolean.TRUE.equals(ensured.get("created"))) {
                    categoriesCreated++;
                } else if (Boolean.TRUE.equals(ensured.get("updated"))) {
                    categoriesUpdated++;
                }
            }
            itemToCanonicalCat.put(itemId, categoryId);

            XyCashShopItemDO existing = existingItems.get(itemId);
            if (existing == null) {
                existing = XyCashShopItemDO.builder()
                        .itemId(itemId)
                        .price(defaultPrice)
                        .count(1)
                        .period(0)
                        .gender(0)
                        .name(name)
                        .enabled(1)
                        .remark("client-sync")
                        .build();
                if (fillIcons && tryFillLocalIconOnly(itemId, iconDir, existing)) {
                    iconsFilled++;
                }
                pendingInsertItems.add(existing);
                existingItems.put(itemId, existing);
                itemsUpserted++;
            } else {
                boolean dirty = false;
                if (!Objects.equals(existing.getName(), name)) {
                    existing.setName(name);
                    dirty = true;
                }
                if (fillIcons && isCdnOrEmptyIcon(existing.getIconUrl())
                        && tryFillLocalIconOnly(itemId, iconDir, existing)) {
                    iconsFilled++;
                    dirty = true;
                }
                if (dirty) {
                    pendingUpdateItems.add(existing);
                    itemsUpserted++;
                }
            }

            final String linkKey = categoryId + ":" + itemId;
            if (!existingLinks.contains(linkKey)) {
                pendingLinks.add(XyCashShopCategoryItemDO.builder()
                        .categoryId(categoryId)
                        .itemId(itemId)
                        .sort(linksUpserted)
                        .enabled(1)
                        .build());
                existingLinks.add(linkKey);
                linksUpserted++;
            }

            flushPendingBatches(pendingInsertItems, pendingUpdateItems, pendingLinks, false);
        }
        flushPendingBatches(pendingInsertItems, pendingUpdateItems, pendingLinks, true);

        final int linksMigrated = migrateStaleAutoLinks(itemToCanonicalCat, bucketToCategoryId);
        final int categoriesPruned = pruneEmptyAutoCategories();
        final boolean catalogReloaded = loadFromDbIntoMemory();

        final long durationMs = System.currentTimeMillis() - started;
        String emptyReason = null;
        if (scannedIds.isEmpty()) {
            emptyReason = cashOnly
                    ? "未扫到任何现金物品。请确认路径为 …\\Data（含 Character、Item/Cash），且客户端有散图/包。"
                    : "未扫到任何物品文件。请确认路径为客户端 Data 根目录。";
        } else if (itemsUpserted == 0 && linksUpserted == 0 && linksMigrated == 0) {
            emptyReason = "已扫描 " + scannedIds.size()
                    + " 个候选，但无需新增（均已存在或无可用名称）。可切换分类查看，或清空后重试。";
        }

        final String message = String.format(Locale.CHINA,
                "路径=%s | 扫描=%d | 新建分类=%d | 更新分类=%d | 清理空分类=%d | 迁移关联=%d | 商品upsert=%d | 关联=%d | 本地图标=%d | 跳过=%d | 热重载=%s(%d) | 耗时=%dms%s",
                root, scannedIds.size(), categoriesCreated, categoriesUpdated,
                categoriesPruned, linksMigrated, itemsUpserted, linksUpserted, iconsFilled, skipped,
                catalogReloaded ? CashShopCatalog.source() : "skip",
                CashShopCatalog.size(), durationMs,
                emptyReason != null ? " | " + emptyReason : "");

        log.info("[windowCashShop] syncFromClientData done: {}", message);
        return WindowCashShopClientSyncRtnDTO.builder()
                .clientDataPath(root.toString())
                .categoriesCreated(categoriesCreated)
                .categoriesUpdated(categoriesUpdated)
                .itemsUpserted(itemsUpserted)
                .linksUpserted(linksUpserted)
                .iconsFilled(iconsFilled)
                .scanned(scannedIds.size())
                .skipped(skipped)
                .categoriesPruned(categoriesPruned)
                .linksMigrated(linksMigrated)
                .catalogReloaded(catalogReloaded)
                .catalogSource(CashShopCatalog.source())
                .catalogSize(CashShopCatalog.size())
                .durationMs(durationMs)
                .emptyReason(emptyReason)
                .message(message)
                .build();
    }

    /**
     * Upsert a kCats (tab,category) row by legacy pair — never by display name,
     * so "宠物" at the wrong tab is not reused for 脸饰.
     */
    private Map<String, Object> ensureKCatsCategory(CashShopTaxonomy.Bucket bucket) {
        XyCashShopCategoryDO existing = categoryMapper.selectOneByQuery(
                QueryWrapper.create()
                        .eq("legacy_tab", bucket.legacyTab())
                        .eq("legacy_category", bucket.legacyCategory()));
        boolean created = false;
        boolean updated = false;
        if (existing == null) {
            existing = XyCashShopCategoryDO.builder()
                    .name(bucket.name())
                    .parentId(null)
                    .sort(bucket.sort())
                    .enabled(1)
                    .clickType(CashShopClickType.SHOW_ITEMS.name())
                    .isHot(0)
                    .legacyTab(bucket.legacyTab())
                    .legacyCategory(bucket.legacyCategory())
                    .remark("client-sync")
                    .updatedAt(new Date())
                    .build();
            categoryMapper.insertSelective(existing);
            created = true;
        } else {
            boolean dirty = false;
            if (!Objects.equals(existing.getName(), bucket.name())) {
                existing.setName(bucket.name());
                dirty = true;
            }
            if (!Objects.equals(existing.getSort(), bucket.sort())) {
                existing.setSort(bucket.sort());
                dirty = true;
            }
            if (!StringUtils.hasText(existing.getClickType())) {
                existing.setClickType(CashShopClickType.SHOW_ITEMS.name());
                dirty = true;
            }
            if (dirty) {
                existing.setUpdatedAt(new Date());
                categoryMapper.update(existing);
                updated = true;
            }
        }
        final Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", existing.getId());
        info.put("created", created);
        info.put("updated", updated);
        info.put("name", existing.getName());
        return info;
    }

    /**
     * 把误挂的 SHOW_ITEMS 关联挪到 {@link CashShopTaxonomy#forItemId} 的 kCats 桶。
     * 覆盖本轮扫描 <b>以及</b> 库里已有商品（170xxxx 曾进「帽子」时，即使本轮没扫到该 img 也会改挂到「武器」）。
     */
    private int migrateStaleAutoLinks(Map<Integer, Integer> itemToCanonicalCat,
                                      Map<String, Integer> bucketToCategoryId) {
        for (XyCashShopItemDO item : itemMapper.selectAll()) {
            if (item == null || item.getItemId() == null) {
                continue;
            }
            if (itemToCanonicalCat.containsKey(item.getItemId())) {
                continue;
            }
            final CashShopTaxonomy.Bucket bucket = CashShopTaxonomy.forItemId(item.getItemId());
            Integer categoryId = bucketToCategoryId.get(bucket.key());
            if (categoryId == null) {
                final Map<String, Object> ensured = ensureKCatsCategory(bucket);
                categoryId = (Integer) ensured.get("id");
                bucketToCategoryId.put(bucket.key(), categoryId);
            }
            itemToCanonicalCat.put(item.getItemId(), categoryId);
        }
        if (itemToCanonicalCat.isEmpty()) {
            return 0;
        }
        final Map<Integer, XyCashShopCategoryDO> cats = new HashMap<>();
        for (XyCashShopCategoryDO cat : categoryMapper.selectAll()) {
            if (cat != null && cat.getId() != null) {
                cats.put(cat.getId(), cat);
            }
        }
        final Set<String> existingLinks = new HashSet<>();
        final List<XyCashShopCategoryItemDO> allLinks = categoryItemMapper.selectAll();
        for (XyCashShopCategoryItemDO link : allLinks) {
            if (link != null && link.getCategoryId() != null && link.getItemId() != null) {
                existingLinks.add(link.getCategoryId() + ":" + link.getItemId());
            }
        }
        int moved = 0;
        for (XyCashShopCategoryItemDO link : allLinks) {
            if (link == null || link.getItemId() == null || link.getCategoryId() == null) {
                continue;
            }
            final Integer canonical = itemToCanonicalCat.get(link.getItemId());
            if (canonical == null || canonical.equals(link.getCategoryId())) {
                continue;
            }
            final XyCashShopCategoryDO cat = cats.get(link.getCategoryId());
            if (cat == null) {
                continue;
            }
            if (cat.getIsHot() != null && cat.getIsHot() == 1) {
                continue;
            }
            if (CashShopClickType.from(cat.getClickType()) != CashShopClickType.SHOW_ITEMS) {
                continue;
            }
            if (cat.getLegacyTab() != null && cat.getLegacyTab() == 9) {
                continue;
            }
            if (!CashShopTaxonomy.isRemappableAutoCategory(
                    cat.getLegacyTab(), cat.getLegacyCategory(), cat.getName(), cat.getRemark())) {
                continue;
            }
            final String newKey = canonical + ":" + link.getItemId();
            if (!existingLinks.contains(newKey)) {
                categoryItemMapper.insertSelective(XyCashShopCategoryItemDO.builder()
                        .categoryId(canonical)
                        .itemId(link.getItemId())
                        .sort(link.getSort() == null ? 0 : link.getSort())
                        .enabled(link.getEnabled() == null ? 1 : link.getEnabled())
                        .updatedAt(new Date())
                        .build());
                existingLinks.add(newKey);
            }
            categoryItemMapper.deleteByQuery(
                    QueryWrapper.create().eq("category_id", link.getCategoryId()).eq("item_id", link.getItemId()));
            moved++;
            if (moved <= 20 || moved % 200 == 0) {
                log.info("[windowCashShop] remap item {} {} → categoryId={}",
                        link.getItemId(), cat.getName(), canonical);
            }
        }
        return moved;
    }

    /** Drop empty invented / client-sync SHOW_ITEMS categories (keep 热门 / OPEN_WINDOW / tab 9). */
    private int pruneEmptyAutoCategories() {
        final Map<Integer, Long> counts = categoryItemMapper.selectAll().stream()
                .filter(l -> l.getCategoryId() != null)
                .collect(Collectors.groupingBy(XyCashShopCategoryItemDO::getCategoryId, Collectors.counting()));
        int pruned = 0;
        for (XyCashShopCategoryDO cat : categoryMapper.selectAll()) {
            if (cat == null || cat.getId() == null) {
                continue;
            }
            if (CashShopClickType.from(cat.getClickType()) != CashShopClickType.SHOW_ITEMS) {
                continue;
            }
            if (cat.getIsHot() != null && cat.getIsHot() == 1) {
                continue;
            }
            if (cat.getLegacyTab() != null && cat.getLegacyTab() == 9) {
                continue;
            }
            if (counts.getOrDefault(cat.getId(), 0L) > 0) {
                continue;
            }
            final boolean auto = "client-sync".equals(cat.getRemark()) || "auto-tab".equals(cat.getRemark());
            final boolean obsolete = CashShopTaxonomy.isObsoleteAutoName(cat.getName());
            if (!auto && !obsolete) {
                continue;
            }
            categoryMapper.deleteById(cat.getId());
            pruned++;
        }
        return pruned;
    }

    private static boolean isCdnIconUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        return url.toLowerCase(Locale.ROOT).contains("maplestory.io");
    }

    private static boolean isCdnOrEmptyIcon(String url) {
        return !StringUtils.hasText(url) || isCdnIconUrl(url);
    }

    /** Local/legacy PNG only — never hit CDN during bulk client sync. */
    private boolean tryFillLocalIconOnly(int itemId, Path iconDir, XyCashShopItemDO target) {
        if (target == null || iconDir == null) {
            return false;
        }
        if (Files.isRegularFile(ItemIconFiles.pngPath(iconDir, itemId))
                || ItemIconFiles.copyFromLegacyCacheIfPresent(itemId)) {
            target.setIconUrl(ItemIconFiles.webUrl(itemId));
            return true;
        }
        return false;
    }

    private void flushPendingBatches(List<XyCashShopItemDO> inserts,
                                     List<XyCashShopItemDO> updates,
                                     List<XyCashShopCategoryItemDO> links,
                                     boolean force) {
        final int batch = 200;
        final boolean flushInserts = force || inserts.size() >= batch;
        final boolean flushUpdates = force || updates.size() >= batch;
        final boolean flushLinks = force || links.size() >= batch;
        // FK: xy_cashshop_category_item.item_id → xy_cashshop_item.
        // Links can reach batch size while new items are still pending — always insert items first.
        if (flushInserts || flushLinks) {
            if (!inserts.isEmpty()) {
                stampUpdatedAt(inserts);
                itemMapper.insertBatch(inserts);
                inserts.clear();
            }
        }
        if (flushUpdates) {
            if (!updates.isEmpty()) {
                stampUpdatedAt(updates);
                for (XyCashShopItemDO row : updates) {
                    itemMapper.update(row);
                }
                updates.clear();
            }
        }
        if (flushLinks) {
            if (!links.isEmpty()) {
                stampUpdatedAt(links);
                categoryItemMapper.insertBatch(links);
                links.clear();
            }
        }
    }

    private static void stampUpdatedAt(List<? extends Object> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        final Date now = new Date();
        for (Object row : rows) {
            if (row instanceof XyCashShopItemDO item) {
                item.setUpdatedAt(now);
            } else if (row instanceof XyCashShopCategoryItemDO link) {
                link.setUpdatedAt(now);
            } else if (row instanceof XyCashShopCategoryDO cat) {
                cat.setUpdatedAt(now);
            }
        }
    }

    /**
     * Scan client Data for item ids. cashOnly: Character folders keep only cash equips
     * (direct folder load); Item packs enumerate WZ children once (no 10k brute force).
     */
    private Set<Integer> scanClientItemIds(Path dataRoot, boolean cashOnly) {
        final Set<Integer> ids = new LinkedHashSet<>();
        final ItemInformationProvider ii = ItemInformationProvider.getInstance();

        final Path character = dataRoot.resolve("Character");
        if (Files.isDirectory(character)) {
            for (CashShopTaxonomy.Bucket bucket : CashShopTaxonomy.characterFolders()) {
                final Path folder = character.resolve(bucket.key());
                if (!Files.isDirectory(folder)) {
                    continue;
                }
                int folderAdded = 0;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.img")) {
                    for (Path file : stream) {
                        final Integer id = CashShopTaxonomy.parseImgItemId(
                                file.getFileName() != null ? file.getFileName().toString() : null);
                        if (id == null) {
                            continue;
                        }
                        if (cashOnly) {
                            // O(1) path load + cash flag — skip non-cash equips early
                            if (!ii.isCashEquipInFolder(bucket.key(), id)) {
                                continue;
                            }
                        }
                        ids.add(id);
                        folderAdded++;
                    }
                } catch (IOException e) {
                    log.warn("scan Character/{} failed: {}", bucket.key(), e.toString());
                }
                log.info("[windowCashShop] Character/{} cashOnly={} added={}",
                        bucket.key(), cashOnly, folderAdded);
            }
        }

        final Path pet = dataRoot.resolve("Item/Pet");
        if (Files.isDirectory(pet)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(pet, "*.img")) {
                for (Path file : stream) {
                    final Integer id = CashShopTaxonomy.parseImgItemId(
                            file.getFileName() != null ? file.getFileName().toString() : null);
                    if (id != null) {
                        ids.add(id);
                    }
                }
            } catch (IOException e) {
                log.warn("scan Item/Pet failed: {}", e.toString());
            }
        }

        for (String sub : new String[]{"Cash", "Consume", "Etc", "Install"}) {
            if (cashOnly && !"Cash".equals(sub)) {
                continue;
            }
            final Path dir = dataRoot.resolve("Item").resolve(sub);
            if (!Files.isDirectory(dir)) {
                continue;
            }
            int packExpanded = 0;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.img")) {
                for (Path file : stream) {
                    final String fname = file.getFileName() != null ? file.getFileName().toString() : "";
                    // skip restore/bak style sidecars
                    if (fname.contains("_") || fname.toLowerCase(Locale.ROOT).contains("bak")) {
                        continue;
                    }
                    final Integer looseId = CashShopTaxonomy.parseImgItemId(fname);
                    if (looseId != null) {
                        ids.add(looseId);
                        continue;
                    }
                    final Integer prefix = CashShopTaxonomy.parsePackPrefix(fname);
                    if (prefix == null) {
                        continue;
                    }
                    final List<Integer> packIds = ii.listIdsInItemPack(sub, prefix);
                    if (packIds.isEmpty()) {
                        log.warn("[windowCashShop] pack {}.img under Item/{} has no WZ children (server Item.wz?)",
                                String.format(Locale.ROOT, "%04d", prefix), sub);
                        continue;
                    }
                    ids.addAll(packIds);
                    packExpanded += packIds.size();
                }
            } catch (IOException e) {
                log.warn("scan Item/{} failed: {}", sub, e.toString());
            }
            log.info("[windowCashShop] Item/{} pack children added={}", sub, packExpanded);
        }
        return ids;
    }
}

package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.Pet;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.config.GameConfig;
import org.gms.config.PetGrowthManager;
import org.gms.constants.inventory.ItemConstants;
import org.gms.dao.entity.PetGrowthProgressDO;
import org.gms.dao.entity.PetGrowthStageDO;
import org.gms.dao.mapper.PetGrowthProgressMapper;
import org.gms.dao.mapper.PetGrowthStageMapper;
import org.gms.model.dto.PetGrowthPreviewDTO;
import org.gms.model.dto.PetGrowthStageDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 宠物成长：后台 CRUD + 喂养加经验 + 安全进阶。
 * <p>
 * 进阶前强制校验服务端 Item.wz 存在目标宠物，避免向客户端下发未知宠物 ID 导致异常。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class PetGrowthService {

    private final PetGrowthStageMapper stageMapper;
    private final PetGrowthProgressMapper progressMapper;

    public List<PetGrowthStageDO> listEnabledStages() {
        return stageMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("enabled = 1")
                        .orderBy("sort_order", true)
                        .orderBy("stage", true));
    }

    public List<PetGrowthStageDTO> listAll() {
        List<PetGrowthStageDTO> result = new ArrayList<>();
        for (PetGrowthStageDO d : stageMapper.selectListByQuery(
                QueryWrapper.create().orderBy("sort_order", true).orderBy("chain_code", true).orderBy("stage", true))) {
            result.add(toDTO(d, true));
        }
        return result;
    }

    public PetGrowthStageDTO getDetail(Long id) {
        PetGrowthStageDO d = stageMapper.selectOneById(id);
        return d == null ? null : toDTO(d, true);
    }

    public List<PetGrowthPreviewDTO> previewChains() {
        Map<String, PetGrowthPreviewDTO> map = new LinkedHashMap<>();
        for (PetGrowthStageDTO stage : listAll()) {
            PetGrowthPreviewDTO chain = map.computeIfAbsent(stage.getChainCode(), code ->
                    PetGrowthPreviewDTO.builder().chainCode(code).safe(true).stages(new ArrayList<>()).build());
            chain.getStages().add(stage);
            if (Boolean.FALSE.equals(stage.getPetExists())
                    || (stage.getNextPetId() != null && stage.getNextPetId() > 0
                    && Boolean.FALSE.equals(stage.getNextPetExists()))) {
                chain.setSafe(false);
                chain.setWarning("存在服务端缺少的宠物 ID，进阶会被跳过（不会闪退）");
            }
        }
        for (PetGrowthPreviewDTO chain : map.values()) {
            chain.getStages().sort(Comparator.comparing(s -> s.getStage() == null ? 0 : s.getStage()));
        }
        return new ArrayList<>(map.values());
    }

    @Transactional
    public PetGrowthStageDTO saveStage(PetGrowthStageDTO dto) {
        if (dto.getPetId() == null || dto.getPetId() <= 0) {
            throw new IllegalArgumentException("petId 必填");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("名称必填");
        }
        if (dto.getChainCode() == null || dto.getChainCode().isBlank()) {
            throw new IllegalArgumentException("chainCode 必填");
        }
        Date now = new Date();
        PetGrowthStageDO entity = PetGrowthStageDO.builder()
                .id(dto.getId())
                .chainCode(dto.getChainCode().trim())
                .stage(dto.getStage() == null ? 1 : dto.getStage())
                .name(dto.getName().trim())
                .petId(dto.getPetId())
                .nextPetId(normalizeNext(dto.getNextPetId()))
                .needExp(dto.getNeedExp() == null ? 100 : Math.max(0, dto.getNeedExp()))
                .expPerFeed(dto.getExpPerFeed() == null ? 10 : Math.max(1, dto.getExpPerFeed()))
                .feedItemIds(blankToNull(dto.getFeedItemIds()))
                .expRate(dto.getExpRate() == null ? 1.0 : dto.getExpRate())
                .dropRate(dto.getDropRate() == null ? 1.0 : dto.getDropRate())
                .mesoRate(dto.getMesoRate() == null ? 1.0 : dto.getMesoRate())
                .sortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder())
                .enabled(dto.getEnabled() == null ? 1 : dto.getEnabled())
                .updateTime(now)
                .build();
        if (entity.getId() == null) {
            entity.setCreateTime(now);
            stageMapper.insert(entity);
        } else {
            stageMapper.update(entity);
        }
        PetGrowthManager.reload();
        return toDTO(stageMapper.selectOneById(entity.getId()), true);
    }

    @Transactional
    public void toggleEnabled(Long id) {
        PetGrowthStageDO d = stageMapper.selectOneById(id);
        if (d == null) {
            throw new IllegalArgumentException("配置不存在");
        }
        d.setEnabled(d.getEnabled() != null && d.getEnabled() == 1 ? 0 : 1);
        d.setUpdateTime(new Date());
        stageMapper.update(d);
        PetGrowthManager.reload();
    }

    @Transactional
    public void deleteStage(Long id) {
        stageMapper.deleteById(id);
        PetGrowthManager.reload();
    }

    /**
     * 喂养后尝试增加成长经验；满则安全进阶。
     * 仅在原版喂食成功后调用；失败只打日志/提示，不影响原版饱食度逻辑。
     */
    public void onPetFed(Character chr, Pet pet, int foodItemId) {
        if (chr == null || pet == null || !PetGrowthManager.isSystemEnabled()) {
            return;
        }
        try {
            PetGrowthStageDO stage = PetGrowthManager.getStageByPetId(pet.getItemId());
            if (stage == null) {
                return;
            }
            if (!matchesFeedItem(stage, foodItemId)) {
                return;
            }

            int perFeed = resolveExpPerFeed(stage, foodItemId);
            int need = stage.getNeedExp() == null ? 0 : Math.max(0, stage.getNeedExp());
            Integer nextId = normalizeNext(stage.getNextPetId());

            PetGrowthProgressDO prog = getOrCreateProgress(pet.getUniqueId());
            int cur = prog.getGrowthExp() == null ? 0 : prog.getGrowthExp();
            int nextExp = cur + perFeed;

            if (nextId != null && need > 0 && nextExp >= need) {
                if (!ItemInformationProvider.getInstance().itemExists(nextId)) {
                    log.warn("宠物进阶跳过：目标 {} 服务端 Item.wz 不存在（当前宠 {}）", nextId, pet.getItemId());
                    prog.setGrowthExp(Math.max(0, need - 1));
                    prog.setUpdateTime(new Date());
                    progressMapper.update(prog);
                    chr.dropMessage(5, "宠物成长已满，但进阶目标数据缺失，已跳过进阶（不影响游戏）。");
                    return;
                }
                byte slot = (byte) chr.getPetIndex(pet);
                if (slot < 0) {
                    return;
                }
                long oldPetId = pet.getUniqueId();
                boolean evolved = evolveSummonedPet(chr, slot, nextId);
                if (evolved) {
                    progressMapper.deleteById(oldPetId);
                    Pet evolvedPet = chr.getPet(slot);
                    if (evolvedPet != null) {
                        progressMapper.insert(PetGrowthProgressDO.builder()
                                .petid((long) evolvedPet.getUniqueId())
                                .growthExp(0)
                                .updateTime(new Date())
                                .build());
                    }
                    String nextName = ItemInformationProvider.getInstance().getName(nextId);
                    chr.dropMessage(5, "宠物进阶成功 → " + (nextName != null ? nextName : nextId)
                            + "！当前召唤倍率已按新阶段生效。");
                } else {
                    chr.dropMessage(5, "宠物进阶失败，请检查现金栏空位后重试。");
                }
                return;
            }

            if (nextId == null || need <= 0) {
                // 终阶：仍可喂，经验封顶展示
                prog.setGrowthExp(Math.min(nextExp, Math.max(need, cur)));
                prog.setUpdateTime(new Date());
                progressMapper.update(prog);
                chr.dropMessage(5, "【" + stage.getName() + "】已是最终形态，召唤倍率生效中。");
                return;
            }

            prog.setGrowthExp(nextExp);
            prog.setUpdateTime(new Date());
            progressMapper.update(prog);
            chr.dropMessage(5, "【" + stage.getName() + "】成长经验 " + nextExp + "/" + need
                    + "（+" + perFeed + "）");
        } catch (Exception e) {
            log.error("宠物成长处理异常（已忽略，不影响原版喂食） chr={} petItem={}",
                    chr.getId(), pet.getItemId(), e);
        }
    }

    private boolean evolveSummonedPet(Character chr, byte slot, int afterId) {
        Client c = chr.getClient();
        Pet from = chr.getPet(slot);
        if (from == null || !ItemConstants.isPet(afterId)) {
            return false;
        }
        if (!ItemInformationProvider.getInstance().itemExists(afterId)) {
            return false;
        }
        if (!InventoryManipulator.checkSpace(c, afterId, (short) 1, "")) {
            return false;
        }

        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        long expiration = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(90);
        int newDbPetId = Pet.createPet(afterId);
        if (newDbPetId <= 0) {
            return false;
        }

        Pet evolved = Pet.loadFromDb(afterId, (short) 0, newDbPetId);
        if (evolved == null) {
            return false;
        }

        String fromName = ii.getName(from.getItemId());
        String customName = from.getName();
        if (customName != null && fromName != null && !customName.equals(fromName)) {
            evolved.setName(customName);
        } else {
            String n = ii.getName(afterId);
            evolved.setName(n != null ? n : String.valueOf(afterId));
        }
        evolved.setTameness(from.getTameness());
        evolved.setFullness(from.getFullness());
        evolved.setLevel(from.getLevel());
        evolved.setExpiration(expiration);
        evolved.setSummoned(false);
        evolved.saveToDb();

        short fromPos = from.getPosition();
        chr.unEquipPet(from, true);
        InventoryManipulator.removeFromSlot(c, InventoryType.CASH, fromPos, (short) 1, false);

        if (!InventoryManipulator.addById(c, afterId, (short) 1, null, newDbPetId, expiration)) {
            log.error("宠物进阶发放失败 afterId={} chr={}", afterId, chr.getId());
            return false;
        }

        Item cashItem = null;
        for (Item it : chr.getInventory(InventoryType.CASH)) {
            if (it != null && it.getPetId() == newDbPetId) {
                cashItem = it;
                break;
            }
        }
        if (cashItem == null || cashItem.getPet() == null) {
            return false;
        }

        Pet summoned = cashItem.getPet();
        Point pos = chr.getPosition();
        pos.y -= 12;
        summoned.setPos(pos);
        var fh = chr.getMap().getFootholds().findBelow(summoned.getPos());
        if (fh != null) {
            summoned.setFh(fh.getId());
        }
        summoned.setStance(0);
        summoned.setSummoned(true);
        summoned.saveToDb();
        chr.addPet(summoned);
        chr.loadPetExcludedItems(summoned.getUniqueId());
        chr.syncPetSkillsFromEquips(chr.getPetIndex(summoned));
        chr.getMap().broadcastMessage(chr, PacketCreator.showPet(chr, summoned, false, false), true);
        c.sendPacket(PacketCreator.petStatUpdate(chr));
        c.sendPacket(PacketCreator.enableActions());
        chr.commitExcludedItems();
        c.getWorldServer().registerPetHunger(chr, chr.getPetIndex(summoned));
        return true;
    }

    /**
     * 用其他栏宠物精华喂养召唤中的成长宠。
     *
     * @param petSlot 召唤槽 0~2；&lt;0 时自动选第一只可成长宠
     * @return 空串成功，否则失败原因
     */
    @Transactional
    public String feedWithEssence(Character chr, int foodItemId, int petSlot) {
        if (chr == null) {
            return "角色无效";
        }
        if (!PetGrowthManager.isSystemEnabled()) {
            return "宠物成长系统未开启";
        }
        if (foodItemId != PetGrowthManager.JUNIOR_ESSENCE && foodItemId != PetGrowthManager.SENIOR_ESSENCE) {
            return "请使用初级/高级宠物精华";
        }
        if (chr.getItemQuantity(foodItemId, false) < 1) {
            return "背包中没有该宠物精华";
        }

        Pet pet = null;
        if (petSlot >= 0 && petSlot <= 2) {
            pet = chr.getPet((byte) petSlot);
        } else {
            Pet[] pets = chr.getPets();
            if (pets != null) {
                for (Pet p : pets) {
                    if (p != null && PetGrowthManager.getStageByPetId(p.getItemId()) != null) {
                        pet = p;
                        break;
                    }
                }
            }
        }
        if (pet == null) {
            return "请先召唤可成长的宠物";
        }
        PetGrowthStageDO stage = PetGrowthManager.getStageByPetId(pet.getItemId());
        if (stage == null) {
            return "该宠物不在成长配置中";
        }
        if (!matchesFeedItem(stage, foodItemId)) {
            return "当前阶段不允许使用此精华（请检查后台 feedItemIds）";
        }

        InventoryManipulator.removeById(chr.getClient(), InventoryType.ETC, foodItemId, 1, false, false);
        onPetFed(chr, pet, foodItemId);
        return "";
    }

    public int getGrowthExp(long petUniqueId) {
        PetGrowthProgressDO prog = progressMapper.selectOneById(petUniqueId);
        return prog == null || prog.getGrowthExp() == null ? 0 : prog.getGrowthExp();
    }

    private PetGrowthProgressDO getOrCreateProgress(long petUniqueId) {
        PetGrowthProgressDO prog = progressMapper.selectOneById(petUniqueId);
        if (prog != null) {
            return prog;
        }
        prog = PetGrowthProgressDO.builder()
                .petid(petUniqueId)
                .growthExp(0)
                .updateTime(new Date())
                .build();
        progressMapper.insert(prog);
        return prog;
    }

    /** 初级/高级精华可配置不同经验；其它喂养物用阶段默认值 */
    private static int resolveExpPerFeed(PetGrowthStageDO stage, int foodItemId) {
        if (foodItemId == PetGrowthManager.JUNIOR_ESSENCE) {
            int v = GameConfig.getServerInt("pet_growth_junior_essence_exp");
            return v > 0 ? v : 10;
        }
        if (foodItemId == PetGrowthManager.SENIOR_ESSENCE) {
            int v = GameConfig.getServerInt("pet_growth_senior_essence_exp");
            return v > 0 ? v : 50;
        }
        return stage.getExpPerFeed() == null ? 10 : Math.max(1, stage.getExpPerFeed());
    }

    private static boolean matchesFeedItem(PetGrowthStageDO stage, int foodItemId) {
        String raw = stage.getFeedItemIds();
        if (raw == null || raw.isBlank()) {
            // 未配置时：精华 + 原版宠物食品均可
            if (foodItemId == PetGrowthManager.JUNIOR_ESSENCE
                    || foodItemId == PetGrowthManager.SENIOR_ESSENCE) {
                return true;
            }
            int type = foodItemId / 10000;
            return type == 212 || type == 524;
        }
        for (String part : raw.split(",")) {
            String t = part.trim();
            if (t.isEmpty()) {
                continue;
            }
            try {
                if (Integer.parseInt(t) == foodItemId) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
                // skip bad token
            }
        }
        return false;
    }

    private PetGrowthStageDTO toDTO(PetGrowthStageDO d, boolean resolve) {
        PetGrowthStageDTO.PetGrowthStageDTOBuilder b = PetGrowthStageDTO.builder()
                .id(d.getId())
                .chainCode(d.getChainCode())
                .stage(d.getStage())
                .name(d.getName())
                .petId(d.getPetId())
                .nextPetId(d.getNextPetId())
                .needExp(d.getNeedExp())
                .expPerFeed(d.getExpPerFeed())
                .feedItemIds(d.getFeedItemIds())
                .expRate(d.getExpRate())
                .dropRate(d.getDropRate())
                .mesoRate(d.getMesoRate())
                .sortOrder(d.getSortOrder())
                .enabled(d.getEnabled());
        if (resolve) {
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            boolean petOk = d.getPetId() != null && ii.itemExists(d.getPetId());
            Integer next = normalizeNext(d.getNextPetId());
            boolean nextOk = next == null || ii.itemExists(next);
            b.petExists(petOk)
                    .nextPetExists(nextOk)
                    .petNameResolved(d.getPetId() == null ? null : ii.getName(d.getPetId()))
                    .nextPetNameResolved(next == null ? null : ii.getName(next));
        }
        return b.build();
    }

    private static Integer normalizeNext(Integer next) {
        if (next == null || next <= 0) {
            return null;
        }
        return next;
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}

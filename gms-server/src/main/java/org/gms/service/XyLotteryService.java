package org.gms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.inventory.ItemConstants;
import org.gms.dao.entity.XyLotteryItemDO;
import org.gms.dao.entity.XyLotteryMachineDO;
import org.gms.dao.mapper.XyLotteryItemMapper;
import org.gms.dao.mapper.XyLotteryMachineMapper;
import org.gms.exception.BizException;
import org.gms.net.server.Server;
import org.gms.server.CashShop;
import org.gms.server.ItemInformationProvider;
import org.gms.server.life.LifeFactory;
import org.gms.util.PacketCreator;
import org.gms.util.Randomizer;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 独立抽奖（xy_lottery_*），与百宝箱 GachaponService 分离。
 * <p>
 * 缓存懒加载：禁止在 Spring @PostConstruct 中预热。启动期若触碰
 * {@link ItemInformationProvider} 静态初始化，会因 ServerManager 尚未挂上
 * ApplicationContext 而 ExceptionInInitializerError，并毒化该类导致后续永久失败。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class XyLotteryService {
    public static final int ITEM_TYPE_SPECIAL = 1;
    public static final int ITEM_TYPE_EQUIP = 2;
    public static final int ITEM_TYPE_CONSUME = 3;
    public static final int ITEM_TYPE_OTHER = 4;

    private static final Pattern POOL_ENTRY = Pattern.compile(
            "(?m)^\\s*(//)?\\s*\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*]");

    private final XyLotteryMachineMapper machineMapper;
    private final XyLotteryItemMapper itemMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<Integer, CachedMachine> cache = new ConcurrentHashMap<>();

    public record CachedMachine(XyLotteryMachineDO machine, List<XyLotteryItemDO> items, List<Integer> multiDraws,
                                int totalWeight) {
    }

    public void reloadAll() {
        lock.writeLock().lock();
        try {
            cache.clear();
            List<XyLotteryMachineDO> machines = machineMapper.selectAll();
            for (XyLotteryMachineDO m : machines) {
                putCache(m);
            }
            log.info("xy_lottery reloadAll: {} machines", cache.size());
        } catch (Exception e) {
            log.warn("xy_lottery reloadAll failed: {}", e.toString());
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void reloadNpc(int npcId) {
        lock.writeLock().lock();
        try {
            cache.remove(npcId);
            XyLotteryMachineDO m = machineMapper.selectOneByQuery(
                    QueryWrapper.create().where("npc_id = ?", npcId));
            if (m != null) {
                putCache(m);
            }
            log.info("xy_lottery reloadNpc={}", npcId);
        } catch (Exception e) {
            log.warn("xy_lottery reloadNpc={} failed: {}", npcId, e.toString());
            cache.remove(npcId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void putCache(XyLotteryMachineDO m) {
        // 抽奖缓存不解析物品名，避免启动期触碰 ItemInformationProvider
        List<XyLotteryItemDO> items = loadItemsSorted(m.getNpcId(), true);
        int total = items.stream().mapToInt(i -> i.getWeight() == null ? 0 : i.getWeight()).sum();
        cache.put(m.getNpcId(), new CachedMachine(m, items, parseMultiDraws(m.getMultiDraws()), total));
    }

    private List<XyLotteryItemDO> loadItemsSorted(int npcId, boolean enabledOnly) {
        QueryWrapper qw = QueryWrapper.create().where("npc_id = ?", npcId);
        if (enabledOnly) {
            qw.and("enabled = 1").and("item_valid = 1");
        }
        qw.orderBy("item_type", true).orderBy("sort_order", true).orderBy("id", true);
        return itemMapper.selectListByQuery(qw);
    }

    private void enrichItemNames(List<XyLotteryItemDO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        try {
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            for (XyLotteryItemDO it : list) {
                it.setItemName(ii.getName(it.getItemId()));
            }
        } catch (Throwable t) {
            log.warn("xy_lottery enrichItemNames skipped: {}", t.toString());
        }
    }

    public List<Integer> parseMultiDraws(String json) {
        try {
            if (RequireUtil.isEmpty(json)) {
                return List.of(1, 10);
            }
            List<Integer> list = objectMapper.readValue(json, new TypeReference<>() {
            });
            list.sort(Comparator.naturalOrder());
            return list;
        } catch (Exception e) {
            return List.of(1, 10);
        }
    }

    public String toMultiDrawsJson(List<Integer> draws) {
        try {
            return objectMapper.writeValueAsString(draws == null || draws.isEmpty() ? List.of(1, 10) : draws);
        } catch (Exception e) {
            return "[1,10]";
        }
    }

    /**
     * InventoryType → item_type：EQUIP=2，USE=3，CASH/SETUP 等偏特殊或其它。
     * EQUIP→2；USE→3；CASH→1（特殊）；其余→4。
     */
    public static int detectItemType(int itemId) {
        InventoryType type = ItemConstants.getInventoryType(itemId);
        return switch (type) {
            case EQUIP -> ITEM_TYPE_EQUIP;
            case USE -> ITEM_TYPE_CONSUME;
            case CASH -> ITEM_TYPE_SPECIAL;
            default -> ITEM_TYPE_OTHER;
        };
    }

    public CachedMachine getCached(int npcId) {
        lock.readLock().lock();
        try {
            CachedMachine c = cache.get(npcId);
            if (c != null) {
                return c;
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            reloadNpc(npcId);
        } catch (Exception e) {
            log.warn("xy_lottery getCached lazy load npc={} failed: {}", npcId, e.toString());
            return null;
        }
        lock.readLock().lock();
        try {
            return cache.get(npcId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<XyLotteryMachineDO> listMachines() {
        List<XyLotteryMachineDO> list = machineMapper.selectAll();
        list.sort(Comparator.comparing(XyLotteryMachineDO::getNpcId));
        return list;
    }

    public XyLotteryMachineDO getMachine(int npcId) {
        return machineMapper.selectOneByQuery(QueryWrapper.create().where("npc_id = ?", npcId));
    }

    public List<XyLotteryItemDO> listItems(int npcId) {
        List<XyLotteryItemDO> list = loadItemsSorted(npcId, false);
        enrichItemNames(list);
        return list;
    }

    /** 按物品反查绑定的 NPC */
    public List<Map<String, Object>> findNpcsByItem(int itemId) {
        List<XyLotteryItemDO> rows = itemMapper.selectListByQuery(
                QueryWrapper.create().where("item_id = ?", itemId).orderBy("npc_id", true));
        Map<Integer, Map<String, Object>> map = new LinkedHashMap<>();
        for (XyLotteryItemDO row : rows) {
            map.computeIfAbsent(row.getNpcId(), npcId -> {
                XyLotteryMachineDO m = getMachine(npcId);
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("npcId", npcId);
                info.put("npcName", LifeFactory.getNPCName(npcId));
                info.put("machineName", m == null ? "" : m.getName());
                info.put("count", 0);
                return info;
            });
            Map<String, Object> info = map.get(row.getNpcId());
            info.put("count", ((Integer) info.get("count")) + 1);
        }
        return new ArrayList<>(map.values());
    }

    @Transactional
    public XyLotteryMachineDO saveMachine(XyLotteryMachineDO data) {
        RequireUtil.requireNotNull(data.getNpcId(), "npcId");
        if (RequireUtil.isEmpty(data.getMultiDraws())) {
            data.setMultiDraws("[1,10]");
        }
        if (RequireUtil.isEmpty(data.getCostType())) {
            data.setCostType("NX");
        }
        if (data.getCostAmount() == null) {
            data.setCostAmount(0L);
        }
        if (data.getEnabled() == null) {
            data.setEnabled(1);
        }
        if (!"ITEM".equalsIgnoreCase(data.getCostType())) {
            data.setCostItemId(null);
        } else if (data.getCostItemId() == null || data.getCostItemId() <= 0) {
            throw new BizException("ITEM 消耗需配置 costItemId");
        }
        XyLotteryMachineDO exist = data.getId() != null
                ? machineMapper.selectOneById(data.getId())
                : getMachine(data.getNpcId());
        if (exist != null) {
            data.setId(exist.getId());
            machineMapper.update(data);
        } else {
            XyLotteryMachineDO conflict = getMachine(data.getNpcId());
            if (conflict != null && (data.getId() == null || !conflict.getId().equals(data.getId()))) {
                throw new BizException("NPC 已绑定抽奖机: " + data.getNpcId());
            }
            machineMapper.insert(data);
        }
        reloadNpc(data.getNpcId());
        return getMachine(data.getNpcId());
    }

    @Transactional
    public void deleteMachine(int npcId) {
        itemMapper.deleteByQuery(QueryWrapper.create().where("npc_id = ?", npcId));
        machineMapper.deleteByQuery(QueryWrapper.create().where("npc_id = ?", npcId));
        lock.writeLock().lock();
        try {
            cache.remove(npcId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Transactional
    public XyLotteryItemDO saveItem(XyLotteryItemDO data) {
        RequireUtil.requireNotNull(data.getNpcId(), "npcId");
        RequireUtil.requireNotNull(data.getItemId(), "itemId");
        if (data.getQuantity() == null || data.getQuantity() <= 0) {
            data.setQuantity(1);
        }
        if (data.getWeight() == null || data.getWeight() < 0) {
            data.setWeight(1);
        }
        if (data.getItemType() == null) {
            data.setItemType(detectItemType(data.getItemId()));
        }
        if (data.getSortOrder() == null) {
            data.setSortOrder(0);
        }
        boolean valid = ItemInformationProvider.getInstance().itemExists(data.getItemId());
        data.setItemValid(valid ? 1 : 0);
        if (data.getEnabled() == null) {
            data.setEnabled(valid ? 1 : 0);
        }
        defaultsBool(data);
        if (data.getId() == null) {
            itemMapper.insert(data);
        } else {
            itemMapper.update(data);
        }
        reloadNpc(data.getNpcId());
        Long id = data.getId();
        XyLotteryItemDO saved = id == null ? null : itemMapper.selectOneById(id);
        if (saved != null) {
            saved.setItemName(ItemInformationProvider.getInstance().getName(saved.getItemId()));
        }
        return saved;
    }

    private void defaultsBool(XyLotteryItemDO data) {
        if (data.getAnnounce() == null) data.setAnnounce(0);
        if (data.getAnnounceChannel() == null) data.setAnnounceChannel(6);
        if (data.getAnnounceBanner() == null) data.setAnnounceBanner(0);
        if (data.getRandomStats() == null) data.setRandomStats(0);
        if (data.getUntradeable() == null) data.setUntradeable(0);
        if (data.getAccountBound() == null) data.setAccountBound(0);
        if (data.getUniqueEquip() == null) data.setUniqueEquip(0);
        if (data.getFromComment() == null) data.setFromComment(0);
    }

    @Transactional
    public void deleteItem(long id) {
        XyLotteryItemDO row = itemMapper.selectOneById(id);
        if (row == null) {
            return;
        }
        itemMapper.deleteById(id);
        reloadNpc(row.getNpcId());
    }

    /**
     * 脚本侧：抽奖。返回获得的物品 ID 列表；失败抛 BizException 或返回空并 message。
     */
    public List<Integer> draw(Character player, int npcId, int times) {
        CachedMachine cached = getCached(npcId);
        if (cached == null || cached.machine().getEnabled() == null || cached.machine().getEnabled() != 1) {
            player.dropMessage(5, "抽奖机未启用或不存在（npc=" + npcId + "）");
            return List.of();
        }
        if (!cached.multiDraws().contains(times)) {
            player.dropMessage(5, "不支持的连抽次数: " + times);
            return List.of();
        }
        if (cached.items().isEmpty() || cached.totalWeight() <= 0) {
            player.dropMessage(5, "奖池为空，请联系管理员");
            return List.of();
        }
        long need = cached.machine().getCostAmount() == null ? 0L : cached.machine().getCostAmount() * times;
        if (!payCost(player, cached.machine(), need)) {
            return List.of();
        }
        List<Integer> gained = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            XyLotteryItemDO reward = pickWeighted(cached);
            Item item = grantReward(player, reward, cached.machine());
            if (item != null) {
                gained.add(reward.getItemId());
            }
        }
        return gained;
    }

    private boolean payCost(Character player, XyLotteryMachineDO machine, long total) {
        if (total <= 0) {
            return true;
        }
        String type = machine.getCostType() == null ? "NX" : machine.getCostType().toUpperCase();
        switch (type) {
            case "MESO" -> {
                if (player.getMeso() < total) {
                    player.dropMessage(5, "金币不足，需要 " + total);
                    return false;
                }
                player.gainMeso(-total, true);
            }
            case "ITEM" -> {
                int itemId = machine.getCostItemId() == null ? 0 : machine.getCostItemId();
                int qty = (int) Math.min(total, Integer.MAX_VALUE);
                if (itemId <= 0 || !player.getAbstractPlayerInteraction().haveItem(itemId, qty)) {
                    player.dropMessage(5, "道具不足，需要 #" + itemId + " x" + qty);
                    return false;
                }
                player.getAbstractPlayerInteraction().gainItem(itemId, (short) -qty);
            }
            case "NX" -> {
                CashShop cs = player.getCashShop();
                if (cs.getCash(CashShop.NX_CREDIT) < total) {
                    player.dropMessage(5, "点卷不足，需要 " + total);
                    return false;
                }
                cs.gainCash(CashShop.NX_CREDIT, (int) -total);
            }
            case "MAPLE_POINT" -> {
                CashShop cs = player.getCashShop();
                if (cs.getCash(CashShop.MAPLE_POINT) < total) {
                    player.dropMessage(5, "抵用卷不足，需要 " + total);
                    return false;
                }
                cs.gainCash(CashShop.MAPLE_POINT, (int) -total);
            }
            default -> {
                player.dropMessage(5, "未知消耗类型: " + type);
                return false;
            }
        }
        return true;
    }

    private XyLotteryItemDO pickWeighted(CachedMachine cached) {
        int roll = Randomizer.nextInt(cached.totalWeight());
        int acc = 0;
        for (XyLotteryItemDO it : cached.items()) {
            acc += it.getWeight() == null ? 0 : it.getWeight();
            if (roll < acc) {
                return it;
            }
        }
        return cached.items().getLast();
    }

    private Item grantReward(Character player, XyLotteryItemDO reward, XyLotteryMachineDO machine) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        int itemId = reward.getItemId();
        short qty = reward.getQuantity() == null ? 1 : reward.getQuantity().shortValue();
        boolean randomStats = reward.getRandomStats() != null && reward.getRandomStats() == 1;

        Item item;
        if (ItemConstants.getInventoryType(itemId) == InventoryType.EQUIP) {
            item = ii.getEquipById(itemId);
            if (item == null) {
                return null;
            }
            if (randomStats) {
                item = ii.randomizeStats((Equip) item);
            }
        } else {
            item = new Item(itemId, (short) 0, qty);
        }
        applyFlags(item, reward);
        if (!InventoryManipulator.checkSpace(player.getClient(), itemId, qty, "")) {
            player.dropMessage(5, "背包空间不足，未获得物品 " + itemId);
            return null;
        }
        InventoryManipulator.addFromDrop(player.getClient(), item, true);
        announce(player, item, reward, machine);
        return item;
    }

    private void applyFlags(Item item, XyLotteryItemDO reward) {
        short flag = item.getFlag();
        if (reward.getUntradeable() != null && reward.getUntradeable() == 1) {
            flag = (short) (flag | ItemConstants.UNTRADEABLE);
        }
        if (reward.getAccountBound() != null && reward.getAccountBound() == 1) {
            flag = (short) (flag | ItemConstants.ACCOUNT_SHARING);
        }
        if (reward.getUniqueEquip() != null && reward.getUniqueEquip() == 1) {
            flag = (short) (flag | ItemConstants.LOCK);
        }
        item.setFlag(flag);
    }

    private void announce(Character player, Item item, XyLotteryItemDO reward, XyLotteryMachineDO machine) {
        if (reward.getAnnounce() == null || reward.getAnnounce() != 1) {
            return;
        }
        String label = RequireUtil.isEmpty(reward.getAnnounceLabel())
                ? (RequireUtil.isEmpty(machine.getName()) ? "抽奖" : machine.getName())
                : reward.getAnnounceLabel();
        String name = ItemInformationProvider.getInstance().getName(item.getItemId());
        String msg = "[" + label + "] " + player.getName() + " 获得了 " + name;
        int channel = reward.getAnnounceChannel() == null ? 6 : reward.getAnnounceChannel();
        Server.getInstance().broadcastMessage(player.getWorld(), PacketCreator.serverNotice(channel, msg));
        if (reward.getAnnounceBanner() != null && reward.getAnnounceBanner() == 1) {
            Server.getInstance().broadcastMessage(player.getWorld(),
                    PacketCreator.gachaponMessage(item, label, player));
        }
    }

    /** 脚本：消耗说明文案 */
    public String formatCostLabel(XyLotteryMachineDO m) {
        if (m == null) {
            return "";
        }
        long amt = m.getCostAmount() == null ? 0 : m.getCostAmount();
        String type = m.getCostType() == null ? "NX" : m.getCostType().toUpperCase();
        return switch (type) {
            case "MESO" -> "金币 x" + amt + "/次";
            case "ITEM" -> "#v" + m.getCostItemId() + "##z" + m.getCostItemId() + "# x" + amt + "/次";
            case "NX" -> "点卷 x" + amt + "/次";
            case "MAPLE_POINT" -> "抵用卷 x" + amt + "/次";
            default -> type + " x" + amt + "/次";
        };
    }

    /** 奖池图标密排分页（每页 pageSize 个） */
    public String buildPoolPreviewPage(int npcId, int page, int pageSize) {
        CachedMachine cached = getCached(npcId);
        List<XyLotteryItemDO> all = cached == null ? listItems(npcId) : cached.items();
        // 预览含全部启用项；管理库若仅缓存启用，已足够
        if (all.isEmpty()) {
            all = loadItemsSorted(npcId, true);
        }
        if (all.isEmpty()) {
            return "当前奖池暂无配置。";
        }
        int size = Math.max(1, pageSize);
        int totalPages = (all.size() + size - 1) / size;
        int p = Math.min(Math.max(page, 0), totalPages - 1);
        int from = p * size;
        int to = Math.min(from + size, all.size());
        StringBuilder sb = new StringBuilder();
        sb.append("#e奖池预览#n (").append(p + 1).append("/").append(totalPages)
                .append(") 共 ").append(all.size()).append(" 种\r\n");
        for (int i = from; i < to; i++) {
            sb.append("#i").append(all.get(i).getItemId()).append(":#");
        }
        sb.append("\r\n");
        for (int i = from; i < to; i++) {
            XyLotteryItemDO it = all.get(i);
            sb.append("#v").append(it.getItemId()).append("##z").append(it.getItemId()).append("#");
            if (it.getQuantity() != null && it.getQuantity() > 1) {
                sb.append(" x").append(it.getQuantity());
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public int poolPageCount(int npcId, int pageSize) {
        CachedMachine cached = getCached(npcId);
        int n = cached == null ? loadItemsSorted(npcId, true).size() : cached.items().size();
        int size = Math.max(1, pageSize);
        return Math.max(1, (n + size - 1) / size);
    }

    /**
     * 从 NPC 奖池脚本导入（含注释行 //[id, weight, qty, announce]）。
     * 金猪默认源：{@code 9310022_123.js}。
     */
    @Transactional
    public Map<String, Object> importFromScript(int npcId, Path scriptPath, boolean replaceExisting) throws IOException {
        if (scriptPath == null || !Files.isRegularFile(scriptPath)) {
            log.warn("xy_lottery import skipped, script missing: {}", scriptPath);
            Map<String, Object> miss = new LinkedHashMap<>();
            miss.put("npcId", npcId);
            miss.put("inserted", 0);
            miss.put("invalid", 0);
            miss.put("fromComment", 0);
            miss.put("path", scriptPath == null ? null : scriptPath.toString());
            miss.put("error", "脚本不存在");
            return miss;
        }
        String scriptName = scriptPath.getFileName() != null ? scriptPath.getFileName().toString() : scriptPath.toString();
        XyLotteryMachineDO machine = getMachine(npcId);
        if (machine == null) {
            machine = XyLotteryMachineDO.builder()
                    .npcId(npcId)
                    .name("自由金猪")
                    .comment("import " + scriptName)
                    .enabled(1)
                    .multiDraws("[1,10]")
                    .costType("NX")
                    .costAmount(10000L)
                    .build();
            machineMapper.insert(machine);
        } else if (machine.getComment() == null || machine.getComment().isBlank()
                || machine.getComment().contains("9310022_303")) {
            machine.setComment("import " + scriptName);
            machineMapper.update(machine);
        }
        if (replaceExisting) {
            itemMapper.deleteByQuery(QueryWrapper.create().where("npc_id = ?", npcId));
        }
        String text = Files.readString(scriptPath, StandardCharsets.UTF_8);
        Matcher matcher = POOL_ENTRY.matcher(text);
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        int inserted = 0;
        int invalid = 0;
        int commented = 0;
        int order = 0;
        while (matcher.find()) {
            boolean fromComment = matcher.group(1) != null;
            int itemId = Integer.parseInt(matcher.group(2));
            int weight = Integer.parseInt(matcher.group(3));
            int qty = Integer.parseInt(matcher.group(4));
            int announce = Integer.parseInt(matcher.group(5));
            boolean valid;
            try {
                valid = ii.itemExists(itemId);
            } catch (Throwable t) {
                log.warn("xy_lottery itemExists failed itemId={}: {}", itemId, t.toString());
                valid = false;
            }
            XyLotteryItemDO row = XyLotteryItemDO.builder()
                    .npcId(npcId)
                    .itemId(itemId)
                    .quantity(qty)
                    .weight(Math.max(weight, 1))
                    .announce(announce)
                    .announceChannel(6)
                    .announceBanner(0)
                    .announceLabel("自由金猪")
                    .randomStats(0)
                    .untradeable(0)
                    .accountBound(0)
                    .uniqueEquip(0)
                    .enabled(valid ? 1 : 0)
                    .fromComment(fromComment ? 1 : 0)
                    .itemValid(valid ? 1 : 0)
                    .itemType(detectItemType(itemId))
                    .sortOrder(order++)
                    .build();
            itemMapper.insert(row);
            inserted++;
            if (!valid) {
                invalid++;
            }
            if (fromComment) {
                commented++;
            }
        }
        reloadNpc(npcId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("npcId", npcId);
        result.put("inserted", inserted);
        result.put("invalid", invalid);
        result.put("fromComment", commented);
        result.put("path", scriptPath.toString());
        result.put("script", scriptName);
        return result;
    }

    /** @deprecated 兼容旧调用；请改用 {@link #importFromScript} */
    @Deprecated
    @Transactional
    public Map<String, Object> importFrom303(int npcId, Path scriptPath, boolean replaceExisting) throws IOException {
        return importFromScript(npcId, scriptPath, replaceExisting);
    }
}

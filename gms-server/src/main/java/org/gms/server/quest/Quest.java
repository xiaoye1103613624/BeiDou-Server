/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gms.server.quest;

import org.gms.client.Character;
import org.gms.client.QuestStatus;
import org.gms.client.QuestStatus.Status;
import org.gms.config.GameConfig;
import org.gms.constants.game.DelayedQuestUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.server.quest.actions.AbstractQuestAction;
import org.gms.server.quest.actions.BuffAction;
import org.gms.server.quest.actions.ExpAction;
import org.gms.server.quest.actions.FameAction;
import org.gms.server.quest.actions.InfoAction;
import org.gms.server.quest.actions.ItemAction;
import org.gms.server.quest.actions.MesoAction;
import org.gms.server.quest.actions.NextQuestAction;
import org.gms.server.quest.actions.PetSkillAction;
import org.gms.server.quest.actions.PetSpeedAction;
import org.gms.server.quest.actions.PetTamenessAction;
import org.gms.server.quest.actions.QuestAction;
import org.gms.server.quest.actions.SkillAction;
import org.gms.server.quest.requirements.AbstractQuestRequirement;
import org.gms.server.quest.requirements.BuffExceptRequirement;
import org.gms.server.quest.requirements.BuffRequirement;
import org.gms.server.quest.requirements.CompletedQuestRequirement;
import org.gms.server.quest.requirements.EndDateRequirement;
import org.gms.server.quest.requirements.FieldEnterRequirement;
import org.gms.server.quest.requirements.InfoExRequirement;
import org.gms.server.quest.requirements.InfoNumberRequirement;
import org.gms.server.quest.requirements.IntervalRequirement;
import org.gms.server.quest.requirements.ItemRequirement;
import org.gms.server.quest.requirements.JobRequirement;
import org.gms.server.quest.requirements.MaxLevelRequirement;
import org.gms.server.quest.requirements.MesoRequirement;
import org.gms.server.quest.requirements.MinLevelRequirement;
import org.gms.server.quest.requirements.MinTamenessRequirement;
import org.gms.server.quest.requirements.MobRequirement;
import org.gms.server.quest.requirements.MonsterBookCountRequirement;
import org.gms.server.quest.requirements.NpcRequirement;
import org.gms.server.quest.requirements.PetRequirement;
import org.gms.server.quest.requirements.QuestRequirement;
import org.gms.server.quest.requirements.ScriptRequirement;
import org.gms.util.PacketCreator;
import org.gms.util.StringUtil;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 【类型】Quest（class），包 {@code org.gms.server.quest}。
 * 任务定义类，封装任务的接取条件、完成条件和奖励动作，任务数据从WZ文件（Quest.wz）加载。
 * 通过 {@link #getInstance(int)} 获取任务实例，内部维护实例缓存以提升性能。
 *
 * @author Matze
 * @author Ronan - support for medal quests
 */
public class Quest {
    private static final Logger log = LoggerFactory.getLogger(Quest.class);
    /** 任务实例缓存（任务ID -> Quest实例） */
    private static volatile Map<Integer, Quest> quests = new HashMap<>();
    /** infoNumber到任务ID的映射表 */
    private static volatile Map<Integer, Integer> infoNumberQuests = new HashMap<>();
    /** 勋章任务映射（任务ID -> 勋章物品ID） */
    private static final Map<Short, Integer> medals = new HashMap<>();

    /** 可重复利用的任务ID集合（允许通过对话反复接取/完成） */
    private static final Set<Short> exploitableQuests = new HashSet<>();

    static {
        exploitableQuests.add((short) 2338);    // there are a lot more exploitable quests, they need to be nit-picked
        exploitableQuests.add((short) 3637);
        exploitableQuests.add((short) 3714);
        exploitableQuests.add((short) 21752);
    }

    /** 任务ID */
    protected short id;
    /** 任务时间限制（秒，0表示无限制） */
    protected int timeLimit, timeLimit2;
    /** 接取任务的条件集合 */
    protected Map<QuestRequirementType, AbstractQuestRequirement> startReqs = new EnumMap<>(QuestRequirementType.class);
    /** 完成任务的条件集合 */
    protected Map<QuestRequirementType, AbstractQuestRequirement> completeReqs = new EnumMap<>(QuestRequirementType.class);
    /** 接取任务时执行的动作集合 */
    protected Map<QuestActionType, AbstractQuestAction> startActs = new EnumMap<>(QuestActionType.class);
    /** 完成任务时执行的动作集合 */
    protected Map<QuestActionType, AbstractQuestAction> completeActs = new EnumMap<>(QuestActionType.class);
    /** 关联的怪物ID列表 */
    protected List<Integer> relevantMobs = new LinkedList<>();
    /** 是否自动接取 */
    private boolean autoStart;
    /** 是否自动完成 */
    private boolean autoPreComplete, autoComplete;
    /** 是否可重复接取 */
    private boolean repeatable = false;
    /** 任务名称 */
    private String name = "";
    /** 父任务名称 */
    private String parent = "";
    /** WZ数据提供器（Quest.wz） */
    private final static DataProvider questData = DataProviderFactory.getDataProvider(WZFiles.QUEST);
    /** QuestInfo.img数据节点 */
    private final static Data questInfo = questData.getData("QuestInfo.img");
    /** Act.img数据节点 */
    private final static Data questAct = questData.getData("Act.img");
    /** Check.img数据节点 */
    private final static Data questReq = questData.getData("Check.img");

    /**
     * 构造函数，从WZ文件加载任务数据
     *
     * @param id 任务ID
     */
    private Quest(int id) {
        this.id = (short) id;

        Data reqData = questReq.getChildByPath(String.valueOf(id));
        if (reqData == null) {//most likely infoEx
            return;
        }

        if (questInfo != null) {
            Data reqInfo = questInfo.getChildByPath(String.valueOf(id));
            if (reqInfo != null) {
                name = DataTool.getString("name", reqInfo, "");
                parent = DataTool.getString("parent", reqInfo, "");

                timeLimit = DataTool.getInt("timeLimit", reqInfo, 0);
                timeLimit2 = DataTool.getInt("timeLimit2", reqInfo, 0);
                autoStart = DataTool.getInt("autoStart", reqInfo, 0) == 1;
                autoPreComplete = DataTool.getInt("autoPreComplete", reqInfo, 0) == 1;
                autoComplete = DataTool.getInt("autoComplete", reqInfo, 0) == 1;

                int medalid = DataTool.getInt("viewMedalItem", reqInfo, 0);
                if (medalid != 0) {
                    medals.put(this.id, medalid);
                }
            } else {
                log.warn("No quest data for id {}", id);
            }
        }

        Data startReqData = reqData.getChildByPath("0");
        if (startReqData != null) {
            for (Data startReq : startReqData.getChildren()) {
                QuestRequirementType type = QuestRequirementType.getByWZName(startReq.getName());
                switch (type) {
                case INTERVAL:
                    repeatable = true;
                    break;
                case MOB:
                    for (Data mob : startReq.getChildren()) {
                        relevantMobs.add(DataTool.getInt(mob.getChildByPath("id")));
                    }
                    break;
                }

                AbstractQuestRequirement req = this.getRequirement(type, startReq);
                if (req == null) {
                    continue;
                }

                startReqs.put(type, req);
            }
        }

        Data completeReqData = reqData.getChildByPath("1");
        if (completeReqData != null) {
            for (Data completeReq : completeReqData.getChildren()) {
                QuestRequirementType type = QuestRequirementType.getByWZName(completeReq.getName());

                AbstractQuestRequirement req = this.getRequirement(type, completeReq);
                if (req == null) {
                    continue;
                }

                if (type.equals(QuestRequirementType.MOB)) {
                    for (Data mob : completeReq.getChildren()) {
                        relevantMobs.add(DataTool.getInt(mob.getChildByPath("id")));
                    }
                }
                completeReqs.put(type, req);
            }
        }
        Data actData = questAct.getChildByPath(String.valueOf(id));
        if (actData == null) {
            return;
        }
        final Data startActData = actData.getChildByPath("0");
        if (startActData != null) {
            for (Data startAct : startActData.getChildren()) {
                QuestActionType questActionType = QuestActionType.getByWZName(startAct.getName());
                AbstractQuestAction act = this.getAction(questActionType, startAct);

                if (act == null) {
                    continue;
                }

                startActs.put(questActionType, act);
            }
        }
        Data completeActData = actData.getChildByPath("1");
        if (completeActData != null) {
            for (Data completeAct : completeActData.getChildren()) {
                QuestActionType questActionType = QuestActionType.getByWZName(completeAct.getName());
                AbstractQuestAction act = this.getAction(questActionType, completeAct);

                if (act == null) {
                    continue;
                }

                completeActs.put(questActionType, act);
            }
        }
    }

    /**
     * 判断任务是否自动完成
     *
     * @return 如果任务配置了自动完成则返回true
     */
    public boolean isAutoComplete() {
        return autoPreComplete || autoComplete;
    }

    /**
     * 判断任务是否自动接取
     *
     * @return 如果任务配置了自动接取则返回true
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * 获取任务实例（单例模式），从缓存中获取或创建新实例
     *
     * @param id 任务ID
     * @return 任务实例
     */
    public static Quest getInstance(int id) {
        Quest ret = quests.get(id);
        if (ret == null) {
            ret = new Quest(id);
            quests.put(id, ret);
        }
        return ret;
    }

    /**
     * 通过infoNumber获取任务实例
     *
     * @param infoNumber 信息编号
     * @return 任务实例
     */
    public static Quest getInstanceFromInfoNumber(int infoNumber) {
        Integer id = infoNumberQuests.get(infoNumber);
        if (id == null) {
            id = infoNumber;
        }

        return getInstance(id);
    }

    /**
     * 判断任务是否可在同一天内重复完成
     *
     * @return 如果可同一天重复则返回true
     */
    public boolean isSameDayRepeatable() {
        if (!repeatable) {
            return false;
        }

        IntervalRequirement ir = (IntervalRequirement) startReqs.get(QuestRequirementType.INTERVAL);
        return ir.getInterval() < HOURS.toMillis(GameConfig.getServerLong("quest_point_repeatable_interval"));
    }

    /**
     * 根据任务状态判断是否可以接取任务
     *
     * @param chr 玩家角色
     * @return 如果可以接取则返回true
     */
    public boolean canStartQuestByStatus(Character chr) {
        QuestStatus mqs = chr.getQuest(this);
        return !(!mqs.getStatus().equals(Status.NOT_STARTED) && !(mqs.getStatus().equals(Status.COMPLETED) && repeatable));
    }

    /**
     * 检查玩家的任务进度是否满足infoEx条件
     *
     * @param chr 玩家角色
     * @return 如果进度满足条件则返回true
     */
    public boolean canQuestByInfoProgress(Character chr) {
        QuestStatus mqs = chr.getQuest(this);
        List<String> ix = mqs.getInfoEx();
        if (!ix.isEmpty()) {
            short questid = mqs.getQuestID();
            short infoNumber = mqs.getInfoNumber();
            if (infoNumber <= 0) {
                infoNumber = questid;
            }

            int ixSize = ix.size();
            for (int i = 0; i < ixSize; i++) {
                String progress = chr.getAbstractPlayerInteraction().getQuestProgress(infoNumber, i);
                String ixProgress = ix.get(i);

                if (!progress.contentEquals(ixProgress)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断玩家是否可以接取任务（综合判断状态、条件和进度）
     *
     * @param chr   玩家角色
     * @param npcid NPC ID
     * @return 如果可以接取则返回true
     */
    public boolean canStart(Character chr, int npcid) {
        if (!canStartQuestByStatus(chr)) {
            return false;
        }

        for (AbstractQuestRequirement r : startReqs.values()) {
            if (!r.check(chr, npcid)) {
                return false;
            }
        }

        return canQuestByInfoProgress(chr);
    }

    /**
     * 判断玩家是否可以完成任务
     *
     * @param chr   玩家角色
     * @param npcid NPC ID
     * @return 如果可以完成则返回true
     */
    public boolean canComplete(Character chr, Integer npcid) {
        QuestStatus mqs = chr.getQuest(this);
        if (!mqs.getStatus().equals(Status.STARTED)) {
            return false;
        }

        for (AbstractQuestRequirement r : completeReqs.values()) {
            if (!r.check(chr, npcid)) {
                return false;
            }
        }

        return canQuestByInfoProgress(chr);
    }

    /**
     * 接取任务，执行接取动作并更新任务状态
     *
     * @param chr 玩家角色
     * @param npc NPC ID
     */
    public void start(Character chr, int npc) {
        if (autoStart || canStart(chr, npc)) {
            Collection<AbstractQuestAction> acts = startActs.values();
            for (AbstractQuestAction a : acts) {
                if (!a.check(chr, null)) {
                    return;
                }
            }
            for (AbstractQuestAction a : acts) {
                a.run(chr, null);
            }
            forceStart(chr, npc);
        }
    }

    /**
     * 完成任务（不带选项）
     *
     * @param chr 玩家角色
     * @param npc NPC ID
     */
    public void complete(Character chr, int npc) {
        complete(chr, npc, null);
    }

    /**
     * 完成任务（带选项）
     *
     * @param chr       玩家角色
     * @param npc       NPC ID
     * @param selection 选择项（用于多选奖励任务）
     */
    public void complete(Character chr, int npc, Integer selection) {
        if (autoPreComplete || canComplete(chr, npc)) {
            Collection<AbstractQuestAction> acts = completeActs.values();
            for (AbstractQuestAction a : acts) {
                if (!a.check(chr, selection)) {
                    return;
                }
            }
            forceComplete(chr, npc);
            for (AbstractQuestAction a : acts) {
                a.run(chr, selection);
            }
            if (!this.hasNextQuestAction()) {
                chr.announceUpdateQuest(DelayedQuestUpdate.INFO, chr.getQuest(this));
            }
        }
    }

    /**
     * 重置任务状态为未接取
     *
     * @param chr 玩家角色
     */
    public void reset(Character chr) {
        QuestStatus newStatus = new QuestStatus(this, QuestStatus.Status.NOT_STARTED);
        chr.updateQuestStatus(newStatus);
    }

    /**
     * 放弃任务
     *
     * @param chr 玩家角色
     * @return 如果放弃成功则返回true
     */
    public boolean forfeit(Character chr) {
        if (!chr.getQuest(this).getStatus().equals(Status.STARTED)) {
            return false;
        }
        if (timeLimit > 0) {
            chr.sendPacket(PacketCreator.removeQuestTimeLimit(id));
        }
        QuestStatus newStatus = new QuestStatus(this, QuestStatus.Status.NOT_STARTED);
        newStatus.setForfeited(chr.getQuest(this).getForfeited() + 1);
        chr.updateQuestStatus(newStatus);
        return true;
    }

    /**
     * 强制接取任务（绕过条件检查）
     *
     * @param chr 玩家角色
     * @param npc NPC ID
     * @return 始终返回true
     */
    public boolean forceStart(Character chr, int npc) {
        QuestStatus newStatus = new QuestStatus(this, QuestStatus.Status.STARTED, npc);

        QuestStatus oldStatus = chr.getQuest(this.getId());
        for (Entry<Integer, String> e : oldStatus.getProgress().entrySet()) {
            newStatus.setProgress(e.getKey(), e.getValue());
        }

        if (id / 100 == 35 && GameConfig.getServerInt("tot_mob_quest_requirement") > 0) {
            int setProg = 999 - Math.min(999, GameConfig.getServerInt("tot_mob_quest_requirement"));

            for (Integer pid : newStatus.getProgress().keySet()) {
                if (pid >= 8200000 && pid <= 8200012) {
                    String pr = StringUtil.getLeftPaddedStr(Integer.toString(setProg), '0', 3);
                    newStatus.setProgress(pid, pr);
                }
            }
        }

        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        newStatus.setCompleted(chr.getQuest(this).getCompleted());

        if (timeLimit > 0) {
            newStatus.setExpirationTime(System.currentTimeMillis() + SECONDS.toMillis(timeLimit));
            chr.questTimeLimit(this, timeLimit);
        }
        if (timeLimit2 > 0) {
            newStatus.setExpirationTime(System.currentTimeMillis() + timeLimit2);
            chr.questTimeLimit2(this, newStatus.getExpirationTime());
        }

        chr.updateQuestStatus(newStatus);

        return true;
    }

    /**
     * 强制完成任务（绕过条件检查）
     *
     * @param chr 玩家角色
     * @param npc NPC ID
     * @return 始终返回true
     */
    public boolean forceComplete(Character chr, int npc) {
        if (timeLimit > 0) {
            chr.sendPacket(PacketCreator.removeQuestTimeLimit(id));
        }

        QuestStatus newStatus = new QuestStatus(this, QuestStatus.Status.COMPLETED, npc);
        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        newStatus.setCompleted(chr.getQuest(this).getCompleted());
        newStatus.setCompletionTime(System.currentTimeMillis());
        chr.updateQuestStatus(newStatus);

        chr.sendPacket(PacketCreator.showSpecialEffect(9));
        chr.getMap().broadcastMessage(chr, PacketCreator.showForeignEffect(chr.getId(), 9), false);
        return true;
    }

    /**
     * 获取任务ID
     *
     * @return 任务ID
     */
    public short getId() {
        return id;
    }

    /**
     * 获取与任务相关的怪物ID列表
     *
     * @return 怪物ID列表
     */
    public List<Integer> getRelevantMobs() {
        return relevantMobs;
    }

    /**
     * 获取接取任务所需的物品数量
     *
     * @param itemid 物品ID
     * @return 所需物品数量，无需求时返回Integer.MIN_VALUE
     */
    public int getStartItemAmountNeeded(int itemid) {
        AbstractQuestRequirement req = startReqs.get(QuestRequirementType.ITEM);
        if (req == null) {
            return Integer.MIN_VALUE;
        }

        ItemRequirement ireq = (ItemRequirement) req;
        return ireq.getItemAmountNeeded(itemid, false);
    }

    /**
     * 获取完成任务所需的物品数量
     *
     * @param itemid 物品ID
     * @return 所需物品数量，无需求时返回Integer.MAX_VALUE
     */
    public int getCompleteItemAmountNeeded(int itemid) {
        AbstractQuestRequirement req = completeReqs.get(QuestRequirementType.ITEM);
        if (req == null) {
            return Integer.MAX_VALUE;
        }

        ItemRequirement ireq = (ItemRequirement) req;
        return ireq.getItemAmountNeeded(itemid, true);
    }

    /**
     * 获取完成任务所需击杀的怪物数量
     *
     * @param mid 怪物ID
     * @return 所需击杀数量，无需求时返回0
     */
    public int getMobAmountNeeded(int mid) {
        AbstractQuestRequirement req = completeReqs.get(QuestRequirementType.MOB);
        if (req == null) {
            return 0;
        }

        MobRequirement mreq = (MobRequirement) req;

        return mreq.getRequiredMobCount(mid);
    }

    /**
     * 获取任务的infoNumber
     *
     * @param qs 任务状态（STARTED表示获取完成条件的infoNumber）
     * @return infoNumber，无配置时返回0
     */
    public short getInfoNumber(Status qs) {
        boolean checkEnd = qs.equals(Status.STARTED);
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;

        AbstractQuestRequirement req = reqs.get(QuestRequirementType.INFO_NUMBER);
        if (req != null) {
            InfoNumberRequirement inReq = (InfoNumberRequirement) req;
            return inReq.getInfoNumber();
        } else {
            return 0;
        }
    }

    /**
     * 获取指定索引的infoEx值
     *
     * @param qs    任务状态（STARTED表示获取完成条件的infoEx）
     * @param index 索引
     * @return infoEx值，获取失败时返回空字符串
     */
    public String getInfoEx(Status qs, int index) {
        boolean checkEnd = qs.equals(Status.STARTED);
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
        try {
            AbstractQuestRequirement req = reqs.get(QuestRequirementType.INFO_EX);
            InfoExRequirement ixReq = (InfoExRequirement) req;
            return ixReq.getInfo().get(index);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取infoEx列表
     *
     * @param qs 任务状态（STARTED表示获取完成条件的infoEx）
     * @return infoEx列表，获取失败时返回空列表
     */
    public List<String> getInfoEx(Status qs) {
        boolean checkEnd = qs.equals(Status.STARTED);
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
        try {
            AbstractQuestRequirement req = reqs.get(QuestRequirementType.INFO_EX);
            InfoExRequirement ixReq = (InfoExRequirement) req;
            return ixReq.getInfo();
        } catch (Exception e) {
            return new LinkedList<>();
        }
    }

    /**
     * 获取任务时间限制（秒）
     *
     * @return 时间限制，0表示无限制
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * 清除指定任务的缓存
     *
     * @param quest 任务ID
     */
    public static void clearCache(int quest) {
        quests.remove(quest);
    }

    /**
     * 清除所有任务缓存
     */
    public static void clearCache() {
        quests.clear();
    }

    private AbstractQuestRequirement getRequirement(QuestRequirementType type, Data data) {
        AbstractQuestRequirement ret = null;
        switch (type) {
            case END_DATE:
                ret = new EndDateRequirement(this, data);
                break;
            case JOB:
                ret = new JobRequirement(this, data);
                break;
            case QUEST:
                ret = new QuestRequirement(this, data);
                break;
            case FIELD_ENTER:
                ret = new FieldEnterRequirement(this, data);
                break;
            case INFO_NUMBER:
                ret = new InfoNumberRequirement(this, data);
                break;
            case INFO_EX:
                ret = new InfoExRequirement(this, data);
                break;
            case INTERVAL:
                ret = new IntervalRequirement(this, data);
                break;
            case COMPLETED_QUEST:
                ret = new CompletedQuestRequirement(this, data);
                break;
            case ITEM:
                ret = new ItemRequirement(this, data);
                break;
            case MAX_LEVEL:
                ret = new MaxLevelRequirement(this, data);
                break;
            case MESO:
                ret = new MesoRequirement(this, data);
                break;
            case MIN_LEVEL:
                ret = new MinLevelRequirement(this, data);
                break;
            case MIN_PET_TAMENESS:
                ret = new MinTamenessRequirement(this, data);
                break;
            case MOB:
                ret = new MobRequirement(this, data);
                break;
            case MONSTER_BOOK:
                ret = new MonsterBookCountRequirement(this, data);
                break;
            case NPC:
                ret = new NpcRequirement(this, data);
                break;
            case PET:
                ret = new PetRequirement(this, data);
                break;
            case BUFF:
                ret = new BuffRequirement(this, data);
                break;
            case EXCEPT_BUFF:
                ret = new BuffExceptRequirement(this, data);
                break;
            case SCRIPT:
                ret = new ScriptRequirement(this, data);
                break;
            case NORMAL_AUTO_START:
            case START:
            case END:
                break;
            default:
                //FilePrinter.printError(FilePrinter.EXCEPTION_CAUGHT, "Unhandled Requirement Type: " + type.toString() + " QuestID: " + this.getId());
                break;
        }
        return ret;
    }

    private AbstractQuestAction getAction(QuestActionType type, Data data) {
        AbstractQuestAction ret = null;
        switch (type) {
            case BUFF:
                ret = new BuffAction(this, data);
                break;
            case EXP:
                ret = new ExpAction(this, data);
                break;
            case FAME:
                ret = new FameAction(this, data);
                break;
            case ITEM:
                ret = new ItemAction(this, data);
                break;
            case MESO:
                ret = new MesoAction(this, data);
                break;
            case NEXTQUEST:
                ret = new NextQuestAction(this, data);
                break;
            case PETSKILL:
                ret = new PetSkillAction(this, data);
                break;
            case QUEST:
                ret = new QuestAction(this, data);
                break;
            case SKILL:
                ret = new SkillAction(this, data);
                break;
            case PETTAMENESS:
                ret = new PetTamenessAction(this, data);
                break;
            case PETSPEED:
                ret = new PetSpeedAction(this, data);
                break;
            case INFO:
                ret = new InfoAction(this, data);
                break;
            default:
                //FilePrinter.printError(FilePrinter.EXCEPTION_CAUGHT, "Unhandled Action Type: " + type.toString() + " QuestID: " + this.getId());
                break;
        }
        return ret;
    }

    public boolean restoreLostItem(Character chr, int itemid) {
        if (chr.getQuest(this).getStatus().equals(QuestStatus.Status.STARTED)) {
            ItemAction itemAct = (ItemAction) startActs.get(QuestActionType.ITEM);
            if (itemAct != null) {
                return itemAct.restoreLostItem(chr, itemid);
            }
        }

        return false;
    }

    public int getMedalRequirement() {
        Integer medalid = medals.get(id);
        return medalid != null ? medalid : -1;
    }

    public int getNpcRequirement(boolean checkEnd) {
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
        AbstractQuestRequirement mqr = reqs.get(QuestRequirementType.NPC);
        if (mqr != null) {
            return ((NpcRequirement) mqr).get();
        } else {
            return -1;
        }
    }

    public boolean hasScriptRequirement(boolean checkEnd) {
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
        AbstractQuestRequirement mqr = reqs.get(QuestRequirementType.SCRIPT);

        if (mqr != null) {
            return ((ScriptRequirement) mqr).get();
        } else {
            return false;
        }
    }

    public boolean hasNextQuestAction() {
        Map<QuestActionType, AbstractQuestAction> acts = completeActs;
        AbstractQuestAction mqa = acts.get(QuestActionType.NEXTQUEST);

        return mqa != null;
    }

    public String getName() {
        return name;
    }

    public String getParentName() {
        return parent;
    }

    public static boolean isExploitableQuest(short questid) {
        return exploitableQuests.contains(questid);
    }

    public static List<Quest> getMatchedQuests(String search) {
        List<Quest> ret = new LinkedList<>();

        search = search.toLowerCase();
        for (Quest mq : quests.values()) {
            if (mq.name.toLowerCase().contains(search) || mq.parent.toLowerCase().contains(search)) {
                ret.add(mq);
            }
        }

        return ret;
    }

    public static void loadAllQuests() {
        final Map<Integer, Quest> loadedQuests = new HashMap<>();
        final Map<Integer, Integer> loadedInfoNumberQuests = new HashMap<>();

        for (Data quest : questInfo.getChildren()) {
            int questID = Integer.parseInt(quest.getName());

            Quest q = new Quest(questID);
            loadedQuests.put(questID, q);

            int infoNumber;

            infoNumber = q.getInfoNumber(Status.STARTED);
            if (infoNumber > 0) {
                loadedInfoNumberQuests.put(infoNumber, questID);
            }

            infoNumber = q.getInfoNumber(Status.COMPLETED);
            if (infoNumber > 0) {
                loadedInfoNumberQuests.put(infoNumber, questID);
            }
        }

        Quest.quests = loadedQuests;
        Quest.infoNumberQuests = loadedInfoNumberQuests;
    }

    public void expireQuest(Character chr) {
        if (forfeit(chr)) {
            chr.sendPacket(PacketCreator.questExpire(getId()));
        }
    }
}
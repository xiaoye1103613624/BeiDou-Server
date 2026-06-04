package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.client.QuestStatus;
import org.gms.dao.entity.MedalmapsDO;
import org.gms.dao.entity.QuestprogressDO;
import org.gms.dao.entity.QueststatusDO;
import org.gms.dao.mapper.MedalmapsMapper;
import org.gms.dao.mapper.QuestprogressMapper;
import org.gms.dao.mapper.QueststatusMapper;
import org.gms.server.quest.Quest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.gms.dao.entity.table.MedalmapsDOTableDef.MEDALMAPS_D_O;
import static org.gms.dao.entity.table.QuestprogressDOTableDef.QUESTPROGRESS_D_O;
import static org.gms.dao.entity.table.QueststatusDOTableDef.QUESTSTATUS_D_O;

/**
 * 【业务服务】QuestService：任务服务类，负责角色任务数据的管理。
 * 
 * <p>提供任务进度查询、删除等功能，支持角色任务状态的加载和清理。</p>
 */
@Service
@AllArgsConstructor
public class QuestService {
    /** 勋章地图数据访问接口 */
    private final MedalmapsMapper medalmapsMapper;
    /** 任务进度数据访问接口 */
    private final QuestprogressMapper questprogressMapper;
    /** 任务状态数据访问接口 */
    private final QueststatusMapper queststatusMapper;

    /**
     * 根据角色ID删除所有任务相关数据。
     * 
     * <p>级联删除角色的勋章地图、任务进度和任务状态记录。</p>
     * 
     * @param cid 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestProgressByCharacter(int cid) {
        // 删除勋章地图记录
        medalmapsMapper.deleteByQuery(QueryWrapper.create().where(MEDALMAPS_D_O.CHARACTERID.eq(cid)));
        // 删除任务进度记录
        questprogressMapper.deleteByQuery(QueryWrapper.create().where(QUESTPROGRESS_D_O.CHARACTERID.eq(cid)));
        // 删除任务状态记录
        queststatusMapper.deleteByQuery(QueryWrapper.create().where(QUESTSTATUS_D_O.CHARACTERID.eq(cid)));
    }

    /**
     * 获取角色的所有任务状态。
     * 
     * <p>查询角色的任务状态、进度和勋章地图数据，组装成QuestStatus对象列表。</p>
     * 
     * @param cid 角色ID
     * @return 任务状态列表
     */
    public List<QuestStatus> getQuestStatusByCharacter(int cid) {
        // 查询任务状态、进度和勋章地图
        List<QueststatusDO> queststatusDOList = queststatusMapper.selectListByQuery(
                QueryWrapper.create().where(QUESTSTATUS_D_O.CHARACTERID.eq(cid)));
        List<QuestprogressDO> questprogressDOList = questprogressMapper.selectListByQuery(
                QueryWrapper.create().where(QUESTPROGRESS_D_O.CHARACTERID.eq(cid)));
        List<MedalmapsDO> medalmapsDOList = medalmapsMapper.selectListByQuery(
                QueryWrapper.create().where(MEDALMAPS_D_O.CHARACTERID.eq(cid)));

        // 转换为QuestStatus对象
        return queststatusDOList.stream().map(queststatusDO -> {
            // 获取任务实例
            Quest quest = Quest.getInstance(queststatusDO.getQuest());
            QuestStatus questStatus = new QuestStatus(quest, 
                    QuestStatus.Status.getById(queststatusDO.getStatus()));
            
            // 设置完成时间
            if (queststatusDO.getTime() > -1) {
                questStatus.setCompletionTime(TimeUnit.SECONDS.toMillis(queststatusDO.getTime()));
            }
            // 设置过期时间
            if (queststatusDO.getExpires() > 0) {
                questStatus.setExpirationTime(queststatusDO.getExpires());
            }
            
            questStatus.setForfeited(queststatusDO.getForfeited());
            questStatus.setCompleted(queststatusDO.getCompleted());
            
            // 设置任务进度
            questprogressDOList.stream()
                    .filter(progress -> Objects.equals(queststatusDO.getQueststatusid(), progress.getQueststatusid()))
                    .forEach(progress -> questStatus.setProgress(progress.getProgressid(), progress.getProgress()));
            
            // 设置勋章地图
            medalmapsDOList.stream()
                    .filter(medal -> Objects.equals(queststatusDO.getQueststatusid(), medal.getQueststatusid()))
                    .forEach(medal -> questStatus.addMedalMap(medal.getMapid()));
            
            return questStatus;
        }).toList();
    }
}
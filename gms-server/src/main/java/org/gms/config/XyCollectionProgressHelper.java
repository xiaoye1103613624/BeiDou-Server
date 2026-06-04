package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.XyCollectionProgressDO;
import org.gms.dao.mapper.XyCollectionProgressMapper;
import org.gms.manager.ServerManager;

import java.sql.Date;

import static org.gms.dao.entity.table.XyCollectionProgressDOTableDef.XY_COLLECTION_PROGRESS_D_O;

/**
 * XY收集进度辅助类，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 * 用于读写玩家收集进度到数据库。
 */
public class XyCollectionProgressHelper {

    /** 进度数据访问接口，通过Spring容器获取 */
    private static final XyCollectionProgressMapper progressMapper =
            ServerManager.getApplicationContext().getBean(XyCollectionProgressMapper.class);

    /** 私有构造函数，防止实例化 */
    private XyCollectionProgressHelper() {
    }

    /**
     * 获取玩家某阶段物品的收集进度
     * @param characterId 角色ID
     * @param stageId 阶段ID
     * @param itemId 物品ID
     * @return 进度记录，不存在返回null
     */
    public static XyCollectionProgressDO getProgress(int characterId, Long stageId, int itemId) {
        return progressMapper.selectOneByQuery(QueryWrapper.create()
                .where(XY_COLLECTION_PROGRESS_D_O.CHARACTER_ID.eq(characterId))
                .and(XY_COLLECTION_PROGRESS_D_O.STAGE_ID.eq(stageId))
                .and(XY_COLLECTION_PROGRESS_D_O.ITEM_ID.eq(itemId)));
    }

    /**
     * 保存玩家收集进度（新增或更新）
     * @param characterId 角色ID
     * @param typeId 类型ID
     * @param stageId 阶段ID
     * @param itemId 物品ID
     * @param collectedCount 已收集数量
     */
    public static void saveProgress(int characterId, Long typeId, Long stageId, int itemId, int collectedCount) {
        XyCollectionProgressDO exist = getProgress(characterId, stageId, itemId);
        if (exist == null) {
            // 新增记录
            progressMapper.insertSelective(XyCollectionProgressDO.builder()
                    .characterId(characterId)
                    .typeId(typeId)
                    .stageId(stageId)
                    .itemId(itemId)
                    .collectedCount(collectedCount)
                    .stageCompleted(0)
                    .typeCompleted(0)
                    .createTime(new Date(System.currentTimeMillis()))
                    .build());
        } else {
            // 更新记录
            exist.setCollectedCount(collectedCount);
            exist.setUpdateTime(new Date(System.currentTimeMillis()));
            progressMapper.update(exist);
        }
    }

    /**
     * 判断玩家是否完成某阶段
     * @param characterId 角色ID
     * @param stageId 阶段ID
     * @return 已完成返回true，否则返回false
     */
    public static boolean isStageCompleted(int characterId, Long stageId) {
        XyCollectionProgressDO record = progressMapper.selectOneByQuery(QueryWrapper.create()
                .where(XY_COLLECTION_PROGRESS_D_O.CHARACTER_ID.eq(characterId))
                .and(XY_COLLECTION_PROGRESS_D_O.STAGE_ID.eq(stageId))
                .and(XY_COLLECTION_PROGRESS_D_O.STAGE_COMPLETED.eq(1)));
        return record != null;
    }

    /**
     * 判断玩家是否完成某类型所有阶段
     * @param characterId 角色ID
     * @param typeId 类型ID
     * @return 已完成返回true，否则返回false
     */
    public static boolean isTypeCompleted(int characterId, Long typeId) {
        XyCollectionProgressDO record = progressMapper.selectOneByQuery(QueryWrapper.create()
                .where(XY_COLLECTION_PROGRESS_D_O.CHARACTER_ID.eq(characterId))
                .and(XY_COLLECTION_PROGRESS_D_O.TYPE_ID.eq(typeId))
                .and(XY_COLLECTION_PROGRESS_D_O.TYPE_COMPLETED.eq(1)));
        return record != null;
    }

    /**
     * 标记玩家完成某阶段
     * @param characterId 角色ID
     * @param stageId 阶段ID
     */
    public static void markStageCompleted(int characterId, Long stageId) {
        progressMapper.updateByQuery(
                XyCollectionProgressDO.builder().stageCompleted(1).build(),
                QueryWrapper.create()
                        .where(XY_COLLECTION_PROGRESS_D_O.CHARACTER_ID.eq(characterId))
                        .and(XY_COLLECTION_PROGRESS_D_O.STAGE_ID.eq(stageId)));
    }

    /**
     * 标记玩家完成某类型所有阶段
     * @param characterId 角色ID
     * @param typeId 类型ID
     */
    public static void markTypeCompleted(int characterId, Long typeId) {
        progressMapper.updateByQuery(
                XyCollectionProgressDO.builder().typeCompleted(1).build(),
                QueryWrapper.create()
                        .where(XY_COLLECTION_PROGRESS_D_O.CHARACTER_ID.eq(characterId))
                        .and(XY_COLLECTION_PROGRESS_D_O.TYPE_ID.eq(typeId)));
    }
}
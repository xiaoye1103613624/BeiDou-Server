package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.client.Character;
import org.gms.model.pojo.NewYearCardRecord;
import org.gms.dao.entity.NewyearDO;
import org.gms.dao.mapper.NewyearMapper;
import org.gms.net.server.Server;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.gms.dao.entity.table.NewyearDOTableDef.NEWYEAR_D_O;

/**
 * 新年贺卡服务类，管理新年贺卡任务的启动和查询。
 */
@Service
@AllArgsConstructor
public class NewYearCardService {

    /**
     * 新年贺卡数据访问对象
     */
    private final NewyearMapper newyearMapper;

    /**
     * 启动所有待处理的新年贺卡请求。
     * 查询未接收且发送方未丢弃的贺卡，创建对应的定时任务。
     */
    public void startPendingNewYearCardRequests() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(NEWYEAR_D_O)
                .where(NEWYEAR_D_O.TIMERECEIVED.eq(0))
                .and(NEWYEAR_D_O.SENDERDISCARD.eq(0));
        // 查询所有待处理贺卡（未接收且发送方未丢弃）
        List<NewyearDO> newyearDOList = newyearMapper.selectListByQuery(queryWrapper);
        for (NewyearDO newyearDO : newyearDOList) {
            // 构建贺卡记录对象
            NewYearCardRecord newYearCardRecord = new NewYearCardRecord(newyearDO.getSenderid(), newyearDO.getSendername(), newyearDO.getReceiverid(),
                    newyearDO.getReceivername(), newyearDO.getMessage());
            newYearCardRecord.setExtraNewYearCardRecord(newyearDO.getId().intValue(), newyearDO.getSenderdiscard(), newyearDO.getReceiverdiscard(),
                    newyearDO.getReceived(), newyearDO.getTimesent(), newyearDO.getTimereceived());
            // 注册到服务器并启动定时任务
            Server.getInstance().setNewYearCard(newYearCardRecord);
            newYearCardRecord.startNewYearCardTask();
        }
    }

    /**
     * 加载玩家的新年贺卡记录（发送或接收的）。
     *
     * @param chr 角色对象
     * @return 新年贺卡记录列表
     */
    public List<NewyearDO> loadPlayerNewYearCards(Character chr) {
        return newyearMapper.selectListByQuery(QueryWrapper.create().where(NEWYEAR_D_O.SENDERID.eq(chr.getId())).or(NEWYEAR_D_O.RECEIVERID.eq(chr.getId())));
    }
}
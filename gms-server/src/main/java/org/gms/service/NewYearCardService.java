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
 * 【业务服务】NewYearCardService：新年贺卡服务类，负责新年贺卡系统的管理。
 * 
 * <p>提供新年贺卡的发送、接收和查询功能，支持服务重启后恢复待处理的贺卡请求。</p>
 */
@Service
@AllArgsConstructor
public class NewYearCardService {
    /** 新年贺卡数据访问接口 */
    private final NewyearMapper newyearMapper;

    /**
     * 启动所有待处理的新年贺卡请求。
     * 
     * <p>服务启动时调用，查询所有未接收（timereceived=0）且发送者未丢弃（senderdiscard=0）的贺卡，
     * 创建贺卡记录并启动发送任务，确保服务重启后未完成的贺卡能继续发送。</p>
     */
    public void startPendingNewYearCardRequests() {
        // 查询未接收且发送者未丢弃的贺卡
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(NEWYEAR_D_O)
                .where(NEWYEAR_D_O.TIMERECEIVED.eq(0))
                .and(NEWYEAR_D_O.SENDERDISCARD.eq(0));
        
        List<NewyearDO> newyearDOList = newyearMapper.selectListByQuery(queryWrapper);
        
        for (NewyearDO newyearDO : newyearDOList) {
            // 创建贺卡记录
            NewYearCardRecord newYearCardRecord = new NewYearCardRecord(
                    newyearDO.getSenderid(), newyearDO.getSendername(), 
                    newyearDO.getReceiverid(), newyearDO.getReceivername(), 
                    newyearDO.getMessage());
            // 设置额外属性
            newYearCardRecord.setExtraNewYearCardRecord(
                    newyearDO.getId().intValue(), 
                    newyearDO.getSenderdiscard(), 
                    newyearDO.getReceiverdiscard(),
                    newyearDO.getReceived(), 
                    newyearDO.getTimesent(), 
                    newyearDO.getTimereceived());
            // 注册到服务器并启动发送任务
            Server.getInstance().setNewYearCard(newYearCardRecord);
            newYearCardRecord.startNewYearCardTask();
        }
    }

    /**
     * 加载玩家的所有新年贺卡记录。
     * 
     * <p>查询玩家作为发送者或接收者的所有贺卡记录。</p>
     * 
     * @param chr 角色对象
     * @return 新年贺卡列表
     */
    public List<NewyearDO> loadPlayerNewYearCards(Character chr) {
        return newyearMapper.selectListByQuery(QueryWrapper.create()
                .where(NEWYEAR_D_O.SENDERID.eq(chr.getId()))
                .or(NEWYEAR_D_O.RECEIVERID.eq(chr.getId())));
    }
}
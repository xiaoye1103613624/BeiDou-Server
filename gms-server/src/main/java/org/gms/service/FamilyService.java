package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.client.Family;
import org.gms.client.FamilyEntry;
import org.gms.client.Job;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.entity.FamilyCharacterDO;
import org.gms.dao.entity.FamilyEntitlementDO;
import org.gms.dao.mapper.FamilyCharacterMapper;
import org.gms.dao.mapper.FamilyEntitlementMapper;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.gms.dao.entity.table.FamilyEntitlementDOTableDef.FAMILY_ENTITLEMENT_D_O;

/**
 * 家族服务类
 * 管理游戏内家族系统，包括家族加载、成员管理和权限分配
 */
@Service
@AllArgsConstructor
public class FamilyService {
    /** 家族成员数据访问对象 */
    private final FamilyCharacterMapper familyCharacterMapper;
    /** 家族权限数据访问对象 */
    private final FamilyEntitlementMapper familyEntitlementMapper;
    /** 角色服务 */
    private final CharacterService characterService;

    /**
     * 加载所有家族数据到内存
     * 遍历所有家族成员记录，构建家族、成员树形结构，并处理师徒关系
     */
    public void loadAllFamilies() {
        List<FamilyCharacterDO> familyCharacterDOList = familyCharacterMapper.selectAll();
        // 未匹配的师徒关系临时列表：key为师傅ID，value为徒弟的FamilyEntry
        List<Pair<Integer, FamilyEntry>> unmatchedJuniors = new ArrayList<>();
        for (FamilyCharacterDO familyCharacterDO : familyCharacterDOList) {
            CharactersDO charactersDO = characterService.findById(familyCharacterDO.getCid());
            if (charactersDO == null) {
                continue;
            }
            World world = Server.getInstance().getWorld(charactersDO.getWorld());
            if (world == null) {
                continue;
            }
            // 查找或创建家族
            Family family = world.getFamily(familyCharacterDO.getFamilyid());
            if (family == null) {
                family = new Family(familyCharacterDO.getFamilyid(), charactersDO.getWorld());
                world.addFamily(familyCharacterDO.getFamilyid(), family);
            }
            // 构建家族成员条目
            FamilyEntry familyEntry = new FamilyEntry(family, charactersDO.getId(), charactersDO.getName(),
                    charactersDO.getLevel(), Job.getById(charactersDO.getJob()));
            familyEntry.setReputation(familyCharacterDO.getReputation());
            familyEntry.setTodaysRep(familyCharacterDO.getTodaysrep());
            familyEntry.setTotalReputation(familyCharacterDO.getTotalreputation());
            familyEntry.setRepsToSenior(familyCharacterDO.getReptosenior());
            family.addEntry(familyEntry);
            // 无师傅的成员即为家族族长
            if (familyCharacterDO.getSeniorid() <= 0) {
                family.setLeader(familyEntry);
                family.setMessage(familyCharacterDO.getPrecepts(), false);
            }
            // 建立师徒关系
            FamilyEntry senior = family.getEntryByID(familyCharacterDO.getSeniorid());
            if (senior != null) {
                familyEntry.setSenior(senior, false);
            } else if (familyCharacterDO.getSeniorid() > 0) {
                // 师傅尚未加载，暂存到未匹配列表
                unmatchedJuniors.add(new Pair<>(familyCharacterDO.getSeniorid(), familyEntry));
            }
            // 加载家族权限
            List<FamilyEntitlementDO> familyEntitlementDOList = familyEntitlementMapper.selectListByQuery(QueryWrapper.create()
                    .select(FAMILY_ENTITLEMENT_D_O.ENTITLEMENTID)
                    .from(FAMILY_ENTITLEMENT_D_O)
                    .where(FAMILY_ENTITLEMENT_D_O.CHARID.eq(charactersDO.getId())));
            familyEntitlementDOList.forEach(familyEntitlementDO -> familyEntry.setEntitlementUsed(familyEntitlementDO.getEntitlementid()));
        }
        // 修补未匹配的师徒关系
        for (Pair<Integer, FamilyEntry> unmatchedJunior : unmatchedJuniors) {
            FamilyEntry senior = Server.getInstance()
                    .getWorld(unmatchedJunior.getRight().getFamily().getWorld())
                    .getFamily(unmatchedJunior.getRight().getFamily().getID())
                    .getEntryByID(unmatchedJunior.getLeft());
            if (senior != null) {
                unmatchedJunior.getRight().setSenior(senior, false);
            }
        }
        // 重新统计所有族长的下级成员数
        for (World world : Server.getInstance().getWorlds()) {
            for (Family family : world.getFamilies()) {
                family.getLeader().doFullCount();
            }
        }
    }
}
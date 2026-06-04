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
 * 【业务服务】FamilyService：师徒家族服务类，负责师徒家族系统的数据加载与管理。
 * 
 * <p>提供师徒家族数据从数据库加载到内存的功能，包括家族成员、师徒关系、声望值等信息。
 * 服务启动时调用 {@link #loadAllFamilies()} 方法将数据库中的家族数据加载到内存中。</p>
 * 
 * <p>师徒家族系统结构：
 * <ul>
 *   <li>Family（家族）：由多个成员组成，有一个族长</li>
 *   <li>FamilyEntry（家族成员）：每个成员有师傅（senior）和徒弟（junior）关系</li>
 *   <li>声望系统：成员可以获得声望，用于师徒互动和奖励</li>
 * </ul></p>
 */
@Service
@AllArgsConstructor
public class FamilyService {
    /** 师徒家族角色数据访问接口 */
    private final FamilyCharacterMapper familyCharacterMapper;
    /** 师徒家族权限数据访问接口 */
    private final FamilyEntitlementMapper familyEntitlementMapper;
    /** 角色服务，用于查询角色信息 */
    private final CharacterService characterService;

    /**
     * 加载所有师徒家族数据到内存。
     * 
     * <p>从数据库读取所有家族角色记录，构建家族对象并加载到对应的World中。
     * 处理流程：
     * <ol>
     *   <li>查询所有家族角色记录</li>
     *   <li>为每个角色创建FamilyEntry并加入家族</li>
     *   <li>处理师徒关系（可能需要二次匹配）</li>
     *   <li>加载成员的权限信息</li>
     *   <li>完成师徒关系的二次匹配</li>
     *   <li>执行家族统计计算</li>
     * </ol></p>
     */
    public void loadAllFamilies() {
        // 查询所有家族角色记录
        List<FamilyCharacterDO> familyCharacterDOList = familyCharacterMapper.selectAll();
        // 存储暂未匹配到师傅的徒弟
        List<Pair<Integer, FamilyEntry>> unmatchedJuniors = new ArrayList<>();
        
        for (FamilyCharacterDO familyCharacterDO : familyCharacterDOList) {
            // 查询角色信息
            CharactersDO charactersDO = characterService.findById(familyCharacterDO.getCid());
            if (charactersDO == null) {
                continue;
            }
            
            // 获取角色所在的World
            World world = Server.getInstance().getWorld(charactersDO.getWorld());
            if (world == null) {
                continue;
            }
            
            // 获取或创建家族
            Family family = world.getFamily(familyCharacterDO.getFamilyid());
            if (family == null) {
                family = new Family(familyCharacterDO.getFamilyid(), charactersDO.getWorld());
                world.addFamily(familyCharacterDO.getFamilyid(), family);
            }
            
            // 创建家族成员条目
            FamilyEntry familyEntry = new FamilyEntry(family, charactersDO.getId(), 
                    charactersDO.getName(), charactersDO.getLevel(), 
                    Job.getById(charactersDO.getJob()));
            // 设置声望相关属性
            familyEntry.setReputation(familyCharacterDO.getReputation());
            familyEntry.setTodaysRep(familyCharacterDO.getTodaysrep());
            familyEntry.setTotalReputation(familyCharacterDO.getTotalreputation());
            familyEntry.setRepsToSenior(familyCharacterDO.getReptosenior());
            
            // 将成员加入家族
            family.addEntry(familyEntry);
            
            // 判断是否为族长（seniorid <= 0 表示族长）
            if (familyCharacterDO.getSeniorid() <= 0) {
                family.setLeader(familyEntry);
                family.setMessage(familyCharacterDO.getPrecepts(), false);
            }
            
            // 处理师徒关系
            FamilyEntry senior = family.getEntryByID(familyCharacterDO.getSeniorid());
            if (senior != null) {
                // 找到师傅，建立师徒关系
                familyEntry.setSenior(senior, false);
            } else if (familyCharacterDO.getSeniorid() > 0) {
                // 师傅尚未加载，暂存待后续匹配
                unmatchedJuniors.add(new Pair<>(familyCharacterDO.getSeniorid(), familyEntry));
            }
            
            // 加载成员的权限信息
            List<FamilyEntitlementDO> familyEntitlementDOList = familyEntitlementMapper.selectListByQuery(
                    QueryWrapper.create()
                            .select(FAMILY_ENTITLEMENT_D_O.ENTITLEMENTID)
                            .from(FAMILY_ENTITLEMENT_D_O)
                            .where(FAMILY_ENTITLEMENT_D_O.CHARID.eq(charactersDO.getId())));
            familyEntitlementDOList.forEach(entitlement -> 
                    familyEntry.setEntitlementUsed(entitlement.getEntitlementid()));
        }
        
        // 处理二次匹配：为暂未找到师傅的徒弟匹配师傅
        for (Pair<Integer, FamilyEntry> unmatchedJunior : unmatchedJuniors) {
            FamilyEntry senior = Server.getInstance()
                    .getWorld(unmatchedJunior.getRight().getFamily().getWorld())
                    .getFamily(unmatchedJunior.getRight().getFamily().getID())
                    .getEntryByID(unmatchedJunior.getLeft());
            if (senior != null) {
                unmatchedJunior.getRight().setSenior(senior, false);
            }
        }
        
        // 执行所有家族的统计计算
        for (World world : Server.getInstance().getWorlds()) {
            for (Family family : world.getFamilies()) {
                family.getLeader().doFullCount();
            }
        }
    }
}
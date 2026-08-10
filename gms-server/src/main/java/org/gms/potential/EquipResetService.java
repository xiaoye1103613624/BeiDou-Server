package org.gms.potential;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.constants.inventory.ItemConstants;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可直接移植的强化扩展：普通还原卷轴。
 * <p>
 * 对齐 Wiki「普通还原」简化版：清砸卷属性 / Hyper★ / 黄金锤次数；保留潜能、附加、灵魂、星岩、白金锤。
 */
public final class EquipResetService {
    private static final Logger log = LoggerFactory.getLogger(EquipResetService.class);

    private EquipResetService() {}

    public static PotentialHyperService.Result applyResetScroll(Character chr, Equip equip, int scrollId,
                                                                boolean forceSuccess) {
        if (equip == null || !ItemConstants.isResetScroll(scrollId)) {
            return PotentialHyperService.Result.INVALID;
        }
        int success = ItemConstants.resetScrollSuccess(scrollId);
        if (!forceSuccess && Randomizer.nextInt(100) >= success) {
            return PotentialHyperService.Result.FAIL;
        }
        resetScrollAndHyper(equip);
        log.info("reset scroll char={} equip={} scroll={} platinum={}",
                chr != null ? chr.getId() : -1, equip.getItemId(), scrollId, equip.getPlatinum());
        return PotentialHyperService.Result.SUCCESS;
    }

    /**
     * 还原到白板属性 + 基础升级次数 + 白金锤永久次数；不清潜能系字段。
     */
    public static void resetScrollAndHyper(Equip equip) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Equip blank = (Equip) ii.getEquipById(equip.getItemId());
        if (blank == null) {
            blank = new Equip(equip.getItemId(), (short) 0);
        }
        byte plat = equip.getPlatinum();
        equip.setStr(blank.getStr());
        equip.setDex(blank.getDex());
        equip.setInt(blank.getInt());
        equip.setLuk(blank.getLuk());
        equip.setHp(blank.getHp());
        equip.setMp(blank.getMp());
        equip.setWatk(blank.getWatk());
        equip.setMatk(blank.getMatk());
        equip.setWdef(blank.getWdef());
        equip.setMdef(blank.getMdef());
        equip.setAcc(blank.getAcc());
        equip.setAvoid(blank.getAvoid());
        equip.setHands(blank.getHands());
        equip.setSpeed(blank.getSpeed());
        equip.setJump(blank.getJump());
        equip.setLevel((byte) 0);
        equip.setEnhance((byte) 0);
        equip.setVicious((short) 0);
        equip.clearChaosLedger();
        // 基础槽 + 白金锤永久次数（黄金锤次数已清零）
        int baseSlots = blank.getUpgradeSlots();
        equip.setUpgradeSlots((byte) Math.min(127, Math.max(0, baseSlots + plat)));
        equip.setPlatinum(plat);
    }
}

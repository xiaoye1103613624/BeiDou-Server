package org.gms.server.expeditions;

import org.gms.config.GameConfig;

/**
 * 远征死亡次数：从 game_config 读取。0 / 缺键 = 关闭。
 * switch 穷尽 ExpeditionType，新增类型时必须在此补配置。
 */
public final class ExpeditionDeathCount {

    private ExpeditionDeathCount() {
    }

    public static int configuredFor(ExpeditionType type) {
        if (type == null) {
            return 0;
        }
        int configured = switch (type) {
            case ZAKUM -> GameConfig.getServerInt("zakum_expedition_death_count");
            case CHAOS_ZAKUM -> GameConfig.getServerInt("chaos_zakum_expedition_death_count");
            case HORNTAIL -> GameConfig.getServerInt("horntail_expedition_death_count");
            case CHAOS_HORNTAIL -> GameConfig.getServerInt("chaos_horntail_expedition_death_count");
            case PINKBEAN -> GameConfig.getServerInt("pinkbean_expedition_death_count");
            case BALROG_EASY -> GameConfig.getServerInt("balrog_easy_expedition_death_count");
            case BALROG_NORMAL -> GameConfig.getServerInt("balrog_normal_expedition_death_count");
            case SCARGA -> GameConfig.getServerInt("scarga_expedition_death_count");
            case SHOWA -> GameConfig.getServerInt("showa_expedition_death_count");
            case CWKPQ -> GameConfig.getServerInt("cwkpq_expedition_death_count");
            case ARIANT, ARIANT1, ARIANT2 -> 0;
            case VonLeon -> GameConfig.getServerInt("vonleon_expedition_death_count");
            case Cydnus -> GameConfig.getServerInt("cydnus_expedition_death_count");
            case VONBON -> GameConfig.getServerInt("vonbon_expedition_death_count");
            case PIERRE -> GameConfig.getServerInt("pierre_expedition_death_count");
            case CQ -> GameConfig.getServerInt("cq_expedition_death_count");
            case VELLUM -> GameConfig.getServerInt("vellum_expedition_death_count");
            case AKAYRUM -> GameConfig.getServerInt("akayrum_expedition_death_count");
            default -> 0;
        };
        return Math.max(0, configured);
    }
}

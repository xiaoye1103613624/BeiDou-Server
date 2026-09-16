package org.gms.service.skill;

import org.gms.model.dto.SkillJobLineDTO;
import org.gms.model.dto.SkillJobStageDTO;
import org.gms.model.dto.SkillLineageDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * v83 技能 Web 管理职业线目录：冒险家 / 骑士团 / 战神启用；龙神 / GM / Mob 预留。
 */
public final class SkillJobCatalog {

    private SkillJobCatalog() {
    }

    public static List<SkillLineageDTO> lineages() {
        List<SkillLineageDTO> list = new ArrayList<>();
        list.add(lineage("explorer", "skillAdmin.lineage.explorer", true, null));
        list.add(lineage("cygnus", "skillAdmin.lineage.cygnus", true, null));
        list.add(lineage("aran", "skillAdmin.lineage.aran", true, null));
        list.add(lineage("evan", "skillAdmin.lineage.evan", false, "skillAdmin.lineage.reserved"));
        list.add(lineage("gm", "skillAdmin.lineage.gm", false, "skillAdmin.lineage.reserved"));
        list.add(lineage("mob", "skillAdmin.lineage.mob", false, "skillAdmin.lineage.reserved"));
        return list;
    }

    public static List<SkillJobLineDTO> jobLines(String lineage) {
        if (lineage == null || lineage.isBlank()) {
            return List.of();
        }
        return switch (lineage.trim().toLowerCase()) {
            case "explorer" -> explorerLines();
            case "cygnus" -> cygnusLines();
            case "aran" -> aranLines();
            default -> List.of();
        };
    }

    public static Optional<SkillJobLineDTO> findLine(String lineage, String lineId) {
        return jobLines(lineage).stream().filter(l -> l.getLineId().equals(lineId)).findFirst();
    }

    /** 所有已启用职业线的 jobId（含各转职阶段），用于 patch 全量导出。 */
    public static List<Integer> allEnabledJobIds() {
        Map<Integer, Boolean> ids = new LinkedHashMap<>();
        for (SkillLineageDTO lin : lineages()) {
            if (!Boolean.TRUE.equals(lin.getEnabled())) {
                continue;
            }
            for (SkillJobLineDTO line : jobLines(lin.getCode())) {
                if (line.getStages() == null) {
                    continue;
                }
                for (SkillJobStageDTO st : line.getStages()) {
                    if (st.getJobId() != null) {
                        ids.put(st.getJobId(), Boolean.TRUE);
                    }
                }
            }
        }
        return List.copyOf(ids.keySet());
    }

    private static SkillLineageDTO lineage(String code, String nameKey, boolean enabled, String remark) {
        return SkillLineageDTO.builder()
                .code(code)
                .nameKey(nameKey)
                .enabled(enabled)
                .remark(remark)
                .build();
    }

    private static List<SkillJobLineDTO> explorerLines() {
        List<SkillJobLineDTO> lines = new ArrayList<>();
        lines.add(line("explorer", "explorer-beginner", "job.name.0", stages(0)));
        lines.add(line("explorer", "explorer-hero", "job.name.112",
                stages(100, 110, 111, 112)));
        lines.add(line("explorer", "explorer-paladin", "job.name.122",
                stages(100, 120, 121, 122)));
        lines.add(line("explorer", "explorer-darkknight", "job.name.132",
                stages(100, 130, 131, 132)));
        lines.add(line("explorer", "explorer-fp", "job.name.212",
                stages(200, 210, 211, 212)));
        lines.add(line("explorer", "explorer-il", "job.name.222",
                stages(200, 220, 221, 222)));
        lines.add(line("explorer", "explorer-bishop", "job.name.232",
                stages(200, 230, 231, 232)));
        lines.add(line("explorer", "explorer-bowmaster", "job.name.312",
                stages(300, 310, 311, 312)));
        lines.add(line("explorer", "explorer-marksman", "job.name.322",
                stages(300, 320, 321, 322)));
        lines.add(line("explorer", "explorer-nightlord", "job.name.412",
                stages(400, 410, 411, 412)));
        lines.add(line("explorer", "explorer-shadower", "job.name.422",
                stages(400, 420, 421, 422)));
        lines.add(line("explorer", "explorer-buccaneer", "job.name.512",
                stages(500, 510, 511, 512)));
        lines.add(line("explorer", "explorer-corsair", "job.name.522",
                stages(500, 520, 521, 522)));
        return lines;
    }

    private static List<SkillJobLineDTO> cygnusLines() {
        List<SkillJobLineDTO> lines = new ArrayList<>();
        lines.add(line("cygnus", "cygnus-noblesse", "job.name.1000", stages(1000)));
        lines.add(line("cygnus", "cygnus-dawn", "job.name.1112",
                stages(1100, 1110, 1111, 1112)));
        lines.add(line("cygnus", "cygnus-blaze", "job.name.1212",
                stages(1200, 1210, 1211, 1212)));
        lines.add(line("cygnus", "cygnus-wind", "job.name.1312",
                stages(1300, 1310, 1311, 1312)));
        lines.add(line("cygnus", "cygnus-night", "job.name.1412",
                stages(1400, 1410, 1411, 1412)));
        lines.add(line("cygnus", "cygnus-thunder", "job.name.1512",
                stages(1500, 1510, 1511, 1512)));
        return lines;
    }

    private static List<SkillJobLineDTO> aranLines() {
        return List.of(
                line("aran", "aran-legend", "job.name.2000", stages(2000)),
                line("aran", "aran-main", "job.name.2112", stages(2100, 2110, 2111, 2112))
        );
    }

    private static SkillJobLineDTO line(String lineage, String lineId, String nameKey, List<SkillJobStageDTO> stages) {
        return SkillJobLineDTO.builder()
                .lineage(lineage)
                .lineId(lineId)
                .nameKey(nameKey)
                .stages(stages)
                .build();
    }

    /**
     * jobIds 顺序对应转职阶段；branch 用 GameConstants 语义近似：0 新手、1 一转、2 二转、3 三转、4 四转。
     */
    private static List<SkillJobStageDTO> stages(int... jobIds) {
        if (jobIds.length == 1 && jobIds[0] % 1000 == 0) {
            return List.of(stage(0, jobIds[0]));
        }
        List<SkillJobStageDTO> list = new ArrayList<>();
        for (int i = 0; i < jobIds.length; i++) {
            int jobId = jobIds[i];
            int branch;
            if (jobId % 100 == 0 && jobId % 1000 != 0) {
                branch = 1;
            } else if (jobIds.length == 4) {
                branch = i + 1;
            } else {
                branch = Math.min(4, i + 1);
            }
            list.add(stage(branch, jobId));
        }
        return Collections.unmodifiableList(list);
    }

    private static SkillJobStageDTO stage(int branch, int jobId) {
        return SkillJobStageDTO.builder()
                .branch(branch)
                .jobId(jobId)
                .nameKey("job.name." + jobId)
                .build();
    }
}

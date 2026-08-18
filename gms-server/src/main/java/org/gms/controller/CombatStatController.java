package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gms.combat.stat.CombatStatCaps;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatProfile;
import org.gms.combat.stat.CombatStatResolver;
import org.gms.combat.stat.CombatStatSource;
import org.gms.combat.stat.CombatStatType;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/combatStat")
public class CombatStatController {

    @Tag(name = "/combatStat/" + ApiConstant.LATEST)
    @Operation(summary = "战斗属性类型字典")
    @GetMapping("/" + ApiConstant.LATEST + "/types")
    public ResultBody<Object> types() {
        CombatStatCaps caps = CombatStatCaps.fromGameConfig();
        List<Map<String, Object>> list = new ArrayList<>();
        for (CombatStatType t : CombatStatType.values()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("key", t.getKey());
            row.put("label", t.getLabel());
            row.put("stackRule", t.getStackRule().name());
            row.put("cap", caps.capOf(t) == Integer.MAX_VALUE ? 0 : caps.capOf(t));
            list.add(row);
        }
        return ResultBody.success(list);
    }

    @Tag(name = "/combatStat/" + ApiConstant.LATEST)
    @Operation(summary = "传入 modifiers 预览聚合 Profile")
    @PostMapping("/" + ApiConstant.LATEST + "/preview")
    public ResultBody<Object> preview(@RequestBody SubmitBody<List<Map<String, Object>>> request) {
        List<CombatStatModifier> mods = new ArrayList<>();
        List<Map<String, Object>> rows = request != null ? request.getData() : null;
        if (rows != null) {
            int i = 0;
            for (Map<String, Object> row : rows) {
                if (row == null) {
                    continue;
                }
                CombatStatType type = CombatStatType.fromKey(String.valueOf(row.getOrDefault("key", "")));
                if (type == null) {
                    continue;
                }
                int value = toInt(row.get("value"));
                if (value == 0) {
                    continue;
                }
                CombatStatSource source = CombatStatSource.GM;
                Object src = row.get("source");
                if (src != null) {
                    try {
                        source = CombatStatSource.valueOf(String.valueOf(src));
                    } catch (IllegalArgumentException ignored) {
                        source = CombatStatSource.GM;
                    }
                }
                String sourceId = String.valueOf(row.getOrDefault("sourceId", "preview:" + i));
                mods.add(new CombatStatModifier(type, value, source, sourceId));
                i++;
            }
        }
        CombatStatProfile profile = new CombatStatResolver(CombatStatCaps.fromGameConfig()).resolve(mods);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("damR", profile.damR);
        out.put("bdR", profile.bossDamR);
        out.put("nbdR", profile.normalDamR);
        out.put("ignorePDR", profile.ignorePDR);
        out.put("ignoreMDR", profile.ignoreMDR);
        out.put("cr", profile.critRate);
        out.put("cd", profile.critDam);
        out.put("padR", profile.padR);
        out.put("madR", profile.madR);
        out.put("finalDamageSources", profile.finalDamageSources);
        out.put("finalDamageMultiplier", profile.finalDamageMultiplier);
        return ResultBody.success(out);
    }

    private static int toInt(Object v) {
        if (v instanceof Number n) {
            return n.intValue();
        }
        if (v == null) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception e) {
            return 0;
        }
    }
}

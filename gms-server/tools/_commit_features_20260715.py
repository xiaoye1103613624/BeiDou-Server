# -*- coding: utf-8 -*-
"""Feature-grouped commits for BeiDou-Server_xy (Windows)."""
from __future__ import annotations

import subprocess
import sys
from pathlib import Path

ROOT = Path(r"E:\pro\BeiDou-Server_xy")


def run(cmd: list[str], check: bool = True) -> subprocess.CompletedProcess:
    print("+", " ".join(cmd[:8]), ("..." if len(cmd) > 8 else ""))
    return subprocess.run(cmd, cwd=ROOT, check=check)


def commit(msg: str) -> None:
    st = subprocess.run(
        ["git", "diff", "--cached", "--name-only"],
        cwd=ROOT,
        capture_output=True,
        text=True,
        encoding="utf-8",
    )
    files = [x for x in st.stdout.splitlines() if x.strip()]
    if not files:
        print(f"SKIP empty: {msg}")
        return
    run(["git", "commit", "-m", msg])
    print(f"OK ({len(files)} files): {msg}")


def add(paths: list[str]) -> None:
    existing = []
    for p in paths:
        fp = ROOT / p
        if fp.exists() or list(ROOT.glob(p)):
            existing.append(p)
        else:
            # try as relative glob
            matches = list(ROOT.glob(p))
            if matches:
                existing.extend(str(m.relative_to(ROOT)).replace("\\", "/") for m in matches)
            else:
                print("MISSING", p)
    if existing:
        run(["git", "add", "--"] + existing)


def find_under(rel: str, name_contains: str | None = None) -> list[str]:
    base = ROOT / rel
    out = []
    if not base.exists():
        return out
    for p in base.rglob("*"):
        if not p.is_file():
            continue
        if name_contains and name_contains not in p.name:
            continue
        out.append(str(p.relative_to(ROOT)).replace("\\", "/"))
    return out


def main() -> int:
    # Clear index quietly
    run(["git", "reset", "HEAD"], check=False)

    # --- 1 Spirit ---
    spirit_scripts = []
    spirit_scripts += find_under("gms-server/scripts-zh-CN/npc/xy", "灵韵觉醒")
    spirit_scripts += find_under("gms-server/scripts-zh-CN/npc/xy", "导览")
    spirit_scripts += find_under("gms-server/scripts-zh-CN/BeiDouSpecial", "武器进阶")
    add(
        [
            "gms-server/src/main/java/org/gms/spirit",
            "gms-server/src/main/resources/db/migration/V1.11.18__spirit_awaken.sql",
            "gms-server/src/main/java/org/gms/client/inventory/Equip.java",
            "gms-server/src/main/java/org/gms/dao/entity/InventoryequipmentDO.java",
            "gms-server/src/main/java/org/gms/model/dto/InventoryEquipRtnDTO.java",
            "gms-server/src/main/java/org/gms/client/inventory/ItemFactory.java",
            "gms-server/src/main/java/org/gms/client/inventory/manipulator/InventoryManipulator.java",
            "gms-server/src/main/java/org/gms/service/InventoryService.java",
            "gms-server/src/main/java/org/gms/server/Trade.java",
            "gms-server/scripts-zh-CN/npc/9031012.js",
            "gms-server/scripts-zh-CN/npc/9031014.js",
            "gms-server/wz-zh-CN/Item.wz/Etc/0402.img.xml",
            "gms-server/wz-zh-CN/Item.wz/Etc/0446.img.xml",
            "gms-server/wz-zh-CN/String.wz/Etc.img.xml",
            "gms-server/wz-zh-CN/String.wz/Item.img.xml",
            "gms-server/wz/Item.wz/Etc/0402.img.xml",
        ]
        + spirit_scripts
    )
    commit("feat(gms): spirit awaken system with equip skill fields and NPC")

    # --- 2 Talent ---
    add(
        [
            "gms-server/src/main/java/org/gms/talent",
            "gms-server/src/main/java/org/gms/combat/provider/TalentStatProvider.java",
            "gms-server/src/main/java/org/gms/dao/entity/CharacterTalentDO.java",
            "gms-server/src/main/java/org/gms/dao/mapper/CharacterTalentMapper.java",
            "gms-server/src/main/resources/db/migration/V1.11.17__talent_system.sql",
            "gms-server/src/main/java/org/gms/scripting/npc/NPCConversationManager.java",
            "gms-server/src/main/java/org/gms/server/maps/MapleMap.java",
            "gms-server/src/main/java/org/gms/net/server/channel/handlers/TakeDamageHandler.java",
            "gms-server/src/main/java/org/gms/net/server/channel/handlers/AbstractDealDamageHandler.java",
            "gms-server/src/main/java/org/gms/server/StatEffect.java",
            "gms-server/src/main/java/org/gms/combat/provider/CombatProfileService.java",
            "gms-server/src/main/java/org/gms/combat/stat/CombatStatSource.java",
        ]
    )
    commit("feat(gms): talent system with combat hooks and SQL")

    # --- 3 Level300 ---
    add(
        [
            "gms-server/src/main/java/org/gms/constants/game/ExpTable.java",
            "gms-server/src/main/java/org/gms/constants/game/GameConstants.java",
            "gms-server/src/main/java/org/gms/client/AbstractCharacterObject.java",
            "gms-server/src/main/java/org/gms/dao/entity/CharactersDO.java",
            "gms-server/src/main/java/org/gms/server/ExpLogger.java",
            "gms-server/src/main/java/org/gms/client/command/commands/gm2/MaxStatCommand.java",
            "gms-server/src/main/resources/db/migration/V1.11.20__level300_exp_bigint.sql",
            "gms-server/src/main/resources/i18n/message_en_US.properties",
            "gms-server/src/main/resources/i18n/message_zh_CN.properties",
            "gms-server/src/main/java/org/gms/client/Client.java",
            "gms-server/src/main/java/org/gms/client/CharacterListener.java",
            "gms-server/src/main/java/org/gms/service/CharacterService.java",
            "gms-server/src/main/java/org/gms/service/GiveService.java",
        ]
    )
    commit("feat(gms): Level300 EXP table and bigint experience storage")

    # --- 4 Party tracker ---
    add(
        [
            "gms-server/src/main/java/org/gms/net/server/world/Party.java",
            "gms-server/src/main/java/org/gms/net/server/world/World.java",
            "gms-server/src/main/java/org/gms/client/command/commands/gm0/PartyTrackerCommand.java",
            "gms-server/src/main/resources/db/migration/V1.11.16__party_tracker_command.sql",
            "gms-server/src/main/java/org/gms/net/opcodes/SendOpcode.java",
            "gms-server/src/main/java/org/gms/net/server/handlers/CustomPacketHandler.java",
        ]
    )
    commit("feat(gms): party buff tracker packets and command")

    # --- 5 Character + PacketCreator wiring (multi-feature) ---
    add(
        [
            "gms-server/src/main/java/org/gms/client/Character.java",
            "gms-server/src/main/java/org/gms/util/PacketCreator.java",
        ]
    )
    commit("feat(gms): Character and PacketCreator align spirit talent level300 party")

    # --- 6 Slot lock / shop slots / char slots ---
    add(
        [
            "gms-server/src/main/java/org/gms/net/server/channel/handlers/InventoryMergeHandler.java",
            "gms-server/src/main/java/org/gms/net/server/channel/handlers/InventorySortHandler.java",
            "gms-server/src/main/java/org/gms/server/maps/HiredMerchant.java",
            "gms-server/src/main/java/org/gms/server/maps/PlayerShop.java",
            "gms-server/src/main/java/org/gms/net/server/channel/handlers/CashOperationHandler.java",
        ]
    )
    commit("feat(gms): inventory slot-lock merge and shop/char slot caps")

    # --- 7 Monster cards ---
    add(
        [
            "gms-server/src/main/resources/db/migration/V1.11.19__high_version_monster_cards.sql",
            "gms-server/src/main/resources/db/migration/V1.11.21__high_version_monster_cards_remaining.sql",
            "gms-server/src/main/resources/db/migration/V1.11.22__missing_monster_card_drops.sql",
            "gms-server/wz-zh-CN/Item.wz/Consume/0238.img.xml",
            "gms-server/wz-zh-CN/String.wz/Consume.img.xml",
            "gms-server/tools/import_missing_card_mobs.py",
            "gms-server/tools/missing_mobs_high_version_cards.txt",
            "gms-server/tools/missing_monster_card_mobs.txt",
        ]
    )
    # optional string merge helper if present
    add(["gms-server/tools/_merge_monster_card_string.py"])
    commit("feat(gms): high-version monster cards WZ and SQL migrations")

    # --- 8 Forgotten valley + 0400/0403 ---
    valley_maps = find_under("gms-server/wz-zh-CN/Map.wz/Map/Map0")
    valley_mobs = [p for p in find_under("gms-server/wz-zh-CN/Mob.wz") if "/Mob/" not in p and not p.endswith(".bak")]
    # only root xml mobs, skip Mob/Mob nested if huge accidental
    valley_mobs = [p for p in valley_mobs if Path(p).parent.name == "Mob.wz"]
    valley_npcs = find_under("gms-server/wz-zh-CN/Npc.wz")
    add(
        valley_maps
        + valley_mobs
        + valley_npcs
        + [
            "gms-server/wz-zh-CN/Item.wz/Etc/0400.img.xml",
            "gms-server/wz-zh-CN/Item.wz/Etc/0403.img.xml",
            "gms-server/wz-zh-CN/Map.wz/Map/Map1/101020000.img.xml",
            "gms-server/wz-zh-CN/Map.wz/Map/Map1/105040305.img.xml",
            "gms-server/wz-zh-CN/String.wz/Map.img.xml",
            "gms-server/wz-zh-CN/String.wz/Mob.img.xml",
            "gms-server/wz-zh-CN/String.wz/Npc.img.xml",
            ".eval_append/valley_port_report_20260715.md",
            "gms-server/tools/_port_forgotten_valley.py",
            "gms-server/tools/_valley_finalize.py",
            "gms-server/tools/_valley_orz_finish.py",
            "gms-server/tools/_valley_fix_portals_maps.py",
            "gms-server/tools/_valley_inventory.py",
            "gms-server/tools/_valley_item_ids.py",
            "gms-server/tools/_valley_check_mapstr.py",
            "gms-server/tools/_valley_find_mapstr2.py",
        ]
    )
    commit("feat(wz): forgotten valley sync and 0400/0403 imgdir close fixes")

    # --- 9 Shop bg tools ---
    add(
        [
            "gms-server/tools/merge_beauty_img/MergeBeautyImg.cs",
            "gms-server/tools/merge_beauty_img/_patch_storage_bg/Program.cs",
        ]
    )
    commit("feat(tools): PatchStorageBg shop background extend for HigherShopList")

    # --- 10 Docs + ingest ---
    add(
        [
            ".eval_append/error_ops_lessons_20260715.md",
            ".eval_append/wz_port_lessons_20260715.md",
            ".eval_append/recent_mods_summary_20260715.md",
            "gms-server/tools/_ingest_wz_port_lessons_20260715.py",
            "gms-server/tools/_ingest_recent_mods_20260715.py",
            "gms-server/tools/_readd_wz_port_lessons.py",
            "gms-server/tools/ingest_gms083_mistakes.py",
        ]
    )
    commit("docs: error-ops WZ lessons recent-mods summary and ingest scripts")

    # show leftover
    left = subprocess.run(
        ["git", "status", "--short"],
        cwd=ROOT,
        capture_output=True,
        text=True,
        encoding="utf-8",
    )
    print("--- leftover (first 60) ---")
    for line in left.stdout.splitlines()[:60]:
        print(line)
    return 0


if __name__ == "__main__":
    sys.exit(main())

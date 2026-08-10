# Addon Aux (−62) — Server Parity Flip Checklist

**Scope:** `gms-server` only. Client BP62 / CD64 / IDA patches are sibling tracks.

**Invariant (zero coupling):**
| Prefix | islot | Seat | Notes |
|--------|-------|------|--------|
| 109 | `Si` | −10 / −110 | Shield only |
| 134 / 135 | `Aw` | −62 / −162 | Aux Addon row3 — never share −10 |

## Live gate (A0BE14C9-safe)

Single flip constant:

```text
ExtendedEquipRegistry.GREEN_ENTER_OMIT_AUX62 = false  // PEER_COMPLETE + V2 enter-green (2026-08-04)
```

When `true`, all of these stay on:

| Path | Behavior |
|------|----------|
| `PacketCreator.addInventoryInfo` | Skip −62/−162 in CharInfo |
| `InventoryManipulator.equip` (134/135) | Reject-wear + `enableActions` |
| `migrateAuxWeaponOffShieldSlot` | Park 134/135 off −10/−110 and flush −62/−162 → bag |
| `recoverWireOmittedAuxToBag` | Ghost −62 → bag (no −62 remove packet); always `enableActions` |
| Sort / Merge handlers | Call recover before bag rearrange |

When `false` (after client ready): reject-wear / CharInfo omit / ghost-flush become no-ops; login migrate runs `−10 → −62`; wear uses normal replace-at−62→bag + `modifyInventory(true,…)`.

## Flip when client BP62 is enter-green

Do **not** flip on “jg raised to 62” alone — prior `0F244A5D` ENTER-RED. Require sibling report: **select→enter A/B green** on Client_1 with BP62 (+ CD64 if that track lands).

1. Confirm client SHA / stamp (BP62 green) and that CharInfo can hold −62 ZRef.
2. Set `GREEN_ENTER_OMIT_AUX62 = false` in `ExtendedEquipRegistry.java` (**one line**).
3. Rebuild `gms-server`; restart channel/world as usual.
4. Smoke:
   - Login with 109 on −10 + 134 in bag → wear 134 → lands −62; shield stays.
   - Wear 134 onto occupied −62 → old aux → bag; `enableActions` OK.
   - Two-hand main weapon still unequips **only** −10 (109), not −62.
   - 整理 / 合并: no SendBusy hang; recover path is no-op.
   - Relog: CharInfo includes −62/−162; no park-to-bag of worn aux.
5. Optional cleanup later: remove reject-wear message string / recover call sites (behavior already gated; not required for flip).

## Audit snapshot (prep complete)

| Area | Coupling 134/135→−10 or Si? | Status |
|------|------------------------------|--------|
| `EquipSlot` | No — `SHIELD` Si/−10; `AUX_WEAPON` Aw/−62 | OK |
| `ExtendedEquipRegistry` | Prefix 109→−10; 134/135→−62; omit flag | OK |
| `ItemInformationProvider.getEquipmentSlot` | 109→Si; 134/135→Aw | OK |
| `AbleToWear` / `isPrefixSlotAllowed` | Prefix wins | OK |
| `InventoryManipulator.equip` | Fixed dst −62; reject behind omit | OK |
| `migrateAux*` | Omit→bag; else −10→−62 | OK |
| `ItemMoveHandler` | No Si/Aw special-case | OK |
| `PacketCreator` CharInfo | Omit via flag | OK |
| AvatarLook `addCharEquips` | Always skips −62 (appearance) | Intentional |

## Do not

- Map 134/135 to `Si` / −10 “for compatibility”.
- Clear omit without enter-green proof (breaks A0BE14C9 login enter).
- Raw-copy client Data / patch EXE from this track.

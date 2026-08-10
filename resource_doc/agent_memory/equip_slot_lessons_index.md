# 装备槽错误/正确行为索引

戳记参考：`FIX_APPLY_SHADOW_DBL_OUTER_20260729`  |  健康向量库：`resource_doc/agent_memory/chroma` + `D:\xy_vector_db\equip_slot_chroma`（collection `equip_slot_lessons`）
会话教训：`session_lessons.jsonl` → `session_chroma` / `D:\xy_vector_db\session_chroma`
入库：`ingest_equip_slot_chroma_fresh.py`

| id | kind | summary | stamp |
|----|------|---------|-------|
| `equip_wrong_01` | wrong | ForceDrawLoop forever / DllMain early attach → login ALL_IDLE | `RING_34_BIND_SAFE_20260726ac` |
| `equip_wrong_02` | wrong | Patch pet HitTest 0x801214/0x8013A3 instead of character equip | `RING_34_BIND_SAFE_20260726ac` |
| `equip_wrong_03` | wrong | Wrong VA / corrupt ModRM (n-build) | `RING_34_BIND_SAFE_20260726ac` |
| `equip_wrong_04` | wrong | MASM [ebp-14] decimal vs PE [ebp-14h] in DrawHasItem cave → E freeze | `FIX_EQUIP_UI_HANG_20260726ab` |
| `equip_wrong_05` | wrong | Expanded (336,165) / +33 layout on classic narrow | `RING_34_BIND_SAFE_20260726ac` |
| `equip_wrong_06` | wrong | Align HitTest→Draw on right column → weapon on shield | `RING_34_BIND_SAFE_20260726ac` |
| `equip_wrong_07` | wrong | Park BP22/23 on pet coords → soft pet UI | `RING_34_BIND_SAFE_20260726ac` |
| `equip_wrong_08` | wrong | Deploy ezorsia\out stale DLL | `RING_34_BIND_SAFE_20260726ac` |
| `equip_wrong_09` | wrong | Half red-fixes without shoulder AbleToWear/TSecType pattern | `RING_34_BIND_SAFE_20260726ac` |
| `equip_wrong_10` | wrong | Unlock -53 into TSecType without shadow | `RING_34_BIND_20260726aa` |
| `equip_correct_01` | correct | Character GetBodyPartFromPoint @ 0x7FEC6F for HT extend | `RING_34_BIND_SAFE_20260726ac` |
| `equip_correct_02` | correct | Shoulder mirror: AbleToWear call-orig-then-OK + TSecType gate NOP | `RING_34_BIND_SAFE_20260726ac` |
| `equip_correct_03` | correct | Draw loop setne untouched; only add-imm 0x32→0x34 | `RING_ICON_ONESHOT_20260726z` |
| `equip_correct_04` | correct | Cave must use [ebp-14h] matching PE 8B 4D EC | `RING_34_BIND_SAFE_20260726ac` |
| `equip_correct_05` | correct | -52 native gap; -53 DLL shadow g_ring53ZRef; careful GetItem/SetItem unlock | `RING_34_BIND_SAFE_20260726ac` |
| `equip_correct_06` | correct | Byte-verify patches; deploy out\Release only | `RING_34_BIND_SAFE_20260726ac` |
| `equip_correct_07` | correct | kBindExtraRingInventory false in ab — re-enable with FIXED cave only after review | `RING_34_BIND_SAFE_20260726ac` |
| `equip_wrong_11` | wrong | draw imm 0x34 leaves BP53 unpainted when Character+0x5E8==0 | `RING_34_ONESHOT_20260726ad` |
| `equip_wrong_12` | wrong | DrawHasItem cave fixes ZF only — edi still TSecType junk | `RING_34_ONESHOT_20260726ad` |
| `equip_wrong_13` | wrong | BP52/53 skip DrawHasItem cave when esi=TSecType junk | `RING_34_ONESHOT_20260726ad` |
| `equip_correct_08` | correct | [superseded] Raise draw add-imm to 0x35 — caused BP54 hang | `RING_34_ONESHOT_20260726ad` |
| `equip_correct_09` | correct | [superseded] ForceNormal@7FEEC5 — wrong VA | `RING_34_ONESHOT_20260726ad` |
| `equip_wrong_14` | wrong | draw add-imm 0x35 + flag1 => max BP54 -> client hang | `FIX_AD_HANG_20260726ae` |
| `equip_wrong_15` | wrong | ForceNormal VA 7FEEC5 is mid-jz; real test esi at 7FEEC2 | `FIX_AD_HANG_20260726ae` |
| `equip_correct_10` | correct | Loop-end fixed max 53 via mov eax,53;nop — never BP54 | `FIX_AD_HANG_20260726ae` |
| `equip_correct_11` | correct | ForceNormal@7FEEC2 BP52/53 + DrawHasItem edi=item* + BP>53 empty | `FIX_AD_HANG_20260726ae` |
| `equip_wrong_16` | wrong | ForceNormal special path skipped mov edx,[ebp-14h] => E hang | `FIX_E_HANG_20260727af` |
| `equip_correct_12` | correct | ForceNormal special must re-emit mov edx,[ebp-14h] then 7FEECC | `FIX_E_HANG_20260727af` |
| `equip_wrong_17` | wrong | BP52 do52 must not mov edi from UI [ebp-14h] walker | `FIX_RING3_ICON_20260727ag` |
| `equip_correct_13` | correct | BP52 DrawHasItem: edi from [ebp-20h]-0x1A0 (= PE Char -52) | `FIX_RING3_ICON_20260727ag` |
| `equip_wrong_20` | wrong | Ring bag dblclick: PE pet-gate smash edi->1 -> wear BP12 replace, skip empty 52/53 | `FIX_RING_EMPTY34_20260727ai` |
| `equip_wrong_21` | wrong | Server ring route only when dst==-12 && classicFull misses other replace dsts | `FIX_RING_EMPTY34_20260727ai` |
| `equip_correct_16` | correct | Inventory ring dblclick: restore edi=6 + get_bodypart order {52,53,12,13,15,16} | `FIX_RING_EMPTY34_20260727ai` |
| `equip_correct_17` | correct | Ring equip: if dst occupied prefer empty -52/-53 then classic before replace | `FIX_RING_EMPTY34_20260727ai` |
| `equip_wrong_22` | wrong | ah unequip joined 4F0B98 mid-call imm; native after wear is 4F0B9B | `FIX_RING_UNEQUIP_PERSIST_20260727aj` |
| `equip_wrong_23` | wrong | Client apply/walk stops at BP51 so -52/-53 look gone after relog though DB saved | `FIX_RING_UNEQUIP_PERSIST_20260727aj` |
| `equip_correct_18` | correct | Equipped dblclick: jmp native single-BP wear @4F0B89; inventory keeps edi=6 empty-search | `FIX_RING_UNEQUIP_PERSIST_20260727aj` |
| `equip_wrong_24` | wrong | Raising draw via add-imm into BP54/55 without fixed mov max repeats ad hang | `BADGE_TOTEM_910_20260727ak` |
| `equip_wrong_25` | wrong | Unlock -54/-55 into native Char ZRef without shadows | `BADGE_TOTEM_910_20260727ak` |
| `equip_wrong_26` | wrong | Leaving Totem islot=Po routes to PET_EQUIP / pocket | `BADGE_TOTEM_910_20260727ak` |
| `equip_correct_19` | correct | Badge/Totem red9/10: BP54/55 fixed max55 + shadows + Accessory path 120 | `BADGE_TOTEM_910_20260727ak` |
| `equip_wrong_27` | wrong | Raise Get/Set bound to -55 before address caves install; missing cave smashes TSecType | `FIX_WEAR_PERSIST_20260727al` |
| `equip_correct_20` | correct | Install Get/Set shadow caves first and verify E9, then raise bound; draw max at DllMain | `FIX_WEAR_PERSIST_20260727al` |
| `equip_wrong_28` | wrong | Ring preferEmpty used dst<=-100 not item isCash | `FIX_RING_CRASH_20260727am` |
| `equip_wrong_29` | wrong | Cash -152/-153 use unhooked cash ZRef path; Draw reads -52/-53 | `FIX_RING_CRASH_20260727am` |
| `equip_correct_21` | correct | Early bound-site cave remaps -152→-52 -153→-53 before cash split | `FIX_RING_CRASH_20260727am` |
| `equip_note_01` | wrong | StringPool#4253 数据无效 is MARRIAGE_RESULT case 11 tip | `FIX_RING_CRASH_20260727am` |
| `equip_wrong_30` | wrong | am CashRingRemap -152/-153 on login SetItem crashes enter-map | `FIX_ENTER_INVALID_20260727an` |
| `equip_correct_22` | correct | Cash rings classic -112..; no -152 remap; migrate before getCharInfo | `FIX_ENTER_INVALID_20260727an` |
| `equip_note_02` | note | case11 tip is invitation-invalid string misused for engagement; not login | `FIX_ENTER_INVALID_20260727an` |
| `equip_wrong_31` | wrong | Rolling Client_1 back to aj for enter-map A/B loses al persist → red 3/4 look empty | `FIX_RING34_PERSIST_SAFE_20260727ap` |
| `equip_correct_23` | correct | ap = al caves-before-bound + apply max55 + an no -152 cash remap | `FIX_RING34_PERSIST_SAFE_20260727ap` |
| `equip_note_03` | note | Shoulder -20 and rings -52/-53 share DB save; only client shadow apply differs | `FIX_RING34_PERSIST_SAFE_20260727ap` |
| `equip_wrong_32` | wrong | Treat -52 Char gap as login storage while -53 uses shadow → red3 lost after relog | `FIX_RING3_RELOG_20260727as` |
| `equip_correct_24` | correct | -52 and -53 both DLL shadows; apply max55; walk stays -51 | `FIX_RING3_RELOG_20260727as` |
| `equip_note_04` | note | -52≠-53 storage historically; both shadows after as | `FIX_RING3_RELOG_20260727as` |
| `equip_wrong_33` | wrong | OccBp cave must not clobber eax (anBodyPart cursor) or ecx | `FIX_DBLCLICK_SWAP_20260727at` |
| `equip_correct_25` | correct | OccBp: push eax/ecx, cmp shadow, pop, push join;ret (keep ZF) | `FIX_DBLCLICK_SWAP_20260727at` |
| `equip_wrong_34` | wrong | Six rings all-full dblclick native reject — no replace | `FIX_DBLCLICK_SWAP_20260727at` |
| `equip_correct_26` | correct | All-full ring dblclick: rotate ecx over {52,53,12,13,15,16} → wear@4F0B89 | `FIX_RING_DBLCLICK_ROTATE_20260727au` |
| `equip_wrong_35` | wrong | HitTest coords only in pendant2 post-field → drag red3/4 fails | `FIX_EXT_SLOT_UI_RELOG_20260727av` |
| `equip_wrong_36` | wrong | Totem cash=1 stores -155; DrawHasItem reads g_totem55ZRef only | `FIX_EXT_SLOT_UI_RELOG_20260727av` |
| `equip_correct_27` | correct | Extended-slot HT+coords at DllMain; no ForceDrawLoop | `FIX_EXT_SLOT_UI_RELOG_20260727av` |
| `equip_wrong_37` | wrong | Remap -155→-55 after canWearEquipment → reject totem wear | `FIX_EXT_SLOT_UI_RELOG_20260727av` |
| `equip_note_05` | note | Shoulder/2nd pendant native ZRef; -52..-55 DLL shadow — same server save, different client store | `FIX_EXT_SLOT_UI_RELOG_20260727av` |
| `equip_correct_28` | correct | After map.addPlayer: remove+add -52..-55 to refill ijl15 shadows | `FIX_EXT_SLOT_UI_RELOG_20260727av` |
| `equip_wrong_38` | wrong | remove+add -52..-55 even after addPlayer → enter-map crash | `FIX_LOGIN_CRASH_20260727ar` |
| `equip_correct_36` | correct | Login apply lea ZRef@4E5D55→shadows; OnDoubleClick attach AFTER damageskin outermost | `FIX_APPLY_SHADOW_DBL_OUTER_20260729` |
| `equip_wrong_39` | wrong | Writing HitTest index 51+ into PE BE2260 overflows into cash coord table BE23F0 | `FIX_HT_CASH_OVERFLOW_20260728` |
| `equip_correct_37` | correct | Independent 56-slot HitTest table in DLL; restore cash defaults; stop PE write >=50 | `FIX_HT_CASH_OVERFLOW_20260728` |
| `equip_wrong_40` | wrong | Deploying CharacterData-expanded cd64 EXE to live Client_1 causes enter-map E_POINTER | `NATIVE_CD64_20260728` |
| `equip_correct_38` | correct | Live Client_1 stays vanilla EXE + STABLE_SHADOW rings; cd64 only in test copies | `NATIVE_CD64_20260728` |
| `equip_wrong_41` | wrong | Assuming login getCharInfo apply always hits SetItem caves — cold start empties -52..-55 | `FIX_APPLY_SHADOW_DBL_OUTER_20260729` |
| `equip_wrong_42` | wrong | DllMain DetourAttach OnDoubleClick then damageskin re-attach orphans unequip hook | `FIX_APPLY_SHADOW_DBL_OUTER_20260729` |

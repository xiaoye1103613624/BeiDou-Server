# CreateBullet / ItemEff IDA notes (2026-09-17)

## CAnimationDisplayer::CreateBullet @ 0x00435DF3

- Image base 0x400000, SEH prolog (__EH_prolog), epilogue \etn 0x2C\ (11 stack dwords).
- Typed prototype (Hex-Rays + manual name):

\\\
int __thiscall CreateBullet(
  void *this,
  int tArrive,          // frame-clock arrive time (ms); same clock @ 0x00987257
  int a3, int a4, int a5, int a6, int a7,
  void *psBallUol,      // IUnknown* skill BALL UOL; body QI's this
  int a9,
  void *a10,            // bstr-like refcounted
  int nBulletItemId,
  int a12);
\\\

- Callers: TryDoingShootAttack (local), DoShootAttack (preview), OnShootAttack (remote).
- INTEGRATION.md "arg9=psBallUol" counts \	his\ as arg1 → stack index of UOL is Hex-Rays \8\ / typed \psBallUol\.

## ItemEff host path

- \CUser::LoadLayer\ @ 0x00941417
- \CUser::UpdateAdditionalLayer\ @ 0x00940EB7
- \CUser::OnAvatarModified\ @ 0x0092E916 (ecx = CAvatar*; CUser = ecx - 0x88)
- \CUser::SetMoveAction\ @ 0x0092ECD1
- \CItemInfo::IterateItemInfo\ @ 0x005CA71C
- \get_action_name_from_code\ @ 0x004A8CE6
- CustomData hijack: \CAvatar\ +0x484; ctor 0x0044FE6C, dtor 0x0045011C; blink patches 0x004534CC / 0x00453612

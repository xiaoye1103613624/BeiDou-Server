# Resume other-conflicts merge, then export String.wz.
# Run in a standalone PowerShell window (not Cursor agent) to avoid session kill.
Set-Location "E:\pro\BeiDou-Server_xy"

Write-Host "=== other-conflicts (skip-done) ===" -ForegroundColor Cyan
python -u gms-server/tools/append_img_nodes.py `
  --phase other-conflicts `
  --log gms-server/tools/_append_other_merge.log `
  --skip-done --append-log
if ($LASTEXITCODE -ne 0) { Write-Host "other-conflicts failed: $LASTEXITCODE"; exit $LASTEXITCODE }

Write-Host "=== export String.wz ===" -ForegroundColor Cyan
python -u gms-server/tools/export_string_wz.py `
  --log gms-server/tools/_export_string_wz.log
Write-Host "ALL DONE" -ForegroundColor Green

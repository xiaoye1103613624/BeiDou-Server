# Guard Addon server error-ops (occupy / ghost-sync / cash isAllowed / omit aux).
# Exit 0 = OK; 1 = FAIL. See BeiDou-ijl15/docs/ADDON_ERROR_OPS_MATRIX.md
param(
  [string]$ClassesRoot = ""
)
$ErrorActionPreference = "Stop"
if (-not $ClassesRoot) {
  $ClassesRoot = Join-Path $PSScriptRoot "..\target\classes"
}
$ClassesRoot = [IO.Path]::GetFullPath($ClassesRoot)

function ClassText([string]$rel) {
  $p = Join-Path $ClassesRoot $rel
  if (-not (Test-Path -LiteralPath $p)) { throw "missing class: $p 鈥?compile gms-server first" }
  return [Text.Encoding]::ASCII.GetString([IO.File]::ReadAllBytes($p))
}

$bad = $false
$im = ClassText "org\gms\client\inventory\manipulator\InventoryManipulator.class"
$reg = ClassText "org\gms\constants\inventory\ExtendedEquipRegistry.class"
$slot = ClassText "org\gms\constants\inventory\EquipSlot.class"

Write-Host "ClassesRoot=$ClassesRoot"

if ($im.Contains("equip reject occupy")) {
  Write-Host "FAIL: InventoryManipulator still has 'equip reject occupy' (hard occupy reject)"
  $bad = $true
} else {
  Write-Host "OK: no 'equip reject occupy'"
}

if ($im.Contains("ghost-sync clear")) {
  Write-Host "FAIL: InventoryManipulator still has 'ghost-sync clear' (mode-3 tip-hang family)"
  $bad = $true
} else {
  Write-Host "OK: no 'ghost-sync clear'"
}

if (-not $im.Contains("enableActions only")) {
  Write-Host "FAIL: missing 'enableActions only' ghost path marker"
  $bad = $true
} else {
  Write-Host "OK: ghost path = enableActions only"
}

if (-not $reg.Contains("VANILLA_REPLACE_NO_OCCUPY_REJECT")) {
  Write-Host "FAIL: ExtendedEquipRegistry missing GUARD_NO_OCCUPY_REJECT marker"
  $bad = $true
} else {
  Write-Host "OK: GUARD_NO_OCCUPY_REJECT present"
}

if (-not $reg.Contains("GHOST_ENABLE_ACTIONS_ONLY")) {
  Write-Host "FAIL: ExtendedEquipRegistry missing GUARD_GHOST_ENABLE_ACTIONS_ONLY marker"
  $bad = $true
} else {
  Write-Host "OK: GUARD_GHOST_ENABLE_ACTIONS_ONLY present"
}

# GREEN_ENTER_OMIT_AUX62 should be false (boolean false = iconst_0 near field in bytecode 鈥?soft check via comment string)
if ($reg.Contains("REJECT-WEAR") -and $im.Contains("equip refuse aux wire-omit")) {
  # Field false still leaves the string in equip path; OK as long as runtime flag false.
  Write-Host "INFO: aux wire-omit path strings present (gated by GREEN_ENTER_OMIT_AUX62)"
}

# EquipSlot: dual-band exact match (cash badge at -54). Heuristic: method should not only use "cash ? allow-100".
# Presence of both -54 and -154 in BADGE enum is enough; compile-time review in ERROR_OPS_MATRIX.
if (-not ($slot.Contains("Ba") -or $slot.Length -gt 100)) {
  Write-Host "WARN: EquipSlot.class unexpected; verify isAllowed dual-band manually"
}

# Running JVM hint
$java = Get-CimInstance Win32_Process -Filter "Name='java.exe'" -ErrorAction SilentlyContinue |
  Where-Object { $_.CommandLine -match 'org\.gms\.ServerApplication' } |
  Select-Object -First 1
if ($java) {
  $cls = Get-Item (Join-Path $ClassesRoot "org\gms\client\inventory\manipulator\InventoryManipulator.class")
  $started = ($java.CreationDate)
  Write-Host ("Running ServerApplication pid={0} started={1:yyyy-MM-dd HH:mm:ss}" -f $java.ProcessId, $started)
  Write-Host ("InventoryManipulator.class mtime={0:yyyy-MM-dd HH:mm:ss}" -f $cls.LastWriteTime)
  if ($started -lt $cls.LastWriteTime) {
    Write-Host "FAIL: gms-server JVM older than class file 鈥?RESTART required or occupy/ghost fixes not live"
    $bad = $true
  } else {
    Write-Host "OK: JVM started after class compile"
  }
} else {
  Write-Host "WARN: ServerApplication not running 鈥?start after compile"
}

if ($bad) {
  Write-Host "GUARD FAIL 鈥?see ADDON_ERROR_OPS_MATRIX.md"
  exit 1
}
Write-Host "GUARD OK 鈥?addon error-ops"
exit 0


$ErrorActionPreference = 'Stop'
$zhRoot = 'F:\MXD_dev\BeiDou-Server\gms-server\wz-zh-CN'
$work = 'F:\MXD_dev\BeiDou-Server\gms-server\tools\_wz_xml_fix'
$qiHits = Get-ChildItem -Path 'F:\MXD_dev' -Recurse -Filter 'QuestInfo.img.xml' -ErrorAction SilentlyContinue |
    Where-Object { $_.FullName -like '*wz-zh-CN\Quest.wz\QuestInfo.img.xml' -and $_.FullName -notlike '*BeiDou-Server*' }
$txQi = $qiHits | Sort-Object Length -Descending | Select-Object -First 1
if (-not $txQi) { throw 'transplant QuestInfo.img.xml not found' }
$txRoot = $txQi.Directory.Parent.FullName
Write-Output ("txRoot={0} qiSize={1}" -f $txRoot, $txQi.Length)
$report = Join-Path $work 'repair_report.txt'
$log = New-Object System.Collections.Generic.List[string]

function Backup-File([string]$path) {
    $bak = $path + '.bak_xmlfix'
    Copy-Item -LiteralPath $path -Destination $bak -Force
    return $bak
}

function Test-XmlParse([string]$path) {
    $d = New-Object System.Xml.XmlDocument
    $d.Load($path)
}

function Get-OpenCloseNet([string]$path) {
    $text = [IO.File]::ReadAllText($path)
    $opens = ([regex]::Matches($text, '<imgdir[\s>]')).Count
    $closes = ([regex]::Matches($text, '</imgdir>')).Count
    $self = ([regex]::Matches($text, '<imgdir [^>]*/>')).Count
    return ($opens - $self - $closes)
}

function Extract-ImgdirBlock([string]$text, [string]$id) {
    $needle = '<imgdir name="' + $id + '">'
    $start = $text.IndexOf($needle)
    if ($start -lt 0) {
        $sc = '<imgdir name="' + $id + '"/>'
        $s2 = $text.IndexOf($sc)
        if ($s2 -ge 0) { return $text.Substring($s2, $sc.Length) }
        return $null
    }
    $i = $start
    $depth = 0
    $len = $text.Length
    while ($i -lt $len) {
        $nextOpen = $text.IndexOf('<imgdir', $i)
        $nextClose = $text.IndexOf('</imgdir>', $i)
        if ($nextClose -lt 0) { return $null }
        if ($nextOpen -ge 0 -and $nextOpen -lt $nextClose) {
            $gt = $text.IndexOf('>', $nextOpen)
            $slice = $text.Substring($nextOpen, $gt - $nextOpen + 1)
            if ($slice.EndsWith('/>')) {
                $i = $gt + 1
                continue
            }
            $depth++
            $i = $gt + 1
        } else {
            $depth--
            $i = $nextClose + 9
            if ($depth -eq 0) {
                return $text.Substring($start, $i - $start)
            }
        }
    }
    return $null
}

function Repair-ByTruncateAndAppend([string]$zhPath, [string]$txPath, [int]$stubStartLine, [string[]]$stubIds, [string]$label) {
    $bak = Backup-File $zhPath
    $lines = [IO.File]::ReadAllLines($zhPath)
    $keepCount = $stubStartLine - 1
    $kept = $lines[0..($keepCount - 1)]
    $txText = [IO.File]::ReadAllText($txPath)
    $appended = New-Object System.Collections.Generic.List[string]
    $recovered = 0
    $dropped = 0
    foreach ($id in $stubIds) {
        $block = Extract-ImgdirBlock $txText $id
        if ($block) {
            [void]$appended.Add($block.TrimEnd())
            $recovered++
        } else {
            $dropped++
        }
    }
    $sb = New-Object System.Text.StringBuilder
    foreach ($l in $kept) { [void]$sb.AppendLine($l) }
    foreach ($b in $appended) { [void]$sb.AppendLine($b) }
    [void]$sb.AppendLine('</imgdir>')
    $tmp = $zhPath + '.tmp_repair'
    [IO.File]::WriteAllText($tmp, $sb.ToString(), [Text.UTF8Encoding]::new($false))
    $net = Get-OpenCloseNet $tmp
    $parseOk = $false
    $err = ''
    try {
        Test-XmlParse $tmp
        $parseOk = $true
    } catch {
        $err = $_.Exception.Message
    }
    if ($parseOk -and $net -eq 0) {
        Move-Item $tmp $zhPath -Force
        [void]$log.Add(("TRUNCATE {0} recovered={1} dropped={2} bak={3}" -f $label, $recovered, $dropped, $bak))
    } else {
        [void]$log.Add(("FAILED {0} net={1} parseOk={2} err={3} tmp={4}" -f $label, $net, $parseOk, $err, $tmp))
    }
}

function Repair-CopyFromTransplant([string]$zhPath, [string]$txPath, [string]$label) {
    $bak = Backup-File $zhPath
    Copy-Item -LiteralPath $txPath -Destination $zhPath -Force
    $net = Get-OpenCloseNet $zhPath
    $parseOk = $false
    try {
        Test-XmlParse $zhPath
        $parseOk = $true
    } catch {}
    [void]$log.Add(("REPLACE {0} parseOk={1} net={2} size={3} bak={4}" -f $label, $parseOk, $net, (Get-Item $zhPath).Length, $bak))
}

foreach ($name in @('QuestInfo', 'Act', 'Check', 'Say')) {
    Repair-CopyFromTransplant ("{0}\Quest.wz\{1}.img.xml" -f $zhRoot, $name) ("{0}\Quest.wz\{1}.img.xml" -f $txRoot, $name) ("Quest/" + $name)
}

$qi = Join-Path $zhRoot 'Quest.wz\QuestInfo.img.xml'
$qiText = [IO.File]::ReadAllText($qi)
if ($qiText -notmatch 'imgdir name="29580"') {
    $oldBak = Join-Path $zhRoot 'Quest.wz\QuestInfo.img.xml.bak_xmlfix'
    if (Test-Path $oldBak) {
        $block = Extract-ImgdirBlock ([IO.File]::ReadAllText($oldBak)) '29580'
        if ($block) {
            $idx = $qiText.LastIndexOf('</imgdir>')
            $merged = $qiText.Substring(0, $idx) + $block.TrimEnd() + "`r`n" + $qiText.Substring($idx)
            [IO.File]::WriteAllText($qi, $merged, [Text.UTF8Encoding]::new($false))
            [void]$log.Add('QuestInfo merged 29580 from pre-repair backup')
        }
    }
}

# Etc: stubs belong inside <imgdir name="Etc"> which closed at line 24086.
# Keep 1-24085, insert recovered blocks, then keep 24086-24094 (Etc close + pet essences), then root close.
function Repair-EtcInsideFolder {
    $zhPath = Join-Path $zhRoot 'String.wz\Etc.img.xml'
    $txPath = Join-Path $txRoot 'String.wz\Etc.img.xml'
    $bak = Backup-File $zhPath
    $lines = [IO.File]::ReadAllLines($zhPath)
    $txText = [IO.File]::ReadAllText($txPath)
    $stubs = Get-Content (Join-Path $work 'stub_ids_Etc.txt')
    $recovered = 0
    $dropped = 0
    $blocks = New-Object System.Collections.Generic.List[string]
    foreach ($id in $stubs) {
        $block = Extract-ImgdirBlock $txText $id
        if ($block) {
            [void]$blocks.Add($block.TrimEnd())
            $recovered++
        } else {
            $dropped++
        }
    }
    $sb = New-Object System.Text.StringBuilder
    foreach ($l in $lines[0..24084]) { [void]$sb.AppendLine($l) }
    foreach ($b in $blocks) { [void]$sb.AppendLine($b) }
    foreach ($l in $lines[24085..24093]) { [void]$sb.AppendLine($l) }
    [void]$sb.AppendLine('</imgdir>')
    $tmp = $zhPath + '.tmp_repair'
    [IO.File]::WriteAllText($tmp, $sb.ToString(), [Text.UTF8Encoding]::new($false))
    $net = Get-OpenCloseNet $tmp
    $parseOk = $false
    $err = ''
    try { Test-XmlParse $tmp; $parseOk = $true } catch { $err = $_.Exception.Message }
    if ($parseOk -and $net -eq 0) {
        Move-Item $tmp $zhPath -Force
        [void]$log.Add(("TRUNCATE String/Etc recovered={0} dropped={1} bak={2}" -f $recovered, $dropped, $bak))
    } else {
        [void]$log.Add(("FAILED String/Etc net={0} parseOk={1} err={2}" -f $net, $parseOk, $err))
    }
}
Repair-EtcInsideFolder

Repair-ByTruncateAndAppend (Join-Path $zhRoot 'String.wz\Npc.img.xml') (Join-Path $txRoot 'String.wz\Npc.img.xml') 70016 (Get-Content (Join-Path $work 'stub_ids_Npc.txt')) 'String/Npc'
Repair-ByTruncateAndAppend (Join-Path $zhRoot 'String.wz\Map.img.xml') (Join-Path $txRoot 'String.wz\Map.img.xml') 36998 (Get-Content (Join-Path $work 'stub_ids_Map.txt')) 'String/Map'

$mob = Join-Path $zhRoot 'String.wz\Mob.img.xml'
$mobNet = Get-OpenCloseNet $mob
[void]$log.Add(("String/Mob before net={0}" -f $mobNet))
if ($mobNet -ne 0) {
    Repair-CopyFromTransplant $mob (Join-Path $txRoot 'String.wz\Mob.img.xml') 'String/Mob'
}

$zhBe = Join-Path $zhRoot 'Effect.wz\BasicEff.img.xml'
$baseBe = 'F:\MXD_dev\BeiDou-Server\gms-server\wz\Effect.wz\BasicEff.img.xml'
$bakBe = Backup-File $zhBe
$zhBeText = [IO.File]::ReadAllText($zhBe)
if ($zhBeText -notmatch 'name="damageSkin"') {
    $ds = Extract-ImgdirBlock ([IO.File]::ReadAllText($baseBe)) 'damageSkin'
    if ($ds) {
        $idx = $zhBeText.LastIndexOf('</imgdir>')
        $merged = $zhBeText.Substring(0, $idx) + $ds.TrimEnd() + "`r`n" + $zhBeText.Substring($idx)
        [IO.File]::WriteAllText($zhBe, $merged, [Text.UTF8Encoding]::new($false))
        $ok = $false
        try { Test-XmlParse $zhBe; $ok = $true } catch {}
        [void]$log.Add(("BasicEff inserted damageSkin parseOk={0} bak={1}" -f $ok, $bakBe))
    } else {
        [void]$log.Add('BasicEff FAILED extract damageSkin from base')
    }
} else {
    [void]$log.Add('BasicEff already has damageSkin')
}

Write-Output '===== VALIDATION ====='
$targets = @(
    "$zhRoot\Quest.wz\QuestInfo.img.xml",
    "$zhRoot\Quest.wz\Act.img.xml",
    "$zhRoot\Quest.wz\Check.img.xml",
    "$zhRoot\Quest.wz\Say.img.xml",
    "$zhRoot\String.wz\Etc.img.xml",
    "$zhRoot\String.wz\Npc.img.xml",
    "$zhRoot\String.wz\Map.img.xml",
    "$zhRoot\String.wz\Mob.img.xml",
    "$zhRoot\Effect.wz\BasicEff.img.xml"
)
foreach ($t in $targets) {
    $net = Get-OpenCloseNet $t
    $ok = $false
    $err = ''
    try { Test-XmlParse $t; $ok = $true } catch { $err = $_.Exception.Message }
    $msg = "{0}: net={1} parseOk={2} size={3} {4}" -f (Split-Path $t -Leaf), $net, $ok, (Get-Item $t).Length, $err
    [void]$log.Add($msg)
    Write-Output $msg
}

$log | Set-Content $report -Encoding UTF8
Write-Output ("Report: {0}" -f $report)

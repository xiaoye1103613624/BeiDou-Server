import ctypes, json, os, shutil, subprocess, time
from ctypes import wintypes
CLIENT = r"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1"
ORIG = r"E:\mxd_soft\2.客户端\083\BeiDou-Clinet_原版未动"
LOG = r"E:\pro\BeiDou-Server_xy\gms-server\tools\_bisect_logs\matrix_results.jsonl"
ASSETS = {"ultra_dll": os.path.join(CLIENT, "ijl15.ultra.dll"), "full_dll": os.path.join(CLIENT, "ijl15.full.dll"), "orig_be": os.path.join(ORIG, "Data", "Effect", "BasicEff.img"), "orig_ui": os.path.join(ORIG, "Data", "UI", "UIWindow.img"), "merged_ui": os.path.join(CLIENT, "Data", "UI", "UIWindow.merged.img"), "dmg_skin": os.path.join(CLIENT, "Data", "Effect", "DamageSkin.img")}
TARGETS = {"dll": os.path.join(CLIENT, "ijl15.dll"), "be": os.path.join(CLIENT, "Data", "Effect", "BasicEff.img"), "ui": os.path.join(CLIENT, "Data", "UI", "UIWindow.img"), "dmg": os.path.join(CLIENT, "Data", "Effect", "DamageSkin.img")}
SNAPSHOT = os.path.join(CLIENT, "_matrix_bisect_snapshot")
user32 = ctypes.windll.user32
EnumWindowsProc = ctypes.WINFUNCTYPE(wintypes.BOOL, wintypes.HWND, wintypes.LPARAM)
def kill():
    subprocess.run(["taskkill", "/F", "/T", "/IM", "BeiDou.exe"], capture_output=True); time.sleep(3)
def scopy(src, dst):
    os.makedirs(os.path.dirname(dst) or ".", exist_ok=True)
    for _ in range(5):
        try:
            if os.path.exists(dst): os.chmod(dst, 0o666); os.remove(dst)
            break
        except OSError: time.sleep(1)
    else:
        tmp = dst + ".tmp"; shutil.copy2(src, tmp); os.replace(tmp, dst); return
    shutil.copy2(src, dst)
def snapshot():
    os.makedirs(SNAPSHOT, exist_ok=True)
    for key, path in TARGETS.items():
        if os.path.exists(path): scopy(path, os.path.join(SNAPSHOT, key))
def restore_snapshot():
    for key, path in TARGETS.items():
        snap = os.path.join(SNAPSHOT, key)
        if os.path.exists(snap): scopy(snap, path)
        elif key == "dmg" and os.path.exists(path): os.remove(path)
def scan(pid):
    out = []; h = ctypes.windll.kernel32.OpenProcess(0x1000, False, pid); alive = bool(h)
    if h: ctypes.windll.kernel32.CloseHandle(h)
    def ec(hwnd, _):
        b = ctypes.create_unicode_buffer(4096); user32.SendMessageW(hwnd, 0x000D, 4096, b); c = ctypes.create_unicode_buffer(256); user32.GetClassNameW(hwnd, c, 256)
        if b.value.strip(): out.append("CHILD[%s]: %s" % (c.value, b.value.strip()))
        return True
    def et(hwnd, _):
        p = wintypes.DWORD(); user32.GetWindowThreadProcessId(hwnd, ctypes.byref(p))
        if p.value != pid: return True
        c = ctypes.create_unicode_buffer(256); user32.GetClassNameW(hwnd, c, 256); t = ctypes.create_unicode_buffer(512); user32.GetWindowTextW(hwnd, t, 512)
        out.append("WIN[%s] title=%s" % (c.value, t.value)); user32.EnumChildWindows(hwnd, EnumWindowsProc(ec), 0); return True
    user32.EnumWindows(EnumWindowsProc(et), 0); return out, alive
def launch():
    return subprocess.Popen([os.path.join(CLIENT, "BeiDou.exe")], cwd=CLIENT).pid
def apply_case(case):
    kill(); restore_snapshot(); kill()
    scopy(ASSETS["ultra_dll" if case["dll"]=="ultra" else "full_dll"], TARGETS["dll"])
    scopy(ASSETS["orig_be"], TARGETS["be"])
    scopy(ASSETS["orig_ui" if case["ui"]=="orig" else "merged_ui"], TARGETS["ui"])
    if case["dmg"]:
        if not os.path.exists(TARGETS["dmg"]): scopy(ASSETS["dmg_skin"], TARGETS["dmg"])
    elif os.path.exists(TARGETS["dmg"]): os.remove(TARGETS["dmg"])
def test_case(label, case, wait=10):
    apply_case(case); kill(); pid = launch(); time.sleep(wait); texts, alive = scan(pid)
    comb = "\n".join(texts); efail = any(k in comb.upper() for k in ("E_FAIL", "80004005", "0X80004005")); kill()
    sizes = {k: os.path.getsize(TARGETS[k]) if os.path.exists(TARGETS[k]) else 0 for k in TARGETS}
    status = "EFAIL" if efail else ("DEAD" if not alive else "PASS")
    row = {"label": label, "case": case, "status": status, "alive": alive, "efail": efail, "sizes": sizes}
    print("\n=== %s === %s alive=%s dll=%d be=%d ui=%d dmg=%d" % (label, status, alive, sizes["dll"], sizes["be"], sizes["ui"], sizes["dmg"]))
    for ln in texts:
        if any(k in ln.upper() for k in ("E_FAIL", "80004005", "STATIC", "失败", "ERROR", "MAPLE")): print("  " + ln)
    os.makedirs(os.path.dirname(LOG), exist_ok=True)
    with open(LOG, "a", encoding="utf-8") as f: f.write(json.dumps(row, ensure_ascii=False) + "\n")
    return row
CASES = [("A_ultra_current_imgs", {"dll":"ultra","be":"orig","ui":"merged","dmg":True}), ("B_full_origUI_dmg_origBE", {"dll":"full","be":"orig","ui":"orig","dmg":True}), ("C_full_mergedUI_dmg_origBE", {"dll":"full","be":"orig","ui":"merged","dmg":True}), ("D_ultra_mergedUI_only", {"dll":"ultra","be":"orig","ui":"merged","dmg":False}), ("E_ultra_dmg_only", {"dll":"ultra","be":"orig","ui":"orig","dmg":True})]
if __name__ == "__main__":
    kill(); snapshot(); results=[]
    for label, case in CASES:
        results.append(test_case(label, case, wait=10)); time.sleep(2)
    print("\n=== MATRIX SUMMARY ===")
    for r in results: print("  %s: %s" % (r["label"], r["status"]))

#!/usr/bin/env python3
"""
将客户端 Data/*.img 同步到 gms-server/wz-zh-CN（服务端 gameplay 所需 WZ）。

依据 org.gms.provider.wz.WZFiles 枚举，服务端会读取：
  Quest, Etc, Effect, Item, Character, String, Mob, Map, Npc, Reactor, Skill, UI

不同步（服务端不读 / 纯客户端）：Sound, Morph, TamingMob, Base

并发策略：
  - 多分类并行调用 BatchImg2Xml（每类独立 JVM + 内部线程池）
  - Item 先多线程 stage（排除 XY 补丁文件）再转换
  - 转换完成后恢复 0243/0246 补丁 XML
"""

from __future__ import annotations

import argparse
import shutil
import subprocess
import threading
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path

DEFAULT_CLIENT = Path(r"E:\mxd_soft\2.客户端\083\BeiDou-Client\Data")
DEFAULT_SERVER = Path(__file__).resolve().parents[1] / "wz-zh-CN"
ORANGE_CLASSES = Path(r"E:\pro\orange-wz\target\classes")
ORANGE_DEPS = Path(r"E:\pro\orange-wz\target\dependency")
STAGING = Path(__file__).resolve().parent / "_wz_zh_cn_sync_staging"
LOG_FILE = Path(__file__).resolve().parent / "_wz_zh_cn_sync.log"
PATCH_DIR = Path(r"E:\pro\orange-wz")

# client Data 顶层目录 -> 服务端 wz-zh-CN 子目录
SERVER_WZ_CATEGORIES: dict[str, str] = {
    "String": "String.wz",
    "Item": "Item.wz",
    "Character": "Character.wz",
    "Map": "Map.wz",
    "Mob": "Mob.wz",
    "Npc": "Npc.wz",
    "Skill": "Skill.wz",
    "Etc": "Etc.wz",
    "Quest": "Quest.wz",
    "Reactor": "Reactor.wz",
    "Effect": "Effect.wz",
    "UI": "UI.wz",
}

# 客户端有、但服务端 WZFiles 不使用的目录
SKIP_CLIENT_DIRS = frozenset({"Sound", "Morph", "TamingMob", "Base"})

# XY 快捷道具由 patch_xy_shortcut_items.py 维护，批量同步时跳过
EXCLUDE_ITEM_FILES = frozenset({"0243.img", "0246.img"})

PATCH_ITEM_XML = {
    "0243.img.xml": PATCH_DIR / "0243_xy_shortcut.img.xml",
    "0246.img.xml": PATCH_DIR / "0246_xy_shortcut.img.xml",
}
PATCH_STRING_XML = PATCH_DIR / "String_Consume_xy_shortcut.img.xml"

_log_lock = threading.Lock()


def log(msg: str) -> None:
    line = f"[{datetime.now():%Y-%m-%d %H:%M:%S}] {msg}"
    with _log_lock:
        try:
            print(line)
        except UnicodeEncodeError:
            print(line.encode("utf-8", errors="replace").decode("utf-8", errors="replace"))
        with LOG_FILE.open("a", encoding="utf-8") as f:
            f.write(line + "\n")


def java_cp() -> str:
    return f"{ORANGE_CLASSES};{ORANGE_DEPS / '*'}"


@dataclass
class CategoryResult:
    category: str
    staged: int
    success: int
    failure: int
    elapsed: float
    error: str = ""


def count_imgs(client_data: Path, category: str) -> int:
    root = client_data / category
    if not root.exists():
        return 0
    if category == "Item":
        return sum(
            1 for p in root.rglob("*.img") if p.name not in EXCLUDE_ITEM_FILES
        )
    return sum(1 for _ in root.rglob("*.img"))


def stage_item_parallel(client_data: Path, workers: int) -> int:
    """多线程复制 Item（排除补丁文件）到 staging/Item。"""
    src_root = client_data / "Item"
    if not src_root.exists():
        return 0

    jobs: list[tuple[Path, Path]] = []
    for src in src_root.rglob("*.img"):
        if src.name in EXCLUDE_ITEM_FILES:
            continue
        rel = src.relative_to(client_data)
        jobs.append((src, STAGING / rel))

    if not jobs:
        return 0

    done = 0

    def _copy(pair: tuple[Path, Path]) -> None:
        src, dst = pair
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src, dst)

    with ThreadPoolExecutor(max_workers=workers) as pool:
        futures = [pool.submit(_copy, job) for job in jobs]
        for fut in as_completed(futures):
            fut.result()
            done += 1
            if done % 2000 == 0:
                log(f"[STAGE Item] {done}/{len(jobs)}")

    return done


def run_batch_img2xml(src_dir: Path, dst_dir: Path, category: str) -> tuple[int, int, str]:
    dst_dir.mkdir(parents=True, exist_ok=True)
    cmd = [
        "java",
        "-cp",
        java_cp(),
        "orange.wz.BatchImg2Xml",
        str(src_dir),
        str(dst_dir),
    ]
    t0 = time.time()
    try:
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            timeout=7200,
        )
    except subprocess.TimeoutExpired:
        return 0, 0, f"{category}: BatchImg2Xml timeout"

    elapsed = time.time() - t0
    stdout = result.stdout or ""
    stderr = result.stderr or ""

    success = stdout.count("[OK]")
    failure = stdout.count("[FAIL]")
    if result.returncode != 0 and success == 0:
        tail = (stderr or stdout).strip()[-500:]
        return success, failure, f"{category}: exit {result.returncode} {tail}"

    log(
        f"[CONVERT {category}] ok={success} fail={failure} "
        f"elapsed={elapsed:.1f}s -> {dst_dir}"
    )
    return success, failure, ""


def install_one_xml(src: Path, dst: Path, retries: int = 3) -> None:
    dst.parent.mkdir(parents=True, exist_ok=True)
    last_err: Exception | None = None
    for attempt in range(retries):
        try:
            tmp = dst.with_suffix(dst.suffix + ".tmp")
            shutil.copy2(src, tmp)
            tmp.replace(dst)
            return
        except OSError as exc:
            last_err = exc
            time.sleep(0.05 * (attempt + 1))
    if last_err is not None:
        raise last_err


def install_converted_xml(xml_root: Path, server_wz: Path, category: str, workers: int) -> int:
    """将 BatchImg2Xml 输出安装到 wz-zh-CN/{Category}.wz/（多线程复制，小目录单线程）。"""
    wz_name = SERVER_WZ_CATEGORIES[category]
    target_root = server_wz / wz_name

    jobs: list[tuple[Path, Path]] = []
    for xml in xml_root.rglob("*.img.xml"):
        rel = xml.relative_to(xml_root)
        jobs.append((xml, target_root / rel))

    if not jobs:
        return 0

    # 小目录或 Quest 等同名根文件：避免 Windows 上多线程 copy2 偶发 Errno 22
    pool_workers = 1 if len(jobs) <= 64 else workers
    done = 0

    def _install(pair: tuple[Path, Path]) -> None:
        install_one_xml(pair[0], pair[1])

    with ThreadPoolExecutor(max_workers=pool_workers) as pool:
        futures = [pool.submit(_install, job) for job in jobs]
        for fut in as_completed(futures):
            fut.result()
            done += 1
            if done % 5000 == 0:
                log(f"[INSTALL {category}] {done}/{len(jobs)}")

    return done


def convert_category(
    category: str,
    client_data: Path,
    server_wz: Path,
    workers: int,
) -> CategoryResult:
    t0 = time.time()
    staged = 0
    try:
        if category == "Item":
            staged = stage_item_parallel(client_data, workers)
            src_dir = STAGING / "Item"
            xml_out = STAGING / "_xml_out" / "Item"
        else:
            src_dir = client_data / category
            if not src_dir.exists():
                return CategoryResult(category, 0, 0, 0, 0.0, "missing")
            staged = count_imgs(client_data, category)
            xml_out = STAGING / "_xml_out" / category

        if staged == 0 and category == "Item":
            return CategoryResult(category, 0, 0, 0, time.time() - t0, "empty")

        ok, fail, err = run_batch_img2xml(src_dir, xml_out, category)
        if err:
            return CategoryResult(category, staged, ok, fail, time.time() - t0, err)

        installed = install_converted_xml(xml_out, server_wz, category, workers)
        log(f"[INSTALL {category}] total={installed}")
        return CategoryResult(category, staged, ok, fail, time.time() - t0)
    except Exception as exc:
        return CategoryResult(category, staged, 0, 0, time.time() - t0, str(exc))


def restore_xy_patch(server_wz: Path) -> None:
    """批量同步后恢复 XY 快捷道具补丁（内嵌 canvas 版）。"""
    item_dir = server_wz / "Item.wz" / "Consume"
    for name, src in PATCH_ITEM_XML.items():
        if not src.exists():
            log(f"[PATCH WARN] missing {src}")
            continue
        dst = item_dir / name
        shutil.copy2(src, dst)
        log(f"[PATCH] restored {dst}")

    if PATCH_STRING_XML.exists():
        dst = server_wz / "String.wz" / "Consume.img.xml"
        shutil.copy2(PATCH_STRING_XML, dst)
        log(f"[PATCH] restored {dst}")


def main() -> None:
    ap = argparse.ArgumentParser(description="Sync client Data to server wz-zh-CN (full WZ)")
    ap.add_argument("--client", type=Path, default=DEFAULT_CLIENT)
    ap.add_argument("--server", type=Path, default=DEFAULT_SERVER)
    ap.add_argument(
        "--categories",
        default=",".join(SERVER_WZ_CATEGORIES.keys()),
        help="Comma-separated categories (default: all server-required)",
    )
    ap.add_argument(
        "--workers",
        type=int,
        default=max(8, min(32, (__import__("os").cpu_count() or 8) * 2)),
        help="Thread pool size for stage/install",
    )
    ap.add_argument(
        "--parallel-categories",
        type=int,
        default=3,
        help="How many BatchImg2Xml JVM processes run in parallel",
    )
    ap.add_argument("--skip-patch-restore", action="store_true")
    args = ap.parse_args()

    if not args.client.exists():
        raise SystemExit(f"Client Data not found: {args.client}")
    if not ORANGE_CLASSES.exists():
        raise SystemExit(f"orange-wz not built: {ORANGE_CLASSES}")

    categories = [c.strip() for c in args.categories.split(",") if c.strip()]
    unknown = [c for c in categories if c not in SERVER_WZ_CATEGORIES]
    if unknown:
        raise SystemExit(f"Unknown categories: {unknown}")

    if STAGING.exists():
        shutil.rmtree(STAGING)
    STAGING.mkdir(parents=True, exist_ok=True)
    LOG_FILE.write_text("", encoding="utf-8")

    log(
        f"[START] client={args.client} server={args.server} "
        f"categories={categories} workers={args.workers} "
        f"parallel_jvm={args.parallel_categories}"
    )

    # 预估文件量
    for cat in categories:
        log(f"[PLAN] {cat}: ~{count_imgs(args.client, cat)} img")

    t0 = time.time()
    results: list[CategoryResult] = []

    # 大目录单独跑，避免多个巨型 JVM 同时占用内存
    heavy = {"Character", "Map", "Npc", "Mob"}
    light = [c for c in categories if c not in heavy]
    heavy_present = [c for c in categories if c in heavy]

    def run_batch(cats: list[str], parallel: int) -> None:
        with ThreadPoolExecutor(max_workers=parallel) as pool:
            futs = {
                pool.submit(convert_category, cat, args.client, args.server, args.workers): cat
                for cat in cats
            }
            for fut in as_completed(futs):
                cat = futs[fut]
                res = fut.result()
                results.append(res)
                if res.error:
                    log(f"[ERROR {cat}] {res.error}")
                else:
                    log(
                        f"[DONE {cat}] staged={res.staged} convert_ok={res.success} "
                        f"convert_fail={res.failure} elapsed={res.elapsed:.1f}s"
                    )

    if light:
        run_batch(light, min(args.parallel_categories, len(light)))
    for cat in heavy_present:
        log(f"[HEAVY] starting {cat} ...")
        run_batch([cat], 1)

    if not args.skip_patch_restore:
        restore_xy_patch(args.server)

    elapsed = time.time() - t0
    total_ok = sum(r.success for r in results)
    total_fail = sum(r.failure for r in results)
    errors = [r for r in results if r.error]
    log(
        f"[ALL DONE] elapsed={elapsed:.1f}s convert_ok={total_ok} "
        f"convert_fail={total_fail} errors={len(errors)}"
    )
    if errors:
        for r in errors:
            log(f"  - {r.category}: {r.error}")
        raise SystemExit(1)


if __name__ == "__main__":
    main()

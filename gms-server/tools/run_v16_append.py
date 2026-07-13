#!/usr/bin/env python3
"""Orchestrate V16.1 -> Client_1 append: whole files, node merges, String export."""
from __future__ import annotations

import argparse
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TOOLS = Path(__file__).resolve().parent
EVAL = ROOT / ".eval_append"

SOURCE = Path(r"E:/mxd_soft/2.客户端/083/BeiDou-ClientV16.1/BeiDou-Client/Data")
TARGET = Path(r"E:/mxd_soft/2.客户端/083/beidou_client_xiaoye/BeiDou-Client_1/Data")
CANDIDATES = EVAL / "append_candidates_v16.txt"
CONFLICTS = EVAL / "v16_vs_client1_conflicts.txt"

FINGERPRINTS = {
    TARGET / "UI/UIWindow.img": 12240903,
    TARGET / "Effect/BasicEff.img": 3992551,
    TARGET / "Effect/DamageSkin.img": 70058716,
    TARGET.parent.parent / "ijl15.dll": 654848,
}


def run(name: str, cmd: list[str], log: Path) -> int:
    line = f"=== {name} === {' '.join(cmd)}"
    print(line, flush=True)
    with open(log, "a", encoding="utf-8") as f:
        f.write(f"[{datetime.now():%Y-%m-%d %H:%M:%S}] {line}\n")
    t0 = time.time()
    rc = subprocess.run(cmd, cwd=str(ROOT)).returncode
    status = f"{name} exit={rc} elapsed={time.time()-t0:.1f}s"
    print(status, flush=True)
    with open(log, "a", encoding="utf-8") as f:
        f.write(f"[{datetime.now():%Y-%m-%d %H:%M:%S}] {status}\n")
    return rc


def verify_fingerprints(log: Path) -> bool:
    ok = True
    with open(log, "a", encoding="utf-8") as f:
        f.write(f"\n[{datetime.now():%Y-%m-%d %H:%M:%S}] Fingerprint verify\n")
        for path, expected in FINGERPRINTS.items():
            if not path.exists():
                f.write(f"FAIL missing {path}\n")
                ok = False
                continue
            actual = path.stat().st_size
            status = "PASS" if actual == expected else "WARN"
            if actual != expected:
                ok = False
            f.write(f"{status} {path.name}: expected={expected} actual={actual}\n")
            print(f"  {status} {path.name}: {actual} (expected {expected})")
    return ok


def filter_candidates(missing: Path, out: Path) -> int:
    skip_prefixes = ("Base/", "EN/")
    skip_suffixes = (".log", ".bak")
    exclude = {
        "UI/UIWindow.img",
        "Effect/BasicEff.img",
        "Effect/DamageSkin.img",
    }
    lines: list[str] = []
    for ln in missing.read_text(encoding="utf-8", errors="replace").splitlines():
        ln = ln.strip().replace("\\", "/")
        if not ln or ln.startswith("#"):
            continue
        if ln in exclude or ln.startswith(skip_prefixes):
            continue
        if ln.endswith(skip_suffixes):
            continue
        if ln.startswith("Item/Cash/"):
            name = Path(ln).name
            if name in {"0591.img", "0592.img"} or name.startswith("590"):
                continue
        lines.append(ln)
    out.write_text("\n".join(lines) + "\n", encoding="utf-8")
    return len(lines)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--skip-whole", action="store_true")
    ap.add_argument("--skip-conflicts-gen", action="store_true")
    ap.add_argument("--skip-merge", action="store_true")
    ap.add_argument("--skip-export", action="store_true")
    ap.add_argument("--workers", type=int, default=16)
    ap.add_argument("--mcp-concurrent", type=int, default=6)
    ap.add_argument("--mcp-timeout", type=float, default=300)
    ap.add_argument("--size-only-conflicts", action="store_true")
    args = ap.parse_args()

    date = datetime.now().strftime("%Y%m%d")
    run_log = EVAL / f"v16_append_run_{date}.log"
    py = sys.executable

    run_log.write_text(f"V16 append pipeline start {datetime.now():%Y-%m-%d %H:%M:%S}\n", encoding="utf-8")

    print("Pre-flight fingerprints (before):")
    verify_fingerprints(run_log)

    missing = EVAL / "v16_vs_client1_missing.txt"
    if not missing.exists():
        print(f"ERROR: missing list not found: {missing}")
        return 1

    n = filter_candidates(missing, CANDIDATES)
    print(f"append_candidates_v16.txt: {n} paths")
    with open(run_log, "a", encoding="utf-8") as f:
        f.write(f"candidates={n} from {missing}\n")

    if not args.skip_whole:
        rc = run(
            "phase-a-whole",
            [
                py, "-u", str(TOOLS / "append_whole_files.py"),
                "--source", str(SOURCE),
                "--target", str(TARGET),
                "--list", str(CANDIDATES),
                "--log", str(run_log),
            ],
            run_log,
        )
        if rc != 0:
            print("Phase A had errors (file locks); continuing to merge phases")

    if not args.skip_conflicts_gen:
        run(
            "gen-conflicts",
            [
                py, "-u", str(TOOLS / "gen_wz_conflicts.py"),
                "--source", str(SOURCE),
                "--target", str(TARGET),
                "--out", str(CONFLICTS),
                *(["--size-only"] if args.size_only_conflicts else []),
            ],
            run_log,
        )

    if not args.skip_merge:
        merge_log = TOOLS / "_run_v16_merges.log"
        rc = run(
            "phase-b-merges",
            [
                py, "-u", str(TOOLS / "run_all_merges.py"),
                "--source", str(SOURCE),
                "--conflicts-file", str(CONFLICTS),
                "--workers", str(args.workers),
                "--mcp-concurrent", str(args.mcp_concurrent),
                "--mcp-timeout", str(args.mcp_timeout),
                "--log-suffix", "v16",
            ],
            run_log,
        )
        if rc != 0:
            print(f"Merge phase exit {rc}")

    if not args.skip_export:
        run(
            "phase-c-export",
            [py, "-u", str(TOOLS / "export_string_wz.py"), "--target", str(TARGET)],
            run_log,
        )

    print("Post-flight fingerprints:")
    fp_ok = verify_fingerprints(run_log)
    print(f"Pipeline log: {run_log}")
    return 0 if fp_ok else 2


if __name__ == "__main__":
    raise SystemExit(main())

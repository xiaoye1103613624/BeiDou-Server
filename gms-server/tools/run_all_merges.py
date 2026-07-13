#!/usr/bin/env python3
"""Run remaining merge phases (parallel per-file workers), then export String.wz."""
from __future__ import annotations

import argparse
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TOOLS = Path(__file__).resolve().parent
LOG = TOOLS / "_run_all_merges.log"
DEFAULT_SOURCE = Path(r"E:/mxd_soft/2.客户端/083/北斗GMS083_v1.11_ASM版/BeiDou-Client-V15_fix/Data")
DEFAULT_CONFLICTS = Path(__file__).resolve().parents[2] / ".eval_append" / "conflicts.txt"
DEFAULT_WORKERS = 16
DEFAULT_MCP_CONCURRENT = 6
DEFAULT_MCP_TIMEOUT = 300


def run_step(name: str, cmd: list[str]) -> int:
    line = f"=== {name} ==="
    print(line, flush=True)
    with open(LOG, "a", encoding="utf-8") as f:
        f.write(f"[{datetime.now():%Y-%m-%d %H:%M:%S}] {line}\n")
        f.write(" ".join(cmd) + "\n")
    t0 = time.time()
    p = subprocess.run(cmd, cwd=str(ROOT))
    elapsed = time.time() - t0
    status = f"{name} exit={p.returncode} elapsed={elapsed:.1f}s"
    print(status, flush=True)
    with open(LOG, "a", encoding="utf-8") as f:
        f.write(f"[{datetime.now():%Y-%m-%d %H:%M:%S}] {status}\n")
    return p.returncode


def main():
    ap = argparse.ArgumentParser(description="Run merge phases with parallel workers")
    ap.add_argument("--workers", type=int, default=DEFAULT_WORKERS, help=f"Thread pool size for merge phases (default {DEFAULT_WORKERS})")
    ap.add_argument(
        "--mcp-concurrent",
        type=int,
        default=DEFAULT_MCP_CONCURRENT,
        help=f"Max concurrent MCP calls (default {DEFAULT_MCP_CONCURRENT})",
    )
    ap.add_argument(
        "--mcp-timeout",
        type=float,
        default=DEFAULT_MCP_TIMEOUT,
        help=f"MCP HTTP read timeout in seconds (default {DEFAULT_MCP_TIMEOUT})",
    )
    ap.add_argument(
        "--skip-character",
        action="store_true",
        help="Skip character-conflicts phase (when already complete)",
    )
    ap.add_argument("--source", type=Path, default=DEFAULT_SOURCE)
    ap.add_argument("--target", type=Path, default=None)
    ap.add_argument("--conflicts-file", type=Path, default=DEFAULT_CONFLICTS)
    ap.add_argument(
        "--log-suffix",
        default="",
        help="Log filename suffix e.g. v16 -> _append_character_merge_v16.log",
    )
    ap.add_argument("--skip-export", action="store_true")
    args = ap.parse_args()

    py = sys.executable
    append = str(TOOLS / "append_img_nodes.py")
    export = str(TOOLS / "export_string_wz.py")
    suffix = f"_{args.log_suffix}" if args.log_suffix else ""
    source_flags = ["--source", str(args.source)]
    if args.target is not None:
        source_flags.extend(["--target", str(args.target)])
    conflict_flags = ["--conflicts-file", str(args.conflicts_file)]
    parallel_flags = [
        "--workers", str(args.workers),
        "--mcp-concurrent", str(args.mcp_concurrent),
        "--mcp-timeout", str(args.mcp_timeout),
        "--append-log",
    ]

    steps = [
        ("character-conflicts", [py, "-u", append, "--phase", "character-conflicts", "--log", str(TOOLS / f"_append_character_merge{suffix}.log"), "--skip-done", *source_flags, *conflict_flags, *parallel_flags]),
        ("other-conflicts", [py, "-u", append, "--phase", "other-conflicts", "--log", str(TOOLS / f"_append_other_merge{suffix}.log"), "--skip-done", *source_flags, *conflict_flags, *parallel_flags]),
    ]
    if not args.skip_export:
        export_cmd = [py, "-u", export]
        if args.target is not None:
            export_cmd.extend(["--target", str(args.target)])
        steps.append(("export-string-wz", export_cmd))

    if args.skip_character:
        steps = [s for s in steps if s[0] != "character-conflicts"]

    LOG.write_text(f"[{datetime.now():%Y-%m-%d %H:%M:%S}] run_all_merges start\n", encoding="utf-8")
    for name, cmd in steps:
        rc = run_step(name, cmd)
        if rc != 0 and name != "character-conflicts":
            print(f"Stopped after {name} with exit {rc}")
            return rc
    print("ALL DONE")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

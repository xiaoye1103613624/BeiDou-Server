#!/usr/bin/env python3
"""Run remaining merge phases sequentially, then export String.wz."""
from __future__ import annotations

import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TOOLS = Path(__file__).resolve().parent
LOG = TOOLS / "_run_all_merges.log"


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
    py = sys.executable
    append = str(TOOLS / "append_img_nodes.py")
    export = str(TOOLS / "export_string_wz.py")

    steps = [
        ("character-conflicts", [py, "-u", append, "--phase", "character-conflicts", "--log", str(TOOLS / "_append_character_merge.log"), "--skip-done"]),
        ("other-conflicts", [py, "-u", append, "--phase", "other-conflicts", "--log", str(TOOLS / "_append_other_merge.log"), "--skip-done"]),
        ("export-string-wz", [py, "-u", export]),
    ]

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

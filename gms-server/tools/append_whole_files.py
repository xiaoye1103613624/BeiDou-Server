#!/usr/bin/env python3
"""Phase A: copy whole files from SOURCE -> TARGET for paths in candidate list (strict append)."""
from __future__ import annotations

import argparse
import hashlib
import shutil
import time
from collections import defaultdict
from datetime import datetime
from pathlib import Path

DEFAULT_SOURCE = Path(r"E:/mxd_soft/2.客户端/083/BeiDou-ClientV16.1/BeiDou-Client/Data")
DEFAULT_TARGET = Path(r"E:/mxd_soft/2.客户端/083/beidou_client_xiaoye/BeiDou-Client_1/Data")
DEFAULT_LIST = Path(__file__).resolve().parents[2] / ".eval_append" / "append_candidates_v16.txt"

EXCLUDE_REL = {
    "UI/UIWindow.img",
    "Effect/BasicEff.img",
    "Effect/DamageSkin.img",
}

ITEM_CASH_SKIP = {"0591.img", "0592.img"}


def load_lines(path: Path) -> list[str]:
    out: list[str] = []
    for ln in path.read_text(encoding="utf-8", errors="replace").splitlines():
        ln = ln.strip().replace("\\", "/")
        if not ln or ln.startswith("#"):
            continue
        out.append(ln)
    return out


def should_exclude(rel: str) -> bool:
    if rel in EXCLUDE_REL:
        return True
    if rel.startswith("Base/"):
        return True
    if rel.startswith("EN/"):
        return True
    if rel.endswith(".log") or rel.endswith(".bak"):
        return True
    if rel.startswith("Item/Cash/"):
        name = Path(rel).name
        if name in ITEM_CASH_SKIP or name.startswith("590"):
            return True
    return False


def category(rel: str) -> str:
    return rel.split("/", 1)[0] if "/" in rel else "(root)"


def main() -> int:
    ap = argparse.ArgumentParser(description="Append whole WZ files SOURCE -> TARGET")
    ap.add_argument("--source", type=Path, default=DEFAULT_SOURCE)
    ap.add_argument("--target", type=Path, default=DEFAULT_TARGET)
    ap.add_argument("--list", type=Path, default=DEFAULT_LIST)
    ap.add_argument("--log", type=Path, required=True)
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    rels = [r for r in load_lines(args.list) if not should_exclude(r)]
    args.log.parent.mkdir(parents=True, exist_ok=True)

    stats = defaultdict(int)
    by_cat: dict[str, int] = defaultdict(int)
    errors: list[str] = []
    t0 = time.time()

    with open(args.log, "w", encoding="utf-8") as log:
        log.write(f"Append whole files started {datetime.now():%Y%m%d_%H%M%S}\n")
        log.write(f"SOURCE={args.source}\nTARGET={args.target}\nLIST={args.list}\n")
        log.write(f"Candidates after filter: {len(rels)}\n\n")

        for i, rel in enumerate(rels, 1):
            src = args.source / rel
            tgt = args.target / rel
            cat = category(rel)

            if not src.exists():
                stats["missingSrc"] += 1
                continue
            if tgt.exists():
                stats["skippedExists"] += 1
                continue

            if args.dry_run:
                stats["copied"] += 1
                by_cat[cat] += 1
                continue

            try:
                tgt.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(src, tgt)
                stats["copied"] += 1
                by_cat[cat] += 1
            except OSError as e:
                stats["errors"] += 1
                msg = f"ERROR {rel} : {e}"
                errors.append(msg)
                log.write(msg + "\n")
                if i % 500 == 0:
                    log.flush()

        elapsed = time.time() - t0
        log.write(
            f"\nSUMMARY copied={stats['copied']} skippedExists={stats['skippedExists']} "
            f"missingSrc={stats['missingSrc']} errors={stats['errors']} elapsed={elapsed:.1f}s\n"
        )
        log.write("By category:\n")
        for cat, n in sorted(by_cat.items(), key=lambda x: (-x[1], x[0])):
            log.write(f"{cat}={n}\n")
        if errors:
            log.write(f"\nFirst errors ({min(20, len(errors))}):\n")
            for e in errors[:20]:
                log.write(e + "\n")

    print(
        f"copied={stats['copied']} skipped={stats['skippedExists']} "
        f"errors={stats['errors']} log={args.log}"
    )
    return 1 if stats["errors"] else 0


if __name__ == "__main__":
    raise SystemExit(main())

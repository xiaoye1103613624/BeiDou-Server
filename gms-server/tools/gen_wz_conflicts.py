#!/usr/bin/env python3
"""Generate conflict list: paths present in both SOURCE and TARGET with different content."""
from __future__ import annotations

import argparse
import hashlib
from pathlib import Path

DEFAULT_SOURCE = Path(r"E:/mxd_soft/2.客户端/083/BeiDou-ClientV16.1/BeiDou-Client/Data")
DEFAULT_TARGET = Path(r"E:/mxd_soft/2.客户端/083/beidou_client_xiaoye/BeiDou-Client_1/Data")
DEFAULT_OUT = Path(__file__).resolve().parents[2] / ".eval_append" / "v16_vs_client1_conflicts.txt"

EXCLUDE_REL = {
    "UI/UIWindow.img",
    "Effect/BasicEff.img",
    "Effect/DamageSkin.img",
}


def file_hash(path: Path, chunk: int = 1 << 20) -> str:
    h = hashlib.md5()
    with open(path, "rb") as f:
        while True:
            b = f.read(chunk)
            if not b:
                break
            h.update(b)
    return h.hexdigest()


def should_exclude(rel: str) -> bool:
    if rel in EXCLUDE_REL:
        return True
    if rel.startswith("Base/"):
        return True
    if rel.startswith("Item/Cash/"):
        name = Path(rel).name
        if name in {"0591.img", "0592.img"} or name.startswith("590"):
            return True
    return False


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--source", type=Path, default=DEFAULT_SOURCE)
    ap.add_argument("--target", type=Path, default=DEFAULT_TARGET)
    ap.add_argument("--out", type=Path, default=DEFAULT_OUT)
    ap.add_argument("--size-only", action="store_true", help="Compare size only (faster)")
    args = ap.parse_args()

    conflicts: list[str] = []
    checked = 0

    for src_path in args.source.rglob("*"):
        if not src_path.is_file():
            continue
        rel = src_path.relative_to(args.source).as_posix()
        if should_exclude(rel):
            continue
        tgt_path = args.target / rel
        if not tgt_path.is_file():
            continue
        checked += 1
        if args.size_only:
            if src_path.stat().st_size != tgt_path.stat().st_size:
                conflicts.append(rel)
        else:
            if file_hash(src_path) != file_hash(tgt_path):
                conflicts.append(rel)
        if checked % 5000 == 0:
            print(f"checked {checked}, conflicts {len(conflicts)}", flush=True)

    conflicts.sort()
    args.out.parent.mkdir(parents=True, exist_ok=True)
    with open(args.out, "w", encoding="utf-8") as f:
        f.write(f"# V16 vs Client_1 conflicts (both exist, content differs)\n")
        f.write(f"# SOURCE: {args.source}\n")
        f.write(f"# TARGET: {args.target}\n")
        f.write(f"# Total: {len(conflicts)}\n\n")
        for rel in conflicts:
            f.write(rel + "\n")

    print(f"checked={checked} conflicts={len(conflicts)} out={args.out}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

#!/usr/bin/env python3
"""Run MergeBeautyImg (dotnet) from gms-server/tools."""
import subprocess
import sys
from pathlib import Path

TOOLS = Path(__file__).resolve().parent
PROJ = TOOLS / "merge_beauty_img" / "MergeBeautyImg.csproj"


def main() -> int:
    if not PROJ.is_file():
        print("missing", PROJ, file=sys.stderr)
        return 1
    cmd = ["dotnet", "run", "--project", str(PROJ), "-c", "Release", "--"]
    cmd.extend(sys.argv[1:])
    return subprocess.call(cmd, cwd=TOOLS)


if __name__ == "__main__":
    raise SystemExit(main())

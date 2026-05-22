# -*- coding: utf-8 -*-
"""
Scan Java sources and insert a Chinese class-level Javadoc when missing above the outer type.
Only touches files under the given root. Idempotent: skips if /** ... */ already sits above @* / declaration.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path


def find_package(lines: list[str]) -> str | None:
    for line in lines:
        m = re.match(r"^\s*package\s+([\w.]+)\s*;", line)
        if m:
            return m.group(1)
    return None


def find_outer_declaration_line(lines: list[str], stem: str) -> int | None:
    """Line index of outer class|interface|enum matching file stem (preferred) or first top-level type."""
    # Prefer: (modifiers) class|interface|enum STEM
    pat_stem = re.compile(
        rf"^(?:public\s+)?(?:abstract\s+|final\s+|strictfp\s+)*"
        rf"(?:class|interface|enum)\s+{re.escape(stem)}\b"
    )
    pat_any = re.compile(
        r"^(?:public\s+)?(?:abstract\s+|final\s+|strictfp\s+)*"
        r"(?:class|interface|enum)\s+\w+"
    )
    for i, line in enumerate(lines):
        s = line.strip()
        if not s or s.startswith("//"):
            continue
        if pat_stem.match(s):
            return i
    for i, line in enumerate(lines):
        s = line.strip()
        if not s or s.startswith("//"):
            continue
        if pat_any.match(s) and not s.startswith("import"):
            return i
    return None


def javadoc_closes_above(lines: list[str], decl_idx: int) -> bool:
    """True if a Javadoc block sits immediately above annotations or declaration (ignoring blanks)."""
    j = decl_idx - 1
    while j >= 0 and lines[j].strip() == "":
        j -= 1
    while j >= 0 and lines[j].strip().startswith("@"):
        j -= 1
    while j >= 0 and lines[j].strip() == "":
        j -= 1
    if j < 0 or lines[j].strip() != "*/":
        return False
    k = j - 1
    while k >= 0:
        st = lines[k].strip()
        if st.startswith("/**"):
            return True
        if st.startswith("*") or st == "":
            k -= 1
            continue
        return False
    return False


def first_annotation_line_above_decl(lines: list[str], decl_idx: int) -> int | None:
    """If there are consecutive @ lines directly above decl, return index of first @ line; else None."""
    j = decl_idx - 1
    while j >= 0 and lines[j].strip() == "":
        j -= 1
    if j < 0 or not lines[j].strip().startswith("@"):
        return None
    last = j
    while j >= 0 and lines[j].strip().startswith("@"):
        last = j
        j -= 1
    return last


def build_comment(name: str, kind: str, pkg: str) -> str:
    """One short Chinese paragraph based on naming conventions."""
    tail_pkg = pkg.split(".")[-1] if pkg else "org.gms"
    if name.endswith("Controller"):
        return f"【Web 控制器】{name}：处理 HTTP 请求，归属模块 `{tail_pkg}`。"
    if name.endswith("Service"):
        return f"【业务服务】{name}：封装 `{tail_pkg}` 相关应用逻辑与数据协作。"
    if name.endswith("Mapper"):
        return f"【数据访问】{name}：MyBatis-Flex Mapper，表访问接口。"
    if name.endswith("DO"):
        return f"【实体对象】{name}：与数据库表字段对应的持久化模型。"
    if name.endswith("Handler"):
        return f"【封包处理】{name}：解析客户端 opcode 并更新游戏状态。"
    if name.endswith("Packet"):
        return f"【出站封包】{name}：构造发往客户端的网络数据包。"
    if name.endswith("Command"):
        return f"【GM/玩家指令】{name}：聊天或控制台命令实现。"
    if name.endswith("Provider") or name.endswith("Factory"):
        return f"【工厂/提供者】{name}：创建或提供 `{tail_pkg}` 相关运行时对象。"
    if kind == "enum":
        return f"【枚举】{name}：定义 `{tail_pkg}` 中的一组常量。"
    if kind == "interface":
        return f"【接口】{name}：由 `{tail_pkg}` 模块实现的契约。"
    return f"【类型】{name}（{kind}），包 `{pkg}`。"


def detect_kind(decl_line: str) -> str:
    s = decl_line.strip()
    if re.search(r"\benum\s+\w+", s):
        return "enum"
    if re.search(r"\binterface\s+\w+", s):
        return "interface"
    return "class"


def extract_type_name(decl_line: str) -> str | None:
    m = re.search(r"(?:class|interface|enum)\s+(\w+)", decl_line.strip())
    return m.group(1) if m else None


def process_file(path: Path, dry_run: bool) -> bool:
    if path.name in ("package-info.java", "module-info.java"):
        return False
    text = path.read_text(encoding="utf-8", errors="replace")
    if "\r\n" in text:
        newline = "\r\n"
        lines = text.split("\r\n")
    else:
        newline = "\n"
        lines = text.split("\n")

    stem = path.stem
    pkg = find_package(lines) or ""
    decl_idx = find_outer_declaration_line(lines, stem)
    if decl_idx is None:
        return False

    if javadoc_closes_above(lines, decl_idx):
        return False

    decl_line = lines[decl_idx]
    kind = detect_kind(decl_line)
    type_name = extract_type_name(decl_line) or stem
    comment_body = build_comment(type_name, kind, pkg)

    block = f"/**\n * {comment_body}\n */\n"

    ann_line = first_annotation_line_above_decl(lines, decl_idx)
    insert_at = ann_line if ann_line is not None else decl_idx

    new_lines = lines[:insert_at] + [block.rstrip(newline)] + lines[insert_at:]
    new_text = newline.join(new_lines) if newline == "\n" else "\r\n".join(new_lines)
    if not new_text.endswith(newline) and text.endswith(newline):
        new_text += newline

    if not dry_run:
        path.write_text(new_text, encoding="utf-8")
    return True


def main() -> int:
    base = Path(__file__).resolve().parents[1]
    roots = [base / "src" / "main" / "java", base / "src" / "test" / "java"]
    dry = "--dry-run" in sys.argv
    total = 0
    for root in roots:
        if not root.is_dir():
            continue
        for path in sorted(root.rglob("*.java")):
            try:
                if process_file(path, dry):
                    total += 1
                    if dry:
                        print(f"would update: {path.relative_to(base)}")
            except Exception as e:
                print(f"ERROR {path}: {e}", file=sys.stderr)
    print(f"{'Would modify' if dry else 'Modified'} {total} files under {base / 'src'}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

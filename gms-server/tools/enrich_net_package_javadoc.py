# -*- coding: utf-8 -*-
"""
Enrich class-level Javadoc under org/gms/net only.
Skip files where the outer type already has a detailed Javadoc (multi-line substantive doc, @tags, <p>, etc.).
Otherwise replace short/auto Javadoc or insert a new detailed block above annotations/declaration.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

AUTO_LINE_RE = re.compile(
    r"^【(封包处理|类型|业务服务|Web\s*控制器|数据访问|实体对象|工厂/提供者|枚举|接口|GM/玩家指令|出站封包)】"
)


def find_package(lines: list[str]) -> str:
    for line in lines:
        m = re.match(r"^\s*package\s+([\w.]+)\s*;", line)
        if m:
            return m.group(1)
    return ""


def find_outer_decl_line(lines: list[str], stem: str) -> int | None:
    pat_stem = re.compile(
        rf"^(?:public\s+)?(?:abstract\s+|final\s+|strictfp\s+)*"
        rf"(?:record|class|interface|enum)\s+{re.escape(stem)}\b"
    )
    pat_record = re.compile(rf"^record\s+{re.escape(stem)}\b")
    for i, line in enumerate(lines):
        s = line.strip()
        if not s or s.startswith("//"):
            continue
        if pat_stem.match(s) or pat_record.match(s):
            return i
    pat_any = re.compile(
        r"^(?:public\s+)?(?:abstract\s+|final\s+|strictfp\s+)*"
        r"(?:record|class|interface|enum)\s+\w+"
    )
    pat_any_record = re.compile(r"^record\s+\w+")
    for i, line in enumerate(lines):
        s = line.strip()
        if not s or s.startswith("//") or s.startswith("import"):
            continue
        if pat_any.match(s) or pat_any_record.match(s):
            return i
    return None


def extract_class_javadoc_span(lines: list[str], decl_idx: int) -> tuple[int, int] | None:
    """
    If a Javadoc block sits directly above @annotations or declaration, return (start_line, end_line) inclusive.
    Otherwise return None.
    """
    j = decl_idx - 1
    while j >= 0 and lines[j].strip() == "":
        j -= 1
    while j >= 0 and lines[j].strip().startswith("@"):
        j -= 1
    while j >= 0 and lines[j].strip() == "":
        j -= 1
    if j < 0 or lines[j].strip() != "*/":
        return None
    end_j = j
    k = j - 1
    while k >= 0:
        st = lines[k].strip()
        if st.startswith("/**"):
            return (k, end_j)
        k -= 1
    return None


def javadoc_body_lines(lines: list[str], start: int, end: int) -> list[str]:
    out: list[str] = []
    for i in range(start + 1, end):
        raw = lines[i]
        m = re.match(r"^\s*\*?\s?(.*)$", raw)
        if m:
            out.append(m.group(1).rstrip())
    return out


def is_detailed_javadoc(lines: list[str], start: int, end: int) -> bool:
    block = "\n".join(lines[start : end + 1])
    if "@author" in block or "@since" in block or "@see" in block or "@deprecated" in block:
        return True
    if "<p>" in block or "<ul>" in block or "<pre>" in block:
        return True
    if block.count("{@link") >= 2:
        return True
    body = javadoc_body_lines(lines, start, end)
    nonempty = [b.strip() for b in body if b.strip()]
    if not nonempty:
        return False
    # 自动脚本生成的单行占位，不算「已有详细注释」
    if len(nonempty) == 1:
        one = nonempty[0]
        if AUTO_LINE_RE.match(one) or one.startswith("【类型】"):
            return False
    if len(nonempty) >= 2:
        return True
    one = nonempty[0]
    if len(one) >= 110:
        return True
    if one.count("。") >= 2:
        return True
    if one.count("，") >= 4:
        return True
    if "。" in one and len(one) >= 50:
        return True
    return False


def insert_point_before_decl(lines: list[str], decl_idx: int) -> int:
    """Line index where new javadoc should be inserted (before any @ on class)."""
    j = decl_idx - 1
    while j >= 0 and lines[j].strip() == "":
        j -= 1
    first_ann: int | None = None
    while j >= 0 and lines[j].strip().startswith("@"):
        first_ann = j
        j -= 1
    return first_ann if first_ann is not None else decl_idx


def build_detailed_doc(name: str, pkg: str) -> list[str]:
    """Return lines inside Javadoc (each line WITHOUT leading /** or * prefix — we add *)."""
    p = pkg
    lines: list[str] = []

    if p == "org.gms.net" and name == "PacketHandler":
        lines = [
            "游戏客户端入站封包处理契约。",
            "实现类在频道服或登录服注册到 {@link org.gms.net.PacketProcessor} 后，",
            "由 {@link org.gms.client.Client#channelRead} 在解析 opcode 并校验状态后调用 {@link #handlePacket}。",
            "{@link #validateState} 用于拒绝当前会话状态下不应到达的封包。",
        ]
    elif p.endswith(".channel.handlers") and name.endswith("Handler"):
        lines = [
            f"频道服务器入站封包处理器「{name}」。",
            "对应客户端在频道内发起的一类操作（移动、技能、物品、NPC、商店、社交等之一），",
            "从 {@link org.gms.net.packet.InPacket} 读取字段后更新 {@link org.gms.client.Character} 与地图/世界状态。",
            "通常继承 {@link org.gms.net.AbstractPacketHandler}，并与 {@link org.gms.net.server.channel.Channel} 上的服务协同。",
        ]
    elif p.endswith(".handlers.login") and name.endswith("Handler"):
        lines = [
            f"登录服务器入站封包处理器「{name}」。",
            "处理账号登录、选角、创建角色、PIN/PIC、服务器列表等与尚未进入频道相关的协议。",
            "在验证通过后可能更新 {@link org.gms.client.Client} 的账号状态并切换会话阶段。",
        ]
    elif ".handlers" in p and name.endswith("Handler"):
        lines = [
            f"游戏网络入站封包处理器「{name}」。",
            "位于登录或频道之外的 handler 子包时，负责对应流程的协议解析与状态迁移。",
        ]
    elif p.endswith(".netty") or ".netty." in p:
        lines = [
            f"Netty 网络组件「{name}」。",
            "参与 Channel 管道上的编解码、握手、空闲检测或与 ChannelServer 绑定的 IO 逻辑。",
        ]
    elif ".packet.out" in p or p.endswith(".packet.out"):
        lines = [
            f"出站协议构造「{name}」。",
            "将服务端状态序列化为发往客户端的二进制 {@link org.gms.net.packet.Packet}（或子类）。",
        ]
    elif ".packet.in" in p or p.endswith(".packet.in"):
        lines = [
            f"入站数据视图「{name}」。",
            "封装或辅助读取客户端上报的字段，常与具体 Handler 配对使用。",
        ]
    elif ".packet.logging" in p:
        lines = [
            f"封包日志与可观测性「{name}」。",
            "用于记录、过滤或诊断收发包内容，便于 GM 与开发排查协议问题。",
        ]
    elif ".packet" in p and "handlers" not in p:
        lines = [
            f"网络协议层类型「{name}」。",
            "属于 org.gms.net.packet 下的通用封包、读写或工具定义。",
        ]
    elif ".server.world" in p:
        lines = [
            f"跨频道世界级网络/会话模型「{name}」。",
            "在多个 Channel 之间同步队伍、好友、信使、公告等全局状态。",
        ]
    elif ".server.guild" in p or ".server.alliance" in p:
        lines = [
            f"公会/联盟相关网络模型「{name}」。",
            "维护公会成员、技能、联盟关系及下行通知封包组装所需的数据。",
        ]
    elif ".server.coordinator" in p:
        lines = [
            f"会话与并发协调「{name}」。",
            "在多开检测、登录绕过、匹配、事件召回等场景下集中管理跨连接状态。",
        ]
    elif ".server.services" in p or ".services.task" in p:
        lines = [
            f"频道/世界后台服务「{name}」。",
            "由调度器周期性或在业务触发时运行，与网络层共享 Channel/World 引用。",
        ]
    elif ".server.task" in p:
        lines = [
            f"服务器定时任务「{name}」。",
            "在 org.gms.net.server.task 下注册执行，用于重置、刷新或持久化与在线玩家相关的数据。",
        ]
    elif p.endswith(".server.channel") and name == "Channel":
        lines = [
            "单个游戏频道的运行时宿主：端口、玩家容器、地图工厂、事件脚本与频道内服务。",
            "与 {@link org.gms.net.netty.ChannelServer} 绑定，对外接受玩家连接并在本频道内完成大部分游戏逻辑。",
        ]
    elif ".server.channel" in p:
        lines = [
            f"频道子系统类型「{name}」。",
            "与 {@link org.gms.net.server.channel.Channel} 生命周期或频道内资源管理相关。",
        ]
    elif p.endswith(".server") and name == "Server":
        lines = [
            "游戏主进程入口单例：维护世界列表、频道、登录状态迁移与全局资源。",
            "为 Netty 管线、Handler 与脚本层提供统一的 {@link org.gms.net.server.Server#getInstance()} 访问点。",
        ]
    elif ".server" in p:
        lines = [
            f"游戏服务器网络子系统「{name}」。",
            f"包路径 `{p}`，与在线存储、玩家进出频道及跨服数据协作。",
        ]
    elif ".encryption" in p:
        lines = [
            f"协议加解密与编解码「{name}」。",
            "在客户端与服务器之间对帧或载荷进行变换，与 PacketCodec / Protocol 配置一致。",
        ]
    else:
        lines = [
            f"网络层类型「{name}」。",
            f"位于 `{p}`，参与客户端会话、封包路由或服务器间协作。",
        ]

    return lines


def format_javadoc_lines(inner_lines: list[str]) -> list[str]:
    out = ["/**"]
    out.extend(" * " + ln for ln in inner_lines)
    out.append(" */")
    return out


def process_file(path: Path, dry: bool) -> str | None:
    if path.name in ("package-info.java", "module-info.java"):
        return None
    text = path.read_text(encoding="utf-8", errors="replace")
    lines = text.splitlines()

    stem = path.stem
    pkg = find_package(lines)
    decl = find_outer_decl_line(lines, stem)
    if decl is None:
        return "no_decl"

    span = extract_class_javadoc_span(lines, decl)
    if span is not None:
        start, end = span
        if is_detailed_javadoc(lines, start, end):
            return "skip_detailed"

    inner = build_detailed_doc(stem, pkg)
    block_lines = format_javadoc_lines(inner)

    if span is not None:
        start, end = span
        new_lines = lines[:start] + block_lines + lines[end + 1 :]
    else:
        ins = insert_point_before_decl(lines, decl)
        new_lines = lines[:ins] + block_lines + lines[ins:]

    new_text = "\n".join(new_lines)
    if text.endswith("\n") or text.endswith("\r\n"):
        new_text += "\n"

    if not dry:
        path.write_text(new_text, encoding="utf-8")

    return "updated"


def main() -> int:
    base = Path(__file__).resolve().parents[1] / "src" / "main" / "java" / "org" / "gms" / "net"
    dry = "--dry-run" in sys.argv
    stats: dict[str, int] = {}
    for path in sorted(base.rglob("*.java")):
        try:
            r = process_file(path, dry)
            if r:
                stats[r] = stats.get(r, 0) + 1
        except Exception as e:
            print(f"ERROR {path}: {e}", file=sys.stderr)
            stats["error"] = stats.get("error", 0) + 1
    for k, v in sorted(stats.items()):
        print(f"{k}: {v}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

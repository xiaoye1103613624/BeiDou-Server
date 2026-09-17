#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
GMS083 / Kaentake 聊天表情包 —— 客户端地址静态校验脚本（可复现证据）。

用途：在缺少 IDA GUI 时，用 PE 头解析 + capstone 反汇编校验移植文档中硬编码的
客户端函数地址与特征码是否适用于目标客户端（E:\\MXD\\BeiDou-Client_S9\\BeiDou.exe）。

校验项：
1. kClientSocketProcessPacket(0x004965F1) 处 8 字节是否等于 kProcessPacketSignature；
2. 各硬编码函数入口是否为真实函数体（Nexon wrapper `mov eax,const; call 0xA60B98` + prolog）；
3. 硬编码全局地址（状态栏/UserLocal/UserPool/ClientSocket）在 .text 中是否存在立即数交叉引用。

运行：python ida_static_check.py   （依赖 capstone，输出写入同目录 ida_static_check.out.txt）
"""
import os
import struct
import sys

try:
    import capstone
except ImportError:
    print("需要 capstone：pip install capstone")
    sys.exit(1)

TARGET = r"E:\MXD\BeiDou-Client_S9\BeiDou.exe"

FUNCS = {
    "kClientSocketSendPacket": 0x0049637B,
    "kClientSocketProcessPacket": 0x004965F1,
    "kCreateAnimLayer": 0x0043EA3E,
    "kSetFont": 0x0046341A,
    "kUserGetVecCtrl": 0x004AD42B,
    "kUserPoolGetUser": 0x009716ED,
    "kPlayUiSound": 0x00989588,
}

GLOBALS = {
    "kClientSocketInstance": 0x00BE7914,
    "kUserLocalInstance": 0x00BEBF98,
    "kUserPoolInstance": 0x00BEBFA8,
    "kStatusBarInstance": 0x00BEC208,
}

# custom_packet_dispatcher.cpp 中的 8 字节特征码
PROCESS_PACKET_SIGNATURE = bytes([0xB8, 0xB0, 0x12, 0xA8, 0x00, 0xE8, 0x9D, 0xA5])

out = []


def say(*a):
    line = " ".join(str(x) for x in a)
    out.append(line)
    print(line)


with open(TARGET, "rb") as f:
    data = f.read()

e_lfanew = struct.unpack_from("<I", data, 0x3C)[0]
nsec = struct.unpack_from("<H", data, e_lfanew + 6)[0]
size_opt = struct.unpack_from("<H", data, e_lfanew + 20)[0]
opt_off = e_lfanew + 24
image_base = struct.unpack_from("<I", data, opt_off + 28)[0]
sect_off = opt_off + size_opt
sections = []
for i in range(nsec):
    off = sect_off + i * 40
    name = data[off:off + 8].rstrip(b"\0").decode("ascii", "replace")
    vsize, vaddr, rsize, raddr = struct.unpack_from("<IIII", data, off + 8)
    sections.append((name, vaddr, vsize, raddr, rsize))

say("target:", TARGET)
say("imageBase=0x%08X sections=%d" % (image_base, nsec))
for name, vaddr, vsize, raddr, rsize in sections:
    say("  %-8s rva=0x%08X vsize=0x%06X raw=0x%08X rawsize=0x%06X"
        % (name, vaddr, vsize, raddr, rsize))


def va2off(va):
    rva = va - image_base
    for name, vaddr, vsize, raddr, rsize in sections:
        if vaddr <= rva < vaddr + max(vsize, rsize):
            return raddr + (rva - vaddr)
    return None


say("")
say("== 1. 收包分发 hook 点特征码校验 (0x004965F1)")
off = va2off(0x004965F1)
actual = data[off:off + 8]
say("  expected:", PROCESS_PACKET_SIGNATURE.hex(" ").upper())
say("  actual  :", actual.hex(" ").upper())
say("  match   :", actual == PROCESS_PACKET_SIGNATURE)

say("")
say("== 2. 函数入口反汇编（前 8 条指令）")
md = capstone.Cs(capstone.CS_ARCH_X86, capstone.CS_MODE_32)
for name, va in FUNCS.items():
    o = va2off(va)
    say("  -- %s @ 0x%08X" % (name, va))
    for n, ins in enumerate(md.disasm(data[o:o + 48], va)):
        if n >= 8:
            break
        say("     0x%08X  %-22s %s %s"
            % (ins.address, ins.bytes.hex().upper(), ins.mnemonic, ins.op_str))

say("")
say("== 3. 全局地址交叉引用（.text 中的 32 位立即数引用数）")
text = next(s for s in sections if s[0] == ".text")
body = data[text[3]:text[3] + min(text[2], text[4])]
for name, addr in GLOBALS.items():
    needle = struct.pack("<I", addr)
    hits = []
    pos = body.find(needle)
    while pos != -1 and len(hits) < 8:
        hits.append(text[1] + image_base + pos)
        pos = body.find(needle, pos + 1)
    say("  %-22s 0x%08X -> %s" % (name, addr, ", ".join("0x%08X" % h for h in hits)))

here = os.path.dirname(os.path.abspath(__file__))
with open(os.path.join(here, "ida_static_check.out.txt"), "w", encoding="utf-8") as f:
    f.write("\n".join(out) + "\n")

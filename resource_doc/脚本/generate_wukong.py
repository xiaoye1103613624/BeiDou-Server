#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成黑神话悟空风格 NPC 图片 —— Pollinations.ai 免费 API。
自动去除背景，输出透明 PNG。

@author 萧曵
@since 2026-05-29
"""
import time
from pathlib import Path
import requests

# ============================================================
# 配置
# ============================================================
OUTPUT_DIR = Path(__file__).parent / "images" / "npc" / "wukong"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

# Pollinations.ai 免费 API
API_URL = "https://image.pollinations.ai/prompt"

# 是否尝试去背景（需要 rembg 库: pip install rembg）
REMOVE_BG = False  # 先关闭，Pollinations 不稳定先确保能出图

# 黑神话悟空 NPC 图组
WUKONG_PROMPTS = [
    (
        "wukong_stand",
        "Sun Wukong, Black Myth Wukong style, full body standing pose, golden armor, red cape, holding Ruyi Jingu Bang staff, chibi game character sprite, MapleStory style, solid green background, pixel art",
    ),
    (
        "wukong_attack",
        "Sun Wukong fighting pose swinging golden staff, Black Myth style, fierce eyes, golden armor, red cape flying, chibi game sprite, MapleStory style, solid green background",
    ),
    (
        "wukong_idle",
        "Sun Wukong idle pose hands on staff, calm face, detailed golden armor, red headband, chibi game sprite, MapleStory style, solid green background",
    ),
    (
        "wukong_transform",
        "Sun Wukong magic transformation pose, golden light aura, hair clone effect, chibi game sprite, MapleStory style, solid green background",
    ),
    (
        "wukong_sit",
        "Sun Wukong sitting cross legged on magic cloud, peach in hand, cute chibi, MapleStory style game sprite, solid green background",
    ),
    (
        "wukong_closeup",
        "Sun Wukong portrait close-up, fiery golden eyes, fur texture, golden headband, chibi, MapleStory style game sprite, solid green background",
    ),
    (
        "wukong_jump",
        "Sun Wukong jumping attack from above, staff raised high, dynamic pose, red cape spread, chibi game sprite, MapleStory style, solid green background",
    ),
    (
        "wukong_defense",
        "Sun Wukong defensive stance, staff held horizontally blocking, focused expression, chibi game sprite, MapleStory style, solid green background",
    ),
]


def _session():
    """创建禁用系统代理的 Session（绕过 127.0.0.1:7897）"""
    s = requests.Session()
    s.trust_env = False  # 关键：不走系统代理
    return s


def generate_pollinations(prompt, width=512, height=512):
    """调用 Pollinations.ai 免费 API 生成图片"""
    encoded = requests.utils.quote(prompt, safe="")
    url = f"{API_URL}/{encoded}"

    session = _session()
    try:
        resp = session.get(url, params={
            "width": width, "height": height, "nologo": "true",
        }, timeout=300)
        resp.raise_for_status()
        return resp.content
    except requests.exceptions.RequestException as e:
        print(f"  错误: {e}")
        return None


def remove_background(img_data):
    """使用 rembg 去除背景，返回带透明通道的 PNG"""
    try:
        from rembg import remove
        return remove(img_data)
    except ImportError:
        return None


def main():
    total = len(WUKONG_PROMPTS)
    print(f"{'=' * 60}")
    print(f"黑神话悟空 NPC 图片生成器")
    print(f"引擎: Pollinations.ai (免费)")
    print(f"任务数: {total} 张")
    print(f"输出目录: {OUTPUT_DIR}")
    print(f"去背景: {'是 (rembg)' if REMOVE_BG else '否'}")
    print(f"{'=' * 60}")

    success, fail = 0, 0
    for i, (name, prompt) in enumerate(WUKONG_PROMPTS):
        out_path = OUTPUT_DIR / f"{name}.png"

        if out_path.exists():
            print(f"\n[{i + 1}/{total}] {name} ✓ 已存在，跳过")
            success += 1
            continue

        print(f"\n[{i + 1}/{total}] {name}")
        print(f"  Prompt: {prompt[:80]}...")

        img_data = generate_pollinations(prompt)

        if img_data and len(img_data) > 1000:
            # 去背景（可选）
            if REMOVE_BG:
                bg_removed = remove_background(img_data)
                if bg_removed:
                    img_data = bg_removed
                    print(f"  背景已去除")

            out_path.write_bytes(img_data)
            print(f"  → {out_path.name} ({len(img_data) / 1024:.1f}KB)")
            success += 1
        else:
            print(f"  生成失败")
            fail += 1

        # 限速
        if i < total - 1:
            time.sleep(5)

    print(f"\n{'=' * 60}")
    print(f"完成! 成功: {success}/{total}, 失败: {fail}")
    print(f"输出: {OUTPUT_DIR}")
    for f in sorted(OUTPUT_DIR.glob("*.png")):
        print(f"  {f.name}")
    print(f"{'=' * 60}")


if __name__ == "__main__":
    main()

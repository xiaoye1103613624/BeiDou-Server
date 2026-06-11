#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
基于现有对话框组件结构，生成10组不同主题的对话框组件。
- 5组冒险岛高版本风格（现代蓝、典雅紫、翡翠绿、暗夜黑、玫瑰金）
- 5组自定义清新卡通风格（糖果粉、天空蓝、阳光橙、薄荷绿、薰衣草）

使用源组件的alpha通道保留形状，替换颜色方案。

@author 萧曵
@since 2026-05-29
"""
import os
import colorsys
from pathlib import Path
from PIL import Image
import numpy as np

# ============================================================
# 路径配置
# ============================================================
SRC_DIR = Path(__file__).parent / "images" / "对话框"  # 使用还原后的100%透明度源图
BASE_COMPONENTS = ["c.png", "ic.png", "is.png", "it.png", "s.png", "t.png"]

# 输出根目录：10组主题各一个子目录
OUT_BASE = Path(__file__).parent / "images" / "对话框"

# ============================================================
# 10 组颜色主题定义
# 每组格式: (目录名, 标题颜色RGB, 底色RGB, 阴影颜色RGB, 风格描述)
# ============================================================
THEMES = [
    # ---- 冒险岛高版本风格 (5组) ----
    ("高版_现代蓝", (66, 139, 202), (245, 247, 250), (40, 60, 90), "现代蓝调高版"),
    ("高版_典雅紫", (120, 80, 180), (248, 246, 252), (50, 30, 80), "典雅紫调高版"),
    ("高版_翡翠绿", (80, 160, 110), (245, 250, 246), (30, 60, 40), "翡翠绿调高版"),
    ("高版_暗夜黑", (60, 63, 70), (240, 241, 243), (25, 27, 30), "暗夜黑调高版"),
    ("高版_玫瑰金", (200, 110, 120), (250, 245, 245), (70, 35, 40), "玫瑰金调高版"),
    # ---- 自定义清新卡通风格 (5组) ----
    ("卡通_糖果粉", (255, 130, 160), (255, 248, 250), (80, 30, 45), "糖果粉卡通"),
    ("卡通_天空蓝", (100, 180, 240), (245, 250, 255), (35, 60, 90), "天空蓝卡通"),
    ("卡通_阳光橙", (255, 165, 70), (255, 250, 245), (90, 50, 20), "阳光橙卡通"),
    ("卡通_薄荷绿", (100, 210, 165), (245, 255, 250), (30, 70, 50), "薄荷绿卡通"),
    ("卡通_薰衣草", (180, 150, 220), (250, 247, 255), (55, 40, 80), "薰衣草卡通"),
]


def rgb_to_hsl(r, g, b):
    """RGB转HSL（返回0-1范围）"""
    rn, gn, bn = r / 255.0, g / 255.0, b / 255.0
    mx, mn = max(rn, gn, bn), min(rn, gn, bn)
    lgt = (mx + mn) / 2
    if mx == mn:
        h, s = 0, 0
    else:
        d = mx - mn
        s = d / (2 - mx - mn) if lgt > 0.5 else d / (mx + mn)
        if mx == rn:
            h = ((gn - bn) / d) % 6
        elif mx == gn:
            h = (bn - rn) / d + 2
        else:
            h = (rn - gn) / d + 4
        h /= 6
    return h, s, lgt


def hsl_to_rgb(h, s, lgt):
    """HSL转RGB（输入0-1范围，输出0-255）"""
    if s == 0:
        return (int(lgt * 255),) * 3

    def hue_to_rgb(p, q, t):
        if t < 0: t += 1
        if t > 1: t -= 1
        if t < 1 / 6: return p + (q - p) * 6 * t
        if t < 1 / 2: return q
        if t < 2 / 3: return p + (q - p) * (2 / 3 - t) * 6
        return p

    q = lgt * (1 + s) if lgt < 0.5 else lgt + s - lgt * s
    p = 2 * lgt - q
    r = hue_to_rgb(p, q, h + 1 / 3)
    g = hue_to_rgb(p, q, h)
    b = hue_to_rgb(p, q, h - 1 / 3)
    return (int(r * 255), int(g * 255), int(b * 255))


def apply_theme(src_img, title_rgb, body_rgb, shadow_rgb):
    """
    将源组件替换为目标主题颜色。
    策略：用亮度(L)作为映射键，暗部→阴影色，中间→标题色，亮部→底色。
    """
    rgba = src_img.convert("RGBA")
    arr = np.array(rgba).astype(np.float64)
    alpha = arr[:, :, 3]
    rgb = arr[:, :, :3]

    # 目标HSL
    th, ts, tl = rgb_to_hsl(*title_rgb)
    bh, bs, bl = rgb_to_hsl(*body_rgb)
    sh, ss, sl = rgb_to_hsl(*shadow_rgb)

    result = np.zeros_like(rgb)

    for y in range(rgb.shape[0]):
        for x in range(rgb.shape[1]):
            a = alpha[y, x]
            if a < 5:  # 完全透明保持不变
                result[y, x] = [0, 0, 0]
                continue

            r, g, b = rgb[y, x] / 255.0
            px_h, px_s, px_l = rgb_to_hsl(int(rgb[y, x, 0]), int(rgb[y, x, 1]), int(rgb[y, x, 2]))

            # 用亮度作为映射键：暗→阴影，中暗→标题，亮→底色
            if px_l < 0.15:
                # 暗部：映射到阴影色
                ratio = px_l / 0.15
                nr, ng, nb = hsl_to_rgb(
                    sh + (th - sh) * ratio,
                    ss + (ts - ss) * ratio,
                    sl + (tl - sl) * ratio * 0.5
                )
            elif px_l < 0.55:
                # 中间调：从阴影渐变到标题色
                ratio = (px_l - 0.15) / 0.4
                nr, ng, nb = hsl_to_rgb(
                    th,
                    ts * (0.6 + 0.4 * ratio),
                    sl + (tl - sl) * ratio
                )
            else:
                # 亮部：从标题色渐变到底色
                ratio = (px_l - 0.55) / 0.45
                nr, ng, nb = hsl_to_rgb(
                    bh + (th - bh) * (1 - ratio),
                    bs + (ts - bs) * (1 - ratio) * 0.5,
                    tl + (bl - tl) * ratio
                )

            # 保留原始饱和度特征
            nr = max(0, min(255, nr))
            ng = max(0, min(255, ng))
            nb = max(0, min(255, nb))

            result[y, x] = [nr, ng, nb]

    result_img = Image.fromarray(result.astype(np.uint8), mode="RGB")
    result_img.putalpha(Image.fromarray(alpha.astype(np.uint8)))
    return result_img


def main():
    # 验证源文件
    for f in BASE_COMPONENTS:
        path = SRC_DIR / f
        if not path.exists():
            print(f"错误: 源文件不存在 {path}")
            return

    print(f"{'='*60}")
    print(f"对话框组件主题生成器")
    print(f"源组件: {len(BASE_COMPONENTS)} 个 ({SRC_DIR})")
    print(f"主题数: {len(THEMES)} 组 (5高版 + 5卡通)")
    print(f"{'='*60}")

    total = 0
    for theme_dir, title_rgb, body_rgb, shadow_rgb, desc in THEMES:
        out_dir = OUT_BASE / theme_dir
        os.makedirs(out_dir, exist_ok=True)

        print(f"\n[{desc}] → {theme_dir}/")

        for f in BASE_COMPONENTS:
            src_img = Image.open(SRC_DIR / f)
            themed_img = apply_theme(src_img, title_rgb, body_rgb, shadow_rgb)
            themed_img.save(out_dir / f)
            total += 1

        # 输出该主题的颜色信息
        print(f"  标题: #{title_rgb[0]:02x}{title_rgb[1]:02x}{title_rgb[2]:02x}  |  "
              f"底色: #{body_rgb[0]:02x}{body_rgb[1]:02x}{body_rgb[2]:02x}  |  "
              f"阴影: #{shadow_rgb[0]:02x}{shadow_rgb[1]:02x}{shadow_rgb[2]:02x}")

    print(f"\n{'='*60}")
    print(f"完成! 共生成 {total} 个文件 ({len(THEMES)} 组 × {len(BASE_COMPONENTS)} 组件)")
    print(f"输出根目录: {OUT_BASE}")
    for theme_dir, *_ in THEMES:
        out_dir = OUT_BASE / theme_dir
        print(f"  {theme_dir}/ ({len(BASE_COMPONENTS)} 文件)")
    print(f"{'='*60}")


if __name__ == "__main__":
    main()

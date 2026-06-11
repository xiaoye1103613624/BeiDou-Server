#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
根据调色板.png的9种颜色，为images目录下的所有源图片生成9组颜色变体。
保留原有像素风格和比例，使用NEAREST上采样至高清尺寸，放大不失真。

@author 萧曵
@since 2026-05-29
"""
import os
import sys
import colorsys
import math
from pathlib import Path
from collections import Counter

import numpy as np
from PIL import Image

# ============================================================
# 配置参数（可根据需要修改）
# ============================================================
INPUT_DIR = Path(__file__).parent / "images"
OUTPUT_DIR = Path(__file__).parent / "output_variants"
PALETTE_FILE = INPUT_DIR / "调色板.png"
UPSCALE_FACTOR = 4  # 放大倍数（4x: 32→128, 保持像素风格不失真）

# ============================================================
# 第一步：从调色板3×3网格中提取9种主色
# ============================================================
def extract_palette_colors(palette_path):
    """将调色板图片按3×3网格划分，提取每个格子的主色
    调色板是3×3布局，每个格子代表一种主题色"""
    img = Image.open(palette_path).convert("RGB")
    arr = np.array(img)
    h, w = arr.shape[:2]
    ch, cw = h // 3, w // 3  # 每个格子的高宽

    palette_colors = []
    for row in range(3):
        for col in range(3):
            y1, y2 = row * ch, (row + 1) * ch
            x1, x2 = col * cw, (col + 1) * cw
            cell = arr[y1:y2, x1:x2].reshape(-1, 3)

            # 过滤白色/灰色背景，仅保留有色彩像素
            max_v = cell.max(axis=1)
            min_v = cell.min(axis=1)
            sat = (max_v - min_v) / (max_v + 1e-8)
            colored = cell[(sat > 0.15) & (max_v < 240) & (max_v > 20)]

            if len(colored) > 0:
                avg_color = colored.mean(axis=0).astype(int)
                palette_colors.append(tuple(avg_color))

    return palette_colors


# ============================================================
# 第二步：颜色转换 —— 将源图片色调替换为目标色调
# ============================================================
def apply_palette_color(src_img, target_rgb):
    """将RGBA图片的色调替换为目标调色板颜色，保留明暗层次"""
    rgba = src_img.convert("RGBA")
    arr = np.array(rgba).astype(np.float64)
    alpha = arr[:, :, 3].copy()  # 保留alpha通道
    rgb = arr[:, :, :3]

    # 目标颜色HSV
    tr, tg, tb = target_rgb
    th, ts, tv = colorsys.rgb_to_hsv(tr / 255, tg / 255, tb / 255)

    result = np.zeros_like(rgb)

    # 逐像素处理：保留明度(V)，替换色相(H)和饱和度(S)
    for y in range(rgb.shape[0]):
        for x in range(rgb.shape[1]):
            r, g, b = rgb[y, x] / 255.0
            a = alpha[y, x]

            if a < 10:  # 透明像素保持不变
                result[y, x] = [0, 0, 0]
                continue

            # 源像素HSV
            h, s, v = colorsys.rgb_to_hsv(r, g, b)

            # 仅对有色彩像素进行色相偏移，保留灰色/白色
            if s > 0.05 and v > 0.05 and v < 0.95:
                # 使用目标色相，调整饱和度比例
                new_h = th
                new_s = ts * (0.5 + 0.5 * s)  # 保留原图饱和度层次
                new_v = v  # 保留原图亮度层次（关键：维持明暗关系）
            else:
                # 接近灰/白/黑的像素只调整亮度
                new_h = th
                new_s = s * ts * 0.3
                new_v = v

            nr, ng, nb = colorsys.hsv_to_rgb(new_h, new_s, new_v)
            result[y, x] = [nr * 255, ng * 255, nb * 255]

    result_img = Image.fromarray(result.astype(np.uint8), mode="RGB")
    result_img.putalpha(Image.fromarray(alpha.astype(np.uint8)))
    return result_img


# ============================================================
# 第三步：像素风格上采样（NEAREST邻居，放大不失真）
# ============================================================
def upscale_pixelart(img, factor):
    """使用NEAREST上采样，保留像素艺术风格，任意倍数放大不失真"""
    if factor == 1:
        return img
    w, h = img.size
    return img.resize((w * factor, h * factor), Image.NEAREST)


# ============================================================
# 第四步：主流程
# ============================================================
def main():
    # 获取所有源图片（排除调色板自身）
    src_files = sorted([
        f for f in os.listdir(INPUT_DIR)
        if f.endswith('.png') and '调色板' not in f
    ])
    print(f"找到 {len(src_files)} 张源图片")

    # 提取9种调色板颜色（3×3网格）
    print(f"\n分析调色板: {PALETTE_FILE}")
    palette_colors = extract_palette_colors(PALETTE_FILE)

    # 为每种颜色定义中文名称（按3×3网格位置）
    color_names_cn = [
        "紫罗兰",  # (0,0) 左上 - 紫色
        "薰衣草",  # (0,1) 中上 - 浅紫
        "玫瑰红",  # (0,2) 右上 - 玫红
        "深海蓝",  # (1,0) 左中 - 蓝色
        "桃粉",    # (1,1) 中心 - 粉红
        "暗紫",    # (1,2) 右中 - 深紫
        "淡紫",    # (2,0) 左下 - 中紫
        "翡翠绿",  # (2,1) 中下 - 绿色
        "钢蓝",    # (2,2) 右下 - 灰蓝
    ]

    print(f"\n调色板9种颜色 (RGB + Hex + 中文名):")
    print(f"{'序号':<6}{'中文名':<10}{'RGB':<20}{'Hex':<10}{'色相':<8}{'饱和度':<8}{'明度':<8}")
    print("-" * 68)
    for i, c in enumerate(palette_colors):
        r, g, b = c
        h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
        hex_c = f"#{r:02x}{g:02x}{b:02x}"
        cn_name = color_names_cn[i]
        print(f"颜色{i + 1:<3} {cn_name:<8} RGB({r:3d},{g:3d},{b:3d})  {hex_c:<8} H={h * 360:3.0f}°  S={s:.2f}    V={v:.2f}")

    # 为每种调色板颜色创建输出目录并生成变体
    print(f"\n开始生成 {len(palette_colors)} 组颜色变体...")
    total_generated = 0

    for ci, (color, cn_name) in enumerate(zip(palette_colors, color_names_cn)):
        r, g, b = color
        hex_c = f"{r:02x}{g:02x}{b:02x}"
        color_dir_name = f"group_{ci + 1:02d}_{cn_name}_{hex_c}"
        color_dir = OUTPUT_DIR / color_dir_name
        os.makedirs(color_dir, exist_ok=True)

        print(f"\n[组{ci + 1}/9] {cn_name} {hex_c}")

        for src_file in src_files:
            src_path = INPUT_DIR / src_file
            try:
                # 加载源图片
                src_img = Image.open(src_path)

                # 应用调色板颜色
                colored_img = apply_palette_color(src_img, color)

                # 上采样至高清（保留像素风格）
                hd_img = upscale_pixelart(colored_img, UPSCALE_FACTOR)

                # 输出
                out_path = color_dir / src_file
                hd_img.save(out_path)
                total_generated += 1

            except Exception as e:
                print(f"  处理 {src_file} 失败: {e}")

        print(f"  已生成 {len(src_files)} 张图片 → {color_dir_name}")

    print(f"\n{'=' * 60}")
    print(f"完成! 共生成 {total_generated} 张图片")
    print(f"输出目录: {OUTPUT_DIR}")
    print(f"放大倍数: {UPSCALE_FACTOR}x (像素风格NEAREST上采样，放大不失真)")
    print(f"组织方式: group_01~09_<中文名>_<色值>/ 各含 {len(src_files)} 张图片")
    for i, cn_name in enumerate(color_names_cn):
        c = palette_colors[i]
        print(f"  {i + 1}. {cn_name} #{c[0]:02x}{c[1]:02x}{c[2]:02x}")
    print(f"{'=' * 60}")


if __name__ == "__main__":
    main()

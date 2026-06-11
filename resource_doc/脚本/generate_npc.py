#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
调用 ModelsLab MapleStory Style V2.0 API 生成冒险岛风格 NPC 图片。
支持批量生成，从配置文件读取 NPC 描述列表。

使用前:
  1. 注册 ModelsLab: https://modelslab.com
  2. 获取 API Key: https://modelslab.com/dashboard
  3. 设置环境变量: export MODELSLAB_API_KEY="你的key"
  或者直接修改下方 API_KEY

@author 萧曵
@since 2026-05-29
"""
import json
import os
import time
import sys
from pathlib import Path
from io import BytesIO

import requests

# ============================================================
# 配置
# ============================================================
API_KEY = os.environ.get("MODELSLAB_API_KEY", "")  # 在此填入你的 API Key
API_URL = "https://modelslab.com/api/v6/images/text2img"
MODEL_ID = "maplestory-style-v2-0"
OUTPUT_DIR = Path(__file__).parent / "images" / "npc"
SLEEP_BETWEEN = 2  # API 调用间隔（秒），避免限流

# 默认生成参数
DEFAULT_PARAMS = {
    "width": 512,
    "height": 512,
    "negative_prompt": "worst quality, low quality, bad anatomy, bad hands, missing fingers, extra fingers, blurry",
    "num_inference_steps": 30,
    "guidance_scale": 7.5,
    "samples": 1,
}

# ============================================================
# NPC 生成配置列表
# 格式: (文件名前缀, prompt描述, 自定义参数可选)
# ============================================================
NPC_LIST = [
    # ---- 示例（按需修改） ----
    # ("npc_warrior", "a brave warrior NPC, chibi, full body, standing, armor, sword, MapleStory style, game sprite, white background"),
    # ("npc_mage", "a cute mage NPC girl, chibi, holding magic staff, blue robe, MapleStory style, pixel art, white background"),
    # ("npc_shopkeeper", "a friendly shopkeeper NPC, chibi, apron, smiling, items on counter, MapleStory style, white background"),
]


def generate_image(prompt, output_path, extra_params=None):
    """
    调用 ModelsLab API 生成图片并保存到指定路径。

    Args:
        prompt: 提示词（英文）
        output_path: 输出文件路径
        extra_params: 额外参数（覆盖默认值）

    Returns:
        bool: 是否成功
    """
    if not API_KEY:
        print("错误: 未设置 API Key。请设置环境变量 MODELSLAB_API_KEY 或修改脚本中的 API_KEY")
        return False

    params = {**DEFAULT_PARAMS, "prompt": prompt, "model_id": MODEL_ID, "key": API_KEY}
    if extra_params:
        params.update(extra_params)

    print(f"  生成: {prompt[:80]}...")

    try:
        resp = requests.post(API_URL, headers={"Content-Type": "application/json"}, json=params, timeout=120)
        result = resp.json()

        # 检查不同响应格式
        if result.get("status") == "success":
            img_url = result.get("output") or (result.get("meta", {}).get("image_url"))
        elif result.get("output"):
            img_url = result["output"] if isinstance(result["output"], str) else result["output"][0]
        elif result.get("meta") and result["meta"].get("image_url"):
            img_url = result["meta"]["image_url"]
        else:
            print(f"  API 返回异常: {json.dumps(result, ensure_ascii=False)[:300]}")
            return False

        if not img_url:
            print(f"  未找到图片 URL: {json.dumps(result, ensure_ascii=False)[:200]}")
            return False

        # 下载图片
        img_resp = requests.get(img_url, timeout=60)
        img_resp.raise_for_status()

        output_path = Path(output_path)
        output_path.parent.mkdir(parents=True, exist_ok=True)
        output_path.write_bytes(img_resp.content)
        print(f"  已保存: {output_path.name} ({len(img_resp.content) / 1024:.1f}KB)")
        return True

    except requests.exceptions.RequestException as e:
        print(f"  网络错误: {e}")
        return False
    except (KeyError, IndexError, TypeError) as e:
        print(f"  解析响应失败: {e}")
        return False


def main():
    if not NPC_LIST:
        print("=" * 60)
        print("NPC 图片生成器 (ModelsLab MapleStory Style V2.0)")
        print("=" * 60)
        print()
        print("使用步骤:")
        print(f"  1. 注册 https://modelslab.com 获取 API Key")
        print(f"  2. 设置环境变量: export MODELSLAB_API_KEY=\"你的key\"")
        print(f"    或在脚本中修改 API_KEY 变量")
        print(f"  3. 编辑 NPC_LIST 列表，填入要生成的 NPC 描述")
        print(f"  4. 重新运行本脚本")
        print()
        print(f"输出目录: {OUTPUT_DIR}")
        print(f"预估成本: $0.0047/张")
        return

    print(f"{'=' * 60}")
    print(f"冒险岛 NPC 图片生成器")
    print(f"模型: {MODEL_ID}")
    print(f"任务数: {len(NPC_LIST)}")
    print(f"输出目录: {OUTPUT_DIR}")
    print(f"预估成本: ${len(NPC_LIST) * 0.0047:.3f}")
    print(f"{'=' * 60}")

    success, fail = 0, 0
    for i, item in enumerate(NPC_LIST):
        name, prompt = item[0], item[1]
        extra = item[2] if len(item) > 2 else None
        out_path = OUTPUT_DIR / f"{name}.png"

        if out_path.exists():
            print(f"[{i + 1}/{len(NPC_LIST)}] {name} 已存在，跳过")
            success += 1
            continue

        print(f"[{i + 1}/{len(NPC_LIST)}] {name}")
        if generate_image(prompt, out_path, extra):
            success += 1
        else:
            fail += 1

        if i < len(NPC_LIST) - 1:
            time.sleep(SLEEP_BETWEEN)

    print(f"\n{'=' * 60}")
    print(f"完成! 成功: {success}, 失败: {fail}")
    print(f"输出目录: {OUTPUT_DIR}")
    print(f"{'=' * 60}")


if __name__ == "__main__":
    main()

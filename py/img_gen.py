from PIL import Image, ImageDraw, ImageFont

# 你的 4 种背景颜色
colors = ["#7BC09B", "#5AC0F7", "#F7C0D3", "#58BDF2"]
colors_name = ["绿", "蓝1", "粉", "蓝2"]

# 6 种文字内容
texts = [
    ["聊天"],
    ["对", "有人"],    # 对应“对所有人”（图中分两行）
    ["悄", "话"],      # 对应“悄话”（图中分两行）
    ["对", "组员"],    # 对应“对组员”
    ["对", "好友"],    # 对应“对好友”
    ["对", "家族"]     # 对应“对家族”
]

for color_idx, color in enumerate(colors):
    color_dir = f"Button_Color_{colors_name[color_idx]}"
    import os
    os.makedirs(color_dir, exist_ok=True)

    for i, text_content in enumerate(texts):
        # 创建 64x64 的大画布，用于画得更精细，最后缩成 32x32
        img = Image.new('RGB', (64, 64), color)
        draw = ImageDraw.Draw(img)

        # 绘制复古的深色边框（模拟原图的黑框）
        draw.rectangle([(0, 0), (63, 63)], outline="#4A1A3D", width=4)

        # 加载一个可用的中文字体，字体大小设为 28（组合成像素感）
        try:
            # 通常 Windows 系统自带 'msyh.ttc' (微软雅黑) 或 'simhei.ttf'
            font = ImageFont.truetype("msyh.ttc", 28)
        except:
            font = ImageFont.load_default()

        # 文字颜色和阴影
        text_color = (255, 255, 255) # 白字
        shadow_color = (0, 0, 0)     # 黑阴影

        # 计算文字位置（先画阴影，再画文字）
        # 为了还原像素感，我们要把 64x64 的图缩放回 32x32
        # 这里简化处理：直接把字写在中间
        if len(text_content) == 1:
            text = text_content[0]
            w, h = draw.textbbox((0, 0), text, font=font)[2:]
            draw.text(((64 - w) / 2 - 1, (64 - h) / 2 - 1), text, font=font, fill=shadow_color) # 阴影
            draw.text(((64 - w) / 2, (64 - h) / 2), text, font=font, fill=text_color)
        else:
            # 两行字的情况
            row1, row2 = text_content[0], text_content[1]
            w1, h1 = draw.textbbox((0, 0), row1, font=font)[2:]
            w2, h2 = draw.textbbox((0, 0), row2, font=font)[2:]

            draw.text(((64 - w1) / 2 - 1, 10), row1, font=font, fill=shadow_color) # 阴影
            draw.text(((64 - w1) / 2, 10), row1, font=font, fill=text_color)

            draw.text(((64 - w2) / 2 - 1, 10 + h1 + 5), row2, font=font, fill=shadow_color) # 阴影
            draw.text(((64 - w2) / 2, 10 + h1 + 5), row2, font=font, fill=text_color)

        # 关键步骤：将 64x64 的图像抗锯齿缩放回 32x32，或者缩成 320x320（如果你想追求高分辨率）
        final_img = img.resize((32, 32), Image.Resampling.NEAREST)

        # 如果你想大图（可以接近 500KB），把上面 resize 改为 (800, 800) 即可：
        # final_img = img.resize((800, 800), Image.Resampling.NEAREST)

        final_img.save(f"{color_dir}/{text_content}.png")

print("图片全部生成完毕！")
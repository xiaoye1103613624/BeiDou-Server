"""Measure Trunk/backgrnd horizontal divider Y positions at left vs right columns."""
from PIL import Image
import sys
from pathlib import Path

def find_horizontal_edges(img, x, y_start=90, y_end=None):
    """Find Y positions where luminance changes sharply (row dividers)."""
    if y_end is None:
        y_end = img.height - 30
    px = img.load()
    edges = []
    prev = sum(px[x, y_start][:3]) / 3
    for y in range(y_start + 1, y_end):
        cur = sum(px[x, y][:3]) / 3
        if abs(cur - prev) > 18:
            edges.append(y)
        prev = cur
    # cluster nearby edges
    clusters = []
    for e in edges:
        if clusters and e - clusters[-1][-1] <= 2:
            clusters[-1].append(e)
        else:
            clusters.append([e])
    return [sum(c) // len(c) for c in clusters]

def slot_box_top(img, x, y_start, y_end):
    """Find top Y of grey slot box near column x."""
    px = img.load()
    for y in range(y_start, y_end):
        r, g, b, a = px[x, y]
        if a > 100 and 100 <= g <= 180 and 140 <= b <= 220 and r < 200:
            # walk up to find box top
            top = y
            while top > y_start and sum(px[x, top-1][:3])/3 > sum(px[x, top][:3])/3 - 5:
                top -= 1
            return top
    return None

def analyze(path):
    img = Image.open(path).convert("RGBA")
    print(f"\n=== {Path(path).name} {img.width}x{img.height} ===")
    for x, label in [(35, "left-slot"), (200, "mid"), (350, "right-slot")]:
        edges = find_horizontal_edges(img, x)
        print(f"  {label} x={x}: {len(edges)} edges, first 12 Y={edges[:12]}")
    print("  Grid check 107+40n:", [107 + 40*n for n in range(10)])
    print("  Grid check 147+40n:", [147 + 40*n for n in range(10)])
    for x, label, base in [(35, "left", 107), (350, "right", 147)]:
        slots = []
        y = base
        while y < img.height - 40:
            t = slot_box_top(img, x, y - 5, y + 45)
            if t is not None:
                slots.append(t)
            y += 40
        print(f"  {label} slot tops (base {base}): {slots[:10]}")

if __name__ == "__main__":
    files = sys.argv[1:] or [
        "_trunk_work/Trunk_backgrnd_src.png",
        "_trunk_work/orig_318.png",
    ]
    base = Path(__file__).parent
    for f in files:
        p = base / f if not Path(f).is_absolute() else Path(f)
        if p.exists():
            analyze(p)

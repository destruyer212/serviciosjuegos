from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageOps
import json
import math
import random
import shutil


BASE = Path(r"C:\SpringProjectsnew\serviciosjuegos\IMG3")
SRC = Path(
    r"C:\SpringProjectsnew\serviciosjuegos\java-puzzle-bloques-libgdx\core\src\main\resources\images"
)

created = []
copied = []
skipped = []
organized = []
normalized = []


def target(rel):
    p = BASE / rel.replace("/", "\\")
    p.parent.mkdir(parents=True, exist_ok=True)
    return p


def save_if_missing(img, rel):
    p = target(rel)
    if p.exists():
        skipped.append(rel)
        return False
    img.save(p, "PNG")
    created.append(rel)
    return True


def copy_resize(src_path, rel, size, mode="contain", bg=(0, 0, 0, 0)):
    p = target(rel)
    if p.exists():
        skipped.append(rel)
        return False
    img = Image.open(src_path).convert("RGBA")
    if mode == "cover":
        out = ImageOps.fit(
            img, size, method=Image.Resampling.LANCZOS, centering=(0.5, 0.5)
        )
    else:
        out = Image.new("RGBA", size, bg)
        im = ImageOps.contain(img, size, method=Image.Resampling.LANCZOS)
        out.alpha_composite(im, ((size[0] - im.width) // 2, (size[1] - im.height) // 2))
    out.save(p, "PNG")
    copied.append(rel)
    return True


def backup_before_replace(rel):
    src = target(rel)
    if not src.exists():
        return
    backup = BASE / "_originales_altas" / rel.replace("/", "\\")
    backup.parent.mkdir(parents=True, exist_ok=True)
    if not backup.exists():
        shutil.copy2(src, backup)


def replace_png(rel, img):
    backup_before_replace(rel)
    p = target(rel)
    img.save(p, "PNG")
    normalized.append(rel)


def resize_exact(rel, size, mode="cover"):
    p = target(rel)
    if not p.exists():
        return
    img = Image.open(p).convert("RGBA")
    if img.size == size:
        return
    backup_before_replace(rel)
    if mode == "cover":
        out = ImageOps.fit(img, size, method=Image.Resampling.LANCZOS, centering=(0.5, 0.5))
    else:
        out = Image.new("RGBA", size, (0, 0, 0, 0))
        im = ImageOps.contain(img, size, method=Image.Resampling.LANCZOS)
        out.alpha_composite(im, ((size[0] - im.width) // 2, (size[1] - im.height) // 2))
    out.save(p, "PNG")
    normalized.append(rel)


def scaled(size, scale=4):
    return (size[0] * scale, size[1] * scale)


def down(img, size):
    return img.resize(size, Image.Resampling.LANCZOS)


def gradient_rect(size, top, bottom, radius=12, outline=None, width=2):
    scale = 4
    w, h = scaled(size, scale)
    base = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    grad = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw_grad = ImageDraw.Draw(grad)
    for y in range(h):
        t = y / (h - 1) if h > 1 else 0
        col = tuple(int(top[i] * (1 - t) + bottom[i] * t) for i in range(4))
        draw_grad.line([(0, y), (w, y)], fill=col)

    mask = Image.new("L", (w, h), 0)
    draw_mask = ImageDraw.Draw(mask)
    draw_mask.rounded_rectangle(
        (4 * scale, 4 * scale, w - 4 * scale, h - 7 * scale),
        radius * scale,
        fill=255,
    )
    base.alpha_composite(Image.composite(grad, Image.new("RGBA", (w, h), (0, 0, 0, 0)), mask))
    d = ImageDraw.Draw(base)
    if outline:
        d.rounded_rectangle(
            (4 * scale, 4 * scale, w - 4 * scale, h - 7 * scale),
            radius * scale,
            outline=outline,
            width=width * scale,
        )
    d.rounded_rectangle(
        (10 * scale, 8 * scale, w - 10 * scale, h // 3),
        max(1, radius * scale // 2),
        fill=(255, 255, 255, 45),
    )
    return down(base, size)


def star_points(cx, cy, outer, inner, points=5, rot=-math.pi / 2):
    pts = []
    for i in range(points * 2):
        r = outer if i % 2 == 0 else inner
        a = rot + i * math.pi / points
        pts.append((cx + math.cos(a) * r, cy + math.sin(a) * r))
    return pts


def draw_star(size):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    cx, cy = w / 2, h / 2
    outer = min(w, h) * 0.36
    inner = outer * 0.52

    glow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    gd = ImageDraw.Draw(glow)
    for i, alpha in enumerate([70, 45, 25]):
        r = outer * (1.4 + i * 0.25)
        gd.regular_polygon((cx, cy, r), n_sides=5, rotation=-90, fill=(255, 190, 0, alpha))
    img.alpha_composite(glow.filter(ImageFilter.GaussianBlur(int(outer * 0.22))))

    d = ImageDraw.Draw(img)
    pts = star_points(cx, cy, outer, inner)
    d.polygon(pts, fill=(255, 178, 18, 255), outline=(116, 64, 0, 255))
    d.line(pts + [pts[0]], fill=(255, 225, 42, 255), width=max(3, int(w * 0.025)))
    inner_pts = star_points(cx - outer * 0.07, cy - outer * 0.08, outer * 0.67, inner * 0.67)
    d.polygon(inner_pts, fill=(255, 238, 78, 215))

    for sx, sy, rr in [
        (cx + outer * 0.55, cy - outer * 0.55, outer * 0.12),
        (cx - outer * 0.58, cy + outer * 0.35, outer * 0.1),
    ]:
        d.line([(sx - rr, sy), (sx + rr, sy)], fill=(255, 255, 255, 230), width=max(1, int(rr * 0.18)))
        d.line([(sx, sy - rr), (sx, sy + rr)], fill=(255, 255, 255, 230), width=max(1, int(rr * 0.18)))
    return down(img, size)


def radial_glow(size, color=(255, 190, 0), strength=180):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    pix = img.load()
    cx, cy = w / 2, h / 2
    maxr = min(w, h) * 0.48
    for y in range(h):
        for x in range(w):
            r = math.hypot(x - cx, y - cy) / maxr
            if r < 1:
                a = int(strength * ((1 - r) ** 2.2))
                pix[x, y] = (*color, a)
    return down(img, size)


def sparkle(size, color=(255, 255, 255), cyan=True):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    r = min(w, h) * 0.38
    pts = [
        (cx, cy - r),
        (cx + r * 0.12, cy - r * 0.12),
        (cx + r, cy),
        (cx + r * 0.12, cy + r * 0.12),
        (cx, cy + r),
        (cx - r * 0.12, cy + r * 0.12),
        (cx - r, cy),
        (cx - r * 0.12, cy - r * 0.12),
    ]
    glow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    gd = ImageDraw.Draw(glow)
    gd.polygon(pts, fill=(150, 245, 255, 90) if cyan else (255, 230, 100, 90))
    img.alpha_composite(glow.filter(ImageFilter.GaussianBlur(int(r * 0.18))))
    d.polygon(pts, fill=(*color, 245))
    d.ellipse((cx - r * 0.16, cy - r * 0.16, cx + r * 0.16, cy + r * 0.16), fill=(255, 255, 255, 255))
    return down(img, size)


def coin(size=(64, 64)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    r = min(w, h) * 0.36

    glow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    gd = ImageDraw.Draw(glow)
    gd.ellipse((cx - r * 1.3, cy - r * 1.3, cx + r * 1.3, cy + r * 1.3), fill=(255, 190, 0, 65))
    img.alpha_composite(glow.filter(ImageFilter.GaussianBlur(int(r * 0.35))))
    d.ellipse((cx - r, cy - r, cx + r, cy + r), fill=(230, 137, 0, 255), outline=(96, 58, 0, 255), width=max(2, int(w * 0.018)))
    d.ellipse((cx - r * 0.82, cy - r * 0.82, cx + r * 0.82, cy + r * 0.82), fill=(255, 202, 32, 255), outline=(255, 232, 94, 255), width=max(2, int(w * 0.02)))
    d.arc((cx - r * 0.66, cy - r * 0.66, cx + r * 0.66, cy + r * 0.66), 200, 335, fill=(255, 255, 180, 230), width=max(2, int(w * 0.025)))
    d.polygon(star_points(cx, cy, r * 0.35, r * 0.17), fill=(255, 234, 62, 255), outline=(184, 105, 0, 255))
    return down(img, size)


def rock_prop(size=(96, 96), small=False):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w * 0.5, h * 0.56
    r = min(w, h) * (0.38 if not small else 0.32)
    pts = [
        (cx - r * 0.95, cy - r * 0.10),
        (cx - r * 0.60, cy - r * 0.62),
        (cx - r * 0.05, cy - r * 0.78),
        (cx + r * 0.58, cy - r * 0.62),
        (cx + r * 0.94, cy - r * 0.08),
        (cx + r * 0.70, cy + r * 0.55),
        (cx + r * 0.08, cy + r * 0.78),
        (cx - r * 0.66, cy + r * 0.55),
    ]
    d.polygon(pts, fill=(126, 111, 151, 255), outline=(45, 37, 64, 255))
    d.polygon([(cx - r * 0.55, cy - r * 0.35), (cx - r * 0.05, cy - r * 0.62), (cx + r * 0.22, cy - r * 0.30), (cx - r * 0.08, cy - r * 0.05)], fill=(190, 178, 205, 210))
    d.polygon([(cx + r * 0.22, cy - r * 0.18), (cx + r * 0.60, cy - r * 0.38), (cx + r * 0.72, cy + r * 0.12), (cx + r * 0.32, cy + r * 0.25)], fill=(92, 77, 126, 210))
    d.polygon([(cx - r * 0.28, cy + r * 0.10), (cx + r * 0.06, cy + r * 0.28), (cx - r * 0.14, cy + r * 0.55), (cx - r * 0.48, cy + r * 0.42)], fill=(105, 90, 137, 220))
    return down(img, size)


def bush_prop(size=(64, 64)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    blobs = [
        (0.28, 0.58, 0.24, (53, 154, 55, 255)),
        (0.45, 0.42, 0.28, (70, 188, 65, 255)),
        (0.66, 0.56, 0.25, (40, 139, 51, 255)),
        (0.50, 0.66, 0.27, (47, 166, 56, 255)),
    ]
    for x, y, r, col in blobs:
        d.ellipse((w * (x - r), h * (y - r), w * (x + r), h * (y + r)), fill=col, outline=(18, 92, 37, 210), width=2 * scale)
    d.ellipse((w * 0.34, h * 0.30, w * 0.46, h * 0.42), fill=(144, 231, 93, 190))
    d.ellipse((w * 0.56, h * 0.38, w * 0.68, h * 0.50), fill=(120, 220, 86, 170))
    return down(img, size)


def sprout_prop(size=(32, 48)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.line((w * 0.50, h * 0.78, w * 0.50, h * 0.38), fill=(38, 129, 43, 255), width=4 * scale)
    d.ellipse((w * 0.12, h * 0.34, w * 0.52, h * 0.58), fill=(77, 202, 76, 255), outline=(31, 123, 42, 255), width=scale)
    d.ellipse((w * 0.48, h * 0.28, w * 0.88, h * 0.52), fill=(98, 223, 82, 255), outline=(31, 123, 42, 255), width=scale)
    d.ellipse((w * 0.28, h * 0.77, w * 0.72, h * 0.90), fill=(40, 109, 37, 95))
    return down(img, size)


def flower_prop(size=(32, 32)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    for a in range(0, 360, 60):
        rad = math.radians(a)
        px, py = cx + math.cos(rad) * w * 0.18, cy + math.sin(rad) * h * 0.18
        d.ellipse((px - w * 0.16, py - h * 0.14, px + w * 0.16, py + h * 0.14), fill=(246, 93, 181, 255), outline=(154, 42, 117, 180), width=scale)
    d.ellipse((cx - w * 0.13, cy - h * 0.13, cx + w * 0.13, cy + h * 0.13), fill=(255, 220, 63, 255), outline=(149, 97, 0, 210), width=scale)
    return down(img, size)


def crystal_prop(size=(48, 64)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    glow = radial_glow((w, h), (67, 210, 255), 120)
    img.alpha_composite(glow)
    d = ImageDraw.Draw(img)
    d.polygon([(w * 0.40, h * 0.14), (w * 0.66, h * 0.46), (w * 0.55, h * 0.86), (w * 0.30, h * 0.56)], fill=(70, 218, 255, 235), outline=(31, 83, 164, 255))
    d.polygon([(w * 0.20, h * 0.36), (w * 0.43, h * 0.55), (w * 0.37, h * 0.88), (w * 0.13, h * 0.68)], fill=(118, 91, 235, 230), outline=(43, 37, 139, 255))
    d.polygon([(w * 0.62, h * 0.34), (w * 0.84, h * 0.58), (w * 0.70, h * 0.86), (w * 0.54, h * 0.62)], fill=(93, 152, 255, 230), outline=(31, 83, 164, 255))
    return down(img, size)


def stump_prop(size=(64, 48)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((w * 0.24, h * 0.32, w * 0.76, h * 0.82), 8 * scale, fill=(151, 91, 43, 255), outline=(80, 47, 22, 255), width=2 * scale)
    d.ellipse((w * 0.20, h * 0.14, w * 0.80, h * 0.50), fill=(191, 126, 65, 255), outline=(80, 47, 22, 255), width=2 * scale)
    d.ellipse((w * 0.34, h * 0.23, w * 0.66, h * 0.42), outline=(105, 64, 30, 220), width=2 * scale)
    d.arc((w * 0.42, h * 0.27, w * 0.56, h * 0.38), 0, 330, fill=(105, 64, 30, 220), width=scale)
    d.ellipse((w * 0.18, h * 0.76, w * 0.82, h * 0.94), fill=(53, 148, 52, 120))
    return down(img, size)


def crate_prop(size=(64, 64)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((w * 0.16, h * 0.18, w * 0.84, h * 0.84), 5 * scale, fill=(161, 101, 44, 255), outline=(83, 48, 18, 255), width=3 * scale)
    d.line((w * 0.20, h * 0.28, w * 0.80, h * 0.28), fill=(211, 145, 68, 255), width=3 * scale)
    d.line((w * 0.20, h * 0.50, w * 0.80, h * 0.50), fill=(105, 61, 23, 210), width=3 * scale)
    d.line((w * 0.20, h * 0.76, w * 0.80, h * 0.76), fill=(211, 145, 68, 180), width=2 * scale)
    d.line((w * 0.23, h * 0.22, w * 0.77, h * 0.80), fill=(83, 48, 18, 230), width=4 * scale)
    d.line((w * 0.77, h * 0.22, w * 0.23, h * 0.80), fill=(83, 48, 18, 230), width=4 * scale)
    return down(img, size)


def board_frame(size=(512, 512)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    glow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    gd = ImageDraw.Draw(glow)
    for i, a in [(0, 130), (12, 80), (26, 45)]:
        gd.rounded_rectangle(
            (38 * scale - i * scale, 38 * scale - i * scale, w - 38 * scale + i * scale, h - 38 * scale + i * scale),
            44 * scale + i * scale,
            outline=(23, 216, 255, a),
            width=8 * scale,
        )
    img.alpha_composite(glow.filter(ImageFilter.GaussianBlur(6 * scale)))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((42 * scale, 42 * scale, w - 42 * scale, h - 42 * scale), 38 * scale, outline=(99, 244, 255, 255), width=10 * scale)
    d.rounded_rectangle((60 * scale, 60 * scale, w - 60 * scale, h - 60 * scale), 24 * scale, outline=(8, 57, 130, 230), width=8 * scale)
    return down(img, size)


def inner_glow(size=(512, 512)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    for i in range(90):
        a = int(70 * (1 - i / 90) ** 2)
        d.rounded_rectangle((i * scale, i * scale, w - i * scale, h - i * scale), 35 * scale, outline=(70, 220, 255, a), width=2 * scale)
    return down(img, size)


def panel_hologram(size=(512, 512), program=False):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    glow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    gd = ImageDraw.Draw(glow)
    gd.rounded_rectangle((22 * scale, 22 * scale, w - 22 * scale, h - 22 * scale), 34 * scale, outline=(20, 220, 255, 155), width=8 * scale, fill=(4, 16, 55, 175))
    img.alpha_composite(glow.filter(ImageFilter.GaussianBlur(5 * scale)))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((28 * scale, 28 * scale, w - 28 * scale, h - 28 * scale), 30 * scale, fill=(5, 22, 70, 185), outline=(70, 235, 255, 230), width=4 * scale)
    d.rounded_rectangle((44 * scale, 44 * scale, w - 44 * scale, h - 44 * scale), 20 * scale, outline=(120, 250, 255, 65), width=2 * scale)
    if program:
        d.rounded_rectangle((38 * scale, 36 * scale, w - 38 * scale, 110 * scale), 22 * scale, fill=(16, 116, 207, 210), outline=(113, 239, 255, 220), width=3 * scale)
        for x in range(70 * scale, w - 70 * scale, 48 * scale):
            d.line((x, 135 * scale, x, h - 65 * scale), fill=(90, 220, 255, 25), width=scale)
        for y in range(150 * scale, h - 65 * scale, 48 * scale):
            d.line((65 * scale, y, w - 65 * scale, y), fill=(90, 220, 255, 25), width=scale)
    return down(img, size)


def speech_bubble(size=(512, 256)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    shadow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    sd = ImageDraw.Draw(shadow)
    sd.rounded_rectangle((32 * scale, 28 * scale, w - 28 * scale, h - 56 * scale), 38 * scale, fill=(0, 0, 0, 60))
    sd.polygon([(92 * scale, h - 58 * scale), (126 * scale, h - 28 * scale), (142 * scale, h - 58 * scale)], fill=(0, 0, 0, 60))
    img.alpha_composite(shadow.filter(ImageFilter.GaussianBlur(5 * scale)))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((28 * scale, 24 * scale, w - 32 * scale, h - 60 * scale), 38 * scale, fill=(255, 255, 255, 255), outline=(210, 220, 235, 255), width=3 * scale)
    d.polygon([(92 * scale, h - 60 * scale), (126 * scale, h - 26 * scale), (145 * scale, h - 60 * scale)], fill=(255, 255, 255, 255), outline=(210, 220, 235, 255))
    return down(img, size)


def button(size, top, bottom, icon="play", outline=(255, 255, 255, 100)):
    img = gradient_rect(size, top, bottom, radius=max(12, size[1] // 3), outline=outline, width=3)
    scale = 4
    w, h = scaled(size, scale)
    hi = img.resize((w, h), Image.Resampling.LANCZOS)
    d = ImageDraw.Draw(hi)
    cx, cy = w * 0.22, h * 0.50
    col = (255, 255, 255, 190)
    stroke = max(5, int(h * 0.055))
    if icon == "play":
        d.polygon([(cx - h * 0.15, cy - h * 0.22), (cx - h * 0.15, cy + h * 0.22), (cx + h * 0.22, cy)], fill=col)
    elif icon == "step":
        d.polygon([(cx - h * 0.20, cy - h * 0.21), (cx - h * 0.20, cy + h * 0.21), (cx + h * 0.05, cy)], fill=col)
        d.polygon([(cx + h * 0.02, cy - h * 0.21), (cx + h * 0.02, cy + h * 0.21), (cx + h * 0.28, cy)], fill=col)
    elif icon == "undo":
        d.arc((cx - h * 0.28, cy - h * 0.28, cx + h * 0.28, cy + h * 0.28), 125, 345, fill=col, width=stroke)
        d.polygon([(cx - h * 0.32, cy - h * 0.02), (cx - h * 0.12, cy - h * 0.18), (cx - h * 0.10, cy + h * 0.07)], fill=col)
    elif icon == "trash":
        d.rounded_rectangle((cx - h * 0.18, cy - h * 0.08, cx + h * 0.18, cy + h * 0.22), 5 * scale, outline=col, width=stroke)
        d.rectangle((cx - h * 0.23, cy - h * 0.20, cx + h * 0.23, cy - h * 0.13), fill=col)
    elif icon == "menu":
        for yy in [-0.16, 0, 0.16]:
            d.rounded_rectangle((cx - h * 0.22, cy + h * yy - h * 0.025, cx + h * 0.22, cy + h * yy + h * 0.025), h * 0.025, fill=col)
    elif icon == "code":
        d.line((cx - h * 0.16, cy, cx - h * 0.02, cy - h * 0.16), fill=col, width=stroke)
        d.line((cx - h * 0.16, cy, cx - h * 0.02, cy + h * 0.16), fill=col, width=stroke)
        d.line((cx + h * 0.16, cy, cx + h * 0.02, cy - h * 0.16), fill=col, width=stroke)
        d.line((cx + h * 0.16, cy, cx + h * 0.02, cy + h * 0.16), fill=col, width=stroke)
    return down(hi, size)


def icon_heart(size=(64, 64)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    pts = []
    for i in range(220):
        t = 2 * math.pi * i / 220
        x = 16 * math.sin(t) ** 3
        y = -(13 * math.cos(t) - 5 * math.cos(2 * t) - 2 * math.cos(3 * t) - math.cos(4 * t))
        pts.append((w / 2 + x * w / 38, h * 0.52 + y * h / 42))
    d.polygon(pts, fill=(232, 36, 70, 255), outline=(120, 8, 34, 255))
    d.ellipse((w * 0.32, h * 0.24, w * 0.48, h * 0.38), fill=(255, 150, 165, 160))
    return down(img, size)


def planet_icon(size=(64, 64)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy, r = w / 2, h / 2, w * 0.28
    d.ellipse((cx - r, cy - r, cx + r, cy + r), fill=(91, 62, 210, 255), outline=(39, 24, 103, 255), width=3 * scale)
    d.arc((cx - r * 1.55, cy - r * 0.45, cx + r * 1.55, cy + r * 0.45), 10, 170, fill=(96, 224, 255, 220), width=6 * scale)
    d.arc((cx - r * 1.55, cy - r * 0.45, cx + r * 1.55, cy + r * 0.45), 190, 350, fill=(96, 224, 255, 145), width=6 * scale)
    return down(img, size)


def globe_icon(size=(64, 64)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy, r = w / 2, h / 2, w * 0.34
    d.ellipse((cx - r, cy - r, cx + r, cy + r), fill=(41, 158, 226, 255), outline=(9, 65, 115, 255), width=3 * scale)
    d.pieslice((cx - r * 0.9, cy - r * 0.8, cx + r * 0.2, cy + r * 0.2), 85, 205, fill=(82, 207, 124, 255))
    d.pieslice((cx - r * 0.1, cy - r * 0.15, cx + r * 0.95, cy + r * 0.85), 250, 75, fill=(71, 194, 112, 255))
    return down(img, size)


def chest(opened=True, size=(96, 96)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    if opened:
        img.alpha_composite(radial_glow((w, h), (255, 210, 50), 180))
        d.polygon([(w * 0.2, h * 0.35), (w * 0.5, h * 0.16), (w * 0.8, h * 0.35), (w * 0.7, h * 0.48), (w * 0.3, h * 0.48)], fill=(255, 186, 35, 255), outline=(93, 50, 0, 255))
    else:
        d.rounded_rectangle((w * 0.18, h * 0.24, w * 0.82, h * 0.50), 12 * scale, fill=(238, 168, 25, 255), outline=(93, 50, 0, 255), width=3 * scale)
    d.rounded_rectangle((w * 0.16, h * 0.44, w * 0.84, h * 0.78), 8 * scale, fill=(213, 128, 16, 255), outline=(75, 44, 0, 255), width=3 * scale)
    d.rectangle((w * 0.45, h * 0.42, w * 0.55, h * 0.78), fill=(255, 217, 75, 255))
    d.rounded_rectangle((w * 0.42, h * 0.52, w * 0.58, h * 0.66), 4 * scale, fill=(110, 63, 0, 255))
    return down(img, size)


def trophy(size=(96, 96)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    gold, edge = (245, 179, 31, 255), (112, 69, 0, 255)
    d.rounded_rectangle((w * 0.32, h * 0.16, w * 0.68, h * 0.50), 10 * scale, fill=gold, outline=edge, width=3 * scale)
    d.arc((w * 0.13, h * 0.21, w * 0.40, h * 0.55), 80, 275, fill=edge, width=5 * scale)
    d.arc((w * 0.60, h * 0.21, w * 0.87, h * 0.55), -95, 100, fill=edge, width=5 * scale)
    d.rectangle((w * 0.46, h * 0.50, w * 0.54, h * 0.70), fill=gold)
    d.rounded_rectangle((w * 0.24, h * 0.76, w * 0.76, h * 0.86), 4 * scale, fill=(155, 91, 15, 255), outline=edge, width=2 * scale)
    return down(img, size)


def robot_collection(size=(96, 96)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle((w * 0.23, h * 0.28, w * 0.77, h * 0.76), 18 * scale, fill=(235, 250, 255, 255), outline=(23, 85, 150, 255), width=3 * scale)
    d.rounded_rectangle((w * 0.32, h * 0.39, w * 0.68, h * 0.60), 8 * scale, fill=(11, 42, 86, 255))
    d.ellipse((w * 0.38, h * 0.45, w * 0.44, h * 0.51), fill=(95, 231, 255, 255))
    d.ellipse((w * 0.56, h * 0.45, w * 0.62, h * 0.51), fill=(95, 231, 255, 255))
    d.line((w * 0.50, h * 0.28, w * 0.50, h * 0.16), fill=(23, 85, 150, 255), width=3 * scale)
    d.ellipse((w * 0.46, h * 0.11, w * 0.54, h * 0.19), fill=(95, 231, 255, 255), outline=(23, 85, 150, 255))
    return down(img, size)


def notification(size=(48, 48)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse((w * 0.08, h * 0.08, w * 0.92, h * 0.92), fill=(224, 33, 49, 255), outline=(110, 3, 20, 255), width=3 * scale)
    d.ellipse((w * 0.22, h * 0.16, w * 0.46, h * 0.36), fill=(255, 155, 165, 170))
    return down(img, size)


def draw_white_icon(d, center, r, icon):
    cx, cy = center
    col = (255, 255, 255, 230)
    w = max(2, int(r * 0.22))
    if icon == "arrow":
        d.line((cx - r * 0.6, cy, cx + r * 0.45, cy), fill=col, width=w)
        d.polygon([(cx + r * 0.45, cy), (cx + r * 0.12, cy - r * 0.32), (cx + r * 0.12, cy + r * 0.32)], fill=col)
    elif icon == "turn_left":
        d.arc((cx - r * 0.45, cy - r * 0.55, cx + r * 0.55, cy + r * 0.45), 145, 355, fill=col, width=w)
        d.polygon([(cx - r * 0.50, cy - r * 0.04), (cx - r * 0.10, cy - r * 0.25), (cx - r * 0.12, cy + r * 0.12)], fill=col)
    elif icon == "turn_right":
        d.arc((cx - r * 0.55, cy - r * 0.55, cx + r * 0.45, cy + r * 0.45), 185, 395, fill=col, width=w)
        d.polygon([(cx + r * 0.50, cy - r * 0.04), (cx + r * 0.10, cy - r * 0.25), (cx + r * 0.12, cy + r * 0.12)], fill=col)
    elif icon == "repeat":
        d.arc((cx - r * 0.55, cy - r * 0.55, cx + r * 0.55, cy + r * 0.55), 35, 305, fill=col, width=w)
        d.polygon([(cx + r * 0.48, cy - r * 0.1), (cx + r * 0.15, cy - r * 0.36), (cx + r * 0.2, cy + r * 0.05)], fill=col)
    elif icon == "wall":
        for yy in [-0.32, 0, 0.32]:
            d.rectangle((cx - r * 0.55, cy + r * yy - r * 0.12, cx + r * 0.55, cy + r * yy + r * 0.1), outline=col, width=max(2, w // 2))
    elif icon == "check_path":
        d.line((cx - r * 0.55, cy + r * 0.25, cx - r * 0.15, cy + r * 0.48, cx + r * 0.55, cy - r * 0.42), fill=col, width=w)
    elif icon == "star_hand":
        d.polygon(star_points(cx, cy - r * 0.25, r * 0.30, r * 0.13), fill=col)
        d.arc((cx - r * 0.45, cy - r * 0.05, cx + r * 0.45, cy + r * 0.7), 195, 345, fill=col, width=w)
    elif icon == "rocket":
        d.polygon([(cx - r * 0.45, cy + r * 0.25), (cx + r * 0.35, cy - r * 0.45), (cx + r * 0.55, cy - r * 0.10), (cx - r * 0.10, cy + r * 0.45)], fill=col)
        d.ellipse((cx + r * 0.03, cy - r * 0.19, cx + r * 0.18, cy - r * 0.04), fill=(10, 40, 90, 210))
    elif icon == "logic":
        d.polygon([(cx, cy - r * 0.52), (cx + r * 0.52, cy), (cx, cy + r * 0.52), (cx - r * 0.52, cy)], outline=col, width=w)
    elif icon == "action":
        d.polygon(star_points(cx, cy, r * 0.50, r * 0.24), fill=col)
    elif icon == "menu":
        for yy in [-0.22, 0, 0.22]:
            d.rounded_rectangle((cx - r * 0.55, cy + r * yy - r * 0.06, cx + r * 0.55, cy + r * yy + r * 0.06), r * 0.06, fill=col)


def icon_file(icon, size=(48, 48)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw_white_icon(ImageDraw.Draw(img), (w / 2, h / 2), min(w, h) * 0.38, icon)
    return down(img, size)


def block_bar(size, top, bottom, icon="arrow", notch=False):
    img = gradient_rect(size, top, bottom, radius=14, outline=(255, 255, 255, 80), width=2)
    scale = 4
    w, h = scaled(size, scale)
    hi = img.resize((w, h), Image.Resampling.LANCZOS)
    d = ImageDraw.Draw(hi)
    d.rounded_rectangle((18 * scale, 14 * scale, 76 * scale, h - 14 * scale), 12 * scale, fill=(255, 255, 255, 42), outline=(255, 255, 255, 80), width=2 * scale)
    draw_white_icon(d, (47 * scale, h / 2), h * 0.26, icon)
    if notch:
        d.rounded_rectangle((w * 0.17, h * 0.60, w * 0.62, h * 0.92), 10 * scale, outline=(255, 255, 255, 55), width=2 * scale)
    return down(hi, size)


def loop_bar(size=(32, 256)):
    scale = 4
    w, h = scaled(size, scale)
    hi = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(hi)
    d.rounded_rectangle((6 * scale, 8 * scale, w - 6 * scale, h - 8 * scale), 10 * scale, fill=(245, 132, 22, 230), outline=(255, 213, 76, 245), width=2 * scale)
    d.rounded_rectangle((11 * scale, 20 * scale, w - 11 * scale, h - 20 * scale), 7 * scale, fill=(255, 191, 58, 110))
    return down(hi, size)


def modal_panel(size, mood="win"):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    fill, outline, glow = (
        ((6, 24, 73, 232), (255, 211, 71, 255), (255, 190, 30, 115))
        if mood == "win"
        else ((20, 24, 74, 232), (255, 137, 83, 255), (255, 105, 68, 90))
    )
    g = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    gd = ImageDraw.Draw(g)
    gd.rounded_rectangle((34 * scale, 34 * scale, w - 34 * scale, h - 34 * scale), 36 * scale, outline=glow, width=12 * scale)
    img.alpha_composite(g.filter(ImageFilter.GaussianBlur(8 * scale)))
    d.rounded_rectangle((46 * scale, 44 * scale, w - 46 * scale, h - 46 * scale), 34 * scale, fill=fill, outline=outline, width=6 * scale)
    d.rounded_rectangle((68 * scale, 70 * scale, w - 68 * scale, h - 72 * scale), 20 * scale, outline=(106, 231, 255, 95), width=2 * scale)
    if mood == "win":
        random.seed(3)
        for _ in range(34):
            x = random.randint(70 * scale, w - 70 * scale)
            y = random.randint(40 * scale, h - 45 * scale)
            col = random.choice([(255, 218, 63, 200), (77, 229, 255, 180), (255, 83, 133, 180), (142, 240, 99, 180)])
            d.rectangle((x, y, x + 8 * scale, y + 4 * scale), fill=col)
    return down(img, size)


def confetti_piece(kind, size=(64, 64)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    if kind == 1:
        d.polygon([(w * 0.25, h * 0.24), (w * 0.76, h * 0.32), (w * 0.68, h * 0.76), (w * 0.18, h * 0.66)], fill=(255, 91, 120, 255), outline=(255, 210, 220, 120))
    elif kind == 2:
        d.polygon(star_points(w / 2, h / 2, w * 0.32, w * 0.15), fill=(255, 218, 56, 255), outline=(173, 105, 0, 255))
    else:
        d.ellipse((w * 0.22, h * 0.22, w * 0.78, h * 0.78), fill=(88, 224, 255, 255), outline=(13, 93, 155, 255), width=3 * scale)
    return down(img, size)


def banner_victory(size=(512, 128)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.polygon([(20 * scale, h * 0.28), (90 * scale, h * 0.28), (90 * scale, h * 0.73), (20 * scale, h * 0.73), (50 * scale, h * 0.50)], fill=(197, 98, 12, 255), outline=(102, 52, 0, 255))
    d.polygon([(w - 20 * scale, h * 0.28), (w - 90 * scale, h * 0.28), (w - 90 * scale, h * 0.73), (w - 20 * scale, h * 0.73), (w - 50 * scale, h * 0.50)], fill=(197, 98, 12, 255), outline=(102, 52, 0, 255))
    d.rounded_rectangle((68 * scale, 18 * scale, w - 68 * scale, h - 18 * scale), 18 * scale, fill=(247, 181, 34, 255), outline=(104, 59, 0, 255), width=4 * scale)
    d.rounded_rectangle((92 * scale, 32 * scale, w - 92 * scale, h - 32 * scale), 10 * scale, fill=(255, 221, 76, 160))
    return down(img, size)


def dust_puff(size=(128, 128)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    for cx, cy, r, a in [
        (w * 0.34, h * 0.58, w * 0.20, 110),
        (w * 0.50, h * 0.46, w * 0.25, 130),
        (w * 0.66, h * 0.58, w * 0.19, 100),
        (w * 0.48, h * 0.68, w * 0.28, 90),
    ]:
        d.ellipse((cx - r, cy - r, cx + r, cy + r), fill=(238, 221, 193, a))
    return down(img.filter(ImageFilter.GaussianBlur(4 * scale)), size)


def path_arrow_preview(size=(48, 48)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse((w * 0.08, h * 0.08, w * 0.92, h * 0.92), fill=(26, 136, 217, 210), outline=(121, 236, 255, 230), width=2 * scale)
    draw_white_icon(d, (w / 2, h / 2), w * 0.28, "arrow")
    return down(img, size)


def firework(size=(256, 256)):
    scale = 4
    w, h = scaled(size, scale)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    random.seed(7)
    colors = [(255, 215, 60, 230), (78, 223, 255, 230), (255, 84, 144, 230), (130, 242, 86, 230), (184, 109, 255, 230)]
    for i in range(42):
        a = 2 * math.pi * i / 42
        r1 = w * 0.08
        r2 = w * random.uniform(0.28, 0.45)
        col = random.choice(colors)
        d.line((cx + math.cos(a) * r1, cy + math.sin(a) * r1, cx + math.cos(a) * r2, cy + math.sin(a) * r2), fill=col, width=random.randint(2, 5) * scale)
        ex = cx + math.cos(a) * r2
        ey = cy + math.sin(a) * r2
        d.ellipse((ex - 4 * scale, ey - 4 * scale, ex + 4 * scale, ey + 4 * scale), fill=col)
    return down(img.filter(ImageFilter.GaussianBlur(int(0.25 * scale))), size)


def copy_existing_assets():
    copy_resize(SRC / "bg_alien_planet.png", "bg_alien_planet.png", (1920, 1080), "cover")
    for name, size in [
        ("robo_idle.png", (256, 256)),
        ("robo_right.png", (256, 256)),
        ("robo_left.png", (256, 256)),
        ("robo_up.png", (256, 256)),
        ("robo_down.png", (256, 256)),
        ("robo_win.png", (256, 256)),
        ("robo_error.png", (256, 256)),
        ("robo_menu_pose.png", (512, 512)),
    ]:
        copy_resize(SRC / name, f"chars/{name}", size, "contain")
    copy_resize(SRC / "spaceship_menu.png", "props/spaceship_goal.png", (192, 128), "contain")
    copy_resize(SRC / "logo_codequest.png", "ui/logo_codequest.png", (800, 300), "contain")


def generate_missing_assets():
    save_if_missing(draw_star((128, 128)), "collectibles/star_gold.png")
    save_if_missing(draw_star((64, 64)), "collectibles/star_gold_small.png")
    save_if_missing(radial_glow((128, 128), (255, 190, 0), 180), "fx/star_glow.png")
    save_if_missing(sparkle((64, 64), (255, 255, 255), True), "fx/star_sparkle.png")
    save_if_missing(firework((256, 256)), "fx/star_collect_burst.png")
    save_if_missing(coin((64, 64)), "collectibles/coin_gold.png")
    save_if_missing(board_frame(), "board/board_frame_9slice.png")
    save_if_missing(inner_glow(), "board/board_inner_glow.png")
    save_if_missing(panel_hologram((512, 512)), "ui/panel_hologram.png")
    save_if_missing(gradient_rect((512, 256), (13, 47, 120, 235), (3, 16, 61, 240), radius=28, outline=(91, 220, 255, 200), width=3), "ui/panel_dark_blue.png")
    save_if_missing(speech_bubble(), "ui/speech_bubble.png")
    save_if_missing(gradient_rect((256, 64), (7, 31, 94, 230), (3, 15, 52, 230), radius=28, outline=(74, 232, 255, 240), width=3), "ui/hud_pill.png")
    save_if_missing(button((320, 80), (60, 225, 93, 255), (16, 137, 56, 255), "play", (164, 255, 180, 180)), "ui/btn_execute.png")
    save_if_missing(button((320, 80), (61, 164, 255, 255), (24, 82, 205, 255), "step", (157, 224, 255, 180)), "ui/btn_step.png")
    save_if_missing(button((320, 80), (155, 88, 245, 255), (83, 36, 168, 255), "undo", (217, 188, 255, 180)), "ui/btn_undo.png")
    save_if_missing(button((320, 80), (241, 82, 84, 255), (160, 25, 40, 255), "trash", (255, 178, 178, 180)), "ui/btn_clear.png")
    save_if_missing(button((200, 64), (154, 84, 239, 255), (82, 34, 167, 255), "menu", (217, 188, 255, 180)), "ui/btn_menu.png")
    save_if_missing(button((200, 56), (55, 147, 237, 255), (21, 70, 174, 255), "step", (145, 225, 255, 170)), "ui/btn_secondary.png")
    save_if_missing(button((180, 48), (55, 220, 239, 255), (19, 124, 184, 255), "code", (170, 246, 255, 170)), "ui/btn_code.png")
    save_if_missing(draw_star((64, 64)), "ui/icon_star_hud.png")
    save_if_missing(coin((64, 64)), "ui/icon_coin_hud.png")
    save_if_missing(icon_heart(), "ui/icon_heart_hud.png")
    save_if_missing(planet_icon(), "ui/icon_planet_mission.png")
    save_if_missing(icon_file("menu", (48, 48)), "ui/icon_menu_hamburger.png")
    save_if_missing(chest(True), "ui/icon_chest_gold.png")
    save_if_missing(chest(False), "ui/icon_chest_closed.png")
    save_if_missing(trophy(), "ui/icon_trophy_gold.png")
    save_if_missing(robot_collection(), "ui/icon_robot_collection.png")
    save_if_missing(notification(), "ui/badge_notification_red.png")
    save_if_missing(globe_icon(), "ui/icon_globe_objective.png")
    save_if_missing(block_bar((512, 80), (41, 139, 246, 255), (21, 78, 201, 255), "arrow"), "blocks/block_move_blue.png")
    save_if_missing(block_bar((512, 80), (42, 145, 249, 255), (20, 77, 198, 255), "turn_left"), "blocks/block_turn_blue.png")
    save_if_missing(block_bar((512, 80), (151, 84, 236, 255), (80, 39, 165, 255), "logic"), "blocks/block_logic_purple.png")
    save_if_missing(block_bar((512, 80), (255, 163, 47, 255), (219, 93, 18, 255), "repeat", True), "blocks/block_loop_orange.png")
    save_if_missing(block_bar((512, 80), (72, 214, 98, 255), (26, 139, 60, 255), "action"), "blocks/block_action_green.png")

    scale = 4
    w, h = scaled((512, 72), scale)
    chip = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    dd = ImageDraw.Draw(chip)
    for i, a in [(0, 230), (5, 120), (12, 60)]:
        dd.rounded_rectangle((10 * scale - i * scale, 8 * scale - i * scale, w - 10 * scale + i * scale, h - 8 * scale + i * scale), 15 * scale + i * scale, outline=(255, 223, 44, a), width=4 * scale)
    save_if_missing(down(chip, (512, 72)), "blocks/block_chip_active.png")

    for fname, icon in [
        ("icon_arrow_forward.png", "arrow"),
        ("icon_turn_left.png", "turn_left"),
        ("icon_turn_right.png", "turn_right"),
        ("icon_repeat.png", "repeat"),
        ("icon_wall.png", "wall"),
        ("icon_path_free.png", "check_path"),
        ("icon_collect_star.png", "star_hand"),
        ("icon_use_ship.png", "rocket"),
    ]:
        save_if_missing(icon_file(icon, (48, 48)), f"blocks/{fname}")

    save_if_missing(panel_hologram((512, 512), program=True), "ui/panel_program.png")
    save_if_missing(loop_bar(), "ui/loop_nest_bar.png")
    save_if_missing(modal_panel((800, 600), "win"), "ui/modal_win_panel.png")
    save_if_missing(modal_panel((600, 400), "fail"), "ui/modal_fail_panel.png")
    save_if_missing(confetti_piece(1), "fx/confetti_1.png")
    save_if_missing(confetti_piece(2), "fx/confetti_2.png")
    save_if_missing(confetti_piece(3), "fx/confetti_3.png")
    save_if_missing(banner_victory(), "ui/banner_victory.png")
    save_if_missing(gradient_rect((128, 48), (88, 224, 104, 245), (27, 150, 64, 245), radius=22, outline=(178, 255, 190, 245), width=2), "ui/label_inicio.png")
    save_if_missing(gradient_rect((128, 48), (255, 170, 54, 245), (220, 58, 47, 245), radius=22, outline=(255, 224, 111, 245), width=2), "ui/label_meta.png")
    save_if_missing(dust_puff(), "fx/dust_puff.png")
    save_if_missing(radial_glow((64, 64), (71, 205, 255), 150), "fx/robot_footprint_glow.png")
    save_if_missing(path_arrow_preview(), "fx/path_arrow_preview.png")
    save_if_missing(firework((256, 256)), "fx/win_firework.png")


def normalize_target_sizes():
    for rel in ["bg_game_parallax_far.png", "bg_game_parallax_stars.png"]:
        resize_exact(rel, (1920, 720), "cover")

    for rel in [
        "tiles/tile_grass.png",
        "tiles/tile_grass_variant2.png",
        "tiles/tile_path.png",
        "tiles/tile_path_corner.png",
        "tiles/tile_wall_grass.png",
        "tiles/tile_flight_space.png",
        "tiles/tile_start.png",
        "tiles/tile_goal.png",
    ]:
        resize_exact(rel, (64, 64), "cover")

    replace_png("props/rock_large.png", rock_prop((96, 96)))
    replace_png("props/rock_small.png", rock_prop((48, 48), small=True))
    replace_png("props/bush_green.png", bush_prop((64, 64)))
    replace_png("props/plant_sprout.png", sprout_prop((32, 48)))
    replace_png("props/flower_pink.png", flower_prop((32, 32)))
    replace_png("props/crystal_blue.png", crystal_prop((48, 64)))
    replace_png("props/tree_stump.png", stump_prop((64, 48)))
    replace_png("props/crate_wood.png", crate_prop((64, 64)))


SECTIONS = {
    "01_FONDOS": ["bg_alien_planet.png", "bg_game_parallax_far.png", "bg_game_parallax_stars.png"],
    "02_TILES_DEL_MAPA": [
        "tiles/tile_grass.png",
        "tiles/tile_grass_variant2.png",
        "tiles/tile_path.png",
        "tiles/tile_path_corner.png",
        "tiles/tile_wall_grass.png",
        "tiles/tile_flight_space.png",
        "tiles/tile_start.png",
        "tiles/tile_goal.png",
    ],
    "03_PROPS_DEL_MAPA": [
        "props/rock_large.png",
        "props/rock_small.png",
        "props/bush_green.png",
        "props/plant_sprout.png",
        "props/flower_pink.png",
        "props/crystal_blue.png",
        "props/tree_stump.png",
        "props/crate_wood.png",
    ],
    "04_ESTRELLAS_Y_COLECTIBLES": [
        "collectibles/star_gold.png",
        "collectibles/star_gold_small.png",
        "fx/star_glow.png",
        "fx/star_sparkle.png",
        "fx/star_collect_burst.png",
        "collectibles/coin_gold.png",
    ],
    "05_PERSONAJES": [
        "chars/robo_idle.png",
        "chars/robo_right.png",
        "chars/robo_left.png",
        "chars/robo_up.png",
        "chars/robo_down.png",
        "chars/robo_win.png",
        "chars/robo_error.png",
        "chars/robo_menu_pose.png",
        "props/spaceship_goal.png",
    ],
    "06_MARCO_DEL_TABLERO": ["board/board_frame_9slice.png", "board/board_inner_glow.png"],
    "07_PANELES_UI": ["ui/panel_hologram.png", "ui/panel_dark_blue.png", "ui/speech_bubble.png", "ui/hud_pill.png"],
    "08_BOTONES": [
        "ui/btn_execute.png",
        "ui/btn_step.png",
        "ui/btn_undo.png",
        "ui/btn_clear.png",
        "ui/btn_menu.png",
        "ui/btn_secondary.png",
        "ui/btn_code.png",
    ],
    "09_ICONOS_HUD_SUPERIOR": [
        "ui/icon_star_hud.png",
        "ui/icon_coin_hud.png",
        "ui/icon_heart_hud.png",
        "ui/icon_planet_mission.png",
        "ui/icon_menu_hamburger.png",
    ],
    "10_FOOTER_COFRE_LOGROS_ROBOTS": [
        "ui/icon_chest_gold.png",
        "ui/icon_chest_closed.png",
        "ui/icon_trophy_gold.png",
        "ui/icon_robot_collection.png",
        "ui/badge_notification_red.png",
        "ui/icon_globe_objective.png",
    ],
    "11_BLOQUES_DE_CODIGO": [
        "blocks/block_move_blue.png",
        "blocks/block_turn_blue.png",
        "blocks/block_logic_purple.png",
        "blocks/block_loop_orange.png",
        "blocks/block_action_green.png",
        "blocks/block_chip_active.png",
        "blocks/icon_arrow_forward.png",
        "blocks/icon_turn_left.png",
        "blocks/icon_turn_right.png",
        "blocks/icon_repeat.png",
        "blocks/icon_wall.png",
        "blocks/icon_path_free.png",
        "blocks/icon_collect_star.png",
        "blocks/icon_use_ship.png",
    ],
    "12_PANEL_MI_PROGRAMA": ["ui/panel_program.png", "ui/loop_nest_bar.png"],
    "13_MODALES": [
        "ui/modal_win_panel.png",
        "ui/modal_fail_panel.png",
        "fx/confetti_1.png",
        "fx/confetti_2.png",
        "fx/confetti_3.png",
        "ui/banner_victory.png",
    ],
    "14_ETIQUETAS_Y_LOGO": ["ui/logo_codequest.png", "ui/label_inicio.png", "ui/label_meta.png"],
    "15_EFECTOS_DE_MAPA": [
        "fx/dust_puff.png",
        "fx/robot_footprint_glow.png",
        "fx/path_arrow_preview.png",
        "fx/win_firework.png",
    ],
}


def organize_numbered_folders():
    missing_after = []
    for folder, rels in SECTIONS.items():
        outdir = BASE / folder
        outdir.mkdir(exist_ok=True)
        for rel in rels:
            srcfile = BASE / rel.replace("/", "\\")
            if not srcfile.exists():
                missing_after.append(rel)
                continue
            dst = outdir / rel.replace("/", "\\")
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(srcfile, dst)
            organized.append(str(Path(folder) / rel))

    sheet = BASE / "ESTRELLAS Y COLECTIBLES.png"
    if sheet.exists():
        dst = BASE / "04_ESTRELLAS_Y_COLECTIBLES" / "ESTRELLAS Y COLECTIBLES.png"
        shutil.copy2(sheet, dst)
        organized.append(str(Path("04_ESTRELLAS_Y_COLECTIBLES") / "ESTRELLAS Y COLECTIBLES.png"))
    return missing_after


def write_manifest(missing_after):
    manifest = BASE / "MANIFIESTO_IMG3.txt"
    lines = [
        "IMG3 organizado por Codex",
        "",
        f"Assets esperados: {sum(len(v) for v in SECTIONS.values())}",
        f"Creados con dibujo local PNG: {len(created)}",
        f"Copiados/escalados desde assets existentes: {len(copied)}",
        f"Normalizados a tamano final: {len(normalized)}",
        f"Ya existian y no se tocaron: {len(skipped)}",
        f"Copias en carpetas numeradas: {len(organized)}",
        f"Faltantes despues del proceso: {len(missing_after)}",
    ]
    if missing_after:
        lines += ["", "Faltantes:"]
        lines += [f" - {x}" for x in missing_after]
    lines += ["", "Carpetas numeradas:"]
    lines += [f" - {folder}" for folder in SECTIONS]
    manifest.write_text("\n".join(lines), encoding="utf-8")
    return manifest


def main():
    BASE.mkdir(parents=True, exist_ok=True)
    copy_existing_assets()
    generate_missing_assets()
    normalize_target_sizes()
    missing_after = organize_numbered_folders()
    manifest = write_manifest(missing_after)
    print(
        json.dumps(
            {
                "created": len(created),
                "copied_resized": len(copied),
                "normalized": len(normalized),
                "skipped_existing": len(skipped),
                "organized_copies": len(organized),
                "missing_after": missing_after,
                "manifest": str(manifest),
            },
            ensure_ascii=False,
            indent=2,
        )
    )


if __name__ == "__main__":
    main()

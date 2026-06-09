#!/usr/bin/env python3
"""Quita checkerboard y fondos negros de PNGs del menu (artefactos IA)."""

from pathlib import Path

from PIL import Image

MENU = Path(__file__).resolve().parents[1] / "core" / "src" / "main" / "resources" / "images" / "menu"


def is_checker_gray(r: int, g: int, b: int) -> bool:
    avg = (r + g + b) / 3
    if abs(r - g) > 18 or abs(g - b) > 18:
        return False
    return 105 <= avg <= 225


def is_black_bg(r: int, g: int, b: int) -> bool:
    return r < 28 and g < 28 and b < 28


def fix_pixel(r: int, g: int, b: int, a: int) -> tuple[int, int, int, int]:
    if a < 8:
        return r, g, b, 0
    if is_black_bg(r, g, b):
        return r, g, b, 0
    if is_checker_gray(r, g, b):
        return r, g, b, 0
    return r, g, b, a


def fix_image(path: Path) -> None:
    img = Image.open(path).convert("RGBA")
    px = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            px[x, y] = fix_pixel(r, g, b, a)
    img.save(path, "PNG")
    print("fixed", path.relative_to(MENU))


def main() -> None:
    for png in sorted(MENU.rglob("*.png")):
        if png.name.startswith("REFERENCE"):
            continue
        fix_image(png)
    print("OK")


if __name__ == "__main__":
    main()

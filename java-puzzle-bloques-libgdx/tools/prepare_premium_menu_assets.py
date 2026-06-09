#!/usr/bin/env python3
"""Prepara assets premium del menu: copia img2 + flood-fill de fondos."""

from __future__ import annotations

import shutil
from collections import deque
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "core" / "src" / "main" / "resources" / "images"
MENU = RES / "menu"
IMG2 = ROOT.parent / "img2"


def flood_clear_background(path: Path, tolerance: int = 42) -> None:
    img = Image.open(path).convert("RGBA")
    px = img.load()
    w, h = img.size
    visited = bytearray(w * h)

    def idx(x: int, y: int) -> int:
        return y * w + x

    def similar(a: tuple[int, ...], b: tuple[int, ...]) -> bool:
        return abs(a[0] - b[0]) + abs(a[1] - b[1]) + abs(a[2] - b[2]) <= tolerance

    seeds = []
    for x, y in ((0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)):
        seeds.append((x, y))
    for x in range(w):
        seeds.append((x, 0))
        seeds.append((x, h - 1))
    for y in range(h):
        seeds.append((0, y))
        seeds.append((w - 1, y))

    q = deque()
    for x, y in seeds:
        r, g, b, a = px[x, y]
        if a < 12:
            continue
        if r > 235 and g > 235 and b > 235:
            q.append((x, y))
            visited[idx(x, y)] = 1
        elif r < 30 and g < 30 and b < 30:
            q.append((x, y))
            visited[idx(x, y)] = 1
        elif abs(r - g) < 15 and abs(g - b) < 15 and 100 <= (r + g + b) / 3 <= 220:
            q.append((x, y))
            visited[idx(x, y)] = 1

    while q:
        x, y = q.popleft()
        px[x, y] = (r, g, b, 0) if (r := px[x, y][0]) or True else (0, 0, 0, 0)
        px[x, y] = (0, 0, 0, 0)
        for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            if nx < 0 or ny < 0 or nx >= w or ny >= h:
                continue
            i = idx(nx, ny)
            if visited[i]:
                continue
            cur = px[nx, ny]
            if cur[3] < 12:
                visited[i] = 1
                q.append((nx, ny))
                continue
            cr, cg, cb, _ = cur
            if cr > 235 and cg > 235 and cb > 235:
                visited[i] = 1
                q.append((nx, ny))
            elif cr < 30 and cg < 30 and cb < 30:
                visited[i] = 1
                q.append((nx, ny))
            elif abs(cr - cg) < 15 and abs(cg - cb) < 15 and 100 <= (cr + cg + cb) / 3 <= 220:
                visited[i] = 1
                q.append((nx, ny))

    img.save(path, "PNG")


def copy_premium() -> None:
    pairs = {
        IMG2 / "panelholografico.png": MENU / "ui" / "panel_hologram_premium.png",
        IMG2 / "boton.png": MENU / "ui" / "btn_gloss_base.png",
        IMG2 / "logodejuego.png": MENU / "ui" / "logo_premium.png",
        IMG2 / "robotincial.png": MENU / "ui" / "robo_premium.png",
        RES / "spaceship_menu.png": MENU / "bg" / "ship_decor.png",
        RES / "logo_codequest.png": MENU / "ui" / "logo_codequest.png",
        RES / "robo_menu_pose.png": MENU / "robo" / "robo_idle.png",
    }
    for src, dst in pairs.items():
        if src.exists():
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(src, dst)
            print("copied", dst.name)


def main() -> None:
    copy_premium()
    for folder in ("worlds", "portal", "ui", "icons", "robo"):
        d = MENU / folder
        if not d.exists():
            continue
        for png in d.glob("*.png"):
            if png.name.startswith("REFERENCE"):
                continue
            flood_clear_background(png)
            print("alpha", png.relative_to(MENU))
    print("OK premium assets ready")


if __name__ == "__main__":
    main()

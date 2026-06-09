#!/usr/bin/env python3
"""Organiza y completa el pack de assets del menú AAA CodeQuest."""

from __future__ import annotations

import math
import shutil
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "core" / "src" / "main" / "resources" / "images"
MENU = RES / "menu"
ASSETS = Path(r"C:\Users\CARLOS\.cursor\projects\c-SpringProjectsnew-serviciosjuegos\assets")
PROJECT_ASSETS = ROOT.parent / "assets"


def ensure_dirs() -> None:
    for sub in ("bg", "portal", "worlds", "ui", "icons", "robo", "fx", "labels"):
        (MENU / sub).mkdir(parents=True, exist_ok=True)


def copy_src(name: str, dest: Path) -> None:
    for base in (ASSETS, PROJECT_ASSETS, RES):
        src = base / name
        if src.exists():
            shutil.copy2(src, dest)
            return
    raise FileNotFoundError(name)


def copy_if_exists(src: Path, dest: Path) -> bool:
    if src.exists():
        shutil.copy2(src, dest)
        return True
    return False


def save_rgba(img: Image.Image, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    if img.mode != "RGBA":
        img = img.convert("RGBA")
    img.save(path, "PNG")


def make_particle_sparkle(size: int = 32) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2
    for angle in range(0, 360, 45):
        rad = math.radians(angle)
        x2 = cx + math.cos(rad) * (size * 0.42)
        y2 = cy + math.sin(rad) * (size * 0.42)
        draw.line([(cx, cy), (x2, y2)], fill=(120, 255, 220, 220), width=2)
    draw.ellipse([cx - 4, cy - 4, cx + 4, cy + 4], fill=(200, 255, 255, 255))
    return img.filter(ImageFilter.GaussianBlur(0.6))


def make_particle_star(size: int = 24) -> Image.Image:
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, size // 2
    points = []
    for i in range(10):
        angle = math.radians(-90 + i * 36)
        r = (size * 0.45) if i % 2 == 0 else (size * 0.18)
        points.append((cx + math.cos(angle) * r, cy + math.sin(angle) * r))
    draw.polygon(points, fill=(255, 230, 80, 240))
    return img


def make_hud_pill(w: int, h: int, tint: tuple[int, int, int]) -> Image.Image:
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    r, g, b = tint
    draw.rounded_rectangle([2, 2, w - 3, h - 3], radius=h // 2, fill=(r, g, b, 180))
    draw.rounded_rectangle([0, 0, w - 1, h - 1], radius=h // 2, outline=(255, 255, 255, 90), width=2)
    glow = img.copy().filter(ImageFilter.GaussianBlur(3))
    out = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    out.alpha_composite(glow)
    out.alpha_composite(img)
    return out


def make_glow_line(w: int = 256, h: int = 8) -> Image.Image:
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    for x in range(w):
        t = x / max(w - 1, 1)
        alpha = int(180 * (0.35 + 0.65 * math.sin(t * math.pi)))
        draw.line([(x, h // 2), (x, h // 2)], fill=(80, 220, 255, alpha), width=h)
    return img.filter(ImageFilter.GaussianBlur(1.5))


def make_stars_layer(w: int = 1920, h: int = 1080, count: int = 280) -> Image.Image:
    import random

    random.seed(42)
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    for _ in range(count):
        x = random.randint(0, w - 1)
        y = random.randint(0, h - 1)
        s = random.choice([1, 1, 2, 2, 3])
        a = random.randint(120, 255)
        c = random.choice([(255, 255, 255), (180, 230, 255), (255, 240, 200)])
        draw.rectangle([x, y, x + s, y + s], fill=(*c, a))
    return img


def split_labels_row(sheet: Path, out_dir: Path, names: list[str], target_h: int = 48) -> None:
    if not sheet.exists():
        return
    img = Image.open(sheet).convert("RGBA")
    n = len(names)
    step = img.width // n
    for i, name in enumerate(names):
        crop = img.crop((i * step, 0, (i + 1) * step, img.height))
        ratio = target_h / max(crop.height, 1)
        nw = max(1, int(crop.width * ratio))
        crop = crop.resize((nw, target_h), Image.Resampling.LANCZOS)
        save_rgba(crop, out_dir / name)


def split_row(sheet: Path, out_dir: Path, names: list[str], icon_size: int = 64) -> None:
    if not sheet.exists():
        return
    img = Image.open(sheet).convert("RGBA")
    n = len(names)
    step = img.width // n
    for i, name in enumerate(names):
        box = (i * step, 0, (i + 1) * step, min(icon_size, img.height))
        crop = img.crop(box)
        crop = crop.resize((64, 64), Image.Resampling.LANCZOS)
        save_rgba(crop, out_dir / name)


def split_asteroids(sheet: Path, out_dir: Path) -> None:
    if not sheet.exists():
        return
    img = Image.open(sheet).convert("RGBA")
    n = 6
    step = img.width // n
    for i in range(n):
        crop = img.crop((i * step, 0, (i + 1) * step, img.height))
        max_side = max(crop.size)
        scale = 128 / max(max_side, 1)
        nw, nh = int(crop.width * scale), int(crop.height * scale)
        crop = crop.resize((nw, nh), Image.Resampling.LANCZOS)
        save_rgba(crop, out_dir / f"asteroid_{i + 1:02d}.png")


def split_planets(sheet: Path, out_dir: Path) -> None:
    if not sheet.exists():
        return
    img = Image.open(sheet).convert("RGBA")
    n = 3
    step = img.width // n
    for i in range(n):
        crop = img.crop((i * step, 0, (i + 1) * step, img.height))
        crop = crop.resize((192, 192), Image.Resampling.LANCZOS)
        save_rgba(crop, out_dir / f"planet_distant_{i + 1}.png")


def split_ranks(sheet: Path, out_dir: Path) -> None:
    if not sheet.exists():
        return
    img = Image.open(sheet).convert("RGBA")
    names = ["rank_novato.png", "rank_piloto.png", "rank_capitan.png", "rank_leyenda.png"]
    n = len(names)
    step = img.width // n
    for i, name in enumerate(names):
        crop = img.crop((i * step, 0, (i + 1) * step, img.height))
        crop = crop.resize((64, 64), Image.Resampling.LANCZOS)
        save_rgba(crop, out_dir / name)


def portal_frames(src: Path, out_dir: Path, frames: int = 4) -> None:
    if not src.exists():
        return
    base = Image.open(src).convert("RGBA")
    for i in range(frames):
        rotated = base.rotate(i * (360 / frames), resample=Image.Resampling.BICUBIC, expand=False)
        save_rgba(rotated, out_dir / f"portal_spiral_{i + 1:02d}.png")


def copy_generated_assets() -> None:
    mapping = {
        "menu_bg_base.png": MENU / "bg" / "base_space.png",
        "menu_nebula_layer_1.png": MENU / "bg" / "nebula_1.png",
        "menu_nebula_layer_2.png": MENU / "bg" / "nebula_2.png",
        "menu_portal_spiral.png": MENU / "portal" / "portal_spiral_base.png",
        "menu_portal_glow.png": MENU / "portal" / "portal_glow.png",
        "menu_world_moon.png": MENU / "worlds" / "world_moon.png",
        "menu_world_academy.png": MENU / "worlds" / "world_academy.png",
        "menu_world_saturn.png": MENU / "worlds" / "world_saturn.png",
        "menu_world_nebula.png": MENU / "worlds" / "world_nebula.png",
        "menu_world_station.png": MENU / "worlds" / "world_station.png",
        "menu_world_locked.png": MENU / "worlds" / "world_locked_overlay.png",
        "menu_btn_orange.png": MENU / "ui" / "btn_orange.png",
        "menu_btn_blue.png": MENU / "ui" / "btn_blue.png",
        "menu_btn_purple.png": MENU / "ui" / "btn_purple.png",
        "menu_btn_green.png": MENU / "ui" / "btn_green.png",
        "menu_btn_cyan.png": MENU / "ui" / "btn_cyan.png",
        "menu_btn_dark.png": MENU / "ui" / "btn_dark.png",
        "menu_panel_hologram.png": MENU / "ui" / "panel_hologram_aaa.png",
        "menu_subtitle_banner.png": MENU / "ui" / "subtitle_banner.png",
        "menu_footer_star_pass.png": MENU / "ui" / "footer_star_pass.png",
        "menu_avatar_frame.png": MENU / "ui" / "avatar_frame.png",
        "menu_robo_blink.png": MENU / "robo" / "robo_blink.png",
        "menu_robo_smile.png": MENU / "robo" / "robo_smile.png",
        "menu_robo_wave_1.png": MENU / "robo" / "robo_wave_1.png",
        "menu_robo_wave_2.png": MENU / "robo" / "robo_wave_2.png",
        "menu_robo_look_left.png": MENU / "robo" / "robo_look_left.png",
        "menu_robo_look_right.png": MENU / "robo" / "robo_look_right.png",
        "menu_ship_npc_1.png": MENU / "bg" / "ship_npc_1.png",
        "menu_ship_npc_2.png": MENU / "bg" / "ship_npc_2.png",
        "menu_xp_bar.png": MENU / "ui" / "xp_bar.png",
        "menu_robot_slots.png": MENU / "ui" / "robot_slots_row.png",
    }
    for src_name, dest in mapping.items():
        copied = False
        for base in (ASSETS, PROJECT_ASSETS):
            src = base / src_name
            if copy_if_exists(src, dest):
                copied = True
                break
        if not copied:
            print(f"WARN: missing generated asset {src_name}")


def copy_existing_assets() -> None:
    existing = {
        RES / "logo_codequest.png": MENU / "ui" / "logo_codequest.png",
        RES / "robo_menu_pose.png": MENU / "robo" / "robo_idle.png",
        RES / "spaceship_menu.png": MENU / "bg" / "ship_hero.png",
        RES / "mono_espacial_wave.png": MENU / "robo" / "mono_preview.png",
        RES / "mono_espacial_idle_front.png": MENU / "icons" / "icon_mono_avatar.png",
        RES / "ui" / "icon_star_hud.png": MENU / "icons" / "icon_star_hud.png",
        RES / "ui" / "icon_coin_hud.png": MENU / "icons" / "icon_coin_hud.png",
        RES / "ui" / "icon_robot_collection.png": MENU / "icons" / "icon_robot_collection.png",
        RES / "ui" / "icon_trophy_gold.png": MENU / "icons" / "icon_trophy_hud.png",
        RES / "ui" / "badge_notification_red.png": MENU / "ui" / "badge_notification_red.png",
        RES / "ui" / "hud_pill.png": MENU / "ui" / "hud_pill_base.png",
        RES / "fx" / "star_sparkle.png": MENU / "fx" / "star_sparkle_ref.png",
        RES / "label_start.png": MENU / "labels" / "label_iniciar.png",
        RES / "label_map.png": MENU / "labels" / "label_mapa.png",
        RES / "label_characters.png": MENU / "labels" / "label_robots.png",
        RES / "label_back.png": MENU / "labels" / "label_volver.png",
    }
    for src, dest in existing.items():
        if src.exists():
            shutil.copy2(src, dest)


def create_labels() -> None:
    labels_dir = MENU / "labels"
    # Duplicar labels existentes con nombres del menú AAA
    pairs = [
        ("label_iniciar.png", "label_continuar.png"),
        ("label_mapa.png", "label_logros.png"),
        ("label_robots.png", "label_config.png"),
    ]
    for src_name, dest_name in pairs:
        src = labels_dir / src_name
        dest = labels_dir / dest_name
        if src.exists() and not dest.exists():
            shutil.copy2(src, dest)


def write_manifest(files: list[Path]) -> None:
    manifest = MENU / "ASSETS_MANIFEST.md"
    lines = [
        "# CodeQuest — Pack de assets del menú AAA",
        "",
        f"Total: **{len(files)}** archivos PNG",
        "",
        "Ruta base LibGDX: `images/menu/`",
        "",
        "## Estructura",
        "",
        "| Carpeta | Uso |",
        "|---------|-----|",
        "| `bg/` | Parallax, nebulosas, asteroides, naves |",
        "| `portal/` | Portal galáctico central |",
        "| `worlds/` | 5 islas/mundos orbitando el portal |",
        "| `ui/` | Panel holográfico, botones, HUD, footer |",
        "| `icons/` | Iconos 64×64 de botones y recursos |",
        "| `robo/` | Animaciones de mascota Robo |",
        "| `fx/` | Partículas y efectos |",
        "| `labels/` | Etiquetas de botones (opcional) |",
        "",
        "## Inventario",
        "",
    ]
    for f in sorted(files):
        rel = f.relative_to(MENU).as_posix()
        lines.append(f"- `{rel}`")
    manifest.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> None:
    ensure_dirs()
    copy_generated_assets()
    copy_existing_assets()
    create_labels()

    # Procedural
    save_rgba(make_particle_sparkle(), MENU / "fx" / "particle_sparkle.png")
    save_rgba(make_particle_star(), MENU / "fx" / "particle_star.png")
    save_rgba(make_glow_line(), MENU / "fx" / "world_glow_line.png")
    save_rgba(make_stars_layer(), MENU / "bg" / "stars_twinkle.png")
    save_rgba(make_hud_pill(220, 44, (40, 90, 180)), MENU / "ui" / "hud_pill_star.png")
    save_rgba(make_hud_pill(220, 44, (180, 130, 30)), MENU / "ui" / "hud_pill_coin.png")
    save_rgba(make_hud_pill(220, 44, (120, 50, 180)), MENU / "ui" / "hud_pill_gem.png")

    # wave_3 = copia de wave_1 ligeramente distinta si no existe
    w1 = MENU / "robo" / "robo_wave_1.png"
    w3 = MENU / "robo" / "robo_wave_3.png"
    if w1.exists() and not w3.exists():
        shutil.copy2(w1, w3)

    # Splits
    split_row(ASSETS / "menu_icons_buttons.png", MENU / "icons", [
        "icon_play.png", "icon_continue.png", "icon_map.png",
        "icon_trophy.png", "icon_robot.png", "icon_settings.png",
    ])
    split_row(ASSETS / "menu_icons_hud.png", MENU / "icons", [
        "icon_gem.png", "icon_gift.png", "icon_mail.png",
        "icon_daily.png", "icon_wheel.png", "icon_shop.png",
    ])
    split_asteroids(ASSETS / "menu_asteroid_sheet.png", MENU / "bg")
    split_planets(ASSETS / "menu_planets_distant.png", MENU / "bg")
    split_ranks(ASSETS / "menu_rank_badges.png", MENU / "icons")
    split_labels_row(ASSETS / "menu_labels_buttons.png", MENU / "labels", [
        "label_iniciar_aventura.png",
        "label_continuar.png",
        "label_mapa_galactico.png",
        "label_logros.png",
        "label_robots.png",
        "label_configuracion.png",
    ])
    portal_frames(MENU / "portal" / "portal_spiral_base.png", MENU / "portal")

    files = list(MENU.rglob("*.png"))
    write_manifest(files)
    print(f"OK: {len(files)} assets en {MENU}")


if __name__ == "__main__":
    main()
    # Ejecutar también: python tools/fix_menu_alpha.py

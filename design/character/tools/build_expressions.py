#!/usr/bin/env python3
"""Builds the Owner's newer expression images (T-V1-CHARACTER-2) into app-ready WebP sprites.

Input : design/character/reference/jixxy-*.png  (read-only originals, never edited)
Output: character/src/main/res/drawable-nodpi/jixxy_expr_<name>.webp  (512x512, transparent)
        design/character/tools/expressions_manifest.json              (scale, anchor, sizes; for CI/diff)

Rules (D-011): each file <= 512x512 and <= 300 KB, the module's art <= 3 MB. All sprites made here share ONE
scale and the same anchor (bottom-centre of the cat at y=ANCHOR_Y), so swapping between them does not jump.
The two originals with an opaque black background are cut out by flood fill from the border; thin detached
outline rings and specks are dropped, then the edge is feathered a little. Nothing is redrawn.

Usage: python3 design/character/tools/build_expressions.py [--check]   (--check: verify budgets, write nothing)
Needs: Pillow, numpy, scipy.
"""
import json
import sys
from collections import deque
from pathlib import Path

import numpy as np
from PIL import Image, ImageFilter
from scipy import ndimage

ROOT = Path(__file__).resolve().parents[3]
REF = ROOT / "design/character/reference"
RES = ROOT / "character/src/main/res/drawable-nodpi"
MANIFEST = ROOT / "design/character/tools/expressions_manifest.json"

CANVAS = 512
ANCHOR_Y = 500            # bottom of the cat, same as the sheet-cut sprites
WIDEST_PX = 470           # the widest source cat is scaled to this many canvas px; all others use the same scale
BLACK_MAX = 10            # a pixel is "background black" when its brightest channel is <= this
MIN_PART_PX = 600         # connected parts smaller than this (source px) are specks and are dropped
WIDE_PX = 9              # background channels narrower than this (source px) inside the cat stay opaque
RING_OPEN = 5             # px (source): opening removes detached outline rings thinner than this
QUALITY = 88
MAX_FILE_KB = 300
MAX_TOTAL_KB = 3 * 1024

# name -> (file, has_opaque_black_background)
SOURCES = {
    "sleep": ("jixxy-sleep.png", False),
    "yawn": ("jixxy-yawn-OPAQUE-BLACK-BG.png", True),
    "stretch": ("jixxy-stretch-OPAQUE-BLACK-BG.png", True),
}


def cut_out_black(rgb: Image.Image) -> np.ndarray:
    """Alpha (uint8) for an image on a solid black background: flood fill black from the border."""
    v = np.asarray(rgb).max(axis=2)
    h, w = v.shape
    cand = v <= BLACK_MAX
    bg = np.zeros((h, w), bool)
    q = deque()
    for x in range(w):
        for y in (0, h - 1):
            if cand[y, x] and not bg[y, x]:
                bg[y, x] = True
                q.append((y, x))
    for y in range(h):
        for x in (0, w - 1):
            if cand[y, x] and not bg[y, x]:
                bg[y, x] = True
                q.append((y, x))
    while q:
        y, x = q.popleft()
        for ny, nx in ((y + 1, x), (y - 1, x), (y, x + 1), (y, x - 1)):
            if 0 <= ny < h and 0 <= nx < w and cand[ny, nx] and not bg[ny, nx]:
                bg[ny, nx] = True
                q.append((ny, nx))
    # Thin near-black channels inside the cat (between toes, fur lines) are reachable from the border and would
    # become see-through speckles. Keep only background that is WIDE: erode it, keep what touches the border,
    # then grow back a few px (limited to the original background) so the true edge stays crisp.
    wide = ndimage.binary_erosion(bg, structure=np.ones((WIDE_PX, WIDE_PX), bool), border_value=1)
    labels, n = ndimage.label(wide)
    border = set(np.unique(np.concatenate([labels[0], labels[-1], labels[:, 0], labels[:, -1]]))) - {0}
    core = np.isin(labels, list(border))
    bg = ndimage.binary_dilation(core, structure=np.ones((WIDE_PX, WIDE_PX), bool), iterations=2) & bg
    return np.where(bg, 0, 255).astype(np.uint8)


def clean_alpha(alpha: np.ndarray) -> np.ndarray:
    """Drops detached thin rings and specks, keeps solid parts (the cat and its notice marks)."""
    solid = alpha > 8
    opened = ndimage.binary_opening(solid, structure=np.ones((RING_OPEN, RING_OPEN), bool))
    labels, n = ndimage.label(opened)
    if n == 0:
        return alpha
    sizes = ndimage.sum(opened, labels, range(1, n + 1))
    keep = np.isin(labels, [i + 1 for i, s in enumerate(sizes) if s >= MIN_PART_PX])
    # grow back what the opening ate at the true edge, but only next to kept parts (no rings return)
    keep = ndimage.binary_dilation(keep, structure=np.ones((RING_OPEN, RING_OPEN), bool)) & solid
    return np.where(keep, alpha, 0).astype(np.uint8)


def load_cutout(file: str, opaque_black: bool) -> Image.Image:
    im = Image.open(REF / file).convert("RGBA")
    alpha = cut_out_black(im.convert("RGB")) if opaque_black else np.asarray(im.getchannel("A"))
    alpha = clean_alpha(alpha)
    # soften only the edge a little so the cut does not look sawtooth; interior stays fully opaque
    soft = Image.fromarray(alpha).filter(ImageFilter.GaussianBlur(1.0))
    alpha = np.minimum(alpha, np.asarray(soft)).astype(np.uint8) if opaque_black else alpha
    im.putalpha(Image.fromarray(alpha))
    return im


def main(check_only: bool) -> int:
    cut = {name: load_cutout(*spec) for name, spec in SOURCES.items()}
    boxes = {n: im.getchannel("A").point(lambda a: 255 if a > 8 else 0).getbbox() for n, im in cut.items()}
    widest = max(b[2] - b[0] for b in boxes.values())
    scale = WIDEST_PX / widest
    manifest = {"canvas": CANVAS, "anchorBottomY": ANCHOR_Y, "scale": round(scale, 5), "sprites": {}}
    total_kb = 0
    problems = []
    for name, im in cut.items():
        box = boxes[name]
        crop = im.crop(box).convert("RGBa")  # premultiplied: no dark/light fringe when resizing
        size = (max(1, round((box[2] - box[0]) * scale)), max(1, round((box[3] - box[1]) * scale)))
        crop = crop.resize(size, Image.LANCZOS).convert("RGBA")
        x, y = (CANVAS - size[0]) // 2, ANCHOR_Y - size[1]
        if y < 0 or size[0] > CANVAS:
            problems.append(f"{name}: {size} does not fit the canvas")
            continue
        canvas = Image.new("RGBA", (CANVAS, CANVAS), (0, 0, 0, 0))
        canvas.alpha_composite(crop, (x, y))
        out = RES / f"jixxy_expr_{name}.webp"
        tmp = out.with_suffix(".tmp.webp")
        canvas.save(tmp, "WEBP", quality=QUALITY, method=6, exact=True)
        kb = tmp.stat().st_size / 1024
        total_kb += kb
        if kb > MAX_FILE_KB:
            problems.append(f"{name}: {kb:.0f} KB > {MAX_FILE_KB} KB")
        if check_only:
            tmp.unlink()
        else:
            tmp.replace(out)
        manifest["sprites"][name] = {"source": SOURCES[name][0], "opaqueBlackCutout": SOURCES[name][1],
                                     "cropBox": list(box), "drawnSize": list(size), "offset": [x, y]}
    existing_kb = sum(p.stat().st_size for p in RES.glob("*")) / 1024
    print(f"scale {scale:.4f}; new sprites {total_kb:.0f} KB; res folder now ~{existing_kb:.0f} KB (budget {MAX_TOTAL_KB})")
    if existing_kb > MAX_TOTAL_KB:
        problems.append("character resources exceed 3 MB")
    if problems:
        print("\n".join(problems))
        return 1
    if not check_only:
        MANIFEST.write_text(json.dumps(manifest, indent=2) + "\n")
    return 0


if __name__ == "__main__":
    sys.exit(main("--check" in sys.argv))

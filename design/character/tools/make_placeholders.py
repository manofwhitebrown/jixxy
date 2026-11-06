#!/usr/bin/env python3
"""Generates labelled PLACEHOLDER expression sprites (not the real art).

Real sprites come from the Owner's character sheet (D-012) and replace these files, same names.
Usage: python3 design/character/tools/make_placeholders.py
Output: character/src/main/res/drawable-nodpi/jixxy_expr_<name>.png  (256 x 256, transparent)
"""
import os
from PIL import Image, ImageDraw, ImageFont

INK, CHARCOAL, CREAM, PINK = (21, 18, 26), (43, 40, 51), (243, 235, 221), (232, 160, 180)
SIZE = 256
OUT = os.path.join(os.path.dirname(__file__), "..", "..", "..", "character", "src", "main", "res", "drawable-nodpi")

# name -> (caption, lid 0..1 (0 open, 1 shut), mood tweak)
EXPR = {
    "really": ("...really?", 0.55, "flat"),
    "nap": ("Nap time...", 1.0, "flat"),
    "peek": ("(peek)", 0.2, "flat"),
    "bro_what": ("Bro what?!", 0.3, "angry"),
    "ooh_nice": ("Ooh nice!", 0.0, "happy"),
    "soft": ("...you good?", 0.1, "happy"),
    "smug": ("Hehe.", 0.6, "happy"),
    "shocked": ("WHAT?!", 0.0, "angry"),
    "dropped": ("Rude.", 0.35, "angry"),
    "low_battery": ("5%?!", 0.4, "angry"),
    "charging": ("Finally, food.", 0.5, "happy"),
    "snacks": ("Snacks. Now.", 0.45, "happy"),
    "headphones": ("(music)", 0.5, "happy"),
    "respect": ("Respect.", 0.4, "happy"),
    "blanket": ("sleep...", 0.8, "flat"),
}


def font(px):
    for p in ("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",):
        if os.path.exists(p):
            return ImageFont.truetype(p, px)
    return ImageFont.load_default()


def draw(name, caption, lid, tweak):
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Same anchor in every file: body sits on the bottom centre.
    d.ellipse((46, 70, 210, 236), fill=INK, outline=CHARCOAL, width=4)
    d.polygon([(58, 96), (66, 30), (112, 78)], fill=INK, outline=CHARCOAL)
    d.polygon([(198, 96), (190, 30), (144, 78)], fill=INK, outline=CHARCOAL)
    d.polygon([(70, 84), (74, 52), (98, 78)], fill=PINK)
    d.polygon([(186, 84), (182, 52), (158, 78)], fill=PINK)
    for cx in (98, 158):
        top, bottom = 126, 156
        d.ellipse((cx - 16, top, cx + 16, bottom), fill=CREAM)
        d.rectangle((cx - 17, top - 1, cx + 17, top + (bottom - top) * lid), fill=INK)
        d.line((cx - 17, top + (bottom - top) * lid, cx + 17, top + (bottom - top) * lid), fill=CHARCOAL, width=3)
        if tweak == "angry":
            inner_up = -8 if cx < 128 else 8  # brows slant down towards the centre
            d.line((cx - 18, top - 10 + inner_up, cx + 18, top - 10 - inner_up), fill=CREAM, width=3)
    if tweak == "happy":
        d.arc((108, 160, 148, 186), 20, 160, fill=CREAM, width=3)
    else:
        d.line((112, 176, 144, 176), fill=CREAM, width=3)
    f = font(20)
    w = d.textlength(caption, font=f)
    d.text(((SIZE - w) / 2, 200), caption, font=f, fill=CREAM)
    d.text((8, 4), "PLACEHOLDER", font=font(12), fill=PINK)
    img.save(os.path.join(OUT, f"jixxy_expr_{name}.png"), optimize=True)


if __name__ == "__main__":
    os.makedirs(OUT, exist_ok=True)
    for key, (cap, lid, tweak) in EXPR.items():
        draw(key, cap, lid, tweak)
    print(f"wrote {len(EXPR)} placeholders to {os.path.normpath(OUT)}")

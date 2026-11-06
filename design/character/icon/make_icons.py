"""Builds Jixxy icon source art from jixxy_icon_face_1024.png (Owner's art, untouched).
Run: python3 make_icons.py   (needs Pillow). Outputs sit next to this script."""
from PIL import Image, ImageDraw
import os
HERE = os.path.dirname(os.path.abspath(__file__))
BG = (94, 75, 122)  # muted purple #5E4B7A (palette); keep in sync with CharacterPalette
face = Image.open(os.path.join(HERE, "jixxy_icon_face_1024.png")).convert("RGBA")
face = face.crop(face.getchannel("A").getbbox())

def place(canvas_px, face_w):
    """Face scaled to face_w px wide, centred (optical centre slightly low: ears are empty space)."""
    f = face.resize((face_w, round(face.height * face_w / face.width)), Image.LANCZOS)
    c = Image.new("RGBA", (canvas_px, canvas_px), (0, 0, 0, 0))
    c.alpha_composite(f, ((canvas_px - f.width) // 2, (canvas_px - f.height) // 2))
    return c

# Adaptive icon: 108dp canvas, safe zone = centred 66dp circle (61%). Face bbox diagonal must fit.
# Keep face width <= 0.61*1024*0.80 ~ 500 px so the whole head stays inside the safe zone.
fg = place(1024, 500)
fg.save(os.path.join(HERE, "icon_foreground_1024.png"))

Image.new("RGB", (1024, 1024), BG).save(os.path.join(HERE, "icon_background_1024.png"))

# Monochrome (themed icons): alpha only matters, drawn solid white; features cut out so it reads.
def silhouette(img):
    a = img.getchannel("A")
    out = Image.new("RGBA", img.size, (255, 255, 255, 0))
    out.putalpha(a)
    return out
mono = silhouette(fg)
# cut eyes + nose out of the silhouette using the face's light pixels (eye whites) and dark pupils
px = face.resize((500, round(face.height * 500 / face.width)), Image.LANCZOS)
mono_face = Image.new("RGBA", px.size, (255, 255, 255, 255))
mono_face.putalpha(px.getchannel("A"))
lum = px.convert("L")
ap = mono_face.load(); lp = lum.load(); pp = px.load()
for y in range(px.height):
    for x in range(px.width):
        r, g, b, a = pp[x, y]
        if a > 200 and (r > 150 and g > 120 and y > px.height * 0.55):  # eye whites + pink-ish lower face highlights
            ap[x, y] = (255, 255, 255, 60)
mono_canvas = Image.new("RGBA", (1024, 1024), (0, 0, 0, 0))
mono_canvas.alpha_composite(mono_face, ((1024 - px.width) // 2, (1024 - px.height) // 2))
mono_canvas.save(os.path.join(HERE, "icon_monochrome_1024.png"))

# Notification small icon: 96x96 white silhouette, no colour (Android tints it).
cut = mono_face.copy()
cp = cut.load()
for y in range(cut.height):
    for x in range(cut.width):
        if cp[x, y][3] == 60:
            cp[x, y] = (255, 255, 255, 0)  # eyes fully transparent so they read at 24dp
cut = cut.crop(cut.getchannel("A").getbbox())
cut = cut.resize((84, round(cut.height * 84 / cut.width)), Image.LANCZOS)
small = Image.new("RGBA", (96, 96), (0, 0, 0, 0))
small.alpha_composite(cut, ((96 - cut.width) // 2, (96 - cut.height) // 2))
small.save(os.path.join(HERE, "notification_small_96.png"))

# Store icon 512x512: full-bleed purple, face a little larger than the adaptive one.
store = Image.new("RGBA", (512, 512), BG + (255,))
store.alpha_composite(place(512, 380))
store.convert("RGB").save(os.path.join(HERE, "store_icon_512.png"))
print("ok")

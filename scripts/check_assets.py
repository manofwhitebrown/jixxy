#!/usr/bin/env python3
"""Asset budget guard (decision D-011). Warn-only by default; STRICT=1 makes violations fail.

Checks
  1. character/src/main/res totals <= 3 MB
  2. no single image there is > 512x512 px or > 300 KB
  3. no unexpected binary files anywhere in the repo (allowlist below)
  4. no remote URLs used as resources inside site/ (plain <a href> links are fine)
  6. sounds (D-021): character/src/main/res/raw/*.ogg only, <= 400 KB combined, and every file listed with a source URL and licence
     (CC0 or generated) in design/character/audio/CREDITS.md
  8. generated files (__pycache__, *.pyc) are never committed: they are "unexpected binary file"s (seen once, 2026-11-07)
  7. kept source clips: design/character/audio/source/*.wav, <= 600 KB each and <= 1.5 MB total (not shipped)
  5. site/ stays small: <= 1 MB in total, no single file > 300 KB (T-D3-5)
"""
import os, re, struct, subprocess, sys

CHAR_RES = "character/src/main/res"
MAX_TOTAL = 3 * 1024 * 1024
MAX_FILE = 300 * 1024
MAX_SITE_TOTAL = 1024 * 1024
RAW_DIR = "character/src/main/res/raw"
MAX_AUDIO_TOTAL = 400 * 1024
CREDITS = "design/character/audio/CREDITS.md"
SRC_AUDIO_DIR = "design/character/audio/source"   # kept source clips (not shipped): .wav, <= 600 KB each, <= 1.5 MB total
MAX_SRC_FILE = 600 * 1024
MAX_SRC_TOTAL = 1536 * 1024
MAX_DIM = 512
IMG_EXT = (".png", ".webp", ".jpg", ".jpeg")
# Binary files that are expected. Anything else binary is reported.
BIN_ALLOW_PREFIX = ("design/character/reference/", "character/src/main/res/", "app/src/main/res/",
                    "site/", "store/", "growth/", "design/")
BIN_ALLOW_FILE = {"app/debug.keystore", "gradle/wrapper/gradle-wrapper.jar"}
BIN_ALLOW_EXT = IMG_EXT + (".ico", ".svg", ".woff2")

problems = []


def warn(msg):
    problems.append(msg)
    print(f"::warning title=Asset budget::{msg}")


def image_size(path):
    """(width, height) from the file header, or None if unknown. No third-party libraries."""
    with open(path, "rb") as f:
        h = f.read(64)
    if h[:8] == b"\x89PNG\r\n\x1a\n":
        return struct.unpack(">II", h[16:24])
    if h[:4] == b"RIFF" and h[8:12] == b"WEBP":
        kind = h[12:16]
        if kind == b"VP8X":
            return (1 + int.from_bytes(h[24:27], "little"), 1 + int.from_bytes(h[27:30], "little"))
        if kind == b"VP8L":
            b = int.from_bytes(h[21:25], "little")
            return (1 + (b & 0x3FFF), 1 + ((b >> 14) & 0x3FFF))
        if kind == b"VP8 ":
            return (struct.unpack("<H", h[26:28])[0] & 0x3FFF, struct.unpack("<H", h[28:30])[0] & 0x3FFF)
    return None


def tracked_files():
    out = subprocess.run(["git", "ls-files"], capture_output=True, text=True, check=True).stdout
    return [p for p in out.splitlines() if os.path.isfile(p)]


def is_binary(path):
    with open(path, "rb") as f:
        return b"\0" in f.read(4096)


def main():
    files = tracked_files()

    total = 0
    for p in files:
        if p.startswith(CHAR_RES + "/"):
            size = os.path.getsize(p)
            total += size
            if p.lower().endswith(IMG_EXT):
                if size > MAX_FILE:
                    warn(f"{p} is {size // 1024} KB (limit {MAX_FILE // 1024} KB)")
                dim = image_size(p)
                if dim is None:
                    warn(f"{p}: could not read the image size")
                elif dim[0] > MAX_DIM or dim[1] > MAX_DIM:
                    warn(f"{p} is {dim[0]}x{dim[1]} px (limit {MAX_DIM}x{MAX_DIM})")
    print(f"character resources total: {total / 1024:.0f} KB of {MAX_TOTAL // 1024} KB")
    if total > MAX_TOTAL:
        warn(f"{CHAR_RES} totals {total // 1024} KB (limit {MAX_TOTAL // 1024} KB)")

    for p in files:
        if p.startswith(RAW_DIR + "/") and p.lower().endswith(".ogg"):
            continue   # audio is budgeted and credited below
        if p.startswith(SRC_AUDIO_DIR + "/") and p.lower().endswith(".wav"):
            continue   # source clips, size-checked below
        if p in BIN_ALLOW_FILE or p.lower().endswith(BIN_ALLOW_EXT) and p.startswith(BIN_ALLOW_PREFIX):
            continue
        if is_binary(p):
            warn(f"unexpected binary file: {p}")

    remote = re.compile(
        r"""(?:\b(?:src|srcset)\s*=\s*["']?(?:https?:)?//|<link\b[^>]*\bhref\s*=\s*["']?https?://|url\(\s*["']?https?://|@import\s+["']?(?:url\()?["']?https?://|\bfetch\(\s*["']https?://)""",
        re.I)
    for p in files:
        if p.startswith("site/") and p.lower().endswith((".html", ".htm", ".css", ".js", ".svg")):
            with open(p, encoding="utf-8", errors="replace") as f:
                for n, line in enumerate(f, 1):
                    if remote.search(line):
                        warn(f"{p}:{n} loads a remote resource (site must be self-contained)")

    src = [p for p in files if p.startswith(SRC_AUDIO_DIR + "/") and p.lower().endswith(".wav") and os.path.exists(p)]
    for p in src:
        if os.path.getsize(p) > MAX_SRC_FILE:
            warn(f"{p} is {os.path.getsize(p) // 1024} KB (max {MAX_SRC_FILE // 1024} KB per source clip)")
    if sum(os.path.getsize(p) for p in src) > MAX_SRC_TOTAL:
        warn(f"source audio clips total {sum(os.path.getsize(p) for p in src) // 1024} KB (max {MAX_SRC_TOTAL // 1024} KB)")
    raw = [p for p in files if p.startswith(RAW_DIR + "/") and os.path.exists(p)]
    for p in raw:
        if not p.lower().endswith(".ogg"):
            warn(f"{p}: only .ogg sounds are allowed in res/raw")
    ogg = [p for p in raw if p.lower().endswith(".ogg")]
    if ogg:
        audio_total = sum(os.path.getsize(p) for p in ogg)
        print(f"audio total: {audio_total // 1024} KB of {MAX_AUDIO_TOTAL // 1024} KB ({len(ogg)} files)")
        if audio_total > MAX_AUDIO_TOTAL:
            warn(f"sounds are {audio_total // 1024} KB, budget is {MAX_AUDIO_TOTAL // 1024} KB")
        credits = open(CREDITS, encoding="utf-8").read() if os.path.exists(CREDITS) else None
        if credits is None:
            warn(f"{CREDITS} is missing: every sound needs a credit line with source URL and licence")
        else:
            for p in ogg:
                name = os.path.basename(p)
                lines = [l for l in credits.splitlines() if name in l]
                if not lines:
                    warn(f"{p} has no line in {CREDITS}")
                elif not any(re.search(r"https?://", l) and re.search(r"CC0|generated", l, re.I) for l in lines):
                    warn(f"{p}: its line in {CREDITS} needs a source URL and a licence (CC0 or generated)")

    site_files = [p for p in files if p.startswith("site/") and os.path.exists(p)]
    site_total = sum(os.path.getsize(p) for p in site_files)
    print(f"site total: {site_total // 1024} KB of {MAX_SITE_TOTAL // 1024} KB")
    if site_total > MAX_SITE_TOTAL:
        warn(f"site/ is {site_total // 1024} KB, budget is {MAX_SITE_TOTAL // 1024} KB")
    for p in site_files:
        if os.path.getsize(p) > MAX_FILE:
            warn(f"{p} is {os.path.getsize(p) // 1024} KB (max {MAX_FILE // 1024} KB per site file)")

    if problems:
        print(f"{len(problems)} asset problem(s).")
        if os.environ.get("STRICT") == "1":
            return 1
        print("Warn-only mode; set STRICT=1 to fail on these.")
    else:
        print("Assets within budget.")
    return 0


if __name__ == "__main__":
    sys.exit(main())

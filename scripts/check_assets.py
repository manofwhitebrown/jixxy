#!/usr/bin/env python3
"""Asset budget guard (decision D-011). Warn-only by default; STRICT=1 makes violations fail.

Checks
  1. character/src/main/res totals <= 3 MB
  2. no single image there is > 512x512 px or > 300 KB
  3. no unexpected binary files anywhere in the repo (allowlist below)
  4. no remote URLs used as resources inside site/ (plain <a href> links are fine)
"""
import os, re, struct, subprocess, sys

CHAR_RES = "character/src/main/res"
MAX_TOTAL = 3 * 1024 * 1024
MAX_FILE = 300 * 1024
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
        if p in BIN_ALLOW_FILE or p.lower().endswith(BIN_ALLOW_EXT) and p.startswith(BIN_ALLOW_PREFIX):
            continue
        if is_binary(p):
            warn(f"unexpected binary file: {p}")

    remote = re.compile(
        r"""(?:\bsrc\s*=\s*["']?https?://|<link\b[^>]*\bhref\s*=\s*["']?https?://|url\(\s*["']?https?://|@import\s+["']?(?:url\()?["']?https?://|\bfetch\(\s*["']https?://)""",
        re.I)
    for p in files:
        if p.startswith("site/") and p.lower().endswith((".html", ".htm", ".css", ".js", ".svg")):
            with open(p, encoding="utf-8", errors="replace") as f:
                for n, line in enumerate(f, 1):
                    if remote.search(line):
                        warn(f"{p}:{n} loads a remote resource (site must be self-contained)")

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

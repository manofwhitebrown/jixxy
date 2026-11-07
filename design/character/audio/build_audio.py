#!/usr/bin/env python3
"""Builds Jixxy's meow/purr cues (D-021) from the CC0 source clips in design/character/audio/source/.

Input : design/character/audio/source/*.wav (the five CC0 clips used, kept in the repo).
Output: character/src/main/res/raw/jixxy_snd_<cue>.ogg  (mono Ogg Vorbis, trimmed, faded, peak-normalised)
Budget: all audio <= 400 KB (checked here). Sources and licences: design/character/audio/CREDITS.md.

Each cue is cut from a source clip, optionally re-pitched (rate changes pitch and length together) or given a
wobble, faded so there are no clicks, and peak-normalised. The cut points were chosen from the clips' loudness and
pitch curves, not by ear: if a cue sounds wrong, change its row below and re-run.

Usage: python3 design/character/audio/build_audio.py        Needs: ffmpeg (libvorbis), numpy.
"""
import subprocess
import sys
import tempfile
import wave
from pathlib import Path

import numpy as np

HERE = Path(__file__).resolve().parent
SRC = HERE / "source"
OUT = HERE.parents[2] / "character/src/main/res/raw"
MAX_TOTAL_KB = 400
SR = 32_000  # plenty for meows, keeps files tiny

# cue -> (source, start s, end s, rate (pitch+speed), wobble Hz or 0, fade in s, fade out s, peak 0..1)
CUES = {
    "curious_mew": ("cat_softmew", 0.28, 1.10, 1.00, 0, 0.010, 0.080, 0.85),   # the rising mew
    "annoyed_mew": ("cat_mewpurr", 0.00, 0.75, 1.00, 0, 0.005, 0.120, 0.90),   # short sharp mew
    "grumpy_mew": ("cat_mewpurr2", 0.20, 1.30, 0.85, 0, 0.015, 0.150, 0.85),   # lower and slower
    "purr": ("cat_purrsleepy_loop", 0.40, 2.20, 1.00, 0, 0.250, 0.500, 0.55),  # soft, fades both ends
    "happy_mew": ("cat_mewfood", 0.42, 1.05, 1.00, 0, 0.015, 0.120, 0.85),     # quiet source: normalised up
    "worried_mew": ("cat_softmew", 1.35, 2.15, 1.00, 5.5, 0.010, 0.120, 0.80), # small mew with a wobble
    "yelp": ("cat_mewpurr", 0.00, 0.30, 1.30, 0, 0.005, 0.060, 0.90),          # very short, higher
}


def cut(src: str, start: float, end: float, rate: float, wobble: float, tmp: Path) -> np.ndarray:
    chain = [f"atrim={start}:{end}", "asetpts=PTS-STARTPTS"]
    if rate != 1.0:
        chain.append(f"asetrate={int(44100 * rate)}")
    chain.append(f"aresample={SR}")
    if wobble:
        chain.append(f"vibrato=f={wobble}:d=0.35")
    out = tmp / "cut.wav"
    subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", str(SRC / f"{src}.wav"), "-ac", "1",
                    "-af", ",".join(chain), "-ar", str(SR), str(out)], check=True)
    with wave.open(str(out)) as w:
        return np.frombuffer(w.readframes(w.getnframes()), dtype=np.int16).astype(np.float64) / 32768.0


def main() -> int:
    OUT.mkdir(parents=True, exist_ok=True)
    total = 0.0
    with tempfile.TemporaryDirectory() as t:
        tmp = Path(t)
        for name, (src, a, b, rate, wob, fin, fout, peak) in CUES.items():
            x = cut(src, a, b, rate, wob, tmp)
            n = len(x)
            x *= peak / max(np.abs(x).max(), 1e-6)
            fi, fo = int(fin * SR), int(fout * SR)
            x[:fi] *= np.linspace(0, 1, fi)
            x[n - fo:] *= np.linspace(1, 0, fo)
            wav = tmp / f"{name}.wav"
            with wave.open(str(wav), "wb") as w:
                w.setnchannels(1)
                w.setsampwidth(2)
                w.setframerate(SR)
                w.writeframes((x * 32767).astype(np.int16).tobytes())
            ogg = OUT / f"jixxy_snd_{name}.ogg"
            subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", str(wav), "-c:a", "libvorbis", "-q:a", "2", str(ogg)], check=True)
            kb = ogg.stat().st_size / 1024
            total += kb
            print(f"{name:12s} {n / SR:4.2f}s {kb:5.1f} KB  <- {src} {a}-{b}s rate {rate}")
    print(f"total {total:.0f} KB (budget {MAX_TOTAL_KB})")
    return 0 if total <= MAX_TOTAL_KB else 1


if __name__ == "__main__":
    sys.exit(main())

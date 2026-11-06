#!/usr/bin/env python3
"""Turns Gradle/Kotlin/lint failures in a build log into GitHub ::error:: annotations.

Why: agents without access to raw Actions logs can still read annotations through the
Checks API. GitHub shows at most 10 error annotations per step, so matches are merged
into at most MAX_BLOCKS blocks, each carrying a few lines of context.

Usage: ci_annotate_errors.py <logfile> <label>
Always exits 0 (it only reports)."""
import re, sys
from pathlib import Path

MAX_BLOCKS = 9
CONTEXT_AFTER = 3
MAX_BLOCK_CHARS = 3500
MATCH = re.compile(
    r"(^e: )|(\berror:)|(\bFAILED\b)|(What went wrong)|(Execution failed for task)|"
    r"(Could not (resolve|find|determine))|(Unresolved reference)|(^\s*Lint found .* error)|"
    r"(^\S+\.kt:\d+:\d+.*(Error|error))|(\b\d+ tests? completed, \d+ failed)|(Test .* FAILED)")

def esc(s: str) -> str:
    return s.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")

def main() -> int:
    if len(sys.argv) < 3:
        print("usage: ci_annotate_errors.py <logfile> <label>"); return 0
    path, label = Path(sys.argv[1]), sys.argv[2]
    if not path.exists():
        print(f"::warning::{label}: no log file at {path}"); return 0
    lines = path.read_text(errors="replace").splitlines()
    hits = [i for i, l in enumerate(lines) if MATCH.search(l)]
    if not hits:
        print(f"::warning title={label}::step failed but no recognisable error lines; last lines: "
              + esc(" | ".join(lines[-6:])[:1500]))
        return 0
    blocks, start, end = [], None, None
    for i in hits:
        lo, hi = i, min(len(lines), i + CONTEXT_AFTER + 1)
        if start is None: start, end = lo, hi
        elif lo <= end: end = max(end, hi)
        else: blocks.append((start, end)); start, end = lo, hi
    blocks.append((start, end))
    shown = blocks[:MAX_BLOCKS]
    for n, (lo, hi) in enumerate(shown, 1):
        text = "\n".join(l.rstrip() for l in lines[lo:hi])[:MAX_BLOCK_CHARS]
        print(f"::error title={label} ({n}/{len(blocks)})::{esc(text)}")
    if len(blocks) > len(shown):
        print(f"::warning title={label}::{len(blocks) - len(shown)} more error block(s) not shown")
    return 0

if __name__ == "__main__":
    sys.exit(main())

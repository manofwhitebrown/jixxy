#!/usr/bin/env python3
"""Fallback line-bank validator (used until ENGINE's :engine:validateLines exists).
Checks: valid JSON arrays, required fields, unique ids, text <= 90 chars, declared slots only,
allowed slots only. Skips cleanly if the line bank does not exist yet."""
import json, re, sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LINES = ROOT / "app/src/main/assets/lines"
ALLOWED_SLOTS = {"app", "minutes", "count", "time", "battery"}
REQUIRED = ["id", "trigger", "text", "lang"]

def main() -> int:
    if not LINES.exists():
        print("validate_lines: no line bank yet, skipping")
        return 0
    errors, seen = [], {}
    for f in sorted(LINES.rglob("*.json")):
        rel = f.relative_to(ROOT)
        try:
            data = json.loads(f.read_text(encoding="utf-8"))
        except Exception as e:
            errors.append(f"{rel}: invalid JSON ({e})"); continue
        if not isinstance(data, list):
            errors.append(f"{rel}: top level must be an array"); continue
        for i, line in enumerate(data):
            where = f"{rel}[{i}]"
            missing = [k for k in REQUIRED if k not in line]
            if missing:
                errors.append(f"{where}: missing {missing}"); continue
            lid = line["id"]
            if lid in seen: errors.append(f"{where}: duplicate id '{lid}' (also {seen[lid]})")
            seen[lid] = where
            text = line["text"]
            if len(text) > 90: errors.append(f"{where} ({lid}): text is {len(text)} chars (max 90)")
            used = set(re.findall(r"\{(\w+)\}", text))
            declared = set(line.get("slots", []))
            if used - declared: errors.append(f"{where} ({lid}): uses undeclared slots {sorted(used - declared)}")
            if (used | declared) - ALLOWED_SLOTS: errors.append(f"{where} ({lid}): disallowed slots {sorted((used | declared) - ALLOWED_SLOTS)}")
    for e in errors: print(f"::error::{e}")
    print(f"validate_lines: {len(seen)} lines checked, {len(errors)} problem(s)")
    return 1 if errors else 0

if __name__ == "__main__":
    sys.exit(main())

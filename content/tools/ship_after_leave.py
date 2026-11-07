#!/usr/bin/env python3
"""Moves the staged D-024 after-Leave lines (content/staging/after_leave_*.json) into the live bank.
Run ONLY after ENGINE has reserved the `after_leave` tag in BankLinePicker (otherwise the lines
would show on ordinary first prompts). Idempotent: skips ids already shipped.
    python3 content/tools/ship_after_leave.py && python3 scripts/validate_lines.py"""
import json
from pathlib import Path
R = Path(__file__).resolve().parent.parent.parent
for lang, staged in (("en", "after_leave_en.json"), ("hinglish", "after_leave_hinglish.json")):
    bank = R / f"app/src/main/assets/lines/{lang}/pause_prompt.json"
    d = json.loads(bank.read_text(encoding="utf-8")); have = {l["id"] for l in d}
    new = [l for l in json.loads((R / "content/staging" / staged).read_text(encoding="utf-8")) if l["id"] not in have]
    d += new
    bank.write_text(json.dumps(d, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"{lang}: shipped {len(new)} line(s)")

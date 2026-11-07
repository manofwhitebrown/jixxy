#!/usr/bin/env python3
"""Jixxy line linter (WRITER, T-W3-9). Stdlib only; run from anywhere:
    python3 content/tools/lint_lines.py            # lint EN + Hinglish banks
    python3 content/tools/lint_lines.py --strict   # warnings also fail

ERROR (exit 1): text too long when slots are filled with realistic values, undeclared or
unknown slots, invalid `review` value, banned word/idiom, bad tier tag, missing id/text.
WARNING: duplicate 2-word openers within a trigger+category, emoji density, mild words that
need a human look, lines missing a nag tier (error once --require-nag is passed).
Does not replace scripts/validate_lines.py or :engine:validateLines; it adds content rules.
"""
import json, re, sys, collections
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent.parent
LINES = ROOT / "app/src/main/assets/lines"
MAX_LEN = 90
SLOT_SAMPLE = {"app": "Instagram", "minutes": "125", "count": "12", "time": "11:45 pm", "battery": "100"}
REVIEW = {"draft", "reviewed", "native_reviewed"}
NAG_TAGS = {"calm_ok", "nag_only"}
MAX_SAME_OPENER = 3

# Hard errors: never ship. Word-boundary, case-insensitive.
BANNED = [
    # self-harm and suicide idioms
    r"kill (myself|yourself)", r"\bkms\b", r"\bkys\b", r"end it all", r"want to die", r"hang (myself|yourself)",
    r"\bsuicid\w*", r"self[- ]harm", r"cut (myself|yourself)",
    # body and weight
    r"\bfat\b", r"\bugly\b", r"\bskinny\b", r"\bobese\b", r"\bdiet\b", r"\bcalories?\b", r"\bweigh(t| in)\b",
    # mental-health wording used as a joke or label
    r"\bdepress\w*", r"\banxi\w*", r"\bbipolar\b", r"\badhd\b", r"\bocd\b", r"\bschizo\w*", r"\btherap\w*",
    r"\binsane\b", r"\bpsycho\w*", r"\bretard\w*", r"\bmental(ly)? (ill|health)",
    # religion, caste, region, politics, gender slurs
    r"\b(hindu|muslim|christian|sikh|jewish|islam\w*|church|mosque|temple|quran|bible|bhagwan)\b",
    r"\bpray\w*", r"\bcaste\b", r"\b(dalit|brahmin)\b", r"\b(bjp|congress|modi|trump)\b",
    r"\bslut\b", r"\bwhore\b", r"\bfaggot\b", r"\bnigg\w+", r"\btranny\b",
    # medical claims and screen-time promises
    r"\bdiagnos\w*", r"\bmedicat\w*", r"\bdoctor\b", r"\bcure[sd]?\b", r"\baddict\w*",
    r"reduce[sd]? your screen time", r"lower[s]? your screen time", r"fix(es)? your (sleep|habits)",
    # guilt and shame hooks
    r"\bworthless\b", r"\bdisappoint\w*", r"\bashamed\b", r"\bpathetic\b", r"\bloser\b",
    # hard profanity (EN + Hinglish)
    r"\bfuck\w*", r"\bshit\w*", r"\bbitch\w*", r"\bchutiy\w*", r"\bmadarch\w*", r"\bbhosd\w*", r"\bgaand\w*", r"\bbehench\w*",
]
BANNED_RE = [re.compile(p, re.I) for p in BANNED]

# Warnings: a human should look (owner canon or borderline).
WATCH = [r"seek help", r"\bpagal\b", r"\bcrazy\b", r"\bdead\b", r"\bdie[sd]?\b", r"\bdying\b", r"\bhate\b",
         r"\bstupid\b", r"\bidiot\b", r"\bdumb\b", r"\blazy\b"]
WATCH_RE = [re.compile(p, re.I) for p in WATCH]
EMOJI_RE = re.compile("[\U0001F300-\U0001FAFF\u2600-\u27BF]")


def render(text: str) -> str:
    return re.sub(r"\{(\w+)\}", lambda m: SLOT_SAMPLE.get(m.group(1), "XXXX"), text)


def opener(text: str) -> str:
    words = re.sub(r"[^\w{}' ]", "", text.lower()).split()
    return " ".join(words[:2])


def main() -> int:
    strict = "--strict" in sys.argv
    require_nag = "--require-nag" in sys.argv
    errors, warns = [], []
    total = 0
    openers = collections.defaultdict(list)
    seen_ids = set()
    files = sorted(LINES.rglob("*.json"))
    if not files:
        print("lint_lines: no line bank found, nothing to do")
        return 0
    for f in files:
        rel = f.relative_to(ROOT)
        try:
            data = json.loads(f.read_text(encoding="utf-8"))
        except Exception as e:
            errors.append(f"{rel}: invalid JSON ({e})")
            continue
        for l in data:
            total += 1
            i = l.get("id", "?")
            t = l.get("text", "")
            if not l.get("id") or not t:
                errors.append(f"{rel}: line without id/text")
                continue
            if i in seen_ids:
                errors.append(f"{i}: duplicate id")
            seen_ids.add(i)
            declared = set(l.get("slots", []))
            used = set(re.findall(r"\{(\w+)\}", t))
            if used - declared:
                errors.append(f"{i}: undeclared slot(s) {sorted(used - declared)}")
            unknown = used - set(SLOT_SAMPLE)
            if unknown:
                errors.append(f"{i}: unknown slot(s) {sorted(unknown)}")
            r = render(t)
            if len(r) > MAX_LEN:
                errors.append(f"{i}: {len(r)} chars when filled (max {MAX_LEN}): {t}")
            rv = l.get("review", "draft")
            if rv not in REVIEW:
                errors.append(f"{i}: invalid review '{rv}'")
            if l.get("lang") == "hinglish" and rv == "native_reviewed" and not l.get("reviewed_by"):
                warns.append(f"{i}: native_reviewed without reviewed_by")
            for rx in BANNED_RE:
                m = rx.search(t)
                if m:
                    errors.append(f"{i}: banned '{m.group(0)}' in: {t}")
            for rx in WATCH_RE:
                m = rx.search(t)
                if m:
                    warns.append(f"{i}: check '{m.group(0)}' in: {t}")
            n_emoji = len(EMOJI_RE.findall(t))
            if n_emoji > 1 or (n_emoji == 1 and len(t) > 40):
                warns.append(f"{i}: emoji density ({n_emoji}) in: {t}")
            tags = set(l.get("tags", []))
            nag = tags & NAG_TAGS
            if len(nag) > 1:
                errors.append(f"{i}: both calm_ok and nag_only")
            if not nag and require_nag:
                errors.append(f"{i}: no nag tier tag (calm_ok / nag_only)")
            openers[(l.get("lang"), l.get("trigger"), tuple(l.get("categories", [])), opener(t))].append(i)
    for (lang, trig, cat, op), ids in sorted(openers.items(), key=lambda kv: str(kv[0])):
        if len(ids) > MAX_SAME_OPENER and op:
            warns.append(f"{lang}/{trig}/{'+'.join(cat) or 'any'}: opener '{op}' used {len(ids)}x ({', '.join(ids[:4])}...)")
    for e in errors:
        print("ERROR  ", e)
    for w in warns:
        print("WARNING", w)
    print(f"lint_lines: {total} lines, {len(errors)} error(s), {len(warns)} warning(s)")
    return 1 if errors or (strict and warns) else 0


if __name__ == "__main__":
    sys.exit(main())

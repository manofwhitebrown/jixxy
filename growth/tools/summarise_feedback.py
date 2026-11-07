#!/usr/bin/env python3
"""Summarise tester answers (T-G3-11). Python 3 standard library only.

Usage:
  python3 growth/tools/summarise_feedback.py answers.csv
  python3 growth/tools/summarise_feedback.py --selftest

Start from growth/tools/feedback_template.csv (one row per tester). Columns, matching the five questions in growth/TESTER_PROGRAM.md:
  tester          nickname only (no real names, no phone numbers)
  phone, android  optional, e.g. "Redmi Note 11", "13"
  annoying_1_5    Q1, 1 = never, 5 = wanted to throw the phone
  laugh_line      Q2, the line that made them laugh
  off_line        Q3, the line that felt off
  leave_or_stay   Q4, "leave", "stay", or "both"
  leave_stay_why  Q4 reason
  keep_tomorrow   Q5, yes / maybe / no
  stayed_alive    optional: yes / no (did he stay running in the background)
  notes           anything else

PRIVACY: keep real answers OUTSIDE the repository (do not commit them). Quote testers only with permission, and delete the file when the test ends.
With about 20 testers these are counts and quotes, not statistics: never publish percentages or "reduced screen time" claims.
"""
import csv, sys, io, collections

def norm(v): return (v or "").strip()
def low(v): return norm(v).lower()

def summarise(rows):
    out = []
    n = len(rows)
    out.append(f"Testers: {n}")
    # Q1
    scores = []
    for r in rows:
        try:
            v = int(norm(r.get("annoying_1_5")))
            if 1 <= v <= 5: scores.append(v)
        except ValueError:
            pass
    if scores:
        avg = sum(scores) / len(scores)
        c = collections.Counter(scores)
        out.append(f"Q1 annoying (1-5): average {avg:.1f} from {len(scores)} answers; "
                   + ", ".join(f"{k}:{c.get(k,0)}" for k in range(1, 6)))
        high = [r for r in rows if norm(r.get("annoying_1_5")) in ("4", "5")]
        if high:
            out.append(f"  Rated 4-5 ({len(high)}): " + "; ".join(norm(r.get("tester")) or "?" for r in high))
    else:
        out.append("Q1 annoying: no valid answers")
    # Q4
    ls = collections.Counter(low(r.get("leave_or_stay")) or "(blank)" for r in rows)
    out.append("Q4 Leave vs Stay: " + ", ".join(f"{k}:{v}" for k, v in sorted(ls.items())))
    why = [(norm(r.get("tester")) or "?", norm(r.get("leave_stay_why"))) for r in rows if norm(r.get("leave_stay_why"))]
    if why:
        out.append("  Reasons:")
        out += [f'    {t}: "{w}"' for t, w in why]
    # Q5
    k = collections.Counter(low(r.get("keep_tomorrow")) or "(blank)" for r in rows)
    out.append("Q5 keep him tomorrow: " + ", ".join(f"{a}:{b}" for a, b in sorted(k.items())))
    # alive by brand
    alive = collections.defaultdict(lambda: [0, 0])
    for r in rows:
        a = low(r.get("stayed_alive"))
        if a in ("yes", "no"):
            brand = (norm(r.get("phone")).split() or ["unknown"])[0].title()
            alive[brand][0 if a == "yes" else 1] += 1
    if alive:
        out.append("Stayed alive in background (by phone brand, yes/no): "
                   + ", ".join(f"{b} {y}/{x}" for b, (y, x) in sorted(alive.items())))
        out.append("  A brand where most say no is usually background-kill, not dislike.")
    # lines
    def lines(col):
        c = collections.Counter(norm(r.get(col)) for r in rows if norm(r.get(col)))
        return c.most_common()
    L = lines("laugh_line")
    out.append("Q2 lines that made them laugh (" + str(sum(c for _, c in L)) + "):")
    out += [f'    {c}x "{t}"' for t, c in L] or ["    none"]
    O = lines("off_line")
    out.append("Q3 lines that felt off (" + str(sum(c for _, c in O)) + "):")
    out += [f'    {c}x "{t}"' for t, c in O] or ["    none"]
    out.append("  Send every Q3 line to WRITER. Any 'guilt' or 'too personal' answer goes to MANAGER first.")
    notes = [(norm(r.get("tester")) or "?", norm(r.get("notes"))) for r in rows if norm(r.get("notes"))]
    if notes:
        out.append("Notes:")
        out += [f'    {t}: "{x}"' for t, x in notes]
    return "\n".join(out)

def read_rows(path):
    with open(path, newline="", encoding="utf-8-sig") as f:
        return [r for r in csv.DictReader(f) if any(norm(v) for v in r.values())]

SELFTEST = """tester,phone,android,annoying_1_5,laugh_line,off_line,leave_or_stay,leave_stay_why,keep_tomorrow,stayed_alive,notes
a,Redmi Note 11,13,2,Again?,,leave,he guilt-tripped me,yes,no,
b,Pixel 7,15,5,Again?,too personal,stay,wanted to see what he said,maybe,yes,
c,Redmi 9,12,4,,,both,,no,no,vanished after an hour
d,Samsung A14,14,x,Nice try.,,leave,,yes,yes,
"""

def selftest():
    rows = list(csv.DictReader(io.StringIO(SELFTEST)))
    s = summarise(rows)
    assert "Testers: 4" in s
    assert "average 3.7 from 3 answers" in s, s          # (2+5+4)/3, 'x' ignored
    assert "leave:2" in s and "stay:1" in s and "both:1" in s
    assert "yes:2" in s and "maybe:1" in s and "no:1" in s
    assert "Redmi 0/2" in s and "Pixel 1/0" in s, s       # grouped by brand
    assert '2x "Again?"' in s and 'too personal' in s
    assert "Rated 4-5 (2): b; c" in s
    print("selftest ok")

if __name__ == "__main__":
    if len(sys.argv) == 2 and sys.argv[1] == "--selftest":
        selftest()
    elif len(sys.argv) == 2:
        print(summarise(read_rows(sys.argv[1])))
    else:
        print(__doc__); sys.exit(2)

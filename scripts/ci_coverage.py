#!/usr/bin/env python3
"""Engine line coverage from the JaCoCo XML (ENGINE's task writes it when :engine:test runs).
Prints a table, one ::notice with the numbers, and appends the table to the job summary.
Gate: set COVERAGE_MIN=85 and COVERAGE_PACKAGES=rules,selector to FAIL when any listed package is below that
percentage (match is on the last part of the package name). Without COVERAGE_MIN it only reports.
Usage: python3 scripts/ci_coverage.py [xml path]"""
import os, sys
import xml.etree.ElementTree as ET

path = sys.argv[1] if len(sys.argv) > 1 else "engine/build/reports/jacoco/test/jacocoTestReport.xml"
if not os.path.exists(path):
    print(f"::warning title=Coverage::no JaCoCo report at {path} (tests up to date and cached, or the task did not run)")
    sys.exit(1 if os.environ.get("COVERAGE_MIN") else 0)
root = ET.parse(path).getroot()
rows, tm, tc = [], 0, 0
for pkg in root.iter("package"):
    for c in pkg.findall("counter"):
        if c.get("type") == "LINE":
            m, v = int(c.get("missed")), int(c.get("covered"))
            rows.append((pkg.get("name").replace("/", "."), v, m + v)); tm += m; tc += v
rows.sort()
def pct(v, t): return 100.0 * v / t if t else 100.0
total = pct(tc, tc + tm)
lines = [f"{n}: {pct(v, t):.1f}% ({v}/{t} lines)" for n, v, t in rows]
print("ENGINE LINE COVERAGE\n" + "\n".join(lines) + f"\nTOTAL: {total:.1f}% ({tc}/{tc + tm} lines)")
short = ", ".join(f"{n.split('.')[-1]} {pct(v, t):.0f}%" for n, v, t in rows)
print(f"::notice title=Engine coverage::total {total:.1f}% ({short})")
s = os.environ.get("GITHUB_STEP_SUMMARY")
if s:
    with open(s, "a", encoding="utf-8") as fh:
        fh.write("### Engine line coverage\n" + "\n".join(f"- `{l}`" for l in lines) + f"\n- **TOTAL {total:.1f}%**\n")
mn = os.environ.get("COVERAGE_MIN")
if mn:
    want = [p.strip() for p in os.environ.get("COVERAGE_PACKAGES", "").split(",") if p.strip()]
    bad = []
    for n, v, t in rows:
        if any(n.split(".")[-1] == w for w in want) and pct(v, t) < float(mn):
            bad.append(f"{n} {pct(v, t):.1f}% < {mn}%")
    missing = [w for w in want if not any(n.split(".")[-1] == w for n, _, _ in rows)]
    for w in missing: bad.append(f"package '{w}' not found in report")
    if bad:
        for b in bad: print(f"::error title=Coverage gate::{b}")
        sys.exit(1)

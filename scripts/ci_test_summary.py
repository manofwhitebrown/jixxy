#!/usr/bin/env python3
"""Unit-test summary for CI (T-D3-3). Reads JUnit XML under */build/test-results/ and prints
one ::notice with the counts plus one ::error per failing test, so results are visible in the
Checks annotations without raw logs (decision D-002). Exits 0 always: the Gate step decides pass/fail.
Usage: python3 scripts/ci_test_summary.py [repo_root]"""
import os, sys, glob
import xml.etree.ElementTree as ET

root = sys.argv[1] if len(sys.argv) > 1 else "."
files = sorted(glob.glob(os.path.join(root, "**", "build", "test-results", "**", "*.xml"), recursive=True))
total = failed = errors = skipped = 0
per_module, bad = {}, []
for f in files:
    try:
        suite = ET.parse(f).getroot()
    except ET.ParseError:
        continue
    suites = [suite] if suite.tag == "testsuite" else suite.findall("testsuite")
    rel = os.path.relpath(f, root).split(os.sep)
    module = ":" + rel[0] if rel[0] != "build" else ":root"
    for s in suites:
        t = int(s.get("tests", 0)); fl = int(s.get("failures", 0)); er = int(s.get("errors", 0)); sk = int(s.get("skipped", 0))
        total += t; failed += fl; errors += er; skipped += sk
        m = per_module.setdefault(module, [0, 0, 0])
        m[0] += t; m[1] += fl + er; m[2] += sk
        for case in s.findall("testcase"):
            node = case.find("failure") if case.find("failure") is not None else case.find("error")
            if node is not None:
                msg = (node.get("message") or "").replace("\r", " ").replace("\n", " ")[:200]
                bad.append(f"{case.get('classname')}.{case.get('name')}: {msg}")

if not files:
    print("::notice title=Unit tests::no test result files found")
    sys.exit(0)
detail = "; ".join(f"{k} {v[0]} tests/{v[1]} failed" for k, v in sorted(per_module.items()))
print(f"::notice title=Unit tests::{total} run, {failed + errors} failed, {skipped} skipped ({detail})")
for b in bad[:20]:
    print(f"::error title=Failing test::{b}")
if len(bad) > 20:
    print(f"::error title=Failing test::... and {len(bad) - 20} more")
summary = os.environ.get("GITHUB_STEP_SUMMARY")
if summary:
    with open(summary, "a", encoding="utf-8") as fh:
        fh.write(f"### Unit tests\n{total} run, {failed + errors} failed, {skipped} skipped\n\n")
        for k, v in sorted(per_module.items()):
            fh.write(f"- `{k}`: {v[0]} tests, {v[1]} failed, {v[2]} skipped\n")
        for b in bad[:20]:
            fh.write(f"- FAIL {b}\n")

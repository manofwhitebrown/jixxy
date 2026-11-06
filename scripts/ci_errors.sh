#!/usr/bin/env bash
# Prints the failure annotations of the newest COMPLETED CI run on main (cancelled runs skipped).
# Raw job logs are unreachable from our sandboxes (D-002); annotations are the official channel.
#
# Usage:   GH_TOKEN=... scripts/ci_errors.sh [run_id]
# Env:     GH_TOKEN (required, read from the environment only; never printed)
#          GH_REPO  (optional, default manofwhitebrown/jixxy)
# Exit:    0 = run is green, 1 = run has failures (printed), 2 = could not query
set -u
: "${GH_TOKEN:?Set GH_TOKEN in your environment first (never put it in a file in the repo)}"
export GH_REPO="${GH_REPO:-manofwhitebrown/jixxy}"
exec python3 - "${1:-}" <<'PY'
import json, os, sys, urllib.request, urllib.error

repo, token = os.environ["GH_REPO"], os.environ["GH_TOKEN"]
want_run = sys.argv[1] if len(sys.argv) > 1 and sys.argv[1] else None

def api(path):
    req = urllib.request.Request(
        f"https://api.github.com/repos/{repo}/{path}",
        headers={"Authorization": f"Bearer {token}", "Accept": "application/vnd.github+json"})
    try:
        with urllib.request.urlopen(req, timeout=30) as r:
            return json.load(r)
    except urllib.error.HTTPError as e:
        print(f"ci_errors: GitHub API {e.code} for {path.split('?')[0]}")
        if e.code in (401, 403):
            print("ci_errors: token rejected or lacks read access to Actions/Checks.")
        sys.exit(2)
    except Exception as e:
        print(f"ci_errors: request failed ({type(e).__name__})"); sys.exit(2)

if want_run:
    run = api(f"actions/runs/{want_run}")
else:
    runs = api("actions/workflows/ci.yml/runs?branch=main&status=completed&per_page=20")["workflow_runs"]
    runs = [r for r in runs if r["conclusion"] != "cancelled"]
    if not runs:
        print("ci_errors: no completed CI run on main yet"); sys.exit(2)
    run = runs[0]

msg = run["head_commit"]["message"].splitlines()[0]
print(f"Run {run['id']}  commit {run['head_sha'][:7]}  conclusion: {run['conclusion']}")
print(f"  {msg}\n  {run['html_url']}\n")
failed = False
for job in api(f"actions/runs/{run['id']}/jobs?per_page=50")["jobs"]:
    anns = [a for a in api(f"check-runs/{job['id']}/annotations?per_page=100")
            if a["annotation_level"] == "failure"
            and a["message"].strip() != "Process completed with exit code 1."]
    bad_steps = [s["name"] for s in job["steps"] if s["conclusion"] == "failure"]
    status = job["conclusion"]
    print(f"== {job['name']}: {status}")
    if status == "success":
        continue
    failed = True
    for s in bad_steps:
        print(f"   failed step: {s}")
    seen = set()
    for a in anns:
        body = a["message"].strip()
        if body in seen:
            continue                      # same error is repeated once per Gradle step
        seen.add(body)
        where = f"{a.get('path')}:{a.get('start_line')}" if a.get("path") and a.get("path") != ".github" else ""
        title = f"[{a['title']}] " if a.get("title") else ""
        print(f"\n   {title}{where}")
        print("   " + body.replace("\n", "\n   "))
    print()
sys.exit(1 if failed or run["conclusion"] not in ("success",) else 0)
PY

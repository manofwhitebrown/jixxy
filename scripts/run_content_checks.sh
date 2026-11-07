#!/usr/bin/env bash
# WRITER's line lint + coverage matrix, run in CI (ci.yml guards job and docs-guards.yml).
# lint_lines.py errors FAIL the job; its warnings (Owner-canon phrasings like "idiot") become annotations and never fail.
# The coverage matrix is printed into the job summary. Usage: bash scripts/run_content_checks.sh
set -u
cd "$(git rev-parse --show-toplevel)"
out=$(mktemp); rc=0
python3 content/tools/lint_lines.py >"$out" 2>&1 || rc=$?
while IFS= read -r l; do
  case "$l" in
    ERROR*|error*) printf '::error title=Line lint::%s\n' "$(printf '%s' "$l" | cut -c1-300)" ;;
    WARNING*)      printf '::warning title=Line lint::%s\n' "$(printf '%s' "$l" | cut -c1-300)" ;;
    *)             printf '%s\n' "$l" ;;
  esac
done < <(sort -u "$out" | grep -v '^$')
if [ "$rc" -ne 0 ]; then
  echo "::error title=Line lint::lint_lines.py exited with $rc (errors above). Do not loosen this check; fix the lines (WRITER)."
  exit 1
fi
if python3 content/tools/coverage_matrix.py >cov.txt 2>&1; then
  head -3 cov.txt | grep -m1 "Total lines" | sed 's/^/coverage: /'
  [ -n "${GITHUB_STEP_SUMMARY:-}" ] && { echo '### Line bank coverage (WRITER tool)'; cat cov.txt; } >> "$GITHUB_STEP_SUMMARY"
else
  echo "::warning title=Coverage matrix::coverage_matrix.py failed; see log"; cat cov.txt | tail -5
fi
rm -f cov.txt "$out"

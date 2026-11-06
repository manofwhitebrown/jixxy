#!/usr/bin/env bash
# Fails if tracked files contain token/key patterns or forbidden file types.
# Runs in CI and locally. Never prints the matched secret value, only file:line.
set -u
cd "$(git rev-parse --show-toplevel)"
fail=0

PATTERNS='github_pat_|ghp_[A-Za-z0-9]|gho_[A-Za-z0-9]|ghs_[A-Za-z0-9]|x-access-token:|-----BEGIN [A-Z ]*PRIVATE KEY-----'
# This script and the QA docs legitimately mention the patterns by name.
hits=$(git grep -nIE -e "$PATTERNS" -- . ':!scripts/check_no_secrets.sh' ':!scripts/install_hooks.sh' ':!qa/*' 2>/dev/null | cut -d: -f1,2)
if [ -n "$hits" ]; then
  echo "::error::Possible secret(s) found (values not shown):"
  echo "$hits"
  fail=1
fi

bad=$(git ls-files | grep -E '(\.local\.md$|\.jks$|\.keystore$|(^|/)\.env$|(^|/)local\.properties$)' \
  | grep -vE '^(app/)?debug\.keystore$|^scripts/debug-only/' )
if [ -n "$bad" ]; then
  echo "::error::Forbidden tracked files (keystores, .env, *.local.md, local.properties):"
  echo "$bad"
  fail=1
fi

[ "$fail" -eq 0 ] && echo "check_no_secrets: OK"
exit "$fail"

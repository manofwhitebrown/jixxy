#!/usr/bin/env bash
# Optional: installs a pre-commit hook that runs the secrets guard on staged content.
set -eu
root="$(git rev-parse --show-toplevel)"
hook="$root/.git/hooks/pre-commit"
cat > "$hook" <<'HOOK'
#!/usr/bin/env bash
root="$(git rev-parse --show-toplevel)"
if git diff --cached -U0 | grep -E '^\+' | grep -qE 'github_pat_|ghp_[A-Za-z0-9]|gho_[A-Za-z0-9]|ghs_[A-Za-z0-9]|x-access-token:|-----BEGIN [A-Z ]*PRIVATE KEY-----'; then
  echo "pre-commit: staged changes contain a token/key pattern. Aborting." >&2
  exit 1
fi
if git diff --cached --name-only | grep -qE '\.local\.md$|\.jks$|\.keystore$|(^|/)\.env$|(^|/)local\.properties$'; then
  echo "pre-commit: staged file is on the forbidden list (*.local.md, keystore, .env, local.properties)." >&2
  exit 1
fi
exit 0
HOOK
chmod +x "$hook"
echo "Installed pre-commit hook at $hook"

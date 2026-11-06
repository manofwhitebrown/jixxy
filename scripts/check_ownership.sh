#!/usr/bin/env bash
# WARNING ONLY. For each recent commit "[ROLE] ...", warns when it touches paths the role doesn't own.
set -u
cd "$(git rev-parse --show-toplevel)"
range="${1:-HEAD~20..HEAD}"
git rev-parse "${range%%..*}" >/dev/null 2>&1 || range="HEAD"

owns() { # role path -> 0 if owned
  local r="$1" p="$2"
  case "$r" in
    MANAGER)   [[ "$p" == coord/TASKS.md || "$p" == coord/DECISIONS.md || "$p" == coord/CONTRACTS.md ]] ;;
    PLATFORM)  [[ "$p" == build.gradle* || "$p" == settings.gradle* || "$p" == gradle.properties || "$p" == gradle/* || "$p" == gradlew* || "$p" == .gitignore \
                 || "$p" == app/build.gradle.kts || "$p" == app/src/main/AndroidManifest.xml || "$p" == app/src/main/java/app/jixxy/platform/* || "$p" == app/src/main/java/app/jixxy/data/* ]] ;;
    ENGINE)    [[ "$p" == engine/* || "$p" == app/src/main/assets/config/* ]] ;;
    UI)        [[ "$p" == app/src/main/java/app/jixxy/ui/* || "$p" == app/src/main/res/* || "$p" == design/ui/* ]] ;;
    CHARACTER) [[ "$p" == character/* || "$p" == design/character/* ]] ;;
    WRITER)    [[ "$p" == content/* || "$p" == app/src/main/assets/lines/* ]] ;;
    DEVOPS)    [[ "$p" == .github/* || "$p" == scripts/* || "$p" == qa/* ]] ;;
    GROWTH)    [[ "$p" == README.md || "$p" == PRIVACY.md || "$p" == CHANGELOG.md || "$p" == site/* || "$p" == store/* || "$p" == growth/* ]] ;;
    *) return 0 ;;
  esac
}
for c in $(git rev-list --no-merges "$range" 2>/dev/null); do
  subj=$(git log -1 --format=%s "$c")
  role=$(echo "$subj" | sed -nE 's/^\[([A-Z]+)\].*/\1/p')
  [ -z "$role" ] && continue
  for p in $(git diff-tree --no-commit-id --name-only -r "$c"); do
    [[ "$p" == coord/status/$role.md || "$p" == coord/inbox/* ]] && continue
    owns "$role" "$p" || echo "::warning::$(git rev-parse --short "$c") [$role] touched '$p' outside its ownership"
  done
done
exit 0

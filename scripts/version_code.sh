#!/usr/bin/env bash
# Prints the versionCode to build with: the number of commits reachable from HEAD.
# Why not github.run_number: CI and Release workflows have separate counters, so a tagged release
# could end up with a LOWER versionCode than an earlier CI build, and Android refuses downgrades.
# The commit count only grows on main and is identical for a CI build and a release of the same commit.
# Needs a full checkout (fetch-depth: 0).
set -euo pipefail
if [ "$(git rev-parse --is-shallow-repository)" = "true" ]; then
  echo "version_code: shallow clone, need fetch-depth: 0" >&2; exit 2
fi
git rev-list --count HEAD

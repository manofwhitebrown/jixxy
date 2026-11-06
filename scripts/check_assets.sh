#!/usr/bin/env bash
# Asset budget guard wrapper. Warn-only unless STRICT=1. See check_assets.py.
exec python3 "$(dirname "$0")/check_assets.py" "$@"

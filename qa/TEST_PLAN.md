# Jixxy test plan
Owner: DEVOPS (AI-6). CI is the referee; real-device checks are done by the Owner and testers.

## Scope
Android 8+ (minSdk 26). On-device only. Pause mechanic, overlay sprite, mood/line selection, settings, weekly card, onboarding, keep-alive on aggressive OEMs.

## Risk register (highest first)
1. **Annoyance** (uninstall driver): too many comments, wrong moment, repeated lines. Covered by `KILL_TEST.md`.
2. **OEM battery killers** (Xiaomi/Oppo/Vivo) silently stopping the service.
3. **Overlay blocks touches** or sits over calls/banking.
4. **Privacy regression**: a library pulls in INTERNET or an analytics SDK. Covered by CI guards.
5. **Update breaks install**: signing key changes between builds.
6. **Tone**: a line that mocks a person, a body or mental health. Reviewed by Owner on every line-bank change.

## Automated (CI, every push)
| Check | Tool | Fails build |
|---|---|---|
| Unit tests (engine mandatory) | `./gradlew test` | yes |
| Lint | `./gradlew lint` | yes |
| APK builds | `assembleDebug` | yes |
| No secrets / forbidden files | `scripts/check_no_secrets.sh` | yes |
| No forbidden permissions in merged manifest | `scripts/check_manifest.sh` | yes |
| No analytics/ads/network deps | `scripts/check_dependencies.sh` | yes |
| Line bank schema, unique ids, <= 90 chars | `scripts/validate_lines.py` | yes |
| Role ownership | `scripts/check_ownership.sh` | warning only |

## Entry / exit criteria
| Milestone | Entry | Exit |
|---|---|---|
| M0 Skeleton | scaffold pushed | CI green on main; empty APK installs on one real device |
| M1 Alive | M0 | `SMOKE_CHECKLIST.md` steps 1-8 pass on 2 devices (one Pixel/stock, one Xiaomi/Redmi/POCO) |
| M2 Pause works | M1 | full smoke checklist passes; Leave instant, Stay after delay; PROTECTED apps never paused |
| M3 Tester build | M2 | `KILL_TEST.md` passes; 8-hour survival on a mid-range device; update-over-previous-APK works; signed `v0.1.0` release exists |

## Not covered / honest limits
No emulator runs in CI yet (instrumented tests need an emulator job; add only if free-tier minutes allow). Nothing here has been run on a device by AI-6.

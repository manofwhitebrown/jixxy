# Third-party libraries and licences (DEVOPS, for GROWTH to reference)

**Scope and honesty:** this lists the libraries *we declare* (`gradle/libs.versions.toml`, `app/build.gradle.kts`, `engine`, `character`) with the licence I know each one carries. It is NOT yet the full transitive list: libraries pulled in by these (more AndroidX, Kotlin stdlib, etc.) are not listed one by one. CI now prints the real resolved tree through `scripts/check_resolved_deps.sh` and rejects banned ones. Licences below are from my knowledge, not read from each artifact's POM; please treat "Apache-2.0" rows as high confidence and verify before any store submission.

## Ships inside the APK
| Library | Version | Licence |
|---|---|---|
| Kotlin standard library and compiler plugins | 2.1.21 | Apache-2.0 |
| AndroidX core-ktx | 1.16.0 | Apache-2.0 |
| AndroidX Activity (activity-compose) | 1.10.1 | Apache-2.0 |
| Jetpack Compose (BOM 2025.06.01: ui, material3, ui-tooling-preview) | per BOM | Apache-2.0 |
| AndroidX Navigation Compose | 2.9.0 | Apache-2.0 |
| AndroidX Lifecycle (runtime-compose, viewmodel-compose) | 2.9.1 | Apache-2.0 |
| AndroidX DataStore (preferences) | 1.1.7 | Apache-2.0 |
| kotlinx.coroutines (core, android) | 1.8.1 | Apache-2.0 |

## Debug builds only (not in a release APK)
| Library | Licence |
|---|---|
| Compose ui-tooling | Apache-2.0 |

## Tests only (never shipped)
| Library | Version | Licence |
|---|---|---|
| JUnit 4 | 4.13.2 | EPL-1.0 |
| kotlinx-coroutines-test | 1.8.1 | Apache-2.0 |
| org.json:json | 20240303 | **JSON licence ("The Software shall be used for Good, not Evil")** |

## Flag for MANAGER
- `org.json:json` has a non-standard licence that is not an OSI open-source licence and is disputed. It is only used by JVM unit tests (`testImplementation` in `app`), so it does not ship in the APK. Still, if the repo is made open source under a standard licence, PLATFORM should replace it (for example with a test-only JSON parser under Apache-2.0 or MIT) or the licence memo should mention it.
- The repo itself has no LICENSE file (Owner decision, pending).
- Art, sprites and lines are the Owner's or original; no third-party fonts or images are bundled (the site loads nothing external, checked by `scripts/check_assets.py`).

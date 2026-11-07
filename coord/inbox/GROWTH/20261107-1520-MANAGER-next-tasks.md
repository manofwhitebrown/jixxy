---
to: GROWTH
from: MANAGER
type: REQUEST
priority: normal
refs: T-G3-8..12, D-018, D-020, D-021
---
The Manager applied the README fixes (see earlier message); Pages is ON. Next, none blocked on others, in this order:
1. **T-G3-8 Play Console prep pack** `store/PLAY_CONSOLE_PREP.md`: draft answers for the Data-safety form (offline, nothing collected), the special-use foreground-service declaration + a 30-second demo-video script, overlay and usage-access permission justifications, content-rating/target-audience answers (18+), privacy-policy URL (the Pages URL), a listing-assets checklist, and **today's** closed-testing requirement for new developer accounts. Cite official Google pages and mark anything you could not verify. Preparation only; the Owner has not chosen Play.
2. **T-G3-9 Claims-versus-evidence table** `growth/CLAIMS.md`: for each public claim (no internet permission, nothing leaves the phone, sounds off by default, you set how naggy, etc.) point to the file/test/manifest line that proves it and mark `proven / written / planned`. Re-run it after each `latest` build.
3. **T-G3-10 Static FAQ page** `site/faq.html` (zero external requests): how to install, the "Allow restricted settings" step, what he reads, how to turn him down (Nag level, Nap, Stop), battery, how to delete everything. Match `OWNER_TEST_GUIDE`; no claims beyond T-G3-9's `proven`.
4. **T-G3-11 Tester feedback tooling** `growth/tools/summarise_feedback.py` (stdlib): reads a pasted/CSV file of the five tester answers and prints counts and the quotes behind them (annoying 1-5 average, Leave vs Stay, favourite and worst lines). Plus a blank CSV template for the Owner.
5. **T-G3-12 One-page positioning note** `growth/POSITIONING.md`: how Jixxy differs from friction/screen-time apps and virtual-pet apps, from what you can verify on their public pages (cite, don't copy). Hold F-Droid metadata (`store/metadata/en-US/`) until the Owner picks a licence.
After these, **park**: you will be needed when the Owner confirms a build (README/CHANGELOG/site) and when testers start. Keep your status file current for a replacement.

---
to: PLATFORM
from: GROWTH
type: INFO
priority: low
refs: T-GROWTH-4, store/LISTING.md
---
Not urgent, no change requested yet. For Play review later: the manifest's `<queries>` entry with a MAIN/LAUNCHER intent lets the app list nearly all launchable apps. Google's Permissions and APIs that Access Sensitive Information policy says installed-app inventory is sensitive, and that approximating broad visibility is restricted to user-facing core functionality. We don't use `QUERY_ALL_PACKAGES`, which is good.

If a narrower approach would be enough for the setup screen's app picker, consider it. If not, no problem: I've drafted the justification (choosing up to 3 apps is core setup). Keeping `PRIVACY.md` accurate: it currently says setup shows a list of installed apps via a launcher query. Tell me if that changes.

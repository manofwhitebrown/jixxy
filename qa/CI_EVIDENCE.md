# CI evidence: screenshots and logcat from the emulators

After every green CI run on `main`, `.github/workflows/emulator.yml` installs the debug APK on emulators (API 26, 30, 34, 35) and publishes what it saw to the rolling pre-release **`ci-evidence`** (assets are replaced each run; the notes name the commit).

Files: `api34-01-launched.png`, `api34-02-after-home.png`, `api34-logcat.txt`, `api34-appops.txt`, and `apiNN-crashes.txt` only when a crash or ANR was found.

## Fetch the newest evidence (3 lines, needs `GH_TOKEN` in the environment)
```bash
mkdir -p evidence && cd evidence
gh release download ci-evidence --repo manofwhitebrown/jixxy --clobber            # with the gh CLI
# no gh? list and fetch with curl:
curl -s -H "Authorization: Bearer $GH_TOKEN" https://api.github.com/repos/manofwhitebrown/jixxy/releases/tags/ci-evidence | python3 -c "import sys,json;[print(a['browser_download_url']) for a in json.load(sys.stdin)['assets']]"
# then: curl -sL -H "Authorization: Bearer $GH_TOKEN" -H "Accept: application/octet-stream" -O <asset api url>
```
Public repo: the plain `browser_download_url` works without a token too.

## Status
Whether release-assets hosts are reachable from each role's sandbox is NOT verified by DEVOPS; tell DEVOPS if yours cannot fetch.

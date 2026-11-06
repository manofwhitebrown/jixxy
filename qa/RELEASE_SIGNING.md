# Release signing: Owner guide

Android refuses to update an installed app if the new APK is signed with a different key. So the
key you create here must be kept for the life of the app. **Lose it and every tester has to
uninstall and reinstall; for a Play listing, you could not update the app at all.**

AI roles never create, see or print this key. You do it on your own computer.

## What CI does today
- Every CI build is signed with the shared **debug** keystore (`app/debug.keystore`). It is public by
  design and has no value beyond testing. Builds from different CI runs can update over each other.
- `release.yml` (on tags `v*`) signs with the release key **only if all four secrets below exist**.
  Otherwise it builds with the debug key and says so in the release notes.

## One-time setup (about 10 minutes)
1. **Create the keystore** on your computer (needs a JDK; Android Studio includes `keytool`):
   ```
   keytool -genkeypair -v -keystore jixxy-release.jks -alias jixxy \
     -keyalg RSA -keysize 4096 -validity 10000
   ```
   Choose a strong password when asked and remember the alias (`jixxy` above).
2. **Back it up now**: copy `jixxy-release.jks` and the passwords to two safe places (a password
   manager plus an offline drive). Never put the file in the repo, in chat, or in an email.
3. **Encode it** for GitHub (this prints nothing sensitive to share; copy the output only into GitHub):
   - Mac/Linux: `base64 -w0 jixxy-release.jks` (on macOS: `base64 -i jixxy-release.jks`)
   - Windows PowerShell: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("jixxy-release.jks"))`
4. **Add four repository secrets** in GitHub: Settings > Secrets and variables > Actions > New repository secret:

   | Secret name | Value |
   |---|---|
   | `JIXXY_KEYSTORE_B64` | the base64 text from step 3 |
   | `JIXXY_KEYSTORE_PASSWORD` | the keystore password |
   | `JIXXY_KEY_ALIAS` | the alias (e.g. `jixxy`) |
   | `JIXXY_KEY_PASSWORD` | the key password (same as the keystore password if you pressed Enter) |

5. **Test it** with a throwaway tag before the real one, e.g. `v0.0.1-test`. The release notes must
   NOT contain the "signed with the shared debug keystore" line. Delete the test release afterwards.

## Rules
- Do not paste any of these values into chat with any AI, into issues, or into files in the repo.
- The first release signed with this key becomes the baseline: every later APK must use the same key.
- Switching from debug-signed tester builds to release-signed builds is a one-time break: testers
  must uninstall the debug-signed build once. Tell them in the release notes.
- If the key ever leaks, tell the Manager; there is no way to revoke it for already-installed apps.

## Updates must install over the previous build (T-D2-1)

Android only installs an update if (1) the signing certificate is identical and (2) `versionCode` is higher.

- **Same key:** until the real release key exists, CI and releases both sign with the committed throwaway key `app/debug.keystore`. `scripts/check_apk_cert.sh` prints the APK's certificate SHA-256 (via `apksigner`) in the run summary and **fails** if it differs from the debug key (CI), or from the previous release's APK (release workflow). When the Owner adds the real key, the first signed release will correctly fail this check once, because everybody on a debug-signed build has to uninstall and reinstall. If that is intended, set `ALLOW_KEY_CHANGE=1` in the step env for that one release.
- **Rising versionCode:** both workflows call `scripts/version_code.sh` (number of commits on `main`) and pass `-PversionCode=<n>`. This replaces the idea of `github.run_number`, because CI and Release runs have separate counters: a tagged release could get a lower code than an earlier CI build and phones would refuse it. The commit count only grows and is the same for CI and release builds of one commit. `v0.0.1-alpha` has versionCode 1.
- **Needs PLATFORM:** `app/build.gradle.kts` must read `versionCode` from the Gradle property (request in coord/inbox/PLATFORM).

# Play Console prep pack (T-G3-8)

Written 2026-11-07 by GROWTH. **Preparation only: the Owner has not chosen Play.** Nothing here is submitted. Not legal advice.
Marks: **[verified]** = read in full on the Google page linked in section 9 today. **[unverified]** = not read, or from memory or third parties; check in Play Console on the day.
Facts about the app come from `AndroidManifest.xml` and `build.gradle.kts` (minSdk 26, targetSdk 36, no INTERNET permission). Everything marked "written" is not yet seen on a phone.

## 1. Gate: today's closed-testing rule for new accounts [verified]
- Applies to **personal** accounts created after 13 Nov 2023.
- Run a **closed test with at least 12 testers opted in continuously for 14 days**, then press **Apply for production** on the Dashboard. Testers who opt out and back in need 14 *consecutive* days again.
- Closed testing opens after you complete **app setup**. Production and Pre-registration stay disabled until then. Open testing only after production access.
- The production application has 3 parts: how the closed test went (how easy recruiting was, whether testers used all features, a **summary of tester feedback**), your app (target audience, value, expected installs in year one) and production readiness (changes you made, how you decided it was ready).
- Google says review usually takes **7 days or less**, sometimes longer. It may ask you to keep testing if fewer than 12 testers or too little engagement.
- **Fit with our plan:** the tester kit (`growth/TESTER_PROGRAM.md`) already collects what Part 1 needs. Testers need a Google account email to be added to the closed-test list, which the tester kit does **not** yet ask for (add it only if Play is chosen). Also see "Device verification requirements for new developer accounts" (link in section 9) [not read: check it when creating the account].

## 2. Foreground service declaration (App content page) [verified]
Required because the app targets Android 14+. For each service type declared you give: a description, user impact if delayed or interrupted, a **video link** showing the steps a user takes to trigger it, and a use case. `specialUse` is allowed "in limited scenarios" and **all types are subject to review**.
Our manifest: `OverlayService`, type `specialUse`, subtype text "Shows a user-enabled floating companion sprite over other apps; started and stopped by the user."

**Draft answers (edit when the build is verified)**
- **Type:** Special use. **Why no standard type fits:** a floating companion on screen is not media, location, camera, microphone, data sync or any listed type.
- **What it does:** keeps the overlay cat on screen while the user has switched him on. Started by the user (Wake). Stopped by the user (Stop in the app or notification). Shows a persistent "Jixxy is awake" notification with Nap, Wake and Stop.
- **If delayed:** the cat appears late; nothing else is lost. **If interrupted:** the cat disappears until the user wakes him again; no data is lost (settings are stored on the phone).
- **User perceptible:** a persistent notification and the visible cat [Google's FAQ defines "user perceptible" this way, verified].
- **Alternatives considered:** WorkManager or short jobs cannot hold an overlay window; the overlay needs a running service.
- **Demo video:** see section 3.

## 3. 30-second demo video script (foreground service)
Shows the steps a user takes. Silent screen recording, portrait. Record only on a build the Owner has verified (every step must really work).
| Time | On screen |
|---|---|
| 0:00 | Open Jixxy, show Home with the **Wake** button and the "Off" state |
| 0:04 | Tap **Wake**; the cat appears; the pull-down shows the "Jixxy is awake" notification with Nap / Wake / Stop |
| 0:10 | Switch to another app; the cat stays on top |
| 0:15 | Open a chosen Pause app; the Pause card shows; tap **Leave** and land on Home |
| 0:22 | Pull the notification down and tap **Stop**; the cat disappears and the notification goes |
| 0:27 | Back in Jixxy, Home shows "Off" |

## 4. Permission justifications (draft text)
- **Display over other apps (`SYSTEM_ALERT_WINDOW`).** "Jixxy is a floating companion. The overlay shows the cat and the Pause card over other apps. The user turns it on and can turn it off in the app or the notification. Jixxy does not read or record what is on screen." *(I found no dedicated Play form for this permission [unverified]; use this text if one appears or a reviewer asks.)*
- **Usage access (`PACKAGE_USAGE_STATS`).** "Used only to detect which app is in front, so Jixxy can pause the user before the apps they chose. It reads the app name and time only, never in-app content. Nothing leaves the device." *(Special app access, granted by the user in Settings. Check whether Play asks for a declaration [unverified].)*
- **Installed-apps visibility (`<queries>` launcher intent).** "Used for the setup screen where the user picks which apps get a Pause. Only the list of launchable apps is used, and it stays on the device." Risk: Play restricts broad app-list access; see `store/DISTRIBUTION_OPTIONS.md` B5 [policy text verified earlier; reviewer outcome unknown].
- **Notifications, boot completed, foreground service.** Notification: the persistent "awake" notice. Boot: restart Jixxy after a reboot if the user had him awake. No new permission is needed for sounds [unverified: re-check once D-021 ships].

## 5. Data safety form (draft) [form wording unverified: read it in Play Console]
- **Does the app collect or share user data?** No. The app has no INTERNET permission, no analytics, no ads and no accounts. (True only while it stays offline; `ci` checks the manifest.)
- **Data processed on device only:** chosen apps, settings, recent app-use events used for the "What Jixxy saw" screen (kept 7 days, erasable), local crash/debug log (never sent). Locally processed data that never leaves the device is generally not "collected" under Google's definition [unverified: confirm in the form's help text].
- **Security:** data stays on the device; the user can erase it from the app or by uninstalling. **Deletion request URL:** not needed if no account data is held [unverified].
- **Privacy policy URL:** the site page `https://manofwhitebrown.github.io/jixxy/privacy.html` (page added alongside this pack; it exists only once Pages redeploys and the Owner opens it) [URL requirement unverified].

## 6. Content rating and target audience
- **Target audience:** **18 and older** (project decision D-006). Do not tick child age groups. The app is not "designed for children".
- **Content rating questionnaire (IARC):** answer from the real content: sarcastic humour, no violence, no sex, no gambling, no drugs, no user-generated content, no location sharing, no purchases, no ads. The writer's safety rules (`content/SAFETY_LINES.md`) exclude self-harm, body shaming and mental-health jokes. Expect an everyone/teen-type rating; the number comes from Google's questionnaire [unverified].
- **Ads:** none. **In-app purchases:** none. **News/health/financial declarations:** answer "no"; Jixxy makes no health claims.
- **App category:** Tools or Lifestyle [unverified: pick what fits at setup]. Do not choose Health & Fitness.
- **AI-generated content policy:** the cat art provenance must be known first (`growth/LEGAL_READINESS.md`); Google has an AI-Generated Content policy page [listed in the policy index, not read].

## 7. Listing assets checklist
- [ ] App name and short/full description: drafted in `store/LISTING.md` (**do not submit until every claim is true in a verified build**)
- [ ] Icon 512 x 512 PNG: needs export from the Owner's art [sizes unverified]
- [ ] Feature graphic 1024 x 500 [unverified]
- [ ] Phone screenshots (at least 2): from the Owner's phone, using `growth/launch/SHOT_LIST.md`
- [ ] Privacy policy URL live
- [ ] Contact email: the Owner's choice (do not use a personal address without asking)
- [ ] Release signing: private key and Play App Signing decision (`qa/RELEASE_SIGNING.md`)
- [ ] Target API: we target 36; check the Target API requirements page on the day [unverified]

## 8. Open risks (for the Owner)
- **Naggy by design (D-020):** more frequent overlay prompts raise review risk; see `store/DISTRIBUTION_OPTIONS.md` B8. Keep dismiss, nap and the Calm level obvious in the demo video.
- **Special use is reviewed.** A rejection is possible; budget extra time.
- **Account type:** a personal account must pass the 12-tester rule. Organisation accounts have different rules [unverified].
- **Cost and ID:** $25 once and identity verification [verified earlier in the Android developer pages].

## 9. Sources (read 2026-11-07)
- Closed-testing requirement: https://support.google.com/googleplay/android-developer/answer/14151465
- Foreground service declaration: https://support.google.com/googleplay/android-developer/answer/13392821 (a "Preview" version of this page exists: https://support.google.com/googleplay/android-developer/answer/16965181; re-read it before submitting)
- Device verification for new accounts (listed in Google's sidebar, not read): https://support.google.com/googleplay/android-developer/answer/14316361
- Declare permissions (not read): https://support.google.com/googleplay/android-developer/answer/9214102
- Google's rules change often. Re-read each page on the day you act.

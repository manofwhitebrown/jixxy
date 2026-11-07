# How should Jixxy reach people? (memo for the Owner)

Written 2026-11-07 by GROWTH. Plain language, zero budget, India, Android, today's sideloaded alpha. **Draft for your decision. Nothing here is submitted or promised.**
Marks: **[verified]** = read on the official page linked in section 6. **[unverified]** = third-party pages or memory; check before relying on it. I am not a lawyer.

## The short version

| | A. GitHub only | B. Google Play | C. F-Droid / IzzyOnDroid | D. 2027 sideload rules (not a route; affects A and C) |
|---|---|---|---|---|
| Cost | Free | $25 once [verified] | Free | Free "limited" account, or $25 [verified] |
| Effort | Already done | High: ID check, 12 testers for 14 days, forms, demo video | Medium, **needs an open licence** | Small: register the app name and key |
| Review risk | None | **Highest** (foreground service, overlay, app list) | Low to medium, human review | n/a |
| How testers update | Reinstall the new APK (same signing key + higher versionCode) | Automatic | IzzyOnDroid: automatic if their client is installed. F-Droid: see below | Same as A |

## A. GitHub Releases only (what you do today)
- **Cost/effort:** nothing more to do. The rolling `latest` APK and numbered tags already work.
- **Review risk:** none. No one approves the app.
- **Updates:** testers download the new APK and install over the old one. This works only while the **signing key stays the same** and the **versionCode goes up**. We use the committed debug key for now. Before real users, switch to a private release key and keep it safe; changing keys later forces everyone to uninstall first (DEVOPS notes this in `qa/RELEASE_SIGNING.md`).
- **Downsides:** testers must allow "install unknown apps", and on Android 15+ may hit the "restricted settings" block for overlay and usage access (see `growth/OWNER_TEST_GUIDE.md`) [third-party source, unverified in detail]. Reach is limited to people you can message.
- **Fit:** good for v0.x and your ~20 testers. This is my pick for now.

## B. Google Play
What Google asks, in order:
1. **Developer account:** one-time $25 fee and identity verification [fee and ID: verified via Google's Android developer verification pages; exact payment methods for India **unverified**: confirm at sign-up].
2. **Closed test before launch.** For a *personal* account created after 13 Nov 2023: at least **12 testers opted in continuously for 14 days**, then you apply for production access [verified]. Organisation accounts are exempt, but need a D-U-N-S number which can take weeks [unverified, third-party]. Your ~20 testers would cover the 12, but each must install through Google's test link, which needs their Google account email.
3. **Foreground service declaration.** Jixxy uses `specialUse` (no standard type fits a floating cat). For Android 14+ targets you must describe the feature, say what happens if it is delayed or interrupted, and **link a demo video**; `specialUse` is allowed only in limited cases and every type is reviewed [verified]. This is the biggest rejection risk.
4. **Overlay and usage access.** I found no dedicated Play form for either [unverified; absence is not proof]. The user-data rules still apply, so the in-app screens must explain each permission before asking.
5. **App list (`<queries>`).** The manifest asks Android for the list of launchable apps so you can pick your 3. Play treats the installed-app list as sensitive and restricts broad visibility to core features [verified policy text; my reading]. A reviewer could challenge it, and with no cap on chosen apps (D-020) the picker matters even more. PLATFORM may want a narrower approach.
6. **Data safety form + privacy policy URL + age rating.** "No data collected" is only true while the app stays offline. The privacy page needs a public URL (needs GitHub Pages) [policy URL requirement unverified].
7. **Target API level** must follow Play's yearly rule (we target 36) [unverified: check the Target API Level policy at submission].
8. **Naggy by design (D-020, added 2026-11-07).** Jixxy will interrupt more often (default about 15 prompts a day, per the decision's suggested numbers) and with no cap on paused apps. My reading, **[unverified: I found no Play rule about prompt frequency, and did not re-read the full policy for this]**: Play's Device and Network Abuse policy is where "interfering with other apps" lives, and a reviewer judging an always-on overlay that pops cards over other apps could see more frequent prompts as higher risk than the old quiet design. What helps our case, all of which must be **true in the shipped build**: the user chooses the apps, Leave is instant, a one-tap nap and Stop exist, the Calm level is one tap away, protected apps (banking, calls) are never touched, and the demo video and permission screens show this plainly. What hurts: low-level defaults of Naggy/Chaos, any prompt that cannot be dismissed, or listing text that hides the nagging. Risk goes from "medium" to "medium-high" for Play; **it does not change options A, C or D** (no review there). Re-read the policy before any submission.
- **Time:** budget about 3 weeks minimum on a personal account [third-party estimate], longer after a rejection.
- **Updates:** automatic for users.
- **Fit:** the real route to non-technical users, but not before the Pause is verified on phones. Draft listing: `store/LISTING.md` (do not submit yet).

## C. Free open-source stores
- **IzzyOnDroid** (F-Droid-style repo run by one maintainer). Serves **your own signed APK** taken from your GitHub releases, so it keeps the same signing key and updates smoothly [verified: its pages say binaries are built and signed by the developers]. Requires **FOSS licence**, APK size roughly under 30 MB, no ads/trackers, and apps may be removed if they break the rules [verified]. Free.
- **F-Droid main repo.** Requires free/libre software and builds the app from source on their servers; flags undesirable features as "Anti-Features" rather than always rejecting; apps that fail to rebuild or hide anti-features get rejected [verified]. Needs a **unique application ID you control** (`app.jixxy` is yours only if you own that domain; their policy asks for an ID based on a domain under your control) [verified]. F-Droid normally signs apps with its own key, so a user who installed from GitHub **cannot update in place** from F-Droid and must reinstall [unverified: general knowledge, check their docs].
- **What they dislike:** non-free libraries and tracking. Jixxy has no network access and no analytics, which suits them. Overlay and usage access are fine for F-Droid-style stores [no rule found; unverified].
- **Licence catch:** you cannot be listed with "all rights reserved". Code needs an open licence; the cat art can stay under separate stricter terms only if the store accepts that. Check at submission whether they want the art under a free licence too [unverified]. See `growth/LICENCE_OPTIONS.md`.
- **Fit:** good for privacy-minded users, probably not for your Gen-Z Indian audience, who mostly use Play and WhatsApp links. Cheap to add later; IzzyOnDroid is the easier start.

## D. The 2027 "developer verification" rules
- Google is requiring apps on **certified Android devices** (Play-protected phones) to come from a developer who registered their identity and app name, **including apps installed outside Play** [verified].
- **Dates:** enforcement began 30 Sept 2026 in Brazil, Indonesia, Singapore and Thailand; Google says it expands **globally from 2027** with no single date announced, so **India is not covered yet** [verified: Google's overview and FAQ; for the sideload scope in the four countries, secondary sources disagree on detail, unverified].
- **What you would do:** create an Android Developer Console account and register `app.jixxy` with your signing key.
  - **Limited distribution:** free, no government ID, but capped at **20 authorized devices** [verified]. Fine for your testers, not for the public.
  - **Full distribution:** $25 once, needs ID documents, unlimited installs [verified].
- **If you do nothing:** unverified apps can still be installed through an "advanced flow" (developer mode, a restart and a wait, reported as 24 hours) [verified in Google summary; the exact steps come from a secondary source, unverified]. That would badly hurt a casual sideloaded app after 2027.
- **Practical rule:** the debug signing key in the repo is not suitable for registration. Move to a private release key **before** registering, because the registration is tied to the key and package name [verified].
- Registering is **not** the same as joining Play. It is a separate, cheap step you can take while staying on GitHub only.

## Recommendation
Stay on **A (GitHub Releases)** through v0.x and run the tester kit. Create a **private release signing key** soon, and use the **free limited-distribution account** (20 devices) if you want to be safe during testing. Choose a licence this month. Only after the Pause is verified on real phones and ~20 testers keep him for a week, decide between **Play ($25, 3+ weeks, real review risk)** for reach, and **IzzyOnDroid** (free, needs an open licence) for privacy fans. Do not submit anything while the listing promises more than the build does.

## Decisions only you can make
1. **Licence** for code and for art (memo: `growth/LICENCE_OPTIONS.md`). It decides whether option C is possible at all.
2. **Play account:** yes or no, personal or organisation, and whether to pay $25 now or after the tester week.
3. **Release signing key:** you (not a bot) must own and back up the private key. Approve creating it.
4. **Developer verification:** register now (free limited, or $25 full with ID) or wait for the 2027 date for India.
5. **GitHub Pages on** (needed for a public privacy URL and the site).
6. **Art rights:** confirm who made the cat images and whether they were AI-made (`growth/LEGAL_READINESS.md`), because every store asks.

## 6. Sources (read 2026-11-07)
- Android developer verification overview and FAQ: https://developer.android.com/developer-verification and https://developer.android.com/developer-verification/guides/faq
- Android Developer Console account types ($25 / free limited): https://developer.android.com/developer-verification/guides/android-developer-console
- Android Developers Blog, rollout post: https://android-developers.googleblog.com/2026/03/android-developer-verification-rolling-out-to-all-developers.html
- Play closed-test rule for new personal accounts: https://support.google.com/googleplay/android-developer/answer/14151465
- Play foreground service declaration: https://support.google.com/googleplay/android-developer/answer/13392821
- F-Droid inclusion policy: https://f-droid.org/docs/Inclusion_Policy
- IzzyOnDroid inclusion policy: https://izzyondroid.org/docs/general/AppInclusionPolicy/
- Restricted settings on Android 15 (secondary): https://androidauthority.com/android-15-restricted-settings-sideloading-3481098
- Earlier policy notes: `store/LISTING.md` sections 6 to 8.

Google and F-Droid change these rules often. Re-read the linked pages on the day you act.

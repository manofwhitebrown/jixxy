# Jixxy Play Store prep (T-G2-6)

Status: **draft for Owner review. Nothing is submitted.** Wave 2 update (2026-11-06): listing text rebased on the cat character; the foreground-service rules were re-read and still match section 6 (see "Re-check" at the end). The only numbered release (`v0.0.1-alpha`) has no speech, no Pause card and placeholder art; newer builds on `main` have them in code but **none of it has been seen on a phone**, so **this listing must not be submitted or used until the Owner has verified a build that matches every claim in it.** See also `store/DISTRIBUTION_OPTIONS.md`. Provisional decisions applied: category not Health & Fitness, audience 18+ (D-006). Licence and the paid developer account are pending Owner decisions. Full Play distribution needs a one-time paid developer account and ID verification; the Owner decides whether and when.

How to read this: items marked **[verified]** come from Google's own pages I read on 2026-11-06 (links in section 8). Items marked **[unverified]** are from memory or third-party pages and must be checked in Play Console before relying on them.

## 1. Listing text

**Title (max 30):** `Jixxy: pause with jokes` (23 characters)

**Short description (max 80):** `A lazy, sarcastic cat who pauses you, with jokes, before the apps you choose.` (77 characters)

**Full description (draft; Play's limit is 4,000 characters [unverified], this is well under):**

> *Your lazy, sarcastic, but loyal roommate.* Jixxy is a black, fluffy, heavy-lidded cat who lives on your screen as a floating character.
>
> **What he does**
> You pick the apps. When you open one, Jixxy shows a short pause card with a joke and two buttons: Leave, or Stay for 5 more minutes. You are always in control: Leave works instantly, Stay unlocks after a few seconds.
>
> He reacts to small moments too: a long session, a late night, plugging in the charger, a low battery. He is a naggy cat by design, and you set how naggy (Calm, Naggy or Chaos) **[only once the nag dial ships; remove this clause if it doesn't]**. Every card and bubble can be dismissed, and you can nap him with one tap.
>
> **How he talks**
> He jokes about the situation, never about you. Late at night he gets softer. Once a week he writes a "review of your week" card you can share if you like.
>
> **Why he needs two special permissions**
> - *Display over other apps*: so he can float on screen and show the pause card.
> - *Usage access*: so he knows which app is in front, to react to the apps you chose. He sees app names and time only, never what's inside an app.
>
> **Privacy**
> Everything stays on your phone. Jixxy has no internet permission, no accounts, no ads and no analytics. He does not read messages or notifications, and has no accessibility service, microphone, camera, location or contacts permission.
>
> Banking, payments, passwords, phone and maps apps are protected: he never reacts to or pauses them.
>
> Jixxy is a light-hearted companion, not a health or medical product. Roast level, quiet hours and your chosen apps are all in your control.
>
> English at launch. Android 8.0 or newer. Free.

Draft note: the sign-off *"A little chaos. A lot of care."* can close the description once the Owner approves it for the store.

Notes: the description must only promise what the shipped build does. Re-read it against the release before submitting. Policy requires that requested sensitive permissions are necessary for features **promoted in the listing** [verified], so the description above names each permission and its purpose on purpose.

## 2. Category and audience

- **Category:** Lifestyle or Productivity; **not Health & Fitness** (Manager decision D-006, provisional). Also avoid any wording about wellbeing, addiction or treatment: it invites the Health apps policy and contradicts our honesty rules. [Which of Lifestyle/Productivity reviewers prefer is unverified; Owner decides]
- **Target audience:** **18 and over only** for v1 (D-006, provisional) [Console form options unverified]. Jixxy must not be directed at children. The tester program now says 18+ too.
- **Contains ads:** No. **In-app purchases:** No.

## 3. Content rating (IARC questionnaire) [unverified, answer truthfully in Console]

Expected answers: no violence, no sexual content, no gambling, no user-generated content, no location sharing, no purchases. Jixxy's humour is mild teasing ("goblin", "gremlin", light roasts). If the questionnaire asks about crude or insulting humour, answer honestly and let the resulting rating stand. Do not tune answers to get a lower rating.

## 4. Screenshot plan

Real screenshots only, from real builds. No invented testimonials or fake usage numbers. Aim for 6 phone screenshots:

1. Sprite floating over a neutral home screen, with a short bubble.
2. Pause card with **Leave** and **Stay** buttons (use a marketing-safe line from `content/gold_lines.md`).
3. Setup screen where the user picks the apps, the roast level and the nag level.
4. A soft late-night moment (quiet visual only, per WRITER's off-limits list).
5. The weekly review card.
6. The "What Jixxy saw" log with the erase button, to show privacy visibly.

Rules: no real people's data, no real names, no third-party logos implying endorsement, no health claims in captions. Feature graphic and icon come from CHARACTER's art, not yet available.

## 5. Data safety answers (draft)

Google defines "collect" as transmitting data **off the device**, and says data only processed locally and not sent off device does not need to be disclosed [verified, Google's Data safety guidance, link in section 8]. Jixxy has no internet permission, so:

| Question | Draft answer |
|---|---|
| Does the app collect or share any of the required user data types? | No |
| Is data shared with third parties? | No |
| Data encrypted in transit / deletion requests | Not applicable if "no data collected" [unverified how the form displays this] |
| Privacy policy URL | Required [unverified for apps that collect nothing; assume yes]. Needs a public page: host `PRIVACY.md` content on the Pages site, or link the GitHub file. |

Caveat: if any future SDK or feature transmits data, this answer becomes false. DEVOPS's secret/permission checks and PLATFORM's "no INTERNET, ever" rule are what keep it true. Re-verify against the final manifest and dependencies before every submission.

## 6. Sensitive permission justifications (drafts)

Policy basics [verified]: sensitive permissions must be necessary for current features promoted in the listing; request them in context so users understand why; respect refusals and keep the app functional where possible; never manipulate users into granting them; for special permissions such as `SYSTEM_ALERT_WINDOW`, send users to the system settings page.

**Display over other apps (`SYSTEM_ALERT_WINDOW`)**
> Jixxy's core feature is a floating companion and a pause card that appears over the apps the user chose. The user turns this on during setup via the system settings page and can revoke it at any time. The overlay does not read or capture screen content, and does not block touches outside the sprite or card. The pause card can always be dismissed instantly with Leave.

**Usage access (`PACKAGE_USAGE_STATS`)**
> Used to tell which app is in the foreground so Jixxy can react to, and pause, only the apps the user chose. Only the app's package name and time are used. All processing is on-device; the app has no internet permission. The user grants it in system settings and can revoke it.
> I found **no dedicated Play Console declaration form** for this permission in Google's pages, and one third-party source also says it knows of none. [unverified] User data policy still applies, so the in-app screen that sends the user to the settings page should explain this plainly first (prominent disclosure). UI owns that screen; recommend it is reviewed against Google's User Data policy.

**Foreground service, special use (`FOREGROUND_SERVICE_SPECIAL_USE`)**
Google's rules [verified]:
- For apps targeting Android 14+, every foreground service type must be declared in Play Console (Policy → App content).
- For each type you must describe the feature, describe user impact if the task is deferred or interrupted, and include a link to a video demonstrating each foreground service feature.
- `specialUse` may be declared only in limited scenarios that meet the policy's other characteristics, and all types are subject to review. The service must be user-perceptible.

Draft answers:
> **Feature:** A user-enabled floating companion sprite shown over other apps; the foreground service keeps it running so it can show the pause card in time. Started and stopped by the user, with a visible notification.
> **If deferred:** The sprite would appear late and the pause card might show after the user is already in the chosen app.
> **If interrupted:** The sprite and pause card disappear until the user restarts him; no data is lost.
> **Why no standard type fits:** none of the defined types covers a floating companion overlay. (PLATFORM's manifest already sets the `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` text.)
> **Demo video:** to record on a real device once the build works: enable Jixxy, show the notification, open a chosen app, show the pause card, tap Leave, stop the service.

**Notifications (`POST_NOTIFICATIONS`)**, **Start at boot (`RECEIVE_BOOT_COMPLETED`)**: no special Play declaration found in what I read; they must still match the listing and privacy notice. [unverified]

**Launcher-apps query (`<queries>` with a MAIN/LAUNCHER intent)**: see risk 2 below.

## 7. Risks to report to MANAGER (not assumptions)

0. **D-020 naggy design raises review risk** (more frequent overlay prompts, no app cap). See `store/DISTRIBUTION_OPTIONS.md` section B item 8. Listing text above must say plainly that he is naggy and how to turn him down.

1. **Foreground service review.** `specialUse` is allowed only in limited scenarios and is reviewed; a demo video is required. Real risk of rejection or follow-up questions. [verified]
2. **Installed-apps visibility.** Play treats the installed-app inventory as personal and sensitive, and restricts `QUERY_ALL_PACKAGES`. It also says methods that approximate broad visibility are restricted to user-facing core functionality. Our manifest does not request `QUERY_ALL_PACKAGES` (good), but its launcher-intent `<queries>` entry lets the app list essentially all launchable apps. Our justification is that choosing up to 3 apps is core setup, but a reviewer could challenge it. PLATFORM/UI should consider whether a narrower approach is enough. [verified policy text; interpretation is mine]
3. **Overlay interference.** Device and Network Abuse prohibits unauthorized interference with other apps [verified]. The pause card sits over other apps by design, so the design rules matter: user-enabled, Leave always instant, never blocks touches outside the card, never over PROTECTED apps. Reviewers may still ask.
4. **Usage access disclosure.** No dedicated form found, but User Data policy applies. Onboarding must clearly explain it first. [unverified]
5. **Honesty of listing and Data safety.** Both must match the real build; any future network-using feature breaks the "no data collected" answer.
6. **Account requirements** [unverified, from memory]: new personal developer accounts may need a closed test with a minimum number of testers over a minimum period before production access. The Owner should check Google's current account rules before planning the timeline.
7. **Target API level.** PLATFORM's notes say Play requires target 36 from 2026-08-31; Google's Target API Level policy page is linked below for the Owner/PLATFORM to confirm.
8. Policy pages carry preview notices of changes effective in 2027 (location, contacts, local network, AppFunctions). None apply to Jixxy's current permissions as far as I read, but re-check at submission time.

## 7b. Re-check (2026-11-06, Wave 2)

Re-read Google's foreground-service page (link below). Still says: for apps targeting Android 14+, declare each foreground service type in Play Console (Policy, App content); for each type give a feature description, the user impact if deferred or interrupted, and a link to a demo video; `TYPE_SPECIAL_USE` only in limited scenarios; all types are subject to review. A policy-preview note on that page removes geofencing as an approved use case from 2026-08-26; it does not affect Jixxy. Overlay and usage-access re-check (2026-11-06, Device and Network Abuse page, answer/9888379 and its summary, answer/16559646): I found **no overlay-specific or usage-access-specific rule and no declaration form** on those pages. Three lines matter to us: (1) apps must respect other apps' `FLAG_SECURE`; Jixxy captures no screen content, so this is met by design; (2) the listed violation "circumvent Android sandbox protections in order to derive user activity ... from other apps": Jixxy uses only the user-granted usage-access API, which is not circumvention, but a reviewer reads the prominent-disclosure screen to see that; (3) foreground-service type declaration (above). Several search hits about overlay rules were third-party blogs; I did not rely on them. Sections 6 and 7 stand. Still **unverified**: whether Play review asks extra questions about overlays; re-read at submission. Closed-test account rules (risk 6) are still unverified.

## 8. Sources (Google pages read on 2026-11-06)

- Foreground service and full-screen intent requirements: https://support.google.com/googleplay/android-developer/answer/13392821
- Permissions and APIs that Access Sensitive Information: https://support.google.com/googleplay/android-developer/answer/16558241
- Device and Network Abuse: https://support.google.com/googleplay/android-developer/answer/16559646
- Data safety guidance (definition of "collect"): https://goo.gle/faqs
- Target API Level policy (not read in full; link for PLATFORM/Owner): https://support.google.com/googleplay/android-developer/answer/16561298

Google's pages say policy summaries are overviews and the full policy takes precedence. Re-read them at submission time.

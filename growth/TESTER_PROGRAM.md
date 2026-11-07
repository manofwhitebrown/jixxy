# Jixxy tester kit (T-G3-4)

Draft v0.3, 2026-11-07. Owner: GROWTH. For about **20 testers, all 18 or older** (D-006). Tester #1 is the Owner: start with [`OWNER_TEST_GUIDE.md`](OWNER_TEST_GUIDE.md). **Do not invite anyone until the Owner has run a build where the cat, his speech and the Pause card work on the Owner's phone** (milestone M2). Before that, testers would only find our bugs.
Jixxy has no telemetry. The testers are our analytics, and everything below is voluntary. **No analytics, no sign-ups, no forms that track, nothing collected beyond what a tester chooses to send.**

## 1. What Jixxy is, in one honest line
A free, offline, early-stage Android app: a lazy, sarcastic black cat floats on your screen and, before up to 3 apps *you* choose, makes you pause with a joke. **Leave** or **Stay**, your call. It is rough, parts have not been tested on many phones, and it makes no health claims.

## 2. What he asks permission for, and why

| Permission (what you'll see) | Why | What he does *not* do |
|---|---|---|
| **Display over other apps** | To float on screen and show the pause card | Does not take screenshots or read what's on screen; taps outside the cat and card pass through |
| **Usage access** | To know *which app* is in front, so he can react to the apps you chose | Sees app name and time only, never what's inside an app, never messages |
| **Notifications** | To show the "Jixxy is awake" notice that keeps him running, with a Stop button | No marketing or promo notifications |
| Runs in the background, starts after a restart (no pop-up asked) | So he is there when you open your chosen apps | Nothing is sent anywhere |

He has **no internet permission** (anyone can check in Android's app info), no accessibility service, no microphone, camera, location or contacts. Banking, payments, password, dialer and maps apps are protected: he never reacts to or pauses them. Details: [`PRIVACY.md`](../PRIVACY.md).

## 3. Who to recruit
- **Phones:** a deliberate mix. Some Pixel/Samsung and at least 8 on **Xiaomi/Redmi/Poco, Oppo/Realme, Vivo** (most likely to close background apps). At least 3 on Android 13+ (restricted-settings step) and 2 on Android 8 to 10.
- **Habits:** about half heavy scrollers (their own estimate), half light.
- **Not testers:** anyone under 18, or anyone who needs the app to work for something important.
- Keep a private list **outside the repo**: nickname, phone model, Android version, heavy/light, install date. Delete it when the test ends.

## 4. Copy-paste WhatsApp invite
*(Send only after the Owner's go-ahead; fill in the link the Owner gives you.)*

> Hey! Can you help me test a small free Android app? It's called **Jixxy**: a lazy, sarcastic black cat who floats on your screen and makes you pause, with a joke, before up to 3 apps *you* pick (like reels). You always get **Leave** or **Stay**.
>
> The honest bit: it's early and rough, 18+ only, Android 8 or newer. It asks for "Display over other apps" and "Usage access" so he can float and know which app is open. It has no internet permission, so nothing leaves your phone. No ads, no sign-up.
>
> If you're in: install it, use it for about a week, then reply to me with 5 quick answers (I'll send the questions). You can stop any time. To quit, just uninstall it, no explanation needed.
>
> Reply with your phone model and I'll send the steps. 🐈‍⬛

## 5. Steps to send once they say yes
1. On your phone, open this link and download the file: https://github.com/manofwhitebrown/jixxy/releases/download/latest/jixxy-latest.apk
2. Tap the downloaded file. If Android asks, allow your browser or Files app to install unknown apps (Settings, Apps, Special access, Install unknown apps).
3. Open Jixxy and follow the setup. He explains each permission before he asks. Choose up to 3 apps.
4. **If a permission is greyed out ("Restricted setting"):** tap it once, press OK, then Settings, Apps, Jixxy, three-dot menu (top right), **Allow restricted settings**, then try again. The menu item only shows after you've tapped the blocked permission once. The app shows these steps too.
5. **Keep him running:** set Jixxy's battery use to *Unrestricted* and turn on *Autostart* if your phone has it (the app has a guide per brand). Xiaomi, Oppo, Vivo testers: please do this and tell me if it was enough.
6. Use your phone normally. If he's annoying, tap **Nap**. That is useful feedback too.

**How to leave at any time:** long-press the Jixxy icon, Uninstall. His data is only on your phone and goes with the app. You don't need to tell me why.
**How to report a problem:** send me your phone model, Android version, what you did, what you expected, what happened, and a screenshot if you can. Don't include private messages or other people's content. Once the app has a **Copy debug info** button (Settings), you may paste its text; it only leaves your phone if you paste it.

## 6. The 5 questions (plain text, reply in WhatsApp, nothing tracked)
Send these after about 7 days. Answers can be one word or a voice note. Reply to the sender only.

> **1.** Was he annoying? (1 = never, 5 = I wanted to throw the phone)
> **2.** Which line made you laugh? (type it or screenshot)
> **3.** Which line felt off? (too much, too personal, not funny)
> **4.** On the pause card, did you choose Leave or Stay more? Why?
> **5.** Would you keep him tomorrow? (Yes / Maybe / No)
>
> *(Optional: phone model + Android version, and did he stay alive in the background?)*

Optional short check-ins at **Day 1** ("did setup work, is he on screen?") and **Day 3** ("did he vanish? did you see a pause card?") are fine in the same chat. Keep them to a message or two. Do not send reminders to testers who go quiet more than once.

## 7. Rules for us
- Be honest about what the app is and isn't. No health or "addiction" claims. No promises about dates or features.
- Collect the minimum. Tell testers what you keep (their answers and any screenshot they choose to send), use it only to improve Jixxy, and **delete it when the test ends**.
- If a tester volunteers a screenshot of **What Jixxy saw** or the weekly card, they may crop it first. Ask every time, never require it, never publish a tester's words or screenshots without explicit permission, never invent or change testimonials.
- Tell testers about fixes made because of their feedback, and thank them.

## 8. Reading the answers (directional, not statistics)
With about 20 people, never publish percentages or claims like "reduced screen time by X%". Report counts, quotes (with permission) and what we didn't learn.

| Success metric | Where it comes from |
|---|---|
| Day-7 retention | Q5 plus "is he still installed and on?" at Day 7. Group drop-offs by phone brand: a brand-specific drop is usually background-kill, not dislike. |
| 😂 vs 😴 | Q2 and Q3 (lines that landed or fell flat; send to WRITER). Self-reported only. |
| Leave vs Stay | Q4, with the reasons. "I stayed because he was boring" matters more than the split. |
| Shares of the weekly card | Ask "did you share anything he made?" at Day 7; count as reports. |
| "Was he annoying?" | Q1, coded 1–2 / 3 / 4–5. Any "guilt" or "too personal" answer goes to MANAGER first. |

Send MANAGER a short summary: counts, quotes, brand-by-brand survival notes, lines to change.

# Jixxy Tester Program (T-GROWTH-3)

Draft v0.1. Owner: GROWTH. Jixxy has no telemetry, so the ~20 testers *are* our analytics. Everything below is voluntary for testers and nothing is collected by the app.

## 1. Who to recruit (target about 20)

- **Phones:** a deliberate mix. Some Pixel/Samsung, and at least 8 on **Xiaomi/Redmi/Poco, Oppo/Realme, Vivo** (the brands most likely to kill background apps). Record the exact model and Android version for each tester.
- **Android version:** include at least 3 on Android 13 or newer (restricted settings flow) and at least 2 on Android 8 to 10.
- **Habits:** roughly half heavy scrollers (several hours a day, by their own estimate), half light.
- **Age:** 17 and over only. Do not recruit minors.
- **Not testers:** anyone who needs Jixxy to work for something important. This is pre-release software.

Keep a private sign-up sheet (outside the repo) with: first name or nickname, phone model, Android version, heavy/light, install date. Do not put tester details in the repo.

## 2. Recruitment message (WhatsApp-friendly)

> Hey! I'm helping test a small free Android app called **Jixxy**. He's a tiny dramatic roommate who lives on your screen and makes you pause, with a joke, before up to 3 apps *you* pick. You can always tap Leave or Stay.
>
> It has **no internet permission** (so nothing leaves your phone) and no ads or accounts. It's early and rough, so I want honest feedback, especially if he's annoying.
>
> What I need: install it, use it for 7 days, and answer 3 short check-ins (about 2 minutes each). Android 8+ only, age 17+.
>
> Interested? Reply with your phone model and I'll send the steps.

## 3. Install instructions for testers

1. Download the latest `.apk` from the Releases page: https://github.com/manofwhitebrown/jixxy/releases
2. Open the file. If Android asks, allow your browser or file manager to install unknown apps.
3. **Android 13+:** if permission switches are greyed out, go to Settings → Apps → Jixxy → ⋮ menu → **Allow restricted settings**.
4. Open Jixxy and follow setup. He'll ask for **Display over other apps** and **Usage access**. Choose up to 3 apps for pauses.
5. **Keep him alive:** set battery usage for Jixxy to unrestricted, and turn on autostart if your phone has it (see the README table). Xiaomi/Oppo/Vivo testers: please do this, then tell us whether it was enough.
6. Use your phone normally for 7 days. If he gets annoying, tap the nap button. That is useful feedback too.

If something breaks, send a screenshot and your phone model. Please don't send anything that shows private messages or other people's content.

## 4. Check-in scripts

Send each as a short chat message. Accept voice notes.

**Day 1 (evening)**
1. Did setup work? Where did you get stuck, if anywhere?
2. Is he still on screen right now?
3. Did he say anything that made you smile or wince? Which line?

**Day 3**
1. Has he survived in the background? Any day he disappeared? (Phone model + what you were doing.)
2. Did you see a pause card? Did you tap **Leave** or **Stay**, and why?
3. Was he ever annoying? When, exactly?

**Day 7**
1. Do you still have him installed and switched on? (Yes / napping / uninstalled. If uninstalled, when and why?)
2. Would you keep him? Why or why not?
3. Best line, worst line.
4. Did you share anything he made (a screenshot, the weekly card)? With whom, roughly?
5. One thing to change.

## 5. Feedback form questions

Use a Google Form or the GitHub Issue form below. The questions that matter most:

1. Phone model and Android version
2. Was he ever annoying? When?
3. Pause cards: did you choose Leave or Stay? Why?
4. Which line made you laugh? Which made you cringe? (paste or screenshot)
5. Did he survive in the background? What happened?
6. Did setup (overlay and usage access) make sense? Where did you hesitate?
7. Would you keep him? (Yes / Maybe / No) and why
8. Anything that felt like pressure, guilt, or too personal?

**GitHub Issue form draft (for DEVOPS to place in `.github/ISSUE_TEMPLATE/tester-feedback.yml`; GROWTH does not edit `.github/`):**

```yaml
name: Tester feedback
description: Tell us how Jixxy went for you
title: "[Tester] "
labels: ["tester-feedback"]
body:
  - type: markdown
    attributes:
      value: "Please don't include private messages, other people's content, or personal details. This issue may be public."
  - type: input
    id: device
    attributes:
      label: Phone model and Android version
    validations:
      required: true
  - type: textarea
    id: annoying
    attributes:
      label: Was he ever annoying? When?
  - type: textarea
    id: pause
    attributes:
      label: On pause cards, did you choose Leave or Stay, and why?
  - type: textarea
    id: lines
    attributes:
      label: Which line made you laugh or cringe?
  - type: dropdown
    id: background
    attributes:
      label: Did he survive in the background?
      options: ["Yes, all week", "Mostly", "He kept disappearing", "He never stayed"]
  - type: dropdown
    id: keep
    attributes:
      label: Would you keep him?
      options: ["Yes", "Maybe", "No"]
  - type: textarea
    id: why
    attributes:
      label: Why?
```

Because GitHub issues are public, offer testers the private Google Form or a direct message as an alternative.

## 6. Voluntary sharing of "What Jixxy saw" and the weekly card

The app sends nothing. If a tester wants to help us understand usage, they may screenshot the in-app **What Jixxy saw** log or the weekly card and send it to us.

- Ask first, every time. Never require it.
- Tell them to crop or hide anything they consider private, including app names they'd rather not share.
- Use screenshots only for understanding patterns. Don't publish a tester's screenshot or words without their explicit permission, and don't invent or alter testimonials.
- Delete received screenshots when the program ends.

## 7. Reading results against the success metrics

With about 20 people, treat everything as **directional**, not statistics. Never publish percentages or claims like "X% reduction in screen time" from this group.

| Metric | How we read it from testers | Watch for |
|---|---|---|
| **Day-7 retention** | Count testers at Day 7 who still have him installed and switched on (self-reported). | Group drop-offs by phone brand: a brand-specific drop usually means background-kill problems, not dislike. |
| **😂 vs 😴 ratio** | Testers' self-reports, plus screenshots of the log if volunteered. | A heavy 😴 skew on certain lines means those lines need rework (send to WRITER). |
| **Leave vs Stay** | Day-3 and Day-7 answers on pause cards, with reasons. | "I stayed because it was boring/annoying" matters more than the raw split. |
| **Shares** | Day-7 question 4, counted as reports, not exact numbers. | Which screen or line got shared. |
| **"Was he annoying?"** | Day-3 and form answers, coded as: never / once / often. | Any "guilt" or "too personal" comment is high priority for MANAGER. |

Report to MANAGER as a short summary: counts, quotes (with permission), brand-by-brand survival notes, and a list of lines or screens that need changes. Be explicit about what we didn't learn.

## 8. Rules for us

- Be honest with testers about what the app is and isn't. No health claims.
- Tell testers they can stop at any time and uninstall with no explanation.
- Thank them, and let them know about fixes made because of their feedback.

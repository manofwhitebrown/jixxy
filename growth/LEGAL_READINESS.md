# Legal readiness list for the Owner (T-V1-GROWTH-6)

Draft, 2026-11-06, GROWTH. **I am not a lawyer and this is not legal advice.** It lists decisions and questions so you can decide, or take them to a lawyer. `[verified]` = I read it in a source named here. `[unverified]` = my understanding, check before relying on it.

## 1. Licence (your decision; no LICENSE file until you choose)
Today: no licence file, so by default all rights are reserved and nobody may legally copy or reuse the repo. Choose separately for **code** and for **art/text**.

| Option | Consequence |
|---|---|
| Keep all rights reserved | Safest for control. Repo is public, so people can read it but not legally reuse it. Contributors and reviewers cannot reuse code. |
| Permissive (MIT / Apache-2.0) | Anyone may use, change, sell the code, even in a closed competitor. Apache-2.0 adds a patent grant. Easy for contributors. |
| Copyleft (GPL-3.0) | Derivatives must stay open. Discourages closed forks. Some people consider GPL awkward with app stores. [unverified] |
| Art and brand | Common practice is to license code openly but keep the cat art, name and lines "all rights reserved". If you do, say so in the licence so it is not read as covering the art. |

Also check: our dependencies' licences must be compatible with the one you pick (DEVOPS can list them from Gradle). Anyone else who contributed text or art needs to be covered (see 3).

## 2. Name and trademark "Jixxy"
- Search before launch: India's trademark registry public search, the US USPTO database, and app stores (Play, App Store) for the same or similar names. [unverified: I did not run these searches]
- Also check whether a domain and a social handle are free, and that the name is not confusingly close to another app in the same area.
- A trademark is separate from copyright. Registration is optional but it is what gives strong protection; it costs money, so that is your call (the project rule is free-only for the team's tools, not for you).
- Cheap safeguard: use the name consistently, keep dated evidence of first use (the `v0.0.1-alpha` release is one).

## 3. Who owns the character art (most important)
The Owner supplied the character sheet and expression images. Questions Play, a trademark office or a future collaborator may ask, and you should be able to answer:
1. **Who made the images, and how?** If an artist drew them, keep a written agreement saying you own them or hold a licence that covers app, marketing and merchandise. If an AI image tool made them, note the tool, its terms of service on commercial use, and your prompts.
2. **If AI-generated:** in the US, copyright needs human authorship. The Copyright Office's January 2025 report says prompts alone are not enough, and purely AI-generated output is not protected, while human-made additions, selection and arrangement, or significant edits can be [verified: Copyright Office, "Copyright and Artificial Intelligence, Part 2: Copyrightability", summarised by several law-firm notes]. Practical meaning: others might be free to copy purely AI-generated images, and you may not be able to stop them with copyright. Other countries mostly also require a human author [verified, same report]. India's position I have not checked [unverified].
3. **Proof of ownership:** keep the original files with dates, the sketch or edit history, any commission contract, and the tool's terms as they were on the creation date.
4. **Resemblance risk:** make sure the cat does not copy an existing character or brand. Reverse-image search the sheet once. [unverified: not done]
5. **Edits we made:** CHARACTER and PLATFORM only cropped, resized and converted the Owner's originals; the identity was not changed (design rule D-012).
Tell me the facts and I will write the honest "credits" line for the README and store page. Until then the repo says "art supplied by the Owner" and nothing more.

## 4. Play Store rules touching the art and content
- Play has an AI-generated content policy aimed at apps that **generate** content with AI. Jixxy has no AI features at all, so I do not expect it to apply. Whether using AI-made art in the listing needs any disclosure: [unverified]. Re-read Play's current policy at submission.
- Impersonation / intellectual property policies apply to any art you do not have rights to; hence item 3.

## 5. Privacy policy and terms
- Play requires a privacy policy URL for the listing [unverified for apps that collect nothing; assume yes]. `PRIVACY.md` is the draft; it should be hosted at a public URL (Pages) and linked from the Play Console.
- The privacy text is only as true as `growth/CLAIMS.md` rows allow. Do not publish it before DEVOPS closes the guard gaps listed there.
- Terms of use: optional for a free offline app; a short "as is, no warranty" text is common. Take advice if you want one with limits of liability. [unverified]
- India: the Digital Personal Data Protection Act applies to processing digital personal data. We collect nothing off-device, which should keep our exposure small, but a lawyer should confirm for the app's own on-device data and for tester feedback forms. [unverified]
- Tester feedback: if testers send you screenshots, names or device info, that is personal data you now hold. Collect the minimum, tell them what you keep and delete it after the test (beta ops plan, T-V1-GROWTH-5).

## 6. Age rating and audience
- Provisional decision D-006: 18+ audience, not "Health & Fitness".
- The content rating is a questionnaire in Play Console (IARC). Likely honest answers: no violence, no sexual content, no user-generated content or chat, no location sharing, no purchases. Check one item: **mild crude language or insults.** The line bank roasts the situation and may include mild language; WRITER's bible forbids mocking bodies or mental health. Ask WRITER whether any line uses profanity before you answer. [unverified]
- Declare the target audience honestly. A cute cat can attract children; choosing adults-only audience avoids children's-app rules but you must not market to kids. [unverified: check Play's Families policy wording at submission]

## 7. Money and accounts (flagged for you)
- A Play developer account has a one-time fee; personal accounts created recently may need a closed test with a minimum number of testers before production. [unverified: rules change; check the Play Console help page at that time]
- Payments, ads, subscriptions are out of scope, so no payments terms are needed now.
- Tax and company set-up if you ever earn money: out of scope here, ask a professional.

## 8. Open decisions summary
| # | Decision / action | Who | Blocks |
|---|---|---|---|
| L1 | Licence for code, and for art | Owner | Any LICENSE file, contributors |
| L2 | Name and trademark searches | Owner | Store launch |
| L3 | Facts about who/what made the art, and proof | Owner | Credits line, store listing |
| L4 | Reverse-image check of the character | Owner | Launch |
| L5 | Confirm no profanity in lines, then age-rating answers | WRITER + Owner | Play questionnaire |
| L6 | Privacy policy hosted at a public URL | GROWTH + Owner (Pages) | Store listing |
| L7 | Lawyer review of privacy/terms (optional but recommended before production) | Owner | Production release |

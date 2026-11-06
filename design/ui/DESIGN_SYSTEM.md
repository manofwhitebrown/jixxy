# Jixxy design system (T-V1-UI-1, first cut)

Source of truth in code: `ui/theme/Color.kt`, `Theme.kt`, `Spacing.kt`. Palette origin: `PALETTE.md` (Owner's character sheet, D-012).

## Colour roles
| Role | Dark | Light |
|---|---|---|
| background | near-black fur `#0E0910` | cream `#F8E1E2` |
| surface | charcoal `#1A131C` | `#FFF4F2` |
| primary (accent) | muted purple `#A080B0` | deep purple `#684878` |
| text | cream `#FAE6E6` | ink `#1A1820` |
| tertiary | ear pink `#E8A0B4` (tiny accents only) | `#9C4A63` |

Contrast (WCAG, computed): dark text/background 16.5, text/surface 15.2, onPrimary/primary 5.8, onTertiary/tertiary 9.5; light text/background 14.1, text/surface 16.3, onPrimary/primary 7.0, onTertiary/tertiary 5.5. All meet AA (4.5).

## Shape, spacing, type
- Shapes: 16 / 24 / 32 dp rounded (soft, cat-like).
- Spacing: 4, 8, 16, 24, 32 dp (`Spacing`); touch targets at least 48 dp.
- Type: Material 3 scale on the system font (no bundled fonts until the Manager decides about OFL fonts). Screen titles use headlineMedium and are marked as TalkBack headings.
- Motion: none beyond Material defaults; honour the system "remove animations" setting.

## Rules
Dark-first, cream text, pink never for text or large areas. Copy comes from `strings.xml` (generated from `content/MICROCOPY.md`). No jokes where the user is deciding on a permission.

## Not done yet
Component gallery with previews, motion tokens, font decision, and screenshots (nothing here has been seen rendered).

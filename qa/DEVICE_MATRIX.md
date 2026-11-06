# Device matrix
Our audience is Indian Gen-Z on mid-range phones, so the battery-killers matter more than Pixels.

| Family | Why | Android | Focus |
|---|---|---|---|
| Stock / Pixel | Reference behaviour | 14, 15, 16 | overlay permission flow, foreground service, usage access, notification permission (13+) |
| Samsung One UI | Large share | 13, 14 | "sleeping apps" / battery optimisation, overlay behaviour |
| Xiaomi / Redmi / POCO (HyperOS / MIUI) | Most aggressive killer, high share in India | 13, 14 | autostart toggle, battery saver "No restrictions", lock app in recents, restricted-settings path for sideloaded APK, overlay + usage access prompts |
| Oppo / Realme (ColorOS / realme UI) | Aggressive killer | 13, 14 | auto-launch, background freeze, overlay |
| Vivo (Funtouch / OriginOS) | Aggressive killer | 13, 14 | high background power consumption allow-list, autostart |

Rules: test at least one device per family before M3; record model, Android version and OEM skin in the result. Android 8-12 is covered by lint/minSdk only unless a tester has such a device. Android 13 (notification permission) and 14+ (foreground service type rules) are the version boundaries most likely to break us.

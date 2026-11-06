---
to: UI
from: PLATFORM
type: INFO
priority: normal
refs: T-UI-0, T-UI-1, T-UI-5, T-UI-6
---
3. Gradle: done. app now has Compose BOM, material3, ui-tooling(+preview), navigation-compose 2.9.0, lifecycle-runtime-compose and viewmodel-compose 2.9.1, coroutines. Compose compiler plugin is applied; minSdk 26. Untested build.
5. R namespace is app.jixxy.R (namespace = "app.jixxy").
4. FileProvider: I will add the <provider> once you push res/xml/file_paths.xml (you own res/); otherwise the build breaks. Tell me the cache/files subpath you use. Authority will be ${applicationId}.fileprovider.
1+2. Planned shapes (T-PLATFORM-6, not written yet, may change):
 PermissionsRepository: Flow<Boolean> overlayGranted, usageAccessGranted, notificationsGranted, batteryUnrestricted, restrictedSettingsLikelyBlocked; plus fun intentFor(permission): Intent and a refresh() to re-read after returning from Settings.
 OemIntents: fun detect(): OemBrand (XIAOMI, OPPO, VIVO, SAMSUNG, OTHER) and fun keepAliveIntents(): List<Intent> best-effort, last item always app settings.
I will send a HANDOFF with the final signatures.

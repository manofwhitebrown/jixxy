---
to: UI
from: PLATFORM
type: INFO
priority: normal
refs: T-UI-5
---
FileProvider is in the manifest: authority ${applicationId}.fileprovider (use "$packageName.fileprovider" in code), exported=false, grantUriPermissions=true, @xml/file_paths. Not built yet.
Data layer for you: DataStoreSettingsRepository(context) and SqliteStateStore(context) in app.jixxy.data implement the engine interfaces. Both need a Context; I will expose a single holder in T-PLATFORM-5, so avoid constructing them in UI for now.

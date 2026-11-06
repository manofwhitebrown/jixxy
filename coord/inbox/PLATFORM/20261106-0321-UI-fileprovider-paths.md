---
to: PLATFORM
from: UI
type: HANDOFF
priority: normal
refs: T-UI-5, 20261106-0319-PLATFORM-reply-interfaces.md
---
res/xml/file_paths.xml is pushed: <cache-path name="share" path="share/"/>. I will write share cards to context.cacheDir/share/. Please add the <provider> (authority ${applicationId}.fileprovider, grantUriPermissions=true, exported=false, meta-data @xml/file_paths). Thanks for the Gradle deps; your planned PermissionsRepository / OemIntents shapes work for me, I will adapt them to my UI-side PermissionsUiState.

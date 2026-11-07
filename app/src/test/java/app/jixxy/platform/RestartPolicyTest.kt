package app.jixxy.platform

import android.content.Intent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestartPolicyTest {
    @Test fun restartsAfterBootAndUpdate() {
        assertTrue(RestartPolicy.shouldRestart(Intent.ACTION_BOOT_COMPLETED, true, true))
        assertTrue(RestartPolicy.shouldRestart(Intent.ACTION_MY_PACKAGE_REPLACED, true, true))
    }

    @Test fun neverRestartsWhenUserStoppedHimOrPermissionGone() {
        assertFalse(RestartPolicy.shouldRestart(Intent.ACTION_BOOT_COMPLETED, false, true))
        assertFalse(RestartPolicy.shouldRestart(Intent.ACTION_BOOT_COMPLETED, true, false))
    }

    @Test fun ignoresOtherBroadcasts() {
        assertFalse(RestartPolicy.shouldRestart(Intent.ACTION_SCREEN_ON, true, true))
        assertFalse(RestartPolicy.shouldRestart(null, true, true))
    }
}

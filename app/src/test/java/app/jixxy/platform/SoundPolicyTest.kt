package app.jixxy.platform

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SoundPolicyTest {
    private fun ok(
        enabled: Boolean = true, ringer: Boolean = true, dnd: Boolean = true, call: Boolean = false,
        music: Boolean = false, quiet: Boolean = false, nap: Boolean = false, blind: Boolean = false,
        id: String? = "b2", last: String? = "b1",
    ) = SoundPolicy.allowed(enabled, ringer, dnd, call, music, quiet, nap, blind, id, last)

    @Test fun playsWhenEverythingIsFine() = assertTrue(ok())
    @Test fun offByDefaultSetting() = assertFalse(ok(enabled = false))
    @Test fun silentRingerOrDnd() { assertFalse(ok(ringer = false)); assertFalse(ok(dnd = false)) }
    @Test fun neverInCallOrOverMusic() { assertFalse(ok(call = true)); assertFalse(ok(music = true)) }
    @Test fun neverQuietNapBlind() { assertFalse(ok(quiet = true)); assertFalse(ok(nap = true)); assertFalse(ok(blind = true)) }
    @Test fun oneCuePerBubble() = assertFalse(ok(id = "b1", last = "b1"))
}

package app.jixxy.platform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlatformPureTest {
    @Test fun detectsBrands() {
        assertEquals(OemBrand.XIAOMI, OemIntents.detect("Xiaomi"))
        assertEquals(OemBrand.XIAOMI, OemIntents.detect("POCO"))
        assertEquals(OemBrand.OPPO, OemIntents.detect("realme"))
        assertEquals(OemBrand.OPPO, OemIntents.detect("OnePlus"))
        assertEquals(OemBrand.VIVO, OemIntents.detect("vivo"))
        assertEquals(OemBrand.VIVO, OemIntents.detect("iQOO"))
        assertEquals(OemBrand.SAMSUNG, OemIntents.detect("samsung"))
        assertEquals(OemBrand.OTHER, OemIntents.detect("Google"))
    }

    @Test fun restrictedHeuristic() {
        assertTrue(PermissionsRepository.likelyBlocked(35, null, overlay = false, usage = false))
        assertTrue(PermissionsRepository.likelyBlocked(36, "com.example.store", overlay = true, usage = false))
        assertFalse(PermissionsRepository.likelyBlocked(35, "com.android.vending", false, false))
        assertFalse(PermissionsRepository.likelyBlocked(34, null, false, false))
        assertFalse(PermissionsRepository.likelyBlocked(35, null, overlay = true, usage = true))
    }
}

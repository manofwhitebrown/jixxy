package app.jixxy.engine.lines

import app.jixxy.engine.api.AppCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Guards app_categories.json (schema D-005): valid categories, no clashes, plausible package names. */
class AppCategoriesFileTest {
    private val pkg = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+$")

    private fun root(): Map<*, *>? {
        val f = listOf(File("../app/src/main/assets/config/app_categories.json"), File("app/src/main/assets/config/app_categories.json"))
            .firstOrNull { it.isFile } ?: return null
        return MiniJson.parse(f.readText()) as Map<*, *>
    }

    @Test fun everyOverrideIsAValidNonProtectedCategoryWithAValidPackageName() {
        val o = (root() ?: return)["overrides"] as Map<*, *>
        assertTrue(o.size > 50)
        o.forEach { (k, v) ->
            assertTrue("bad package $k", pkg.matches(k as String))
            val c = AppCategory.valueOf(v as String)
            assertTrue("$k should be listed under protected instead", c != AppCategory.PROTECTED)
        }
    }

    @Test fun protectedListIsGenerousAndNeverOverlapsOverrides() {
        val r = root() ?: return
        val prot = (r["protected"] as List<*>).map { it as String }
        val over = (r["overrides"] as Map<*, *>).keys
        assertEquals("duplicates in protected", prot.size, prot.toSet().size)
        assertTrue(prot.size >= 30)
        prot.forEach { assertTrue("bad package $it", pkg.matches(it)) }
        assertEquals(emptySet<Any?>(), prot.toSet().intersect(over))
        listOf("com.phonepe.app", "net.one97.paytm", "com.google.android.dialer", "com.google.android.apps.maps", "com.x8bit.bitwarden")
            .forEach { assertTrue("$it must be protected", it in prot) }
    }

    @Test fun unverifiedPackagesAreActuallyListedSomewhere() {
        val r = root() ?: return
        val known = (r["protected"] as List<*>).toSet() + (r["overrides"] as Map<*, *>).keys
        (r["_unverified"] as List<*>).forEach { assertTrue("$it is unverified but not used", it in known) }
    }
}

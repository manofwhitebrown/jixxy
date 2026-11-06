package app.jixxy.platform.signals

import android.content.pm.ApplicationInfo
import app.jixxy.engine.api.AppCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryRulesTest {
    private val rules = CategoryRules.parse(
        """{"overrides":{"com.chat":"MESSAGING","com.bad":"NOPE"},"protected":["com.bank"]}""", "app.jixxy"
    )

    @Test fun protectedBeatsOverrideAndAndroidCategory() =
        assertEquals(AppCategory.PROTECTED, rules.resolve("com.bank", ApplicationInfo.CATEGORY_SOCIAL))

    @Test fun overrideBeatsAndroidCategory() =
        assertEquals(AppCategory.MESSAGING, rules.resolve("com.chat", ApplicationInfo.CATEGORY_SOCIAL))

    @Test fun invalidOverrideValueIsIgnored() =
        assertEquals(AppCategory.OTHER, rules.resolve("com.bad", -1))

    @Test fun androidCategoriesMap() {
        assertEquals(AppCategory.SOCIAL, rules.resolve("x", ApplicationInfo.CATEGORY_SOCIAL))
        assertEquals(AppCategory.VIDEO, rules.resolve("x", ApplicationInfo.CATEGORY_VIDEO))
        assertEquals(AppCategory.GAME, rules.resolve("x", ApplicationInfo.CATEGORY_GAME))
        assertEquals(AppCategory.PROTECTED, rules.resolve("x", ApplicationInfo.CATEGORY_MAPS))
    }

    @Test fun unknownIsOther() = assertEquals(AppCategory.OTHER, rules.resolve("x", -1))

    @Test fun ownPackageIsProtected() = assertEquals(AppCategory.PROTECTED, rules.resolve("app.jixxy", -1))

    @Test fun malformedJsonGivesEmptyRules() =
        assertEquals(AppCategory.OTHER, CategoryRules.parse("{oops", "app.jixxy").resolve("com.chat", -1))
}

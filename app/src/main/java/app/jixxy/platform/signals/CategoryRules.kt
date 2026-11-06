package app.jixxy.platform.signals

import android.content.pm.ApplicationInfo
import app.jixxy.engine.api.AppCategory
import org.json.JSONObject

/**
 * Pure category logic, unit-testable on the JVM.
 *
 * Assumed config schema (ENGINE owns assets/config/app_categories.json; not yet pushed):
 * `{"overrides": {"com.pkg": "MESSAGING"}, "protected": ["com.bank"]}`. Unknown keys are ignored.
 */
class CategoryRules(
    private val overrides: Map<String, AppCategory> = emptyMap(),
    private val protectedPkgs: Set<String> = emptySet(),
    private val ownPackage: String = "",
) {
    /** [androidCategory] is `ApplicationInfo.category` (or -1 when unknown). Order: protected, override, Android category. */
    fun resolve(pkg: String, androidCategory: Int): AppCategory = when {
        pkg == ownPackage || pkg in protectedPkgs -> AppCategory.PROTECTED
        pkg in overrides -> overrides.getValue(pkg)
        else -> fromAndroid(androidCategory)
    }

    companion object {
        /** Mapping from the ApplicationInfo.CATEGORY_* constants (developer.android.com/reference/android/content/pm/ApplicationInfo#category). */
        fun fromAndroid(category: Int): AppCategory = when (category) {
            ApplicationInfo.CATEGORY_GAME -> AppCategory.GAME
            ApplicationInfo.CATEGORY_VIDEO -> AppCategory.VIDEO
            ApplicationInfo.CATEGORY_SOCIAL -> AppCategory.SOCIAL
            ApplicationInfo.CATEGORY_PRODUCTIVITY -> AppCategory.PRODUCTIVITY
            ApplicationInfo.CATEGORY_MAPS -> AppCategory.PROTECTED
            else -> AppCategory.OTHER
        }

        /** Parses the JSON config; malformed input yields empty rules rather than a crash. */
        fun parse(json: String?, ownPackage: String): CategoryRules {
            if (json.isNullOrBlank()) return CategoryRules(ownPackage = ownPackage)
            return try {
                val root = JSONObject(json)
                val overrides = mutableMapOf<String, AppCategory>()
                root.optJSONObject("overrides")?.let { o ->
                    o.keys().forEach { pkg ->
                        runCatching { AppCategory.valueOf(o.getString(pkg)) }.getOrNull()?.let { overrides[pkg] = it }
                    }
                }
                val prot = mutableSetOf<String>()
                root.optJSONArray("protected")?.let { a -> for (i in 0 until a.length()) prot += a.getString(i) }
                CategoryRules(overrides, prot, ownPackage)
            } catch (e: org.json.JSONException) {
                CategoryRules(ownPackage = ownPackage)
            }
        }
    }
}

package app.jixxy.platform.signals

import android.content.Context
import android.content.pm.PackageManager
import app.jixxy.engine.api.AppCategory

/** Resolves a package to an [AppCategory] using [CategoryRules] loaded from assets/config/app_categories.json. */
class CategoryResolver(private val context: Context) {
    private val rules: CategoryRules by lazy {
        val json = runCatching {
            context.assets.open(CONFIG_PATH).bufferedReader().use { it.readText() }
        }.getOrNull()
        CategoryRules.parse(json, context.packageName)
    }
    private val cache = HashMap<String, AppCategory>()

    fun resolve(pkg: String): AppCategory = cache.getOrPut(pkg) {
        val androidCategory = try {
            context.packageManager.getApplicationInfo(pkg, 0).category
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        }
        rules.resolve(pkg, androidCategory)
    }

    /** Human-readable app name for Pause cards; falls back to the package name. */
    fun label(pkg: String): String = try {
        val pm = context.packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        pkg
    }

    private companion object {
        const val CONFIG_PATH = "config/app_categories.json"
    }
}

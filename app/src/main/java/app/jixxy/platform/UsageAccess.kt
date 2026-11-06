package app.jixxy.platform

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Process

/** Usage-access (PACKAGE_USAGE_STATS) check shared by the detector and [PermissionsRepository]. */
object UsageAccess {
    fun isGranted(context: Context): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= 29) {
            ops.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else {
            @Suppress("DEPRECATION")
            ops.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
}

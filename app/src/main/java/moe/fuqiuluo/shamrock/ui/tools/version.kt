package moe.fuqiuluo.shamrock.ui.tools

import android.content.Context
import moe.fuqiuluo.shamrock.getAppPackageName

fun getShamrockVersion(context: Context): String {
    val packageManager = context.packageManager
    val packageName = getAppPackageName(context) ?: "moe.fuqiuluo.shamrock"
    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    return packageInfo.versionName
}

//fun getAppPackageName(context: Context): String? {
//    val packageManager = context.packageManager
//    val selfPackageName = context.packageName
//    try {
//        val applicationInfo = packageManager.getApplicationInfo(selfPackageName, 0)
//        return applicationInfo.packageName
//    } catch (e: PackageManager.NameNotFoundException) {
//        e.printStackTrace()
//    }
//    return null
//}
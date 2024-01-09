package com.timejar.app.sensing.app_activity

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import java.util.Date

fun getBlacklistedApps(context: Context): List<String> {
    val packageManager = context.packageManager
    val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    val blacklistedApps = mutableListOf<String>()

    // Add specific apps manually
    blacklistedApps.add("com.timejar.app")
    blacklistedApps.add("com.google.android.apps.nexuslauncher")

    val launcherApps = getLauncherApps(packageManager)

    apps.forEach { app ->
        // System apps
        if ((app.flags and ApplicationInfo.FLAG_SYSTEM != 0) &&
            (app.sourceDir.startsWith("/system/app/") || app.sourceDir.startsWith("/system/priv-app/")) &&
            (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 0)) {

            Log.d("system app", app.packageName)
            blacklistedApps.add(app.packageName)
        }

        // Launcher apps
        if (launcherApps.contains(app.packageName)) {
            Log.d("launcher app", app.packageName)
            blacklistedApps.add(app.packageName)
        }

        // Debug apps
        if (app.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Log.d("debug app", app.packageName)
            blacklistedApps.add(app.packageName)
        }
    }

    val blacklistedAppsFiltered = blacklistedApps.distinct() // Remove duplicates
    Log.d("getBlacklistedApps", blacklistedAppsFiltered.toString())

    return blacklistedAppsFiltered
}

fun getLauncherApps(packageManager: PackageManager): List<String> {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }
    val resolveInfos = packageManager.queryIntentActivities(intent, 0)
    return resolveInfos.mapNotNull { it.activityInfo?.packageName }
}


fun getNonSwitchingApps(context: Context): List<String> {
    val nonSwitchingApps = mutableListOf<String>(
        "com.android.systemui"
    )

    nonSwitchingApps += getInstalledKeyboardApps(context)

    val nonSwitchingAppsFiltered = nonSwitchingApps.distinct() // Remove duplicates

    Log.d("getNonSwitchingApps", nonSwitchingAppsFiltered.toString())

    return nonSwitchingAppsFiltered
}

fun getInstalledKeyboardApps(context: Context): List<String> {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val inputMethods = inputMethodManager.inputMethodList

    return inputMethods.map(InputMethodInfo::getPackageName)
}
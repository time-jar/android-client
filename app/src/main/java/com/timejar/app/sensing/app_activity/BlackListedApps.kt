package com.timejar.app.sensing.app_activity

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import java.util.Date

fun getBlacklistedApps(context: Context): List<String> {
    val packageManager = context.packageManager
    val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    val blacklistedApps = mutableListOf<String>()

    blacklistedApps.add("com.timejar.app")
    blacklistedApps.add("com.google.android.apps.nexuslauncher")

    apps.forEach { app ->
        // System apps
        if (app.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
            if ((app.sourceDir.startsWith("/system/app/") || app.sourceDir.startsWith("/system/priv-app/")) &&
                (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 0)) {

                Log.d("system app", app.packageName)

                blacklistedApps.add(app.packageName)
            }
        }

        // Check for launcher apps
        val launcherIntent = packageManager.getLaunchIntentForPackage(app.packageName)
        if (launcherIntent?.categories?.contains(Intent.CATEGORY_HOME) == true) {

            Log.d("launcher app", app.packageName)

            blacklistedApps.add(app.packageName)
        }

        // Check for debug apps
        if (app.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {

            Log.d("debug app", app.packageName)

            blacklistedApps.add(app.packageName)
        }
    }

    val blacklistedAppsFiltered = blacklistedApps.distinct() // Remove duplicates

    Log.d("getBlacklistedApps", blacklistedAppsFiltered.toString())

    return blacklistedAppsFiltered
}

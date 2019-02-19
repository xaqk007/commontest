package com.anniu.common.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.TextUtils

/**
 * Created by CN-11 on 2017/8/11.
 */

object ApkInfoUtil {
    private var appVersionName: String? = null
    private var channel: String? = null

    fun getAPPVersionCode(ctx: Context): String? {
        if (TextUtils.isEmpty(appVersionName)) {
            val manager = ctx.packageManager
            try {
                val info = manager.getPackageInfo(ctx.packageName, 0)
                appVersionName = info.versionName
            } catch (e: PackageManager.NameNotFoundException) {}
        }
        return appVersionName
    }

    fun getAPPChannel(ctx: Context): String? {
        if (TextUtils.isEmpty(channel)) {
            try {
                val info = ctx.packageManager.getApplicationInfo(ctx.packageName, PackageManager.GET_META_DATA)
                val temp = info.metaData.get("UMENG_CHANNEL")
                channel = temp.toString()
            } catch (e: PackageManager.NameNotFoundException) {}
        }
        return channel
    }

    fun getAppName(context: Context): String? {
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            val labelRes = packageInfo.applicationInfo.labelRes
            return context.resources.getString(labelRes)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return packageInfo != null
    }
}

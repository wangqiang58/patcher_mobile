package com.xdf.studypatch.util

import android.content.Context
import android.content.pm.PackageInfo
import android.util.Log

/**
@author: wangqiang
@date: 2023/8/5
@desc:
 */
object PackageUtils {

    val TAG = PackageUtils::class.simpleName


    fun getVersionName(context: Context): String? {
        val packageInfo: PackageInfo? =
            getPackageInfo(
                context,
                context.packageName
            )
        return if (packageInfo != null) packageInfo.versionName else ""
    }

    fun getVersionCode(context: Context): Int {
        val packageInfo: PackageInfo? = getPackageInfo(
            context,
            context.packageName
        )
        return packageInfo?.versionCode ?: -1
    }

    private fun getPackageInfo(context: Context, pkgName: String): PackageInfo? {
        return getPackageInfo(context, pkgName, 0)
    }


    private fun getPackageInfo(context: Context, pkgName: String, flag: Int): PackageInfo? {
        try {
            return context.packageManager.getPackageInfo(pkgName, flag)
        } catch (e: Exception) {
            Log.w(
                TAG,
                "package not found: $pkgName"
            )
        }
        return null
    }


}
package com.xdf.studypatch.util

import android.content.Context
import android.content.SharedPreferences

/**
@author: wangqiang
@date: 2023/8/5
@desc: 记录Patch加载记录，以及修复记录
       当修复失败超过三次，取消修复
 */
object PatchHistoryUtil {
    private lateinit var historySP: SharedPreferences
    /**
     * sp name
     */
    private const val XDF_ROBUST_KEY_PATCH_LOAD_HISTORY = "xdf_robust_key_patch_load_history"

    /**
     * key:本地补丁的版本号
     */
    private const val XDF_ROBUST_KEY_PATCH_VERSION = "xdf_robust_key_patch_version"

    /**
     * key:本地补丁的校验信息
     */
    private const val XDF_ROBUST_KEY_PATCH_MD5_SIGN = "xdf_robust_key_patch_md5_sign"


    /**
     * key:上一次修复失败的版本号
     */
    private const val XDF_ROBUST_KEY_PATCH_LAST_FIX_VERSION_FAILURE = "xdf_robust_key_patch_last_fix_version_failure"

    /**
     * 初始化
     */
    internal fun init(context: Context) {
        historySP =
            context.getSharedPreferences(XDF_ROBUST_KEY_PATCH_LOAD_HISTORY, Context.MODE_PRIVATE)
    }

    /**
     * 本地补丁的签名信息
     */
    fun localPatchSign(): String {
        return historySP.getString(XDF_ROBUST_KEY_PATCH_MD5_SIGN, "") ?: ""
    }

    /**
     * 保存本地补丁的签名信息
     */
    internal fun putLocalPatchSign(signed: String?) {
        if (signed == null) {
            return
        }
        historySP.edit().putString(XDF_ROBUST_KEY_PATCH_MD5_SIGN, signed)
            .apply()
    }

    /**
     * 本地补丁的版本号
     */
    fun localPatchVersion(): String {
        return historySP.getString(XDF_ROBUST_KEY_PATCH_VERSION, "")?:""
    }

    /**
     * 将补丁的版本号保存到本地
     */
    internal fun putLocalPatchVersion(patchVersion: String?) {
        if (patchVersion == null) {
            return
        }
        historySP.edit().putString(XDF_ROBUST_KEY_PATCH_VERSION, patchVersion)
            .apply()
    }

    /**
     * 上一次修复失败补丁的唯一名称
     */
    fun lastFixFailureVersion(): String {
        return historySP.getString(XDF_ROBUST_KEY_PATCH_LAST_FIX_VERSION_FAILURE, "")?:""
    }

    /**
     * 保存上一次修复失败补丁的唯一名称
     */
    internal fun putLastFixFailureVersion(patchVersion: String?) {
        if (patchVersion == null) {
            return
        }
        historySP.edit()
            .putString(XDF_ROBUST_KEY_PATCH_LAST_FIX_VERSION_FAILURE, patchVersion)
            .apply()
    }

}
package com.xdf.studypatch.callback

import android.content.Context

/** 代理类
@author: wangqiang
@date: 2023/8/5
@desc: 使用KKRobust 必须要实现的接口
 */
interface IRobustDelegate {
    /**
     * 设置项-各个App可以独立定制，
     * 需要确保getPatchesInfoImplClassFullName返回的包名是和robust.xml配置项patchPackname保持一致，
     * 而且类名必须是：PatchesInfoImpl
     * eg -> com.hjq.robust.test.PatchesInfoImpl
     */
    fun getPatchesInfoImplClassFullName(): String

    /**
     * app 版本号
     */
    fun getAppVersionName(context: Context): String
}
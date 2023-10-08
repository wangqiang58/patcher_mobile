package com.freebrio.robustdemo

import android.app.Application

/**
@author: wangqiang
@date: 2023/8/5
@desc:
 */
class StudyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        val patch = PatchBean("3.1.0", "xxxx", "https://ucanos.xdf.cn/troy/android/tbs_core_046421_20230421111403_nolog_fs_obfs_arm64-v8a_release.tbs")
//        val patch = PatchBean("3.1.0", "xxxx", "http://192.168.0.107:8080/file")
//        StudyRobust.init(this, patch, "com.freebrio.robustdemo")
    }
}
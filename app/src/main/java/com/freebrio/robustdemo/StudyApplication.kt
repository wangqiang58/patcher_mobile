package com.freebrio.robustdemo

import android.app.Application
import android.util.Log
import com.meituan.robust.Patch
import com.meituan.robust.PatchExecutor
import com.meituan.robust.RobustCallBack
import com.xdf.studypatch.facade.StudyRobust
import com.xdf.studypatch.model.PatchBean

/**
@author: wangqiang
@date: 2023/8/5
@desc:
 */
class StudyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val patch = PatchBean("1.0", "xxxx", "http://192.168.0.107:8080/file")
        StudyRobust.init(this, patch, "com.freebrio.robustdemo")
        loadPatch()
    }

    private fun loadPatch() {
        PatchExecutor(this, PatchManipulateImp(), object : RobustCallBack {
            override fun onPatchListFetched(
                result: Boolean,
                isNet: Boolean,
                patches: MutableList<Patch>?
            ) {

            }

            override fun onPatchFetched(result: Boolean, isNet: Boolean, patch: Patch?) {
                Log.d("swt", "onPatchFetched")
            }

            override fun onPatchApplied(result: Boolean, patch: Patch?) {
                Log.d("swt", "onPatchApplied")
            }

            override fun logNotify(log: String?, where: String?) {
                Log.d("swt", "logNotify")

            }

            override fun exceptionNotify(throwable: Throwable?, where: String?) {
                Log.d("swt", "exceptionNotify")
            }

        }).start()
    }
}
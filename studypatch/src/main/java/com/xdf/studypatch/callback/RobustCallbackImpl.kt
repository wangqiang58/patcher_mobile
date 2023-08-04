package com.xdf.studypatch.callback

import com.meituan.robust.RobustCallBack
import com.xdf.studypatch.manager.RobustManager
import com.xdf.studypatch.util.PatchHistoryUtil

/**
@author: wangqiang
@date: 2023/8/5
@desc: 补丁修复回调
 */
class RobustCallbackImpl: RobustCallBack {

    /**
     * 在补丁应用后，回调此方法
     *
     * @param result 结果
     * @param patch  补丁
     */
    override fun onPatchApplied(result: Boolean, patch: com.meituan.robust.Patch?) {
        if (!result) {
            //记录修复失败的补丁Name
            PatchHistoryUtil.putLastFixFailureVersion(patch?.name)
            //在补丁修复失败时，自动从本地删除
            RobustManager.removeLocalPatch()
        }
        RobustManager.callbackImpl?.onPatchApplied(
            patchVersion = patch?.name,
            patchPath = patch?.localPath,
            result = result
        )
    }

    /**
     * 获取补丁列表后，回调此方法
     *
     * @param result 结果
     * @param patches 补丁们
     */
    override fun onPatchListFetched(
        result: Boolean,
        isNet: Boolean,
        patches: MutableList<com.meituan.robust.Patch>?
    ) {
        RobustManager.callbackImpl?.logNotify(
            "onPatchListFetched result is $result, isNet is $isNet,patches is ${patches.toString()}",
            "onPatchListFetched"
        )
    }

    /**
     * 在获取补丁后，回调此方法
     *
     * @param result 结果
     * @param patch  补丁
     */
    override fun onPatchFetched(result: Boolean, isNet: Boolean, patch: com.meituan.robust.Patch?) {
        RobustManager.callbackImpl?.onPatchFetched(patch?.name, patch?.localPath, result)
    }

    /**
     * 日志
     */
    override fun logNotify(log: String?, where: String?) {
        RobustManager.callbackImpl?.logNotify(log, where)
    }

    /**
     * 异常
     */
    override fun exceptionNotify(throwable: Throwable?, where: String?) {
        RobustManager.callbackImpl?.exceptionNotify(throwable, where)
    }
}
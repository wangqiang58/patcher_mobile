package com.xdf.studypatch.callback

/**
@author: wangqiang
@date: 2023/8/5
@desc: 将热修复整个事件的回调进行包装，暴露给业务方
 */
interface IRobustCallback {
    /**
     * 验证补丁文件md5是否一致
     * 如果不存在，则动态下载
     *
     * @param patchVersion 补丁名称
     * @param patchPath 补丁路径
     * @return 校验结果
     */
    fun verifyPatch(patchVersion: String?, patchPath: String?, result: Boolean,localSign:String?,remoteSign:String?)

    /**
     * 努力确保补丁文件存在，验证md5是否一致。
     * 如果不存在，则动态下载
     *
     * @param patchVersion 补丁版本
     * @param patchPath 补丁路径
     * @return 是否存在
     */
    fun ensurePatchExist(patchVersion: String?, patchPath: String?, result: Boolean)

    /**
     * 在补丁应用后，回调此方法
     *
     * @param result 结果
     * @param patchVersion 补丁版本
     * @param patchPath 补丁路径
     */
    fun onPatchApplied(patchVersion: String?, patchPath: String?, result: Boolean)

    /**
     * 在获取补丁后，回调此方法
     *
     * @param result 结果
     * @param patchVersion 补丁版本
     * @param patchPath 补丁路径
     */
    fun onPatchFetched(patchVersion: String?, patchPath: String?, result: Boolean)

    /**
     * 日志
     */
    fun logNotify(log: String?, where: String?)

    /**
     * 异常
     */
    fun exceptionNotify(throwable: Throwable?, where: String?)
}
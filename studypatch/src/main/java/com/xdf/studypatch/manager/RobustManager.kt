package com.xdf.studypatch.manager

import android.content.Context
import com.xdf.studypatch.callback.IRobustDelegate
import com.xdf.studypatch.callback.PatchManipulateImp
import com.meituan.robust.PatchExecutor
import com.xdf.studypatch.callback.IRobustCallback
import com.xdf.studypatch.callback.RobustCallbackImpl
import com.xdf.studypatch.model.PatchBean
import com.xdf.studypatch.util.FileUtils
import com.xdf.studypatch.util.PatchHistoryUtil
import java.io.File

/**
@author: wangqiang
@date: 2023/8/5
@desc:
 */
object RobustManager {

    /**
     * 配置信息等的代理
     */
    private var patchDelegate: IRobustDelegate? = null

    /**
     * 补丁加载、校验整体流程中各个环节的回调
     */
    internal var callbackImpl: IRobustCallback? = null

    /**
     * application context的引用，便于内部使用
     */
    lateinit var applicationContext: Context


    /**
     * 在应用初始化阶段激活总管，同时加载本地的补丁。
     * 越早越好
     */
    fun init(
        applicationContext: Context,
        delegate: IRobustDelegate,
        callback: IRobustCallback?
    ) {
        RobustManager.applicationContext = applicationContext
        patchDelegate = delegate
        callbackImpl = callback
        PatchHistoryUtil.init(applicationContext)
    }

    internal fun getPatchDelegate(): IRobustDelegate {
        if (patchDelegate == null) {
            throw NullPointerException("patchDelegate can not be null")
        }
        return patchDelegate!!
    }

    /**
     * 是否需要下载该补丁
     */
    @JvmStatic
    fun needDownload(patchVersion: String): Boolean {
        if (patchVersion.isEmpty()) {
            callbackImpl?.logNotify(
                "The param patchVersion is empty, unable to download",
                " needDownload"
            )
            return false
        }

        val localFailureVersion = PatchHistoryUtil.lastFixFailureVersion()
        if (localFailureVersion.isNotEmpty() && localFailureVersion == patchVersion) {
            callbackImpl?.logNotify(
                "The patch $patchVersion last fix failure, unable to download",
                " needDownload"
            )
            return false
        }

        val localVersion = PatchHistoryUtil.localPatchVersion()
        if (localVersion.isEmpty()) {
            callbackImpl?.logNotify(
                "No patches locally, need to download",
                " needDownload"
            )
            return true
        }
        val value = localVersion != patchVersion
        return if (value) {
            callbackImpl?.logNotify(
                "Local patch is ${localVersion}, Need to download patch is $patchVersion",
                " needDownload"
            )
            true
        } else {
            callbackImpl?.logNotify(
                "Patch $patchVersion already exists locally, no need to load",
                " needDownload"
            )
            false
        }
    }

    /**
     * 判断当前补丁是否需要加载
     */
    private fun needLoad(patchVersion: String?): Boolean {
        if (patchVersion.isNullOrBlank()) {
            callbackImpl?.logNotify("patchVersion is Empty", "needLoad")
            return false
        }

        //判断此补丁是否是上次修复失败的补丁
        if (patchVersion == PatchHistoryUtil.lastFixFailureVersion()) {
            callbackImpl?.logNotify(
                "Patch $patchVersion failed last time",
                "needLoad"
            )
            return false
        }
        //无特殊情况、需要加载补丁
        callbackImpl?.logNotify("needLoad", "Patches need to be loaded")
        return true
    }

    /**
     * 在应用冷启动时调用，默认加载存储在本地的补丁
     */
    @JvmStatic
    fun loadLocalPatch(applicationContext: Context): Boolean {
        val localPatchVersion = PatchHistoryUtil.localPatchVersion()
        if (localPatchVersion.isEmpty()) {
            callbackImpl?.logNotify(
                "No patches locally",
                "loadLocalPatch"
            )
            return false
        }
        // 校验patch version code 和 APP version code是否一致
        patchDelegate?.getAppVersionName(applicationContext)?.let { appVersionName ->
            //删除低版本patch 比如patchcode 6.3.0_all_1 appVerson 6.3.0 当appversin升级到 6.3.1时，需要删除patchcode patchcode 6.3.0_all_1
            if (!localPatchVersion.contains(appVersionName)) {
                // 删除本地补丁
                removeLocalPatch()
                callbackImpl?.logNotify(
                    "The local patch has expired, delete",
                    "loadLocalPatch"
                )
            }
        }

        val patchBean = PatchBean(
            patchVersion = PatchHistoryUtil.localPatchVersion(),
            signed = PatchHistoryUtil.localPatchSign()
        )
        callbackImpl?.logNotify(
            "Load local patch ${patchBean.patchVersion}",
            "loadLocalPatch"
        )
        return loadPatch(
            downloadPatch = File(getFullPatchLocalPath(patchBean.patchVersion)),
            patchBean = patchBean,
            needCopy = false
        )
    }

    /**
     * 删除本地记录的补丁
     */
    @JvmStatic
    fun removeLocalPatch() {
        val localVersion = PatchHistoryUtil.localPatchVersion()
        if (localVersion.isEmpty()) {
            return
        }
        //删除补丁文件
        val file = File(getFullPatchLocalPath(localVersion))
        if (file.exists()) {
            file.delete()
            callbackImpl?.logNotify(
                "Remove local patch file",
                "removeLocalPatch"
            )
        }
        //清空记录的补丁名称和签名
        PatchHistoryUtil.putLocalPatchVersion("")
        PatchHistoryUtil.putLocalPatchSign("")
        callbackImpl?.logNotify(
            "Remove local patch version and signed",
            "removeLocalPatch"
        )
    }

    /**
     * 加载补丁
     * @param downloadPatch 下载的补丁文件
     * @param patchBean 补丁信息
     * @param  needCopy 是否需要将补丁复制到指定目录
     */
    @JvmOverloads
    @JvmStatic
    fun loadPatch(downloadPatch: File, patchBean: PatchBean, needCopy: Boolean = true): Boolean {
        if (!downloadPatch.exists()) {
            callbackImpl?.logNotify(
                " downloadPatch $downloadPatch does not exists, the path is ${downloadPatch.path}",
                "loadPatch"
            )
            return false
        }

        if (patchBean.patchVersion.isNullOrEmpty()) {
            callbackImpl?.logNotify(
                "patchBean.patchVersion ${patchBean.patchVersion} is null or empty",
                "loadPatch"
            )
            return false
        }
        if (needLoad(patchBean.patchVersion)) {
            //1、把补丁文见拷贝到目标路径,存储起来
            // 拼接.jar是为了保持文件格式为jar，且适配Patch.getLocalPatch()
            if (needCopy) {
                val targetDir = getFullPatchLocalPath(patchBean.patchVersion)
                val isSuccess = FileUtils.copyFile(downloadPatch.path, targetDir)
                if (!isSuccess) {
                    callbackImpl?.logNotify(
                        "copy download patch to local patch error, no patch execute in path ${downloadPatch.path}",
                        "loadPatch"
                    )
                    return false
                }
            }
            //2、加载补丁
            PatchExecutor(
                applicationContext,
                //补丁加载回调
                PatchManipulateImp(patchBean),
                //修复回调
                RobustCallbackImpl()
            ).start()
            if (patchBean.patchVersion != PatchHistoryUtil.localPatchVersion()) {
                //加载的文件不存在本地，则执行3和4
                // 清除旧数据，填充新数据
                //3、为了防止废弃补丁占用内存空间，清除本地旧补丁
                removeLocalPatch()
                //4、在本地保存补丁名称和校验签名
                PatchHistoryUtil.putLocalPatchVersion(patchBean.patchVersion)
                PatchHistoryUtil.putLocalPatchSign(patchBean.signed)
            }
            return true
        } else {
            return false
        }
    }

    /**
     * 完整补丁路径
     * 包含.jar
     */
    private fun getFullPatchLocalPath(patchVersion: String?): String {
        return "${getPatchLocalPath(patchVersion)}.jar"
    }

    /**
     * 补丁路径
     * 不包含.jar
     */
    internal fun getPatchLocalPath(patchVersion: String?): String {
        return "${applicationContext.filesDir}${File.separator}robust${File.separator}${patchVersion}"
    }
}
package com.xdf.studypatch.business

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.kuaikan.library.kkpatch.callback.IRobustDelegate
import com.xdf.studypatch.downloader.IDownloadCallback
import com.xdf.studypatch.downloader.StudyDownloader
import com.xdf.studypatch.manager.RobustManager
import com.xdf.studypatch.model.PatchBean
import com.xdf.studypatch.util.FileUtils
import com.xdf.studypatch.util.PackageUtils
import com.xdf.studypatch.util.PatchHistoryUtil

/**
@author: wangqiang
@date: 2023/8/5
@desc:
 */
object StudyRobust {

    const val TAG = "StudyRobust"
    private const val SOURCE_DOWN_PATCH = "study_patch"
    private var PATCH_DIR = "${FileUtils.getSDAppPath()}/patch"
    private lateinit var PATCH_PATH: String
    private var channel: String? = null

    /**
     * @param application
     * @param patchBean:需要下载的patch
     * @param patchesInfoImplClassFullName 注意这里的robust.xml的包名一致,且结尾必须是PatchesInfoImpl
     */
    fun init(application: Application, patchBean: PatchBean, patchesInfoImplClassFullName: String) {
        //1、在应用冷启动时初始化
        RobustManager.init(
            application,
            KKRobustDelegateImpl(patchesInfoImplClassFullName),
            null
        )
        PATCH_PATH =
            "${FileUtils.getSDAppPath()}/patch/patch_${PackageUtils.getVersionCode(application.applicationContext)}"
        //2、加载补丁
        localPatch(application.applicationContext, patchBean)
    }

    /**
     * @description 加载patch
     * @param
     * @return
     * @author hujianqiang
     * @time 2021/8/5 11:20 上午
     */
    private fun localPatch(context: Context, patchBean: PatchBean) {
        val localPatchVersion = PatchHistoryUtil.localPatchVersion()
        val loadPatchSign = PatchHistoryUtil.localPatchSign()
        val remoteVersion = patchBean.patchVersion
        val remoteSign = patchBean.signed
        if (!TextUtils.isEmpty(localPatchVersion) && !TextUtils.isEmpty(loadPatchSign)) {
            //比较本地patch包合远端是否一致
            if (localPatchVersion == remoteVersion && loadPatchSign == remoteSign) {//如果一致则加载本地包，否则加载远端包
                Log.d(
                    TAG,
                    "加载本地补丁包，response = $patchBean local localPatchVersion=${localPatchVersion} loadPatchSign=${loadPatchSign}"
                )
                RobustManager.loadLocalPatch(context)
            } else {//加载远端patch
                Log.d(TAG, "加载远端补丁包，远端包和本地patch不一致")
                RobustManager.removeLocalPatch()//加载remote之前先删除本地数据
                loadRemote(RobustManager.applicationContext, patchBean)
            }
        } else {//加载远端包
            Log.d(TAG, "加载远端补丁包，且本地没有任何补丁记录")
            loadRemote(RobustManager.applicationContext, patchBean)
        }

    }


    private fun loadRemote(context: Context, response: PatchBean) {
        val remoteVersion = response.patchVersion
        if (TextUtils.isEmpty(remoteVersion)) {
            return
        }
        if (!RobustManager.needDownload(remoteVersion!!)) {
            //无需下载,
            return
        }
        downloadPatch(context, response)
    }

    /**
     *下载patch包
     */
    private fun downloadPatch(context: Context, response: PatchBean) {
        val downloadId = 578943
        if (response.patchUrl.isNullOrEmpty()) {
            return
        }
        //在下载之前，先清空数据之前下载的数据以及清空任务，因为id一样会导致参数设置不生效，取数据库里面内容
        val downloader: StudyDownloader = StudyDownloader(context, object : IDownloadCallback {

            override fun onDownloadStart() {

            }

            override fun onDownloadSuccess(patchPath: String) {
            }

            override fun onDownloadError() {

            }

        })
        downloader.startDownload(response.patchUrl, "")

    }

    /**
     * 下载上报
     */
    private fun trackDownload(status: String, url: String) {

    }

    /**
     * KKRobust -热修复定制信息的注入
     */
    class KKRobustDelegateImpl(private var patchesInfoImplClassFullName: String) :
        IRobustDelegate {
        /**
         * 注意这里的robust.xml的包名一致,且结尾必须是PatchesInfoImpl
         */
        override fun getPatchesInfoImplClassFullName(): String {
            return patchesInfoImplClassFullName
        }

        override fun getAppVersionName(context: Context): String {
            return PackageUtils.getVersionName(context) ?: ""
        }
    }


}
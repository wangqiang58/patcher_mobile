package com.xdf.studypatch.callback

import android.content.Context
import com.meituan.robust.Patch
import com.meituan.robust.PatchManipulate
import com.xdf.studypatch.manager.RobustManager
import com.xdf.studypatch.util.FileUtils
import com.xdf.studypatch.util.PatchCoderUtil
import java.io.File

/**
 *
@author: wangqiang
@date: 2023/8/5
@desc:
 * Robust 暴露给业务方的几个接口
 * 1、获取补丁列表（定制逻辑：单次只下发一个补丁，采用覆盖安装的逻辑，一个补丁包实现目标版本的所有bug fix）
 * 2、判断补丁是否存在
 * 3、校验补丁状态
 *
 * 原注释⬇
 *
 * We recommend you rewrite your own PatchManipulate class ,adding your special patch Strategy，in the demo we just load the patch directly
 * Pay attention to the difference of patch's LocalPath and patch's TempPath
 * We recommend LocalPath store the origin patch.jar which may be encrypted,while TempPath is the true runnable jar
 * <br>
 * 我们推荐继承PatchManipulate实现你们App独特的A补丁加载策略，其中setLocalPath设置补丁的原始路径，这个路径存储的补丁是加密过得，setTempPath存储解密之后的补丁，是可以执行的jar文件
 * setTempPath设置的补丁加载完毕即刻删除，如果不需要加密和解密补丁，两者没有啥区别
 *
 */


class PatchManipulateImp(private val patchBean: com.xdf.studypatch.model.PatchBean) : PatchManipulate() {

    override fun fetchPatchList(context: Context?): MutableList<Patch> {
        val list = mutableListOf<com.meituan.robust.Patch>()
        list.add(createPatch())
        return list
    }

    override fun verifyPatch(context: Context?, patch: Patch?): Boolean {
        patch ?: return false
        val patchFile = File(patch.localPath)
        //patch文件是否存在
        if (!patchFile.exists()) {
            RobustManager.callbackImpl?.exceptionNotify(Throwable("no patch execute in path ${patch.localPath} patchFile is not exit"), "PatchManipulateImp verifyPatch 46")
            RobustManager.callbackImpl?.ensurePatchExist(patch.name, patch.localPath, false)
            return false
        }

        val localSign= PatchCoderUtil.encodeMd5ToBase64(patchFile)
        //校验文件MD5
        if (patchBean.signed== localSign) {
            //放到app的私有目录
            patch.tempPath =
                context?.cacheDir.toString() + File.separator + "robust" + File.separator + "patch"
            //in the sample we just copy the file
            val isSuccess=  FileUtils.copyFile(patch.localPath, patch.tempPath)
            if (!isSuccess){
                RobustManager.callbackImpl?.exceptionNotify(
                    Throwable("copy source patch to local patch error, no patch execute in path " + patch.tempPath),
                    "PatchManipulateImp verifyPatch 62"
                )
                RobustManager.callbackImpl?.verifyPatch(patch.name, patch.localPath, false,localSign,patchBean.signed)
                return false
            }
            RobustManager.callbackImpl?.verifyPatch(patch.name, patch.localPath, true,localSign,patchBean.signed)
            return true
        } else {
            RobustManager.callbackImpl?.exceptionNotify(
                Throwable("sign is not math localSign=${localSign} remoteSign=${patchBean.signed}"),
                "PatchManipulateImp verifyPatch 72"
            )
            RobustManager.callbackImpl?.verifyPatch(patch.name, patch.localPath, false,localSign,patchBean.signed)
            return false
        }

    }
    /**
     * 原注释：you may download your patches here, you can check whether patch is in the phone
     *
     * 包装path一定存在我们可以在最开始的时候做下载，不在此处
     * @param patch
     * @return 是否存在
     */
    override fun ensurePatchExist(patch: com.meituan.robust.Patch?): Boolean {
        return true
    }


    /**
     * 将业务方传递的数据包装成Robust接收的补丁包Patch
     */
    private fun createPatch(): Patch {
        val patch = com.meituan.robust.Patch()
        // 补丁名称
        patch.name = patchBean.patchVersion

        // 补丁文件的物理存储路径
        patch.localPath = RobustManager.getPatchLocalPath(patchBean.patchVersion)

        // setPatchesInfoImplClassFullName 设置项各个App可以独立定制，
        // 需要确保的是setPatchesInfoImplClassFullName设置的包名是和xml配置项patchPackname保持一致，
        // 而且类名必须是：PatchesInfoImpl
        // **请注意这里的设置**
        patch.patchesInfoImplClassFullName =
            RobustManager.getPatchDelegate().getPatchesInfoImplClassFullName()
        return patch
    }
}
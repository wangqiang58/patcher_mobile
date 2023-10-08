package com.freebrio.robustdemo

import android.content.Context
import android.util.Log
import com.meituan.robust.Patch
import com.meituan.robust.PatchManipulate
import java.io.*


/**
 * introduce：here is introduce
 * author：sunwentao
 * email：wentao.sun@freebrio.com
 * data: 2021/04/23
 */
class PatchManipulateImp : PatchManipulate() {
    override fun fetchPatchList(context: Context): MutableList<Patch> {
        val patch = Patch()
        patch.name = "test"
        patch.localPath =
            context.externalCacheDir?.absolutePath + File.separator + "robust" + File.separator + "patch"
        Log.d("robust","patch localpath=${patch.localPath}")
        patch.patchesInfoImplClassFullName = "com.freebrio.robustdemo.PatchesInfoImpl"
        val list = arrayListOf<Patch>()
        list.add(patch)
        return list
    }

    override fun verifyPatch(context: Context?, patch: Patch?): Boolean {
        //do your verification, put the real patch to patch
        //放到app的私有目录

        //do your verification, put the real patch to patch
        //放到app的私有目录
        patch!!.tempPath =
            context!!.cacheDir.toString() + File.separator + "robust" + File.separator + "patch"
        //in the sample we just copy the file
        //in the sample we just copy the file
        try {
            copy(patch.localPath, patch.tempPath)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("copy source patch to local patch error, no patch execute in path " + patch.tempPath)
        }

        return true
    }

    @Throws(IOException::class)
    fun copy(srcPath: String?, dstPath: String?) {
        val src = File(srcPath)
        if (!src.exists()) {
            throw java.lang.RuntimeException("source patch does not exist ")
        }
        val dst = File(dstPath)
        if (!dst.parentFile.exists()) {
            dst.parentFile.mkdirs()
        }
        val `in`: InputStream = FileInputStream(src)
        try {
            val out: OutputStream = FileOutputStream(dst)
            try {
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            } finally {
                out.close()
            }
        } finally {
            `in`.close()
        }
    }

    override fun ensurePatchExist(patch: Patch?): Boolean {
        return true
    }
}
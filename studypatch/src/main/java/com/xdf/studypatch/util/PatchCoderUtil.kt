package com.xdf.studypatch.util

import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
@author: wangqiang
@date: 2023/8/5
@desc:
 */
object PatchCoderUtil {

    fun encodeMd5ToBase64(file: File):String?{

        val fis: InputStream
        val buffer = ByteArray(1024)
        var numRead = 0
        val md5: MessageDigest
        fis = try {
            FileInputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
        try {
            md5 = MessageDigest.getInstance("MD5")
            while (fis.read(buffer).also { numRead = it } > 0) {
                md5.update(buffer, 0, numRead)
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                fis.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return Base64Utils.encodeBase64(md5.digest())
    }

}
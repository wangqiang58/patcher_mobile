package com.xdf.studypatch.util

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log
import java.io.*

object IOUtils {
    private const val TAG = "IOUtils"

    @JvmStatic
//    @MethodDesc("安全关闭一个可关闭对象")
    fun closeQuietly(closeable: Closeable?) {
        if (closeable == null) {
            return
        }
        try {
            closeable.close()
        } catch (e: Exception) {
            //Ignore exception
        }
    }

    @JvmStatic
//    @MethodDesc("安全关闭一个cursor")
    fun closeQuietly(cursor: Cursor?) {
        if (cursor == null) {
            return
        }
        try {
            cursor.close()
        } catch (e: Exception) {
            //Ignore exception
        }

    }

    @JvmStatic
//    @MethodDesc("从流中读取字符串")
    fun readStringFromStream(inputStream: InputStream?): String {
        val bytes = readBytesFromStream(inputStream)
        return String(bytes)
    }

    @JvmStatic
//    @MethodDesc("从流中读取byte数组")
    fun readBytesFromStream(inputStream: InputStream?): ByteArray {
        if (inputStream == null) {
            return ByteArray(0)
        }

        val bos = ByteArrayOutputStream()
        try {
            val buffer =
                ByteArray(if (inputStream.available() > 4096) 4096 else inputStream.available())
            var len: Int
            while (true) {
                len = inputStream.read(buffer, 0, buffer.size)
                if (len <= 0) {
                    break
                }
                bos.write(buffer, 0, len)
            }
            return bos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeQuietly(bos)
        }
        return ByteArray(0)
    }

    @JvmStatic
//    @MethodDesc("从文件中读取byte数组")
    fun readBytesFromFile(path: String?): ByteArray {
        if (!FileUtils.isFileExists(path)) {
            return ByteArray(0)
        }
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(path)
            return readBytesFromStream(fis)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            closeQuietly(fis)
        }
        return ByteArray(0)
    }

    @JvmStatic
//    @MethodDesc("从assets文件中读取字符串")
    fun readStringFromAsset(context: Context, path: String): String {
        var bis: BufferedInputStream? = null
        try {
            bis = BufferedInputStream(context.assets.open(path))
            val buffer = ByteArray(1024)
            val bos = ByteArrayOutputStream()
            var len: Int
            while (true) {
                len = bis.read(buffer, 0, buffer.size)
                if (len <= 0) {
                    break
                }
                bos.write(buffer, 0, len)
            }
            return String(bos.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeQuietly(bis)
        }
        return ""
    }

    @JvmStatic
//    @MethodDesc("读取assets文件中的第一行")
    fun readFirstLineFromAsset(context: Context, path: String?): String {
        var br: BufferedReader? = null
        try {
            br = BufferedReader(InputStreamReader(context.assets.open(path!!)))
            return br.readLine()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeQuietly(br)
        }
        return ""
    }

    @JvmStatic
//    @MethodDesc("将字符串写入文件中")
    fun writeString2File(path: String?, content: String?): Boolean {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(content)) {
            return false
        }
        return writeBytes2File(path, content!!.toByteArray())
    }

    @JvmStatic
//    @MethodDesc("将byte数组写入文件")
    fun writeBytes2File(path: String?, data: ByteArray?): Boolean {
        if (path.isNullOrEmpty() || data == null || data.isEmpty()) {
            return false
        }
        var fos: FileOutputStream? = null
        var isSuccessful = false
        val tmpFile = File(path + "_" + System.currentTimeMillis())
        try {
            fos = FileOutputStream(tmpFile)
            fos.write(data)
            isSuccessful = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeQuietly(fos)
        }
        return isSuccessful && tmpFile.renameTo(File(path))
    }

    @JvmStatic
//    @MethodDesc("将InputStream写入文件")
    fun writeStream2File(
        path: String?,
        inStream: InputStream?,
        needCloseInputStream: Boolean
    ): Boolean {
        if (path.isNullOrEmpty() || (inStream == null)) {
            return false
        }
        var fos: FileOutputStream? = null
        var isSuccessful = false
        val tmpFile = File(path + "_" + System.currentTimeMillis())
        try {
            fos = FileOutputStream(tmpFile)
            val buff = ByteArray(1024)
            var len = inStream.read(buff)
            while (len != -1) {
                fos.write(buff, 0, len)
                len = inStream.read(buff)
            }
            fos.flush()
            isSuccessful = true
        } catch (e: Exception) {
            Log.e(TAG, "${e.printStackTrace()}")
        } finally {
            if (needCloseInputStream) {
                closeQuietly(fos)
            }
        }
        return isSuccessful && tmpFile.renameTo(File(path))
    }

    @JvmStatic
//    @MethodDesc("从文件中读取字符串")
    fun readStringFromFile(path: String?): String {
        if (!FileUtils.isFileExists(path)) {
            return ""
        }
        var br: BufferedReader? = null
        try {
            val sb = StringBuilder()
            br = BufferedReader(FileReader(path))
            var readline: String
            while (true) {
                readline = br.readLine() ?: break
                sb.append(readline)
            }
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeQuietly(br)
        }
        return ""
    }

    @JvmStatic
//    @MethodDesc("安全关闭数据库事务")
    fun endDatabaseTransactionQuietly(db: SQLiteDatabase?) {
        if (db == null) {
            return
        }
        try {
            if (db.inTransaction()) {
                db.endTransaction()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
//    @MethodDesc("将输入流中的数据copy到输出流")
    fun copyStream(inStream: InputStream?, outStream: OutputStream?): Boolean {
        if (inStream == null || outStream == null) {
            return false
        }
        val buffer = ByteArray(1024)
        try {
            while (true) {
                val len = inStream.read(buffer, 0, buffer.size)
                if (len <= 0) {
                    break
                }
                outStream.write(buffer, 0, len)
            }
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }
}

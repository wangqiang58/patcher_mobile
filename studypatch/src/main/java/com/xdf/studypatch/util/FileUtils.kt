package com.xdf.studypatch.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

object FileUtils {
    private const val TAG = "FileUtil"

    private var SD_APP_MAIN_PATH: File? = null
    private var SD_ROOT_MAIN_PATH: File? = null

    private var EXTERNAL_CACHE_DIR: File? = null

    @JvmStatic
    fun init(context: Context, baseDir: String) {
        var cacheFile = context.getExternalFilesDir(null)
        if (cacheFile != null) {
            cacheFile.mkdirs()
            if (!cacheFile.exists()) {
                cacheFile = Environment.getExternalStorageDirectory()
            }
        } else {
            cacheFile = Environment.getExternalStorageDirectory()
        }
        EXTERNAL_CACHE_DIR = context.getExternalCacheDir()
        SD_APP_MAIN_PATH = File(cacheFile, baseDir)
        SD_ROOT_MAIN_PATH = File(Environment.getExternalStorageDirectory(), baseDir)

        Log.d(TAG, " init: SD_APP_MAIN_PATH = " + SD_APP_MAIN_PATH!!)

        Log.d(TAG, " init: SD_ROOT_MAIN_PATH = " + SD_ROOT_MAIN_PATH!!)
        Log.d(TAG, " init: EXTERNAL_CACHE_DIR = " + EXTERNAL_CACHE_DIR!!)
    }

    @JvmStatic
    //"判断sdcard是否可用"
    fun isSDCardAvailable(): Boolean {
        try {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
    @JvmStatic
    @Nullable
//    @MethodDesc("获取应用的cache存储路径")
    fun getExternalCacheDir(): File? {
        return EXTERNAL_CACHE_DIR
    }
    @JvmStatic
    @Nullable
    //"获取应用的sdcard存储路径"
    fun getSDAppPath(): File? {
        return SD_APP_MAIN_PATH
    }

    @JvmStatic
//    @MethodDesc("删除文件或文件夹")
    fun deleteFile(filePath: String?): Boolean {
        if (filePath.isNullOrBlank()) {
            return false
        }
        val file = File(filePath)
        if (!file.exists()) {
            return false
        }
        return if (file.isDirectory) {
            deleteDirectory(file)
        } else {
            file.delete()
        }
    }

    @JvmStatic
    private fun deleteDirectory(dirFile: File): Boolean {
        if (!dirFile.exists() || !dirFile.isDirectory) {
            return false
        }
        return if (clearDirectory(dirFile.absolutePath)) {
            dirFile.delete()
        } else false
    }

    @JvmStatic
//    @MethodDesc("清空文件夹，但不删除文件夹")
    fun clearDirectory(dirPath: String?): Boolean {
        if (dirPath.isNullOrBlank()) {
            return false
        }
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) {
            return false
        }
        val files = dir.listFiles() ?: return true

        //遍历删除文件夹下的所有文件(包括子目录)
        for (i in files.indices) {
            if (!deleteFile(files[i].absolutePath)) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    //"判断文件是否存在"
    fun isFileExists(fileName: String?): Boolean {
        return if (!fileName.isNullOrBlank()) {
            isFileExists(File(fileName))
        } else {
            false
        }
    }

    @JvmStatic
    //"判断文件是否存在"
    fun isFileExists(file: File?): Boolean {
        return file?.exists() ?: false
    }

    @JvmStatic
   //"判断文件是否不存在或者为空"
    fun isFileEmpty(file: File?): Boolean {
        if (file == null || !file.exists()) {
            return true
        }
        return if (file.isDirectory) file.listFiles().isNullOrEmpty() else file.length() == 0L
    }

    @JvmStatic
    //"判断文件是否不存在或者为空"
    fun isFileEmpty(path: String?): Boolean {
        return path.isNullOrBlank() || isFileEmpty(File(path))
    }

    @JvmStatic
    //"判断文件是否存在或者非空"
    fun isFileExistsAndNotEmpty(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && isFileExistsAndNotEmpty(File(fileName))
    }

    @JvmStatic
    //"判断文件是否存在或者非空"
    fun isFileExistsAndNotEmpty(file: File?): Boolean {
        return if (file != null) {
            file.exists() && file.length() > 0
        } else {
            false
        }
    }

    @JvmStatic
    //"判断文件路径是否为目录"
    fun isDirectory(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && isDirectory(File(fileName))
    }

    @JvmStatic
    //"判断文件是否为目录"
    fun isDirectory(file: File?): Boolean {
        return file?.isDirectory ?: false
    }

    @JvmStatic
    //"判断文件是否为文件"
    fun isFile(file: File?): Boolean {
        return file?.isFile ?: false
    }

    @JvmStatic
    //"获取文件扩展名"
    fun getFileExt(path: String?): String {
        if (TextUtils.isEmpty(path)) {
            return ""
        }
        try {
            val fileName = File(path).name
            val index = fileName.lastIndexOf(".")
            return if (index >= 0) {
                fileName.substring(index)
            } else ""
        } catch (e: Exception) {

        }
        return ""

    }

    @JvmStatic
    //"判断文件路径是否为文件"
    fun isFile(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && isFile(File(fileName))
    }

    /**
     * @return true if the file is exists or created
     */
    @JvmStatic
    //"如果文件夹不存在，则创建文件"
    fun createFolderIfNotExists(file: File?): Boolean {
        if (file == null) {
            return false
        }

        if (isFileExists(file) && isDirectory(file)) {
            return true
        }
        return file.mkdirs()
    }

    /**
     * @return true if the file is exists or created
     */
    @JvmStatic
   //"如果文件夹不存在，则创建文件"
    fun createFolderIfNotExists(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && createFolderIfNotExists(File(fileName))
    }

    /**
     * @return true if the file is exists or created
     */
    @JvmStatic
    //"如果文件不存在，则创建文件"
    fun createFileIfNotExists(file: File?): Boolean {
        if (file == null) {
            return false
        }
        if (isFileExists(file) && isFile(file)) {
            return true
        }
        val parentFile = file.parentFile
        if (!createFolderIfNotExists(parentFile)) {
            return false
        }
        try {
            return file.createNewFile()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * @return true if the file is exists or created
     */
    @JvmStatic
   //"如果文件不存在，则创建文件"
    fun createFileIfNotExists(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && createFileIfNotExists(File(fileName))
    }

    /**
     * @return the file name without dir path
     */
    @JvmStatic
    @NonNull
   //"获取文件路径中但文件名")
    fun getFileName(path: String?): String {
        if (path.isNullOrBlank()) {
            return ""
        }
        val index = path.lastIndexOf('/')
        return if (index > -1) path.substring(index + 1) else ""
    }

    @JvmStatic
   //"获取默认sdcard存储路径")
    fun getSDRootMainPath(): String {
        return SD_ROOT_MAIN_PATH!!.absolutePath
    }

    @JvmStatic
   //"复制文件")
    fun copyFile(src: File?, target: File?): Boolean {
        if (src == null || target == null) {
            return false
        }
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = FileInputStream(src)
            if (!target.parentFile.exists() && !target.parentFile.mkdirs()) {
                return false
            }
            outputStream = FileOutputStream(target)
            return IOUtils.copyStream(inputStream, outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            IOUtils.closeQuietly(inputStream)
            IOUtils.closeQuietly(outputStream)
        }
        return false
    }

    /**
     * 复制文件，添加输入流为源文件重载方法
     * @param inStream InputStream?
     * @param target File?
     * @return Boolean
     */
    @JvmStatic
    fun copyFile(inStream: InputStream, target: File): Boolean {
        var isSuccess = false
        var out: OutputStream? = null
        try {
            out = FileOutputStream(target)
            copy(inStream, out)
            out.flush()
            isSuccess = true
        } catch (e: java.lang.Exception) {

        } finally {
            if (out != null) {
                try {
                    out.close()
                } catch (ignored: java.lang.Exception) {
                } finally {
                    out = null
                    return isSuccess
                }
            }
            return isSuccess
        }
    }

    private const val BYTES_IN_KILO = 1024

    private fun copy(inStream: InputStream, outStream: OutputStream) {
        val buffer = ByteArray(BYTES_IN_KILO)
        var length: Int
        while (inStream.read(buffer).also { length = it } > 0) {
            outStream.write(buffer, 0, length)
        }
    }

    @JvmStatic
    //"复制文件")
    fun copyFile(srcPath: String?, targetPath: String): Boolean {
        return !srcPath.isNullOrBlank() && !targetPath.isNullOrBlank() && copyFile(File(srcPath), File(targetPath))
    }

    @JvmStatic
    //"从流中复制文件")
    fun copyStream2File(inStream: InputStream?, target: File?): Boolean {
        if (inStream == null || target == null) {
            return false
        }
        var out: OutputStream? = null
        try {
            out = FileOutputStream(target)
            return IOUtils.copyStream(inStream, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            IOUtils.closeQuietly(out)
        }
        return false
    }

    /**
     * 获取/data/data/package_name/cache/目录
     *
     * @return
     */
    @JvmStatic
    //("获取应用内部存储缓存目录")
    fun getAppCacheDir(context: Context): String {
        return context.cacheDir.absolutePath
    }

    @JvmStatic
    //("获取应用sdcard缓存目录")
    fun getAppSdcardCacheDir(context: Context): String? {
        return context.externalCacheDir?.absolutePath
    }

    @JvmStatic
    //("获取应用内部存储目录")
    fun getAppFilesDir(context:Context,folder: String): String {
        val f = File(context.filesDir, folder)
        if (!f.exists()) {
            f.mkdirs()
        }
        return f.absolutePath
    }

    @JvmStatic
    //("判断文件是否为gif文件")
    fun isGifFile(file: File?): Boolean {
        if (file == null) {
            return false
        }
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val tmp = ByteArray(10)
            fis.read(tmp)
            val data = ByteBuffer.wrap(tmp).asReadOnlyBuffer()
            data.position(0)
            data.order(ByteOrder.LITTLE_ENDIAN)
            var header = ""
            for (i in 0..5) {
                header += (data.get() and 0xFF.toByte()).toChar()
            }
            return header.startsWith("GIF")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            IOUtils.closeQuietly(fis)
        }
        return false
    }

    @JvmStatic
    //("获取文件大小")
    fun getFileSize(file: File?): Long {
        return if (file == null || !file.exists()) 0 else file.length()
    }

    @JvmStatic
    //("获取文件大小")
    fun getFileSize(path: String?): Long {
        if (path.isNullOrBlank()) {
            return 0
        }
        return getFileSize(File(path))
    }

    @JvmStatic
//    "判断文件是否可读")
    fun canRead(path: String?): Boolean {
        if (path.isNullOrBlank()) {
            return false
        }
        val f = File(path)
        return f.exists() && f.canRead()
    }

    @JvmStatic
    //("获取文件后缀名")
    fun getFileSuffix(path: String?): String {
        if (path.isNullOrBlank()) {
            return ""
        }

        val fileName = File(path).name
        val index = fileName.lastIndexOf(".")
        return if (index >= 0) {
            fileName.substring(index)
        } else ""
    }


    /**
     * @return SDcard总大小，如果SDcard当前没有挂载，则返回-1
     */
    //("获取SDcard总大小")
    @JvmStatic
    fun getSdCardTotalSpace(): Long {
        val state = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED == state) {
            Environment.getExternalStorageDirectory().totalSpace
        } else -1
    }

    /**
     * @return SDcard剩余空间，如果SDcard当前没有挂载，则返回-1
     */
   //"获取Sdcard剩余空间")
    @JvmStatic
    fun getSdCardFreeSpace(): Long {
        val state = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED == state) {
            Environment.getExternalStorageDirectory().freeSpace
        } else -1
    }

    /**
     *
     */
    //"获取应用私有目录剩余控件")
    @JvmStatic
    fun getFreeDataSpace(context: Context): Long {
        return context.filesDir.freeSpace
    }



    /**
     * 文件夹是否有文件
     *
     * @param folder 文件夹
     * @return 文件夹存在并且有内容
     */
    @JvmStatic
    fun hasChildFiles(folder: File?): Boolean {
        if (folder != null && folder.exists()) {
            val files = folder.listFiles()
            return files != null && files.isNotEmpty()
        }
        return false
    }

    /**
     * 获取存储路径
     * @param context Context
     * @param dir String
     * @return String?
     */
    @JvmStatic
    fun getStoragePath(context: Context, dir: String): String? {
        var file = if (isSDCardAvailable()) {
            File(SD_APP_MAIN_PATH.toString() + File.separator + dir)
        } else {
            context.getDir(dir, Context.MODE_PRIVATE)
        }
        if (!file.exists()) {
            try {
                file.mkdirs()
            } catch (e: SecurityException) {

            }
        }
        return file.absolutePath
    }




    @JvmStatic
    fun getFilesDir(context: Context,folder: String?): String? {
        val f = File(context.filesDir, folder)
        if (!f.exists()) {
            f.mkdirs()
        }
        return f.absolutePath
    }

    @JvmStatic
    fun writeToFileFromStringSafely(path: String, content: String): Boolean {
        val tmpFile = File("$path.bak")
        if (!writeToFileFromString(tmpFile.absolutePath, content)) {
            return false
        }
        tmpFile.renameTo(File(path))
        return true
    }

    /**
     * 将String数据存为文件
     */
    @JvmStatic
    fun writeToFileFromString(path: String?, content: String): Boolean {
        var stream: BufferedOutputStream? = null
        val file: File
        try {
            file = File(path)
            createFileIfNotExists(file)
            val fstream = FileOutputStream(file)
            stream = BufferedOutputStream(fstream)
            stream.write(content.toByteArray())
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            IOUtils.closeQuietly(stream)
        }
        return false
    }


    @JvmStatic
    fun saveBitmapAsFile(path: String, viewToShare: Bitmap?, quality: Int): Boolean {
        var save = false
        try {
            if (viewToShare != null && !viewToShare.isRecycled) {
                val ss = File(path)
                val fos = FileOutputStream(ss)
                save = viewToShare.compress(Bitmap.CompressFormat.JPEG, quality, fos)
                fos.flush()
                fos.close()
            }
        } catch (e: java.lang.Exception) {
            save = false
            e.printStackTrace()
        }
        return save
    }

    @JvmStatic
    fun isPicSuffix(str: String?): Boolean {
        if (TextUtils.isEmpty(str)) {
            return false
        }
        val indexOf = str!!.lastIndexOf(".")
        val lowerCaseString = str.substring(indexOf, str.length).toLowerCase()
        return lowerCaseString == ".jpg" || lowerCaseString == ".png" || lowerCaseString == ".webp"
    }
    @JvmStatic
    fun fileLengthPromot(fileLength: Long): String? {
        return if (fileLength >= 1024) {
            (fileLength / 1024).toString() + "KB"
        } else if (fileLength >= 1024 * 1024) {
            (fileLength / 1024 / 1024).toString() + "MB"
        } else {
            fileLength.toString() + "B"
        }
    }

    @JvmStatic
    fun fileLengthTransform(fileLength: Long): String? {
        return if (fileLength >= 1024 * 1024) {
            val size = fileLength.toDouble() / 1024 / 1024
            BigDecimal(size).setScale(2, RoundingMode.UP).toDouble().toString() + "MB"
        } else if (fileLength >= 1024) {
            val size = fileLength.toDouble() / 1024
            BigDecimal(size).setScale(2, RoundingMode.UP).toDouble().toString() + "KB"
        } else {
            fileLength.toString() + "B"
        }
    }
}
package com.xdf.studypatch.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.kuaikan.library.apt.annotation.MethodDesc
import com.kuaikan.library.apt.annotation.TypeDesc
import com.kuaikan.library.base.Global
import com.kuaikan.library.base.utils.IOUtils.closeQuietly
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.FileHeader
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

@TypeDesc("文件操作工具类")
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

        LogUtils.d(TAG, " init: SD_APP_MAIN_PATH = " + SD_APP_MAIN_PATH!!)
        LogUtils.d(TAG, " init: SD_ROOT_MAIN_PATH = " + SD_ROOT_MAIN_PATH!!)
        LogUtils.d(TAG, " init: EXTERNAL_CACHE_DIR = " + EXTERNAL_CACHE_DIR!!)
    }

    @JvmStatic
    @MethodDesc("判断sdcard是否可用")
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
    @MethodDesc("获取应用的cache存储路径")
    fun getExternalCacheDir(): File? {
        return EXTERNAL_CACHE_DIR
    }
    @JvmStatic
    @Nullable
    @MethodDesc("获取应用的sdcard存储路径")
    fun getSDAppPath(): File? {
        return SD_APP_MAIN_PATH
    }

    @JvmStatic
    @MethodDesc("删除文件或文件夹")
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
    @MethodDesc("清空文件夹，但不删除文件夹")
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
    @MethodDesc("判断文件是否存在")
    fun isFileExists(fileName: String?): Boolean {
        return if (!fileName.isNullOrBlank()) {
            isFileExists(File(fileName))
        } else {
            false
        }
    }

    @JvmStatic
    @MethodDesc("判断文件是否存在")
    fun isFileExists(file: File?): Boolean {
        return file?.exists() ?: false
    }

    @JvmStatic
    @MethodDesc("判断文件是否不存在或者为空")
    fun isFileEmpty(file: File?): Boolean {
        if (file == null || !file.exists()) {
            return true
        }
        return if (file.isDirectory) file.listFiles().isNullOrEmpty() else file.length() == 0L
    }

    @JvmStatic
    @MethodDesc("判断文件是否不存在或者为空")
    fun isFileEmpty(path: String?): Boolean {
        return path.isNullOrBlank() || isFileEmpty(File(path))
    }

    @JvmStatic
    @MethodDesc("判断文件是否存在或者非空")
    fun isFileExistsAndNotEmpty(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && isFileExistsAndNotEmpty(File(fileName))
    }

    @JvmStatic
    @MethodDesc("判断文件是否存在或者非空")
    fun isFileExistsAndNotEmpty(file: File?): Boolean {
        return if (file != null) {
            file.exists() && file.length() > 0
        } else {
            false
        }
    }

    @JvmStatic
    @MethodDesc("判断文件路径是否为目录")
    fun isDirectory(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && isDirectory(File(fileName))
    }

    @JvmStatic
    @MethodDesc("判断文件是否为目录")
    fun isDirectory(file: File?): Boolean {
        return file?.isDirectory ?: false
    }

    @JvmStatic
    @MethodDesc("判断文件是否为文件")
    fun isFile(file: File?): Boolean {
        return file?.isFile ?: false
    }

    @JvmStatic
    @MethodDesc("获取文件扩展名")
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
    @MethodDesc("判断文件路径是否为文件")
    fun isFile(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && isFile(File(fileName))
    }

    /**
     * @return true if the file is exists or created
     */
    @JvmStatic
    @MethodDesc("如果文件夹不存在，则创建文件")
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
    @MethodDesc("如果文件夹不存在，则创建文件")
    fun createFolderIfNotExists(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && createFolderIfNotExists(File(fileName))
    }

    /**
     * @return true if the file is exists or created
     */
    @JvmStatic
    @MethodDesc("如果文件不存在，则创建文件")
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
    @MethodDesc("如果文件不存在，则创建文件")
    fun createFileIfNotExists(fileName: String?): Boolean {
        return !fileName.isNullOrBlank() && createFileIfNotExists(File(fileName))
    }

    /**
     * @return the file name without dir path
     */
    @JvmStatic
    @NonNull
    @MethodDesc("获取文件路径中但文件名")
    fun getFileName(path: String?): String {
        if (path.isNullOrBlank()) {
            return ""
        }
        val index = path.lastIndexOf('/')
        return if (index > -1) path.substring(index + 1) else ""
    }

    @JvmStatic
    @MethodDesc("获取默认sdcard存储路径")
    fun getSDRootMainPath(): String {
        return SD_ROOT_MAIN_PATH!!.absolutePath
    }

    @JvmStatic
    @MethodDesc("复制文件")
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
    @MethodDesc("复制文件")
    fun copyFile(srcPath: String?, targetPath: String): Boolean {
        return !srcPath.isNullOrBlank() && !targetPath.isNullOrBlank() && copyFile(File(srcPath), File(targetPath))
    }

    @JvmStatic
    @MethodDesc("从流中复制文件")
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
    @MethodDesc("获取应用内部存储缓存目录")
    fun getAppCacheDir(): String {
        return Global.getContext().cacheDir.absolutePath
    }

    @JvmStatic
    @MethodDesc("获取应用sdcard缓存目录")
    fun getAppSdcardCacheDir(): String {
        return Global.getContext().externalCacheDir.absolutePath
    }

    @JvmStatic
    @MethodDesc("获取应用内部存储目录")
    fun getAppFilesDir(folder: String): String {
        val f = File(Global.getContext().filesDir, folder)
        if (!f.exists()) {
            f.mkdirs()
        }
        return f.absolutePath
    }

    @JvmStatic
    @MethodDesc("判断文件是否为gif文件")
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
    @MethodDesc("获取文件大小")
    fun getFileSize(file: File?): Long {
        return if (file == null || !file.exists()) 0 else file.length()
    }

    @JvmStatic
    @MethodDesc("获取文件大小")
    fun getFileSize(path: String?): Long {
        if (path.isNullOrBlank()) {
            return 0
        }
        return getFileSize(File(path))
    }

    @JvmStatic
    @MethodDesc("判断文件是否可读")
    fun canRead(path: String?): Boolean {
        if (path.isNullOrBlank()) {
            return false
        }
        val f = File(path)
        return f.exists() && f.canRead()
    }

    @JvmStatic
    @MethodDesc("获取文件后缀名")
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
    @MethodDesc("获取SDcard总大小")
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
    @MethodDesc("获取Sdcard剩余空间")
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
    @MethodDesc("获取应用私有目录剩余控件")
    @JvmStatic
    fun getFreeDataSpace(): Long {
        return Global.getContext().filesDir.freeSpace
    }

    @MethodDesc("创建符合链接")
    @JvmStatic
    fun createSymbolLink(srcPath: String?, targetPath: String?): Boolean {
        if (srcPath.isNullOrEmpty() || targetPath.isNullOrEmpty() || !isFileExists(srcPath)) {
            return false
        }
        val targetFile = File(targetPath)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        createFolderIfNotExists(targetFile.parentFile)
        return FileUtilsNative.createFileLink(srcPath, targetPath)
    }

    /**
     * @param path 目标文件路径
     * @param mode 权限的描述，必须使用8进制，取值与linux chmod命令一致
     */
    @MethodDesc("修改文件访问权限")
    @JvmStatic
    fun chmod(path: String?, mode: String): Boolean {
        if (!isFileExists(path) || mode.isNullOrEmpty()) {
            return false
        }
        try {
            val modeNumber = Integer.parseInt(mode, 8)
            return FileUtilsNative.chmod(path, modeNumber)
        } catch (e: java.lang.Exception) {
            LogUtils.e(TAG, e, e.message)
        }
        return false
    }

    @MethodDesc("获取文件描述符的值")
    @JvmStatic
    fun getFileDescriptor(descriptor: FileDescriptor?): Int {
        descriptor ?: return -1
        return ReflectUtils.getFieldValue<Int>(FileDescriptor::class.java, descriptor, "descriptor")
            ?: return -1
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

    /**
     * 解压文件到目标文件夹
     * @param zipFile File?
     * @param targetFolder String?
     * @return Boolean
     */
    @JvmStatic
    fun extractZipFile(zipFile: File?, targetFolder: String?): Boolean {
        val IGNORE_FOLDER = "__MACOSX"
        val IGNORE_FILE = ".DS_Store"
        var isSuccess = false
        try {
            val zip: ZipFile = ZipFile(zipFile)
            val fileHeaders: List<FileHeader>? = zip.getFileHeaders() as? List<FileHeader>
            if (fileHeaders != null) {
                for (fileHeader in fileHeaders) {
                    var filename: String = fileHeader.getFileName()
                    if (filename.startsWith(IGNORE_FOLDER) || filename.endsWith(IGNORE_FILE)) {
                        continue
                    }
                    if (filename.startsWith(File.separator)) {
                        filename = filename.substring(1)
                    }
                    filename = filename.substring(filename.indexOf(File.separator) + 1)
                    if (filename != null && !filename.isEmpty()) {
                        zip.extractFile(fileHeader, targetFolder, null, filename)
                    }
                }
            }
            isSuccess = true
        } catch (e: java.lang.Exception) {
            isSuccess = false
        }
        return isSuccess
    }


    @JvmStatic
    fun getFilesDir(folder: String?): String? {
        val f = File(Global.getContext().filesDir, folder)
        if (!f.exists()) {
            f.mkdirs()
        }
        return f.absolutePath
    }

    @JvmStatic
    fun readStringFromFile(path: String?): String? {
        var br: BufferedReader? = null
        try {
            //可以换成工程目录下的其他文本文件
            val sb = StringBuffer()
            br = BufferedReader(FileReader(path))
            var readline: String?
            while (br.readLine().also { readline = it } != null) {
                sb.append(readline)
            }
            return sb.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            closeQuietly(br)
        }
        return ""
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
            closeQuietly(stream)
        }
        return false
    }
    @JvmStatic
    fun saveBitmapAsFile(path: String, bitmap: Bitmap?): Boolean {
        if (bitmap == null) {
            return false
        }
        var flag = false
        val newFile = File(path)
        val os: FileOutputStream
        try {
            os = FileOutputStream(newFile)
            flag = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return flag
    }

    fun saveBitmapAsFileInPng(path: String?, bitmap: Bitmap): Boolean {
        var flag = false
        val file = File(path)
        val os: FileOutputStream
        try {
            os = FileOutputStream(file)
            flag = bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return flag
    }


    /**
     * 保存bitmap到SD卡
     */
    @JvmStatic
    fun saveBitmapToSDCard(
        bitmap: Bitmap?, dirPath: String, imageName: String,
        format: Bitmap.CompressFormat?
    ): String? {
        var format = format
        Preconditions.checkArgument(bitmap != null, "bitmap must not be null")
        Preconditions.checkArgument(!TextUtils.isEmpty(dirPath), "dirPath must not be empty")
        Preconditions.checkArgument(!TextUtils.isEmpty(imageName), "imageName must not be empty")
        if (format == null) {
            format = Bitmap.CompressFormat.PNG
        }
        val suffix: String
        suffix = when (format) {
            Bitmap.CompressFormat.JPEG -> ".jpeg"
            Bitmap.CompressFormat.WEBP -> ".webp"
            Bitmap.CompressFormat.PNG -> ".png"
            else -> ".png"
        }
       createFolderIfNotExists(dirPath)
        val path = dirPath + imageName + suffix
       deleteFile(path)
        try {
            val fos = FileOutputStream(path)
            bitmap!!.compress(format, 90, fos)
            fos.close()
            return path
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
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

    @JvmStatic
    fun getTypeByName(fileName: String?): String? {
        if (fileName == null) {
            return ""
        }
        val type = fileName.toLowerCase()
        val index = type.lastIndexOf(".")
        return if (index > 0) {
            type.substring(index + 1)
        } else {
            ""
        }
    }
    @JvmStatic
    fun isImageFile(filePath: String?): Boolean {
        if (!isFileExists(filePath)) {
            return false
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        return options.outWidth > 0
    }
}
package com.jw.library.utils

import android.graphics.Bitmap
import com.jw.library.model.BaseItem
import java.io.*
import java.text.DecimalFormat

/**
 * 作者：jw
 * 创建时间：2017/10/23 20:54
 * 描述:文件工具类
 */
object FileUtils {


    /**
     * Author: jw
     * Created on:  2017/8/12.
     * Description: 获取文件大小工具类
     */
    val SIZETYPE_B = 1//获取文件大小单位为B的double值
    val SIZETYPE_KB = 2//获取文件大小单位为KB的double值
    val SIZETYPE_MB = 3//获取文件大小单位为MB的double值
    val SIZETYPE_GB = 4//获取文件大小单位为GB的double值

    val MIME_MapTable = arrayOf(
        //{后缀名， MIME类型}
        arrayOf(".3gp", "video/3gpp"),
        arrayOf(".apk", "application/vnd.android.package-archive"),
        arrayOf(".asf", "video/x-ms-asf"),
        arrayOf(".avi", "video/x-msvideo"),
        arrayOf(".bin", "application/octet-stream"),
        arrayOf(".bmp", "image/bmp"),
        arrayOf(".c", "text/plain"),
        arrayOf(".class", "application/octet-stream"),
        arrayOf(".conf", "text/plain"),
        arrayOf(".cpp", "text/plain"),
        arrayOf(".doc", "application/msword"),
        arrayOf(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        arrayOf(".xls", "application/vnd.ms-excel"),
        arrayOf(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        arrayOf(".exe", "application/octet-stream"),
        arrayOf(".gif", "image/gif"),
        arrayOf(".gtar", "application/x-gtar"),
        arrayOf(".gz", "application/x-gzip"),
        arrayOf(".h", "text/plain"),
        arrayOf(".htm", "text/html"),
        arrayOf(".html", "text/html"),
        arrayOf(".jar", "application/java-archive"),
        arrayOf(".java", "text/plain"),
        arrayOf(".jpeg", "image/jpeg"),
        arrayOf(".jpg", "image/jpeg"),
        arrayOf(".js", "application/x-javascript"),
        arrayOf(".log", "text/plain"),
        arrayOf(".m3u", "audio/x-mpegurl"),
        arrayOf(".m4a", "audio/mp4a-latm"),
        arrayOf(".m4b", "audio/mp4a-latm"),
        arrayOf(".m4p", "audio/mp4a-latm"),
        arrayOf(".m4u", "video/vnd.mpegurl"),
        arrayOf(".m4v", "video/x-m4v"),
        arrayOf(".mov", "video/quicktime"),
        arrayOf(".mp2", "audio/x-mpeg"),
        arrayOf(".mp3", "audio/x-mpeg"),
        arrayOf(".mp4", "video/mp4"),
        arrayOf(".mpc", "application/vnd.mpohun.certificate"),
        arrayOf(".mpe", "video/mpeg"),
        arrayOf(".mpeg", "video/mpeg"),
        arrayOf(".mpg", "video/mpeg"),
        arrayOf(".mpg4", "video/mp4"),
        arrayOf(".mpga", "audio/mpeg"),
        arrayOf(".msg", "application/vnd.ms-outlook"),
        arrayOf(".ogg", "audio/ogg"),
        arrayOf(".pdf", "application/pdf"),
        arrayOf(".png", "image/png"),
        arrayOf(".pps", "application/vnd.ms-powerpoint"),
        arrayOf(".ppt", "application/vnd.ms-powerpoint"),
        arrayOf(
            ".pptx",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        ),
        arrayOf(".prop", "text/plain"),
        arrayOf(".rc", "text/plain"),
        arrayOf(".rmvb", "audio/x-pn-realaudio"),
        arrayOf(".rtf", "application/rtf"),
        arrayOf(".sh", "text/plain"),
        arrayOf(".tar", "application/x-tar"),
        arrayOf(".tgz", "application/x-compressed"),
        arrayOf(".txt", "text/plain"),
        arrayOf(".wav", "audio/x-wav"),
        arrayOf(".wma", "audio/x-ms-wma"),
        arrayOf(".wmv", "audio/x-ms-wmv"),
        arrayOf(".wps", "application/vnd.ms-works"),
        arrayOf(".xml", "text/plain"),
        arrayOf(".z", "application/x-compress"),
        arrayOf(".zip", "application/x-zip-compressed"),
        arrayOf("", "*/*")
    )

    /**
     * Author: jw
     * Created on:  2017/8/12.
     * Description: 删除文件工具类(文件或者文件夹可以自动识别)
     */
    fun delete(fileName: String): Boolean {
        val file = File(fileName)
        if (!file.exists()) {
            println("删除文件失败:" + fileName + "不存在！")
            return false
        } else {
            return if (file.isFile)
                deleteFile(fileName)
            else
                deleteDirectory(fileName)
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     * 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    fun deleteFile(fileName: String): Boolean {
        val file = File(fileName)
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile) {
            if (file.delete()) {
                println("删除单个文件" + fileName + "成功！")
                return true
            } else {
                println("删除单个文件" + fileName + "失败！")
                return false
            }
        } else {
            println("删除单个文件失败：" + fileName + "不存在！")
            return false
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     * 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    fun deleteDirectory(dir: String): Boolean {
        var dir = dir
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator
        val dirFile = File(dir)
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory) {
            println("删除目录失败：" + dir + "不存在！")
            return false
        }
        var flag = true
        // 删除文件夹中的所有文件包括子目录
        val files = dirFile.listFiles()
        for (i in files.indices) {
            // 删除子文件
            if (files[i].isFile) {
                flag = deleteFile(files[i].absolutePath)
                if (!flag)
                    break
            } else if (files[i].isDirectory) {
                flag = deleteDirectory(
                    files[i]
                        .absolutePath
                )
                if (!flag)
                    break
            }// 删除子目录
        }
        if (!flag) {
            println("删除目录失败！")
            return false
        }
        // 删除当前目录
        if (dirFile.delete()) {
            println("删除目录" + dir + "成功！")
            return true
        } else {
            return false
        }
    }

    /**
     * 获取文件指定文件的指定单位的大小
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    fun getFileOrFilesSize(filePath: String, sizeType: Int): Double {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            if (file.isDirectory) {
                blockSize = getFileSizes(file)
            } else {
                blockSize = getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("获取文件大小获取失败!")
        }

        return FormetFileSize(blockSize, sizeType)
    }


    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    fun getAutoFileOrFilesSize(filePath: String): String {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            if (file.isDirectory) {
                blockSize = getFileSizes(file)
            } else {
                blockSize = getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("获取文件大小获取失败!")
        }

        return FormetFileSize(blockSize)
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            var fis: FileInputStream? = null
            fis = FileInputStream(file)
            size = fis.available().toLong()
        } else {
            file.createNewFile()
            println("不存在!")
        }
        return size
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFileSizes(f: File): Long {
        var size: Long = 0
        val flist = f.listFiles()
        for (i in flist.indices) {
            if (flist[i].isDirectory) {
                size = size + getFileSizes(flist[i])
            } else {
                size = size + getFileSize(flist[i])
            }
        }
        return size
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private fun FormetFileSize(fileS: Long): String {
        val df = DecimalFormat("#.00")
        var fileSizeString = ""
        val wrongSize = "0B"
        if (fileS == 0L) {
            return wrongSize
        }
        if (fileS < 1024) {
            fileSizeString = df.format(fileS.toDouble()) + "B"
        } else if (fileS < 1048576) {
            fileSizeString = df.format(fileS.toDouble() / 1024) + "KB"
        } else if (fileS < 1073741824) {
            fileSizeString = df.format(fileS.toDouble() / 1048576) + "MB"
        } else {
            fileSizeString = df.format(fileS.toDouble() / 1073741824) + "GB"
        }
        return fileSizeString
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private fun FormetFileSize(fileS: Long, sizeType: Int): Double {
        val df = DecimalFormat("#.00")
        var fileSizeLong = 0.0
        when (sizeType) {
            SIZETYPE_B -> fileSizeLong = java.lang.Double.valueOf(df.format(fileS.toDouble()))
            SIZETYPE_KB -> fileSizeLong =
                java.lang.Double.valueOf(df.format(fileS.toDouble() / 1024))
            SIZETYPE_MB -> fileSizeLong =
                java.lang.Double.valueOf(df.format(fileS.toDouble() / 1048576))
            SIZETYPE_GB -> fileSizeLong =
                java.lang.Double.valueOf(df.format(fileS.toDouble() / 1073741824))
            else -> {
            }
        }
        return fileSizeLong
    }

    /**
     * 文件复制
     * @param inPath 文件路径
     * @param outPath 要复制到的路径
     */
    fun copy(inPath: String, outPath: String) {
        /*var is: BufferedInputStream? = null
        var out: BufferedOutputStream? = null
        try {
            is = BufferedInputStream(FileInputStream(inPath))
            val file = File(outPath)
            if (!file.parentFile.exists())
                file.parentFile.mkdirs()
            if (file.exists())
                return
            out = BufferedOutputStream(FileOutputStream(outPath))
            var len = 0
            while ((len = is.read()) != -1) {
                out.write(len)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                is?.close()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }*/
    }


    /**
     * 将对象序列化入一个文件
     * @param t
     * @param outPath 文件路径
     * @param <T>
     * @throws Exception
    </T> */
    @Throws(Exception::class)
    fun <T : Serializable> write(t: T, outPath: String) {
        var oos: ObjectOutputStream? = null
        try {
            val file = File(outPath)
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }

            oos = ObjectOutputStream(FileOutputStream(file))
            oos.writeObject(t)
        } finally {
            oos?.close()
        }
    }

    /**
     * 读出序列化对象
     * @param path 序列化文件路径
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun read(path: String): Serializable? {
        var ois: ObjectInputStream? = null
        try {
            ois = ObjectInputStream(FileInputStream(path))
            val `object` = ois.readObject()

            if (`object` != null) {
                return `object` as Serializable
            }
        } finally {
            ois?.close()
        }
        return null
    }

    fun saveBitmap(path: String, name: String, b: Bitmap): String {
        val absolutePath = path + File.separator + name
        try {
            val fout = FileOutputStream(absolutePath)
            val bos = BufferedOutputStream(fout)
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
            return absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    fun getMIMEType(file: File): String {

        var type = "*/*"
        val fName = file.name
        //获取后缀名前的分隔符"."在fName中的位置。
        val dotIndex = fName.lastIndexOf(".")
        if (dotIndex < 0) {
            return type
        }
        /* 获取文件的后缀名 */
        val end = fName.substring(dotIndex, fName.length).toLowerCase()
        if (end === "") return type
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (i in MIME_MapTable.indices) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end == MIME_MapTable[i][0])
                type = MIME_MapTable[i][1]
        }
        return type
    }

    fun <ITEM : BaseItem> getMediaItem(item: ITEM): ITEM {
        item.name = item.path.split("/").last()
        item.size = getAutoFileOrFilesSize(item.path)
        item.mimeType = getMIMEType(File(item.path))
        return item
    }
}

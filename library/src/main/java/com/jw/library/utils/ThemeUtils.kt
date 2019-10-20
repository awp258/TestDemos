package com.jw.library.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.*
import java.security.MessageDigest
import java.util.*


/**
 * 创建时间：2017/7/10
 * 更新时间：2017/11/10 下午 7:23
 * 作者：Mr.jin
 * 描述：与系统环境相关操作
 */

object ThemeUtils {
    private var window: Window? = null


    /**
     * 吐司，单例且保证吐司在主线程运行
     * @param activity
     * @param content
     */
    fun show(activity: Activity, content: String) {
        activity.runOnUiThread {
            Toast.makeText(activity, content, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 吐司，单例不能保证在主线程运行
     * @param context
     * @param content
     */
    fun show(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }


    fun show(activity: Activity, contentId: Int) {
        activity.runOnUiThread { Toast.makeText(activity, contentId, Toast.LENGTH_SHORT).show() }
    }

    /**
     *
     * @param activity 传入activity以获取窗口
     * @param color 颜色代码，如"#..."
     */
    fun changeStatusBar(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = activity.window
            window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window!!.statusBarColor = color
            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            val localLayoutParams = window!!.attributes
            localLayoutParams.flags = color
        }
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = -1
        //获取status_bar_height资源的ID
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    fun hasVirtualNavigationBar(context: Context): Boolean {
        var hasSoftwareKeys = true
        if (Build.VERSION.SDK_INT >= 17) {
            val d = (context.getSystemService("window") as WindowManager).defaultDisplay
            val realDisplayMetrics = DisplayMetrics()
            d.getRealMetrics(realDisplayMetrics)
            val realHeight = realDisplayMetrics.heightPixels
            val realWidth = realDisplayMetrics.widthPixels
            val displayMetrics = DisplayMetrics()
            d.getMetrics(displayMetrics)
            val displayHeight = displayMetrics.heightPixels
            val displayWidth = displayMetrics.widthPixels
            hasSoftwareKeys = realWidth - displayWidth > 0 || realHeight - displayHeight > 0
        } else if (Build.VERSION.SDK_INT >= 14) {
            val hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey()
            val hasBackKey = KeyCharacterMap.deviceHasKey(4)
            hasSoftwareKeys = !hasMenuKey && !hasBackKey
        }

        return hasSoftwareKeys
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resourceId =
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
    }

    /**
     *
     * @param view 要改变背景颜色的view
     * @param color  颜色代码，如"#..."
     */
    fun changeViewColor(view: View?, color: Int) {
        view?.setBackgroundColor(color)
    }

    /**
     * 得到屏幕宽
     * @param context
     * @return
     */
    fun getWindowWidth(context: Context): Int {
        val metric = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metric)
        return metric.widthPixels
    }

    /**
     * 得到屏幕高
     * @param context
     * @return
     */
    fun getWindowHeight(context: Context): Int {
        val metric = DisplayMetrics()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metric)
        return metric.heightPixels
    }

    /**
     * 得到屏幕的亮度
     * @param activity
     * @return
     */
    fun getWindowBrightness(activity: Activity): Float {
        val lp = activity.window.attributes
        return lp.screenBrightness
    }

    /**
     * 设置屏幕亮度
     * @param activity
     * @param brightness
     */
    fun setWindowBrightness(activity: Activity, brightness: Float) {
        val lp = activity.window.attributes
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f
    }

    /**
     * dip 转 px
     *
     * @param context
     * @param dipValue
     * @return
     */
    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * px 转 dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 释放Assets中的资源
     * @param context
     * @param name 资源名称
     * @param path 释放到的路径
     */
    fun mkdirsAssets(context: Context, name: String, path: String) {
        var bis: BufferedInputStream? = null
        var out: BufferedOutputStream? = null
        try {
            bis = BufferedInputStream(context.assets.open(name))
            val file = File(path)
            //释放目录
            if (!file.parentFile.exists())
                file.parentFile.mkdirs()
            if (file.exists())
                return
            out = BufferedOutputStream(FileOutputStream(path))
            var len = 0
            while (len != -1) {
                out.write(len)
                len = bis.read()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bis?.close()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 得到资源文件的输入流
     * @param name 文件名
     * @return
     */
    fun getAssetsInputStream(context: Context, name: String): InputStream? {
        var open: InputStream? = null
        try {
            open = context.assets.open(name)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return open
    }

    /**
     * 从资源文件输入流中读取字符串
     * @param in
     * @return
     */
    fun readFromAssetsStream(`in`: InputStream): String {
        var result: String? = null
        //字节输出流
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len = 0
        try {
            while (len != -1) {
                out.write(buffer, 0, len)
                len = `in`.read(buffer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            result = out.toString()
            try {
                `in`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return result
    }

    fun getImageView(context: Context): ImageView {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val iv = ImageView(context)
        iv.maxHeight = 300
        params.gravity = Gravity.CENTER_HORIZONTAL
        iv.layoutParams = params
        iv.scaleType = ImageView.ScaleType.CENTER_CROP
        return iv
    }

    fun getTextView(context: Context): TextView {
        val tv = TextView(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv.layoutParams = params
        tv.textSize = 15f
        return tv
    }

    /**
     * 弹出请求窗口
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestPermission(activity: Activity, permissionName: String, requestCode: Int) {
        val permissionStrs = ArrayList<String>()
        permissionStrs.add(permissionName)
        val stringArray = permissionStrs.toTypedArray()
        if (permissionStrs.size > 0) {
            activity.requestPermissions(stringArray, requestCode)
        }
    }

    /**
     * 弹出请求窗口
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestPermissions(activity: Activity, permissionStrs: List<String>, requestCode: Int) {
        val stringArray = permissionStrs.toTypedArray()
        if (permissionStrs.size > 0) {
            activity.requestPermissions(stringArray, requestCode)
        }
    }

    /**
     * Android7.0以上时，不能往外部存储写数据，动态弹出权限框点击同意即可
     * @param permissionName
     * @param activity
     */
    fun checkPermission(activity: Activity, permissionName: String): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(
            activity, permissionName
        )
        return hasPermission == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 判断服务是否运行
     *
     * @param context
     * @param clazz
     * 要判断的服务的class
     * @return
     */
    fun isServiceRunning(
        context: Context,
        clazz: Class<out Service>
    ): Boolean {
        val manager = context
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val services = manager.getRunningServices(100)
        for (i in services.indices) {
            val className = services[i].service.className
            if (className == clazz.name) {
                return true
            }
        }
        return false
    }

    /**
     * 对字符串进行MD5加密
     * @param inStr
     * @return
     */
    fun string2MD5(inStr: String): String {
        var md5: MessageDigest? = null
        try {
            md5 = MessageDigest.getInstance("MD5")
        } catch (e: Exception) {
            println(e.toString())
            e.printStackTrace()
            return ""
        }

        val charArray = inStr.toCharArray()
        val byteArray = ByteArray(charArray.size)

        for (i in charArray.indices)
            byteArray[i] = charArray[i].toByte()
        val md5Bytes = md5!!.digest(byteArray)
        val hexValue = StringBuffer()
        for (i in md5Bytes.indices) {
            val `val` = md5Bytes[i].toInt() and 0xff
            if (`val` < 16)
                hexValue.append("0")
            hexValue.append(Integer.toHexString(`val`))
        }
        return hexValue.toString()
    }

    fun existSDCard(): Boolean {
        return Environment.getExternalStorageState() == "mounted"
    }

    /**
     * 打开文件
     *
     * @param file
     */
    fun openFile(activity: Activity, file: File) {

        val intent = Intent()
        // 这是比较流氓的方法，绕过7.0的文件权限检查
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //设置intent的Action属性
        intent.action = Intent.ACTION_VIEW
        //获取文件file的MIME类型
        val type = FileUtils.getMIMEType(file)
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type)
        //跳转
        activity.startActivity(intent)

    }
}
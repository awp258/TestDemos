package com.jw.uploaddemo.base.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 由Harreke于2016/2/18创建
 */
object PermissionUtil {
    /**
     * 检查Activity所属Package某一权限（Manifest文件中注册的）是否已授权
     *
     * @param activity Activity
     * @param requestCode 回调代码，[Activity.onRequestPermissionsResult]
     * @return 是否有权限未授权，需要向系统申请
     */
    fun checkAndRequestPermission(activity: Activity, permission: String, requestCode: Int): Boolean {
        val granted = ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        return if (granted) {
            true
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            false
        }
    }

    /**
     * 检查Activity所属Package自带的权限（Manifest文件中注册的）是否已授权
     *
     * @param activity Activity
     * @param requestCode 回调代码，[Activity.onRequestPermissionsResult]
     * @return 是否有权限未授权，需要向系统申请
     */
    fun checkAndRequestPermission(activity: Activity, requestCode: Int): Boolean {
        val deniedPermissions = getDeniedPermissions(activity).toTypedArray()
        return if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, deniedPermissions, requestCode)
            true
        } else {
            false
        }
    }

    /**
     * 获得该Activity对应的Manifest文件中，事先注册的所有权限
     *
     * @param activity Activity
     * @param onlyAndroid 是否过滤非安卓系统权限
     * 如果为true，将过滤掉所有非"android."开头的权限
     */
    fun getRequiredPermissions(activity: Activity, onlyAndroid: Boolean): List<String> {
        val requiredPermissions = ArrayList<String>()
        try {
            val info = activity.packageManager.getPackageInfo(activity.packageName, PackageManager.GET_PERMISSIONS)
            if (onlyAndroid) {
                info.requestedPermissions.filterTo(requiredPermissions) {
                    it.startsWith("android.")
                }
            } else {
                requiredPermissions += info.requestedPermissions
            }
        } catch (ignored: Exception) {
        }
        return requiredPermissions
    }

    fun getRequiredPermissions(activity: Activity) = getRequiredPermissions(activity, true)

    fun getDeniedPermissions(activity: Activity, onlyAndroid: Boolean): List<String> {
        val requiredPermissions = getRequiredPermissions(activity, onlyAndroid)
        val deniedPermissions = ArrayList<String>()
        if (requiredPermissions.isEmpty()) {
            return deniedPermissions
        }
        requiredPermissions.filterTo(deniedPermissions) {
            ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }
        return deniedPermissions
    }

    fun getDeniedPermissions(activity: Activity) = getDeniedPermissions(activity, true)

    /**
     * 检查授权结果中是否还存留未授权的权限
     *
     * @param resultPermissions 用户操作返回的授权列表
     * @param grantResults 用户操作返回的授权结果
     * @return 仍然未授权的权限列表
     */
    fun checkPermissionResult(activity: Activity, resultPermissions: Array<out String>, grantResults: IntArray): List<String> {
        val deniedPermissions = ArrayList<String>()
        for ((index, resultPermission) in resultPermissions.withIndex()) {
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (resultPermission == Manifest.permission.SYSTEM_ALERT_WINDOW && Settings.canDrawOverlays(activity)) {
                        continue
                    } else if (resultPermission == Manifest.permission.WRITE_SETTINGS && Settings.System.canWrite(activity)) {
                        continue
                    }
                }
                deniedPermissions.add(resultPermission)
            }
        }
        return deniedPermissions
    }

    /**
     * 检查授权结果中指定的权限是否已全部获得
     *
     * @param requiredPermissions 需要检查的权限
     * @param resultPermissions 用户操作返回的授权列表
     * @param grantResults 用户操作返回的授权结果
     * @return 指定的权限是否已全部获得
     */
    fun checkPermissionResult(activity: Activity, requiredPermissions: List<String>, resultPermissions: Array<out String>, grantResults: IntArray): Boolean {
        var index: Int
        for (requiredPermission in requiredPermissions) {
            index = Arrays.binarySearch(resultPermissions, requiredPermission)
            if (index >= 0) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (requiredPermission == Manifest.permission.SYSTEM_ALERT_WINDOW && Settings.canDrawOverlays(activity)) {
                            continue
                        } else if (requiredPermission == Manifest.permission.WRITE_SETTINGS && Settings.System.canWrite(activity)) {
                            continue
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    /**
     * 检查App是否有权限读写外部存储器
     *
     * @return 是否有权限未授权，需要向系统申请
     */
    fun checkPermissionsResultForExternalStorage(activity: Activity, resultPermissions: Array<out String>, grantResults: IntArray): Boolean {
        val requiredPermissions: List<String> = if (Build.VERSION.SDK_INT >= 16) {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return checkPermissionResult(activity, requiredPermissions, resultPermissions, grantResults)
    }
}
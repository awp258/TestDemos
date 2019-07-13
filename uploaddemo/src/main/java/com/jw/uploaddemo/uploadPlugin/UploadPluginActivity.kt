package com.jw.uploaddemo.uploadPlugin

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.Toast
import com.jw.galary.img.ImagePicker
import com.jw.galary.img.util.CornerUtils
import com.jw.galary.img.util.Utils
import com.jw.uploaddemo.ColorCofig
import com.jw.uploaddemo.base.activity.BaseActivity
import com.jw.uploaddemo.base.utils.PermissionUtil
import com.jw.uploaddemo.base.utils.ThemeUtils

/**
 * 由 jinwangx 创建于 2018/3/5.
 */
abstract class UploadPluginActivity : BaseActivity() {
    private val REQUEST_PERMISSION = 4000
    private val REQUEST_PERMIOSSION_WRITE_SETTINGS = 4002
    private var mPermissionAllGranted = false
    private var mPermissionDialog: AlertDialog? = null
    private val mPermissionDennyClickListener = DialogInterface.OnClickListener { _, _ ->
        mPermissionDialog?.dismiss()
        onBackPressed()
    }
    private val mPermissionRequestClickListener = DialogInterface.OnClickListener { _, _ ->
        mPermissionDialog?.dismiss()
        checkAndRequestPermission()
    }
    private val mPermissionDisabledClickListener = DialogInterface.OnClickListener { _, which ->
        mPermissionDialog?.dismiss()
        when (which) {
            DialogInterface.BUTTON_POSITIVE ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:" + packageName)
                    startActivityForResult(intent, REQUEST_PERMISSION)
                } catch (e: Exception) {
                    //ToastUtil.showToast("系统设置页面跳转失败！")
                }
            DialogInterface.BUTTON_NEGATIVE -> onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtils.changeStatusBar(this, Color.parseColor("#393A3F"))
    }

    override fun doInflate(activity: BaseActivity, savedInstanceState: Bundle?) {

    }

    override fun doConfig(arguments: Intent) {

    }

    fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == 0
    }

    fun showToast(toastText: String) {
        Toast.makeText(this.applicationContext, toastText, Toast.LENGTH_SHORT).show()
    }

    protected open fun shouldRequestPermission() = false

    /**
    获取该Activity需要的权限列表
     */
    protected open fun getRequiredPermissions() = PermissionUtil.getRequiredPermissions(this)

    private fun checkAndRequestPermission() {
        val requiredPermissions = getRequiredPermissions()
        if (requiredPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, requiredPermissions.toTypedArray(), REQUEST_PERMISSION)
        }
    }

    private fun checkAndShowPermissionResultDialog(deniedPermissions: List<String>) {
        for (deniedPermission in deniedPermissions) {
            try {
                when (deniedPermission) {
                    /*Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                        requestAlertWindow()
                        return
                    }*/
                    Manifest.permission.WRITE_SETTINGS -> {
                        requestWriteSettings()
                        return
                    }
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, deniedPermission)) {
                    showRequestDialog()
                } else {
                    showDisabledDialog(deniedPermission)
                }
                return
            } catch (e: Exception) {
            }
        }
        mPermissionAllGranted = true
    }

    private fun showDisabledDialog(deniedPermission: String) {
        mPermissionDialog?.dismiss()
        val builder = AlertDialog.Builder(this)
            .setTitle("权限被禁用")
            .setPositiveButton("开启", mPermissionDisabledClickListener)
            .setNegativeButton("退出", mPermissionDisabledClickListener)
            .setCancelable(false)
        when (deniedPermission) {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                builder.setMessage("禁用存储卡读写权限后，将无法正常读写数据")
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION ->
                builder.setMessage("禁用位置获取权限后，将无法定位地理位置")
            Manifest.permission.READ_PHONE_STATE ->
                builder.setMessage("禁用电话权限后，将无法根据手机识别码验证身份")
            Manifest.permission.CAMERA ->
                builder.setMessage("禁用相机权限后，将无法拍照和录制视频")
            Manifest.permission.RECORD_AUDIO ->
                builder.setMessage("禁用麦克风权限后，将无法录制声音和通话")
            Manifest.permission.READ_CONTACTS ->
                builder.setMessage("禁用联系人权限后，将无法根据联系人匹配好友")
            else -> builder.setMessage("关键权限被禁用，请手动开启")
        }
        val dialog = builder.create()
        dialog.show()
        mPermissionDialog = dialog
    }

    private fun requestWriteSettings() {
        if (Build.VERSION.SDK_INT >= 23) {
            //ToastUtil.showToast("为了修改设定，请授权修改设定权限")
            try {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + packageName)
                startActivityForResult(intent, REQUEST_PERMIOSSION_WRITE_SETTINGS)
            } catch (e: Exception) {
                //ToastUtil.showToast("修改设定授权页面跳转失败！")
            }
        }
    }

    private fun showRequestDialog() {
        mPermissionDialog?.dismiss()
        val dialog = AlertDialog.Builder(this)
            .setTitle("为了能正常运行，请授权这些权限")
            .setPositiveButton("授权", mPermissionRequestClickListener)
            .setNegativeButton("退出", mPermissionDennyClickListener)
            .setCancelable(false)
            .create()
        dialog.show()
        mPermissionDialog = dialog
    }

    protected fun setConfirmButtonBg(mBtnOk: Button) {
        val imagePicker = ImagePicker.getInstance()
        val btnOkDrawable = CornerUtils.btnSelector(
            Utils.dp2px(this, 3.0f).toFloat(),
            Color.parseColor(ColorCofig.oKButtonTitleColorNormal),
            Color.parseColor(ColorCofig.oKButtonTitleColorNormal),
            Color.parseColor(ColorCofig.oKButtonTitleColorDisabled),
            -2
        )
        mBtnOk.background = btnOkDrawable
        mBtnOk.setPadding(Utils.dp2px(this, 12.0f), 0, Utils.dp2px(this, 12.0f), 0)
        mBtnOk.setTextColor(Color.parseColor(ColorCofig.barItemTextColor))
    }

/*    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            val deniedPermissions = PermissionUtil.checkPermissionResult(this, permissions, grantResults)
            if (deniedPermissions.isEmpty()) {
                mPermissionAllGranted = true
                return
            }
            checkAndShowPermissionResultDialog(deniedPermissions)
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_PERMISSION -> checkAndRequestPermission()
        }
    }
}
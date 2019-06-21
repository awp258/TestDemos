package com.jw.shotRecord

import android.app.Dialog
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.util.Log
import android.view.View
import com.jw.uploaddemo.R
import com.jw.uploaddemo.UploadConfig
import com.jw.uploaddemo.activity.ProgressActivity
import com.jw.uploaddemo.base.dialog.SencentBindingDialog
import com.jw.uploaddemo.databinding.DialogVoiceRecordBinding
import com.jw.uploaddemo.utils.FileUtils
import java.io.File
import java.io.IOException

/**
 * 创建时间：2019/6/1416:47
 * 更新时间 2019/6/1416:47
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class VoiceRecordDialog : SencentBindingDialog<DialogVoiceRecordBinding>() {
    private val STATE_STOP = 0  //停止状态
    private val STATE_RECORDING = 1 //正在录制状态
    private val STATE_PAUSE = 2 //暂停状态
    private var mFileName: String? = null
    private var mRecorder: MediaRecorder? = null
    private var mStartingTimeMillis: Long = 0
    private var voiceFile: File? = null
    private var currentState = STATE_STOP   //当前状态
    private var lastPauseTime = 0L
    private var lastResumeTime = 0L
    private var allPauseTimeLength = 0L
    private var isShouldInterrupt = false

    private val runnable = Runnable {
        run {
            while (!isShouldInterrupt) {
                while (currentState == STATE_RECORDING) {
                    binding!!.currentLength =
                        ((System.currentTimeMillis() - mStartingTimeMillis - allPauseTimeLength)).toInt()
                    if (binding!!.currentLength!! > 60 * 1000)
                        stopRecord()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(context!!, theme)

    override fun getLayoutId() = R.layout.dialog_voice_record

    override fun doConfig(arguments: Bundle?) {
        isCancelable = false
        binding?.apply {
            clickListener = View.OnClickListener {
                when (it.id) {
                    R.id.iv_reset -> resetRecord()
                    R.id.iv_start -> {
                        if (currentState == STATE_PAUSE)
                            resumeRecord()
                        else
                            startRecord()
                    }
                    R.id.iv_pause -> pauseRecord()
                    R.id.iv_finish -> finishRecord()
                    R.id.iv_cancel -> cancelRecord()
                }
            }
        }
        resetRecord()
    }

    private fun resetRecord() {
        if (mRecorder != null) {
            mRecorder!!.reset()
            FileUtils.delete(voiceFile!!.absolutePath)
        }
        lastPauseTime = 0L
        lastResumeTime = 0L
        allPauseTimeLength = 0L
        currentState = STATE_STOP
        binding?.apply {
            currentLength = 0
            maxLength = UploadConfig.VOICE_RECORD_LENGTH
            currentState = STATE_STOP
        }
    }

    private fun startRecord() {
        val folder = File(activity!!.filesDir.absolutePath + "/SoundRecorder")
        if (!folder.exists()) {
            folder.mkdir()
        }
        mFileName = ("voice_" + System.currentTimeMillis() + ".m4a")
        voiceFile = File(activity!!.filesDir.absolutePath + "/SoundRecorder/$mFileName")
        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)  // 设置录音的声音来源
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // 设置声音编码的格式
        mRecorder?.setOutputFile(voiceFile!!.absolutePath)
        mRecorder?.setAudioChannels(1)
        mRecorder?.setAudioSamplingRate(44100)
        mRecorder?.setAudioEncodingBitRate(192000)
        try {
            mRecorder?.prepare()
            mRecorder?.start()
            currentState = STATE_RECORDING
            binding!!.currentState = STATE_RECORDING
            mStartingTimeMillis = System.currentTimeMillis()
            Thread(runnable).start()
        } catch (e: IOException) {
            Log.e("RecordingService", "prepare() failed")
        }

    }

    private fun pauseRecord() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRecorder!!.pause()
            lastPauseTime = System.currentTimeMillis()
            currentState = STATE_PAUSE
            binding!!.currentState = currentState
        }
    }

    private fun resumeRecord() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRecorder!!.resume()
            lastResumeTime = System.currentTimeMillis()
            allPauseTimeLength += lastResumeTime - lastPauseTime
            currentState = STATE_RECORDING
            binding!!.currentState = currentState
        }
    }

    private fun stopRecord() {
        if (voiceFile != null && voiceFile!!.exists()) {
            mRecorder!!.stop()
            mRecorder!!.release()
            currentState = STATE_STOP
            isShouldInterrupt = true
        }
    }

    private fun finishRecord() {
        if (voiceFile != null && voiceFile!!.exists()) {
            stopRecord()
            dismissAllowingStateLoss()
            val intent = Intent(activity, ProgressActivity::class.java)
            intent.putExtra("path", voiceFile!!.absolutePath)
            intent.putExtra("type", 2)
            start(intent)
        }
    }

    private fun cancelRecord() {
        stopRecord()
        FileUtils.delete(voiceFile!!.absolutePath)
        mRecorder = null
        dismissAllowingStateLoss()
    }

    override fun doLaunch() {

    }

    override fun doRefresh() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mRecorder != null) {
            mRecorder!!.release()
            currentState = STATE_STOP
            isShouldInterrupt = true
            mRecorder = null
        }
    }
}
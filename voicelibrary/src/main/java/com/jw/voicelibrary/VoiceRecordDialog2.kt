package com.jw.voicelibrary

import android.app.Dialog
import android.app.DialogFragment
import android.databinding.DataBindingUtil
import android.graphics.drawable.ClipDrawable
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jw.library.model.VoiceItem
import com.jw.library.utils.FileUtils
import com.jw.voicelibrary.databinding.DialogVoiceRecordBinding
import kotlinx.android.synthetic.main.dialog_voice_record.*
import java.io.File
import java.io.IOException


/**
 * 创建时间：2019/6/14 16:47
 * 更新时间 2019/6/14 16:47
 * 版本：
 * 作者：Mr.jin
 * 描述：cordova项目不支持supportFragmentManager,只能用DialogFragment
 */
class VoiceRecordDialog2 : DialogFragment() {
    private val VOICE_RECORD_LENGTH = VoiceLibrary.VOICE_RECORD_LENGTH //最大录制时长
    private val CACHE_VOICE_PATH = VoiceLibrary.CACHE_VOICE_PATH //语音缓存路径
    private val AUDIO_SOURSE = MediaRecorder.AudioSource.MIC //录音的声音来源
    private val OUTPUT_FORMAT = MediaRecorder.OutputFormat.MPEG_4 //录制的声音的输出格式
    private val AUDIO_ENCODER = MediaRecorder.AudioEncoder.AAC //声音编码的格式


    private val STATE_STOP = 0  //停止状态
    private val STATE_RECORDING = 1 //正在录制状态
    private val STATE_PAUSE = 2 //暂停状态
    private val STATE_PREPARE = 3 //准备状态

    private var currentState = STATE_PREPARE   //当前状态

    private var isShouldInterrupt = false //线程中断标记(计时)
    private var mStartingTimeMillis: Long = 0 //开始录制时的时间
    private var lastPauseTime = 0L //记录上次暂停的时间
    private var lastResumeTime = 0L //记录上次恢复录制的时间
    private var allPauseTimeLength = 0L //一共暂停的时间

    private var mRecorder: MediaRecorder? = null
    private var voiceFile: File? = null

    private var clipDrawable: ClipDrawable? = null
    private var binding: DialogVoiceRecordBinding? = null
    private var listener: VoiceRecordListener? = null

    private val runnable = Runnable {
        run {
            while (!isShouldInterrupt) {
                while (currentState == STATE_RECORDING) {
                    val currentLength =
                        ((System.currentTimeMillis() - mStartingTimeMillis - allPauseTimeLength)).toInt()
                    binding!!.currentLength = currentLength
                    val msg = Message()
                    msg.obj = currentLength
                    if (currentLength % 100 == 0)
                        activity.runOnUiThread {
                            clipDrawable!!.level = 10000 * msg.obj as Int / VOICE_RECORD_LENGTH!!
                        }
                    if (binding!!.currentLength!! > VOICE_RECORD_LENGTH!!)
                        pauseRecord()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater!!, R.layout.dialog_voice_record, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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
        releaseFolder()
        resetRecord()
        clipDrawable = iv_clip.drawable as ClipDrawable

        clipDrawable!!.level = 0
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(context!!, theme)


    private fun resetRecord() {
        if (mRecorder != null) {
            mRecorder!!.reset()
            FileUtils.delete(voiceFile!!.absolutePath)
        }
        lastPauseTime = 0L
        lastResumeTime = 0L
        allPauseTimeLength = 0L
        currentState = STATE_PREPARE
        isShouldInterrupt = true
        binding?.apply {
            currentLength = 0
            clipDrawable?.level = 0
            maxLength = VOICE_RECORD_LENGTH
            currentState = STATE_PREPARE
        }
    }

    private fun startRecord() {
        isShouldInterrupt = false
        voiceFile = File(CACHE_VOICE_PATH + "/voice_" + System.currentTimeMillis() + ".m4a")
        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(AUDIO_SOURSE)  // 设置录音的声音来源
        mRecorder?.setOutputFormat(OUTPUT_FORMAT) // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
        mRecorder?.setAudioEncoder(AUDIO_ENCODER) // 设置声音编码的格式
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
            Log.e("UploadPlugin", "voice record start failed !")
        }

    }

    /**
     * 暂停录制
     */
    private fun pauseRecord() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            mRecorder!!.pause()
            lastPauseTime = System.currentTimeMillis()
            currentState = STATE_PAUSE
            binding!!.currentState = currentState
        } else {
            Toast.makeText(activity, "您的手机系统版本过低，无法暂停语音录制，点击录制按钮可重录!", Toast.LENGTH_SHORT).show()
            stopRecord()
        }
    }

    /**
     * 恢复录制
     */
    private fun resumeRecord() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRecorder!!.resume()
            lastResumeTime = System.currentTimeMillis()
            allPauseTimeLength += lastResumeTime - lastPauseTime
            currentState = STATE_RECORDING
            binding!!.currentState = currentState
        }
    }

    /**
     * 停止录制
     */
    private fun stopRecord() {
        if (voiceFile != null && voiceFile!!.exists()) {
            mRecorder!!.stop()
            mRecorder!!.release()
            currentState = STATE_STOP
            binding!!.currentState = currentState
            isShouldInterrupt = true
        }
    }

    /**
     * 结束录制
     */
    private fun finishRecord() {
        if (voiceFile != null && voiceFile!!.exists()) {
            stopRecord()
            dismissAllowingStateLoss()
            val voiceItem = VoiceItem(voiceFile!!.absolutePath!!)
            listener!!.onFinish(voiceItem)
        }
    }

    /**
     * 取消录制
     */
    private fun cancelRecord() {
        if (mRecorder != null) {
            stopRecord()
            FileUtils.delete(voiceFile!!.absolutePath)
            mRecorder = null
        }
        dismissAllowingStateLoss()
    }

    fun releaseFolder() {
        val folder = File(CACHE_VOICE_PATH)
        if (!folder.exists()) {
            folder.mkdir()
        }
    }

    fun setStartForResultListener(listener: VoiceRecordListener) {
        this.listener = listener
    }

    interface VoiceRecordListener {
        fun onFinish(voiceItem: VoiceItem)
    }
}
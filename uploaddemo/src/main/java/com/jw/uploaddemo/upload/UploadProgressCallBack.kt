package com.jw.uploaddemo.upload

import com.jw.galary.video.VideoItem
import com.jw.uploaddemo.model.AuthorizationInfo
import com.jw.uploaddemo.model.OrgInfo
import org.json.JSONObject

/**
 * 创建时间：2019/5/2014:13
 * 更新时间 2019/5/2014:13
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
interface UploadProgressCallBack {
    fun onSuccess(index: Int, mediaId: Long, isVideo: Boolean, videoJson: JSONObject?)
    fun onProgress(index: Int, progress: Int, authorizationInfo: AuthorizationInfo?)
    fun onFail(
        index: Int,
        error: String,
        authorizationInfo: AuthorizationInfo?,
        path: String?,
        orgInfo: OrgInfo?,
        videoItem: VideoItem?
    )
}
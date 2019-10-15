package com.jw.uploadlibrary.upload

import com.jw.library.model.VideoItem
import com.jw.uploadlibrary.model.AuthorizationInfo
import com.jw.uploadlibrary.model.OrgInfo
import org.json.JSONObject

/**
 * 创建时间：2019/5/2014:13
 * 更新时间 2019/5/2014:13
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
interface UploadProgressCallBack {
    fun onSuccess(index: Int, mediaIds: ArrayList<Long>, isVideo: Boolean, videoJson: JSONObject?)
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
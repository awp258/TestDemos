package com.jw.uploaddemo

import com.jw.uploaddemo.model.AuthorizationInfo

/**
 * 创建时间：2019/5/2014:13
 * 更新时间 2019/5/2014:13
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
interface UploadProgressCallBack {
    fun onSuccess(index:Int,path:String)
    fun onFail(index:Int,error:String)
    fun onProgress(index:Int,progress:Int,authorizationInfo: AuthorizationInfo?)
}
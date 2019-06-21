package com.jw.uploaddemo.model

/**
 * 创建时间：2019/5/2017:19
 * 更新时间 2019/5/2017:19
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
class AuthorizationInfo {
    lateinit var authorization:String
    lateinit var bucket:String
    lateinit var region:String
    lateinit var sessionToken:String
    lateinit var tmpSecretId:String
    lateinit var tmpSecretKey:String
    var keys:ArrayList<String> = ArrayList()
    var mediaIds:ArrayList<Long> = ArrayList()
}
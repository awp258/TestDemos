package com.jw.uploaddemo.http.service

import com.jw.uploaddemo.model.AuthorizationInfo
import com.jw.uploaddemo.model.D
import com.jw.uploaddemo.model.E
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * 创建时间:2018/9/18 on 15:51
 * 创建人:jinwangx
 * 描述:
 */
interface GoChatService {

    /**
     * //获取必要数据 签名 存储桶 存储域
     */
    @POST("fileUpload/getAuthorization")
    fun getAuthorization(
        @Header("ticket") ticket: Long,
        @Body body: D
    ): Observable<JSONObject>

    /**
     * //获取视频签名
     */
    @POST("videoSign")
    fun getVideoSign(
        @Header("ticket") ticket: Long,
        @Body body: E
    ): Observable<JSONObject>

    /**
     * //获取必要数据 签名 存储桶 存储域
     */
    @POST("fileUpload/getMedias")
    fun getMedias(
        @Header("ticket") ticket: Long,
        @Body body: AuthorizationInfo
    ): Observable<JSONObject>
}
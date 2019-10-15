package com.jw.uploadlibrary.http.service

import com.jw.uploadlibrary.model.KeyReqInfo
import com.jw.uploadlibrary.model.MediaReq
import com.jw.uploadlibrary.model.OrgInfo
import com.jw.uploadlibrary.model.UserInfo
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
     * //获取用户标识
     */
    @POST("user/loginTest")
    fun loginTest(
        @Body body: UserInfo
    ): Observable<JSONObject>

    /**
     * //获取用户标识
     */
    @POST("user/login")
    fun login(
        @Body body: UserInfo
    ): Observable<JSONObject>

    /**
     * //获取必要数据 签名 存储桶 存储域
     */
    @POST("fileUpload/getAuthorization")
    fun getAuthorization(
        @Header("ticket") ticket: Long,
        @Body body: KeyReqInfo
    ): Observable<JSONObject>

    /**
     * //获取视频签名
     */
    @POST("videoSign")
    fun getVideoSign(
        @Header("ticket") ticket: Long,
        @Body body: OrgInfo
    ): Observable<JSONObject>

    /**
     * //获取必要数据 签名 存储桶 存储域
     */
    @POST("fileUpload/getMedias")
    fun getMedias(
        @Header("ticket") ticket: Long,
        @Body body: MediaReq
    ): Observable<JSONObject>
}
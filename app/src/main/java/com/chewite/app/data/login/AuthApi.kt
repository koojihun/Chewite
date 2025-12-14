package com.chewite.app.data.login

import retrofit2.http.GET
import retrofit2.http.Header

interface AuthApi {
    @GET("/account/my-info")
    suspend fun getMyInfo(@Header("AccessToken") token: String): UserInfo
}

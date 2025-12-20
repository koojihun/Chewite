package com.chewite.app.data.api.chewite

import com.chewite.app.data.model.SignUpInfo
import com.chewite.app.data.model.UserInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AccountApi {
    @GET("/account/my-info")
    suspend fun getMyInfo(@Header("AccessToken") token: String): UserInfo

    @POST("/account/sign-up")
    suspend fun signUp(@Header("AccessToken") token: String, @Body info: SignUpInfo): Response<Unit>
}

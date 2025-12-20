package com.chewite.app.data.model

import com.squareup.moshi.Json

data class UserInfo(
    val id: String,
    val authority: String,
    @Json(name = "social_id") val socialId: String,
    @Json(name = "auth_provider") val authProvider: String,
    val email: String,
    val status: String,
    val nickname: String,
    val picture: String?,
    val term: Term,
    @Json(name = "joined_at") val joinedAt: String,
    @Json(name = "expired_at") val expiredAt: String?
)

data class Term(
    @Json(name = "service_agreed") val serviceAgreed: String?,
    @Json(name = "policy_agreed") val policyAgreed: String?,
    @Json(name = "marketing_agreed") val marketingAgreed: String?
)

data class SignUpInfo(
    @Json(name = "service_agreed") val serviceAgreed: Boolean = true,
    @Json(name = "policy_agreed") val policyAgreed: Boolean = true,
    @Json(name = "marketing_agreed") val marketingAgreed: Boolean,
)


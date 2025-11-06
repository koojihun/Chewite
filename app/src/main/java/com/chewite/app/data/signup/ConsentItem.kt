package com.chewite.app.data.signup

const val CONSENT_SERVICE_KEY = "service"
const val CONSENT_PERSONAL_INFO_KEY = "personal_info"
const val CONSENT_MARKETING_KEY = "marketing"

data class ConsentItem (
    val key: String,
    val required: Boolean,
    val agreed: Boolean
)

data class ConsentState(
    val items: List<ConsentItem>
) {
    val isAgreedAllRequired: Boolean
        get() = items.filter { it.required }.all { it.agreed }
}
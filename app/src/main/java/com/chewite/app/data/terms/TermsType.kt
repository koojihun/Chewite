package com.chewite.app.data.terms

import com.chewite.app.R

enum class TermsType(val titleId: Int, val detailsId: Int) {

    SERVICE(R.string.terms_service_title, R.raw.terms_service_details),
    PERSONAL_INFO(R.string.terms_personal_info_title, R.raw.terms_personal_info_details),
    MARKETING(R.string.terms_marketing_title, R.raw.terms_marketing_details);

    companion object {
        const val EXTRA_KEY = "TERMS_TYPE"
    }
}
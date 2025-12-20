package com.chewite.app.ui.signup

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chewite.app.R
import com.chewite.app.ui.common.TopBar
import com.chewite.app.ui.theme.Gray500
import com.chewite.app.ui.theme.Gray900
import com.chewite.app.ui.theme.body2
import com.chewite.app.ui.theme.heading1

@Composable
fun AgreementDetailScreen(navController: NavController, typeName: String) {
    val context = LocalContext.current
    val agreementType = getAgreementType(typeName)
    val agreementContent = getAgreementContent(context, agreementType)
    Scaffold { innerPaddings ->
        Column(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize(),
        ) {
            TopBar(onButtonClicked = { navController.popBackStack() })
            Text(
                modifier = Modifier.padding(top = 32.dp, start = 16.dp),
                text = agreementType?.title ?: "약관",
                style = MaterialTheme.typography.heading1,
                color = Gray900
            )
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = agreementContent, style = MaterialTheme.typography.body2, color = Gray500
                )
            }
        }
    }
}

private fun getAgreementType(typeName: String): AgreementType? =
    runCatching { AgreementType.valueOf(typeName) }.getOrNull()

private fun getAgreementContent(context: Context, agreementType: AgreementType?): String =
    runCatching {
        agreementType?.let {
            context.assets.open("agreements/${it.fileName}").bufferedReader().use { it.readText() }
        } ?: "잘못된 접근입니다."
    }.getOrDefault("내용을 불러올 수 없습니다.")


@Composable
private fun TopBar(onButtonClicked: () -> Unit) {
    TopBar(horizontalAlignment = Alignment.End) {
        IconButton(
            onClick = onButtonClicked, modifier = Modifier
                .padding(end = 8.dp)
                .size(48.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_x),
                contentDescription = "뒤로 가기",
                tint = Gray900
            )
        }
    }
}
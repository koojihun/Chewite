package com.chewite.app.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chewite.app.R
import com.chewite.app.ui.Route
import com.chewite.app.ui.common.TopBar
import com.chewite.app.ui.common.clickableNoRipple
import com.chewite.app.ui.theme.Gray100
import com.chewite.app.ui.theme.Gray200
import com.chewite.app.ui.theme.Gray400
import com.chewite.app.ui.theme.Gray500
import com.chewite.app.ui.theme.Gray900
import com.chewite.app.ui.theme.Primary500
import com.chewite.app.ui.theme.body1
import com.chewite.app.ui.theme.body2
import com.chewite.app.ui.theme.titleLong2

@Composable
fun SignUpScreen(navController: NavController, viewModel: SignUpViewModel = hiltViewModel()) {

    val agreements by viewModel.agreements.collectAsStateWithLifecycle()

    Scaffold { innerPaddings ->
        Column(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize(),
        ) {
            SignUpTopBar(onClick = {
                navController.navigate(Route.LOGIN) { popUpTo(0) { inclusive = true } }
            })
            Description(Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.weight(1f))
            AgreeAllButton(agreements.all { it.isChecked }, viewModel)
            agreements.forEach {
                AgreeButton(it, viewModel) {
                    navController.navigate(Route.makeAgreementRoute(it.type.name))
                }
            }
            SignUpButton(
                viewModel.isAllRequiredAgreed(), Modifier.align(Alignment.CenterHorizontally), {
                    viewModel.signUpFinish({navController.navigate(Route.SIGNUP_FINISH)})
                })
        }
    }
}

@Composable
private fun SignUpTopBar(onClick: () -> Unit) {
    TopBar(horizontalAlignment = Alignment.Start) {
        IconButton(
            onClick = onClick, modifier = Modifier
                .padding(start = 8.dp)
                .size(48.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "뒤로 가기",
                tint = Gray900
            )
        }
    }
}

@Composable
private fun Description(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.signup_text_image),
        contentDescription = "서비스 이용 약관 동의",
        modifier = modifier.padding(top = 40.dp, start = 16.dp)
    )
}

@Composable
private fun AgreeAllButton(isChecked: Boolean, viewModel: SignUpViewModel) {
    val iconBackgroundColor = if (isChecked) Primary500 else Gray200
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp, color = Gray200, shape = RoundedCornerShape(8.dp)
            )
            .clickableNoRipple({
                viewModel.toggleAll(!isChecked)
            }), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 14.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(color = iconBackgroundColor)
                .padding(2.dp),
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "전체 동의하기", style = MaterialTheme.typography.body1, color = Gray900
        )
    }
}

@Composable
private fun AgreeButton(item: AgreementItem, viewModel: SignUpViewModel, onDetail: () -> Unit) {
    val iconBackgroundColor = if (item.isChecked) Primary500 else Gray200
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clickableNoRipple({
                viewModel.toggleAgreement(item)
            }), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 14.dp)
                .size(17.dp)
                .clip(CircleShape)
                .background(color = iconBackgroundColor)
                .padding(2.dp),
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "[${if (item.isRequired) "필수" else "선택"}]",
            style = MaterialTheme.typography.body2,
            color = if (item.isRequired) Primary500 else Gray500
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = item.title, style = MaterialTheme.typography.body2, color = Gray900
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onDetail, modifier = Modifier.size(48.dp)
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_forward),
                contentDescription = "${item.title} 약관 읽기",
                tint = Gray400
            )
        }
    }
}

@Composable
private fun SignUpButton(enabled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(top = 16.dp)
            .padding(16.dp)
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(100.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary500,
            contentColor = Color.White,
            disabledContainerColor = Gray100,
            disabledContentColor = Gray400
        )
    ) {
        Text(
            text = "회원가입",
            style = MaterialTheme.typography.titleLong2,
        )
    }
}

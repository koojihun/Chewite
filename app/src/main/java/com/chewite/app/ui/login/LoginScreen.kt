package com.chewite.app.ui.login

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.chewite.app.R
import com.chewite.app.ui.Route
import com.chewite.app.ui.common.TopBar
import com.chewite.app.ui.theme.Gray200
import com.chewite.app.ui.theme.Gray900
import com.chewite.app.ui.theme.KakaoYellow
import com.chewite.app.ui.theme.Primary500
import com.chewite.app.ui.theme.body1
import com.chewite.app.ui.theme.body2

@Composable
fun LoginScreen(navController: NavController) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    loginViewModel.clearAuthData()

    val context = LocalContext.current
    SubscribeLoginSideEffect(navController, loginViewModel)
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold { innerPaddings ->
            Column(
                modifier = Modifier
                    .padding(innerPaddings)
                    .fillMaxSize(),
            ) {
                LoginTopBar(onButtonClicked = { (context as? Activity)?.finish() })
                LogoDescription(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                ShowLoginButtons(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    navController = navController,
                    loginViewModel
                )
            }
        }
        Loading(loginViewModel)
    }
}

@Composable
private fun SubscribeLoginSideEffect(
    navController: NavController, loginViewModel: LoginViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(loginViewModel.sideEffect, lifecycleOwner) {
        loginViewModel.sideEffect.flowWithLifecycle(lifecycleOwner.lifecycle).collect { effect ->
            when (effect) {
                is LoginSideEffect.NavigateToHome -> {
                    navController.navigate(Route.RECIPE_LIST)
                }

                is LoginSideEffect.NavigateToSignUp -> {
                    navController.navigate(Route.SIGNUP)
                }
            }
        }
    }
}

@Composable
private fun LoginTopBar(onButtonClicked: () -> Unit) {
    TopBar(horizontalAlignment = Alignment.End) {
        IconButton(
            onClick = onButtonClicked, modifier = Modifier
                .padding(end = 8.dp)
                .size(48.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_x),
                contentDescription = "로그인 화면 닫기",
                tint = Gray900
            )
        }
    }
}

@Composable
private fun LogoDescription(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_logo),
            contentDescription = "로그인 화면 로고 이미지",
            modifier = Modifier
                .width(119.dp)
                .height(64.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "반려동물의 건강과 취향을 고려한\n수제간식 레시피를 만나보세요",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun ShowLoginButtons(
    modifier: Modifier = Modifier, navController: NavController, loginViewModel: LoginViewModel
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(top = 8.dp, bottom = 24.dp)
            .width(328.dp)
            .height(104.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        SocialLoginButton(
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = KakaoYellow, contentColor = Color.Black
            ),
            borderStroke = null,
            drawableId = R.drawable.ic_kakao,
            text = "카카오 로그인",
            contentDescription = "카카오 로그인 버튼 이미지",
        ) {
            navController.navigate(Route.SIGNUP)
        }
        SocialLoginButton(
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = Color.White, contentColor = Color.Black
            ),
            borderStroke = BorderStroke(width = 1.dp, color = Gray200),
            drawableId = R.drawable.ic_google,
            text = "Google 로그인",
            contentDescription = "구글 로그인 버튼 이미지",
        ) {
            loginViewModel.googleLogin(context)
//            navController.navigate("signup")
        }
    }
}

@Composable
private fun SocialLoginButton(
    buttonColors: ButtonColors,
    borderStroke: BorderStroke?,
    @DrawableRes drawableId: Int,
    text: String,
    contentDescription: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(40.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        colors = buttonColors,
        border = borderStroke
    ) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Loading(loginViewModel: LoginViewModel) {
    val isLoading by loginViewModel.isLoading.collectAsStateWithLifecycle()
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // 배경을 어둡게 (Dim 처리)
                .clickable( // 터치 이벤트를 여기서 먹어버려서 뒤쪽 UI 클릭 방지
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary500)
        }
    }
}
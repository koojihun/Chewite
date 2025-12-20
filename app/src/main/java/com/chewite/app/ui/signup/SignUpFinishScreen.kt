package com.chewite.app.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.chewite.app.R
import com.chewite.app.ui.Route
import com.chewite.app.ui.theme.Gray500
import com.chewite.app.ui.theme.Gray900
import com.chewite.app.ui.theme.Primary500
import com.chewite.app.ui.theme.body2
import com.chewite.app.ui.theme.heading1
import com.chewite.app.ui.theme.titleLong2

@Composable
fun SignUpFinishScreen(navController: NavController) {

    Scaffold { innerPaddings ->
        Column(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize(),
        ) {
            Description(
                Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.weight(1f))
            FinishButton { navController.navigate(Route.RECIPE_LIST) }
        }
    }
}

@Composable
private fun Description(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.signup_finish_image),
        contentDescription = "서비스 이용 약관 동의",
        modifier = modifier
            .padding(top = 256.dp)
            .width(252.dp)
            .height(48.dp)
    )
    Text(
        modifier = modifier.padding(top = 40.dp),
        text = "회원가입이 완료되었어요",
        style = MaterialTheme.typography.heading1,
        color = Gray900
    )
    Text(modifier = modifier.padding(top = 8.dp),
        text = "반려동물의 건강과 취향을 고려한\n수제간식 레시피를 만나보세요",
        style = MaterialTheme.typography.body2,
        color = Gray500,
        textAlign = TextAlign.Center)
}

@Composable
private fun FinishButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary500, contentColor = Color.White
        )
    ) {
        Text(
            text = "츄이트 시작하기",
            style = MaterialTheme.typography.titleLong2,
        )
    }
}
package com.chewite.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.chewite.app.ui.theme.Gray200

@Composable
fun TopBar(
    horizontalAlignment: Alignment.Horizontal, content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = Gray200,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}
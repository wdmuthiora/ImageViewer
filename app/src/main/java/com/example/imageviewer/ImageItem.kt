package com.example.imageviewer


import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.random.Random

data class ImageItem(
    val id: Long,
    val name: String,
    val uri: Uri,
    val type: String,
    val color: Color = generateRandomColor(),
    val size: Dp,
)


fun generateRandomColor():Color{
    val random = Random.Default
    return Color(
        red = random.nextFloat(),
        green = random.nextFloat(),
        blue = random.nextFloat(),
        alpha = 1f
    )
}


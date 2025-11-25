package com.example.aplicaciongrupo7.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SafeGameImage(
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier
) {

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = title,
        modifier = modifier.size(70.dp)
    )
}

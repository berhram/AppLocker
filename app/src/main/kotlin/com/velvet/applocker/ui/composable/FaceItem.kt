package com.velvet.applocker.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.velvet.applocker.infra.Face

@Composable
fun FaceItem(imageId: Int, textId: Int, face: Face, onChoosing: (Face) -> Unit) {
    Column(
        modifier = Modifier.clickable { onChoosing(face) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = imageId), contentDescription = "Image with id $imageId")
        Text(text = stringResource(id = textId))
    }
}
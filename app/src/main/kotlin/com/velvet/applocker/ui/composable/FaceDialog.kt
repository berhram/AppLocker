package com.velvet.applocker.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.velvet.applocker.R
import com.velvet.applocker.infra.Face

@Composable
fun FacesDialog(
    faces: List<Face>,
    onChoosing: (Face) -> Unit,
    state: MutableState<Boolean>
) {
    Dialog({ state.value = false }) {
        Surface(modifier = Modifier.clip(MaterialTheme.shapes.medium), color = MaterialTheme.colors.surface) {
            Column(Modifier.padding(MaterialTheme.spacing.small)) {
                Text(
                    text = stringResource(id = R.string.face_change_dialog_title),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.primary
                )

                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))

                Text(
                    text = stringResource(id = R.string.face_change_dialog_text),
                    style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onBackground
                )

                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(faces) { face ->
                        FaceItem(
                            imageId = face.iconId,
                            textId = face.textId,
                            face = face, onChoosing = onChoosing
                        )
                    }
                }
                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = { state.value = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        }
    }
}
package com.velvet.applocker.ui.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.velvet.applocker.R

@Composable
fun PasswordChangeDialog(
    state: MutableState<Boolean>,
    setNewPassword: (String) -> Unit,
    @StringRes errorTextId: Int?
) {
    var newPassword by remember { mutableStateOf("") }

    Dialog({ state.value = false }) {
        Surface(color = MaterialTheme.colors.background) {
            Column(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colors.background)
                    .padding(MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(id = R.string.change_password_dialog_title),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.primary
                )

                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))

                Text(
                    text = stringResource(id = R.string.change_password_dialog_text),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground
                )

                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))

                if (errorTextId != null) {
                    Text(
                        text = stringResource(id = errorTextId),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.error
                    )
                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                }

                OutlinedTextField(
                    value = newPassword, onValueChange = { newPassword = it },
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(R.string.new_password_enter),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.caption
                        )
                    }
                )

                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { state.value = false }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }

                    Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            setNewPassword(newPassword)
                            state.value = false
                        }) {
                        Text(text = stringResource(id = R.string.apply))
                    }
                }
            }
        }
    }
}
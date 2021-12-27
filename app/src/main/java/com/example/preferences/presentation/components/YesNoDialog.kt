package com.example.preferences.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.preferences.R

@Composable
fun YesNoDialog(
    modifier: Modifier = Modifier,
    message: Int,
    onDismiss: (()->Unit)?,
    onAccept: () -> Unit
) {
    AlertDialog(
        modifier = modifier
            .background(MaterialTheme.colors.surface)
            .padding(12.dp),
        onDismissRequest = { onDismiss?.invoke() },
        title = {
            Text(
                text = stringResource(R.string.attention),
                style = TextStyle(
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = stringResource(message),
                style = TextStyle(
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp
                )
            )
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier.weight(4.5f),
                    onClick = { onAccept() }
                ) {
                    Text(
                        text = stringResource(R.string.accept_option),
                        style = MaterialTheme.typography.button)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.weight(4.5f),
                    onClick = { onDismiss?.invoke() }
                ) {
                    Text(
                        text = stringResource(R.string.cancel_option),
                        style = MaterialTheme.typography.button)
                }
            }
        }
    )
}
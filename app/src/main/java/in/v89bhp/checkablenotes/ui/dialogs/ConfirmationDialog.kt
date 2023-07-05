package `in`.v89bhp.checkablenotes.ui.dialogs

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import `in`.v89bhp.checkablenotes.R
import `in`.v89bhp.checkablenotes.ui.theme.black
import `in`.v89bhp.checkablenotes.ui.theme.white

@Composable
fun ConfirmationDialog(
    @StringRes title: Int,
    @StringRes text: Int,
    onConfirmation: (Boolean) -> Unit
) {
    AlertDialog( // IMPORTANT: The following three color parameters can be
        // omitted upon which colors from current theme's colorScheme will be used:
        containerColor = white,
        titleContentColor = black,
        textContentColor = black,

        onDismissRequest = { },
        title = {
            Text(text = stringResource(title))
        },
        text = {
            Text(text = stringResource(text))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(true)
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onConfirmation(false)
                }
            ) {
                Text(stringResource(R.string.cancel))
            }
        })

}
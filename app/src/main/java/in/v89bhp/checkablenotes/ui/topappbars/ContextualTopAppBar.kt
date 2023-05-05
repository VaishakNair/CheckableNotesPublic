package `in`.v89bhp.checkablenotes.ui.topappbars


import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import `in`.v89bhp.checkablenotes.R

@Composable
fun ContextualTopAppBar(
    isContextual: Boolean,
    normalTitle: String,
    contextualTitle: String,
    normalActions: @Composable () -> Unit,
    contextualActions: @Composable () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(title = {
        Text(text = if (isContextual) contextualTitle else normalTitle)
    },
        navigationIcon = if (isContextual) {
            {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        } else null,
        actions = if (isContextual) {
            { contextualActions() }
        } else {
            {normalActions()}
        }
    )
}

@Preview
@Composable
fun TopAppBarPreview() {
    MaterialTheme() {
        Scaffold(topBar = {
            ContextualTopAppBar(
                isContextual = true,
                normalTitle = stringResource(id = R.string.app_name),
                contextualTitle = stringResource(R.string.x_selected).format(1),// TODO
                normalActions = { },
                contextualActions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                },
                onClose = {/* TODO */ },

                )
        }) {
            Text("Hello, world", modifier = Modifier.padding(it))
        }
    }

}
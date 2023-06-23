package `in`.v89bhp.checkablenotes.ui.topappbars


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import `in`.v89bhp.checkablenotes.R

@OptIn(ExperimentalMaterial3Api::class)
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
        } else {
            {}
        },
        actions = if (isContextual) {
            { contextualActions() }
        } else {
            { normalActions() }
        }
    )
}
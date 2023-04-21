package `in`.v89bhp.checkablenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import `in`.v89bhp.checkablenotes.composables.CheckableList
import `in`.v89bhp.checkablenotes.ui.theme.CheckableNotesTheme
import `in`.v89bhp.checkablenotes.viewmodels.MainActivityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MyScreenContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    CheckableNotesTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}


@Composable
fun MyScreenContent(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
//    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
//        mutableStateOf(TextFieldValue("", TextRange(0, 7)))
//    }
    var text = viewModel.text
    val titles = listOf("Input", "Checkable List")
    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
        if (selectedTabIndex == 0) { // Tab 1
            TextField(
                value = text,
                onValueChange = {
                    viewModel.text = it
                    viewModel.updateList(it)
                },
                label = { Text("Enter Items Line by Line:") },
                modifier = Modifier.fillMaxSize()
            )
        } else { // Tab 2
            CheckableList(viewModel.list, { checkableItem, newValue ->
                viewModel.onCheckedChange(checkableItem, newValue)
            })
        }
    }
}


//@Preview(fontScale = 1.2f, showBackground = true)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        MyScreenContent()
    }
}
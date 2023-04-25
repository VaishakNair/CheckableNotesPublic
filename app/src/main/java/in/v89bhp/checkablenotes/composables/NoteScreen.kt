package `in`.v89bhp.checkablenotes.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import `in`.v89bhp.checkablenotes.viewmodels.MainActivityViewModel

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
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
                value = viewModel.text,
                onValueChange = {
                    viewModel.text = it
                    viewModel.updateList(it)
                },
                label = { Text("Enter Items Line by Line:") },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        } else { // Tab 2
            CheckableList(checkableItems = viewModel.list,
                modifier = Modifier.padding(16.dp),
                onCheckedChange = { checkableItem, newValue ->
                    viewModel.onCheckedChange(checkableItem, newValue)
                })
        }
    }
}
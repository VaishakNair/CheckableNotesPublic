package `in`.v89bhp.checkablenotes.ui.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import `in`.v89bhp.checkablenotes.data.CheckableItem

@Composable
fun Note(
    fileName: String,
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    if (fileName != "newNote") { viewModel.loadNote(fileName) }

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val titles = listOf("Input", "Checkable List")

    Column(modifier = modifier) {
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

@Composable
fun CheckableList(
    checkableItems: List<CheckableItem>,
    onCheckedChange: (CheckableItem, newValue: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(checkableItems,
            key = { it.id }) { checkableItem ->
            ItemCard(
                checkableItem = checkableItem,
                onCheckedChange = { newValue -> onCheckedChange(checkableItem, newValue) }
            )
        }
    }
}

@Composable
fun ItemCard(
    checkableItem: CheckableItem,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier

) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Checkbox(
                checked = checkableItem.isChecked,
                onCheckedChange = onCheckedChange
            )
            Spacer(Modifier.width(10.dp))
            Text(
                checkableItem.message,
                style = MaterialTheme.typography.body1
            )

        }
    }
}
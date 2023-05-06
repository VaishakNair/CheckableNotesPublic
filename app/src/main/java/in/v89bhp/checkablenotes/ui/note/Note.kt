package `in`.v89bhp.checkablenotes.ui.note

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import `in`.v89bhp.checkablenotes.R
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.data.nameischeckedequals
import `in`.v89bhp.checkablenotes.ui.dialogs.ConfirmationDialog
import `in`.v89bhp.checkablenotes.ui.home.HomeViewModel

@Composable
fun Note(
    fileName: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {

    if (viewModel.firstTime) { // View model has been loaded for the first time. Load note (if any)
        viewModel.loadNote(fileName)
        viewModel.firstTime = false
    }


    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(stringResource(id = R.string.app_name))
            },
            actions = {
                IconButton(onClick = { viewModel.openDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_note)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBackPressed(
                        fileName,
                        viewModel,
                        homeViewModel,
                        navigateBack
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
    }) { contentPadding ->

        var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
        val titles = listOf("Note", "Checkable List")

        Column(modifier = modifier.padding(contentPadding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
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
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
            } else { // Tab 2
                CheckableList(checkableItems = viewModel.list,
                    modifier = Modifier.padding(16.dp),
                    onCheckedChange = { checkableItem, newValue ->
                        viewModel.onCheckedChange(checkableItem, newValue)
                    })
            }

            BackHandler(true) {
                onBackPressed(
                    fileName,
                    viewModel,
                    homeViewModel,
                    navigateBack
                )
            }
        }
        if (viewModel.openDeleteDialog) {
            ConfirmationDialog(title = R.string.delete_note,
                text = R.string.delete_this_note,
                onConfirmation = { confirmed ->
                    if (confirmed) {
                        homeViewModel.deleteNotes(listOf(fileName))
                        navigateBack()
                    }
                    viewModel.openDeleteDialog = false
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
                checkableItem.name,
                style = MaterialTheme.typography.body1
            )

        }
    }
}

fun onBackPressed(
    fileName: String,
    viewModel: NoteViewModel,
    homeViewModel: HomeViewModel,
    navigateBack: () -> Unit
) {
    if (viewModel.text.text.trim() == "") {// Note is empty. Delete the existing note (if any)
        homeViewModel.deleteNotes(listOf(fileName))
    } else {
        viewModel.loadedNote?.let { loadedNote -> // Not a new note:
            if (loadedNote.text.text != viewModel.text.text || !(loadedNote.list nameischeckedequals viewModel.list)) {// Note has been updated (either text or checkable list):
                homeViewModel.saveNote(
                    fileName,
                    viewModel.text,
                    viewModel.list
                )
            }
        } ?: homeViewModel.saveNote(
            fileName,
            viewModel.text,
            viewModel.list
        ) // New note. Save it.

    }
    navigateBack()
}


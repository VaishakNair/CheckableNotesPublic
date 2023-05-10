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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import `in`.v89bhp.checkablenotes.R
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.data.nameischeckedequals
import `in`.v89bhp.checkablenotes.ui.dialogs.ConfirmationDialog
import `in`.v89bhp.checkablenotes.ui.home.HomeViewModel
import `in`.v89bhp.checkablenotes.ui.theme.green
import `in`.v89bhp.checkablenotes.ui.theme.light_green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Note(
    fileName: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    noteViewModel: NoteViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {

    if (noteViewModel.firstTime) { // View model has been loaded for the first time. Load note (if any)
        noteViewModel.loadNote(fileName)
        noteViewModel.firstTime = false
    }


    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(stringResource(id = R.string.app_name))
            },
            actions = {
                IconButton(onClick = { noteViewModel.openDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_note)
                    )
                }
                IconButton(onClick = { noteViewModel.openShareDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.share_note)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBackPressed(
                        fileName,
                        noteViewModel,
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

                            BadgedBox(
                                badge = {
                                    if (index == 1 && noteViewModel.pendingItemsCount > 0) {
                                        Badge {

                                            Text(
                                                noteViewModel.pendingItemsCount.toString(),
                                                modifier = Modifier.semantics {
                                                    contentDescription =
                                                        "${noteViewModel.pendingItemsCount} pending items"
                                                }
                                            )
                                        }
                                    }
                                }) {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }


                        }
                    )
                }
            }
            if (selectedTabIndex == 0) { // Tab 1
                TextField(
                    value = noteViewModel.text,
                    onValueChange = {
                        noteViewModel.text = it
                        noteViewModel.updateList(it)
                    },
                    label = { Text("Enter Items Line by Line:") },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
            } else { // Tab 2
                CheckableList(checkableItems = noteViewModel.list,
                    modifier = Modifier.padding(16.dp),
                    onCheckedChange = { checkableItem, newValue ->
                        noteViewModel.onCheckedChange(checkableItem, newValue)
                    })
            }

            BackHandler(true) {
                onBackPressed(
                    fileName,
                    noteViewModel,
                    homeViewModel,
                    navigateBack
                )
            }
        }
        if (noteViewModel.openDeleteDialog) {
            ConfirmationDialog(title = R.string.delete_note,
                text = R.string.delete_this_note,
                onConfirmation = { confirmed ->
                    if (confirmed) {
                        homeViewModel.deleteNotes(listOf(fileName))
                        navigateBack()
                    }
                    noteViewModel.openDeleteDialog = false
                })
        }

        if (noteViewModel.openShareDialog) {
            // TODO
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
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (checkableItem.isChecked) light_green
            else MaterialTheme.colorScheme.surfaceVariant
        )

    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Checkbox(
                checked = checkableItem.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = green)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                checkableItem.name,
                style = MaterialTheme.typography.bodyLarge
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


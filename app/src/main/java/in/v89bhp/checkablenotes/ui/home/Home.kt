package `in`.v89bhp.checkablenotes.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.v89bhp.checkablenotes.R
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.data.Note
import `in`.v89bhp.checkablenotes.ui.dialogs.ConfirmationDialog
import `in`.v89bhp.checkablenotes.ui.topappbars.ContextualTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navigateToNote: (String) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    ),
) {

    if (homeViewModel.firstTime) { // View model has been loaded for the first time. Load notes (if any)
        homeViewModel.loadNotesInitial()
        homeViewModel.firstTime = false
    }

    Scaffold(
        floatingActionButton = if (homeViewModel.selectedFileNames.isEmpty()) {
            {
                LargeFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer, // This is not needed and is automatically set to this value by the system.
                    onClick = { navigateToNote("${System.currentTimeMillis()}.json") }) {
                    Icon(Icons.Filled.Add, "New note")
                }
            }
        } else {
            {}
        },
        topBar = {
            ContextualTopAppBar(
                isContextual = homeViewModel.selectedFileNames.isNotEmpty(),
                normalTitle = stringResource(id = R.string.app_name),
                contextualTitle = stringResource(R.string.x_selected).format(homeViewModel.selectedFileNames.size),// TODO
                normalActions = { },
                contextualActions = {
                    IconButton(onClick = { homeViewModel.openDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }

                    IconButton(onClick = {
                        if (homeViewModel.allSelected) { // All items have been selected. De-select them:
                            homeViewModel.deselectAll()
                        } else {
                            homeViewModel.selectAll()
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = if (homeViewModel.allSelected) R.drawable.baseline_deselect_24 else R.drawable.baseline_select_all_24),
                            contentDescription = stringResource(if (homeViewModel.allSelected) R.string.deselect_all else R.string.select_all)
                        )
                    }
                },
                onClose = { homeViewModel.selectedFileNames.clear() },
            )
        }
    ) { contentPadding ->
        NotesGrid(
            fileNames = homeViewModel.fileNamesList,
            selectedFileNames = homeViewModel.selectedFileNames,
            notes = homeViewModel.notesList,
            navigateToNote = navigateToNote,
            onLongPress = { fileName ->
                if (fileName !in homeViewModel.selectedFileNames) { // Ignore subsequent long-presses from the same item.
                    homeViewModel.selectedFileNames.add(fileName)
                }
            },
            modifier = modifier.padding(contentPadding)
        )

        if (homeViewModel.openDeleteDialog) {
            ConfirmationDialog(title = R.string.delete_note,
                text = R.string.delete_selected_notes,
                onConfirmation = { confirmed ->
                    if (confirmed) {
                        homeViewModel.deleteNotes(homeViewModel.selectedFileNames)
                    }
                    homeViewModel.openDeleteDialog = false
                })
        }
    }


}

@Composable
fun NotesGrid(
    fileNames: List<String>,
    selectedFileNames: MutableList<String>,
    notes: List<Note>,
    navigateToNote: (String) -> Unit,
    onLongPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier.padding(16.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(notes) { index, note ->
            NoteCard(
                note = note.text.text,
                onClick = {
                    if (fileNames[index] in selectedFileNames) {// The card has been selected by a long press. De-select it
                        selectedFileNames.remove(fileNames[index])
                    } else if (selectedFileNames.isNotEmpty()) {
                        selectedFileNames.add(fileNames[index])
                    } else {// Navigate to the note represented by the card:
                        navigateToNote(fileNames[index])
                    }
                },
                onLongPress = { onLongPress(fileNames[index]) },
                isSelected = fileNames[index] in selectedFileNames,
                isCABActivated = selectedFileNames.isNotEmpty()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    note: String,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    isSelected: Boolean,
    isCABActivated: Boolean,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .size(width = 100.dp, height = 100.dp)
            .semantics { selected = isSelected }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onClick() }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else
                MaterialTheme.colorScheme.surfaceVariant
        )
    )
    {
        Box() {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(0.75f)
                        .align(Alignment.Top)
                )
                if (isSelected || isCABActivated) {
                    Icon(
                        painter = if (isSelected) rememberVectorPainter(Icons.Outlined.CheckCircle) else painterResource(
                            id = R.drawable.outline_circle_24
                        ),
                        contentDescription = null,
                        modifier = Modifier.weight(0.25f)
                    )
                }
            }
            Badge(modifier = Modifier.padding(5.dp).align(Alignment.TopEnd)) {
                Text(
                    "33",
                    modifier = Modifier
                        .semantics {
                            contentDescription =
                                "33 pending items"
                        }
                )
            }
        }
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun NoteCardPreview() {
    NoteCard(
        note = "Tomatodfdfdfdfdf\nPotato\nDates",
        onClick = {},
        onLongPress = {},
        isSelected = true,
        isCABActivated = false,
        modifier = Modifier.padding(8.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun ScaffoldPreview() {
    Home(navigateToNote = {}, modifier = Modifier.padding(0.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun NotesGridPreview() {
    val notes = listOf(
        Note(
            TextFieldValue(text = "a\nb\nc"), listOf(
                CheckableItem(0, "a", true),
                CheckableItem(1, "b", false),
                CheckableItem(2, "c", true)
            )
        ),
        Note(
            TextFieldValue(text = "g\nh\ni"), listOf(
                CheckableItem(0, "g", false),
                CheckableItem(1, "h", false),
                CheckableItem(2, "i", true)
            )
        )
    )
    NotesGrid(
        fileNames = listOf("3", "2", "1"),
        selectedFileNames = mutableListOf<String>(),
        notes = notes,
        navigateToNote = {},
        onLongPress = {})
}
package `in`.v89bhp.checkablenotes.ui.home

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.v89bhp.checkablenotes.R
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.data.Note
import `in`.v89bhp.checkablenotes.ui.dialogs.ConfirmationDialog
import `in`.v89bhp.checkablenotes.ui.progressbars.CircularProgress
import `in`.v89bhp.checkablenotes.ui.theme.blue
import `in`.v89bhp.checkablenotes.ui.theme.light_green
import `in`.v89bhp.checkablenotes.ui.theme.white
import `in`.v89bhp.checkablenotes.ui.topappbars.ContextualTopAppBar
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navigateToNote: (String) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    ),
) {

    LaunchedEffect(homeViewModel) {
        Log.i("LE", "Home load notes")
        delay(150) // Delay for NoteViewModel.saveNote() coroutine to complete. Useful when coming back after creating a new note.
        homeViewModel.loadNotesInitial()
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
                contextualTitle = stringResource(
                    R.string.x_selected,
                    homeViewModel.selectedFileNames.size
                ),
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
        Box(modifier = Modifier.fillMaxSize()) {

            if (homeViewModel.isLoadingNotes) {
                CircularProgress(text = stringResource(R.string.loading))
            }
            if (homeViewModel.fileNamesList.isNotEmpty()) {
                NotesGrid(
                    fileNames = homeViewModel.fileNamesList,
                    selectedFileNames = homeViewModel.selectedFileNames,
                    notes = homeViewModel.notesList,
                    navigateToNote = navigateToNote,
                    onLongPress = { fileName ->
                        if (homeViewModel.selectedFileNames.isEmpty()) { // First long press. Vibrate
                            homeViewModel.longPressVibrate()
                        }
                        if (fileName !in homeViewModel.selectedFileNames) { // Ignore subsequent long-presses from the same item.
                            homeViewModel.selectedFileNames.add(fileName)
                        }
                    },
                    modifier = Modifier.padding(contentPadding)
                )
            }
            if (homeViewModel.noNotes) { // No notes. Show hint:
                Text(
                    text = stringResource(R.string.no_notes),
                    modifier = Modifier
                        .padding(contentPadding)
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

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

@SuppressLint("SimpleDateFormat")
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
                title = note.title.text,
                note = note.text.text,
                onClick = {
                    if (fileNames[index] in selectedFileNames) {// The card has been selected by a long press. De-select it
                        selectedFileNames.remove(fileNames[index])
                    } else if (selectedFileNames.isNotEmpty()) {// Single click after a long press. Add file name to selected file names list:
                        selectedFileNames.add(fileNames[index])
                    } else {// Navigate to the note represented by the card:
                        navigateToNote(fileNames[index])
                    }
                },
                onLongPress = { onLongPress(fileNames[index]) },
                isSelected = fileNames[index] in selectedFileNames,
                isCABActivated = selectedFileNames.isNotEmpty(),
                pendingItemsCount = note.list.sumOf { if (!it.isChecked) 1 as Int else 0 },
                totalItemsCount = note.list.size,
                lastModified = SimpleDateFormat("d MMM yyyy, h:mm a").format(
                    File(
                        LocalContext.current.filesDir,
                        fileNames[index]
                    ).lastModified()
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    title: String,
    note: String,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    isSelected: Boolean,
    isCABActivated: Boolean,
    pendingItemsCount: Int,
    totalItemsCount: Int,
    lastModified: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .size(width = 110.dp, height = 120.dp)
            .semantics { selected = isSelected }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onClick() }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
            else if (pendingItemsCount == 0) light_green
            else MaterialTheme.colorScheme.surfaceVariant
        )
    )
    {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Title:
            if(title.isNotEmpty()) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Note summary and CAB selection button:
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .weight(0.8f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(0.75f)
                        .align(Alignment.Top)
                )
                Column(
                    modifier = Modifier
                        .weight(0.25f)
                        .align(Alignment.CenterVertically)
                ) {
                    if (isSelected || isCABActivated) {
                        Icon(
                            painter = if (isSelected) rememberVectorPainter(Icons.Outlined.CheckCircle) else painterResource(
                                id = R.drawable.outline_circle_24
                            ),
                            contentDescription = null,

                            )
                    }
                }

            }

            // Blue checked/ pending items count row at bottom:
            ItemsCount(
                totalItemsCount - pendingItemsCount, pendingItemsCount,
                modifier = Modifier.align(Alignment.End)
            )
// TODO
//            Text(
//                text = lastModified,
//                style = MaterialTheme.typography.labelSmall,
//                modifier = Modifier
//                    .padding(bottom = 8.dp, start = 8.dp)
//                    .weight(0.2f)
//
//            )
        }
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun NoteCardPreview() {
    NoteCard(
        title = "Title 1",
        note = "Tomatodfdfdfdfdf\nPotato\nDates",
        onClick = {},
        onLongPress = {},
        isSelected = false,
        isCABActivated = false,
        pendingItemsCount = 3,
        totalItemsCount = 5,
        lastModified = "13 Jan 2023 3:01 AM",
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
            TextFieldValue(text = "Alphabets"),
            TextFieldValue(text = "a\nb\nc"), listOf(
                CheckableItem(0, "a", true),
                CheckableItem(1, "b", false),
                CheckableItem(2, "c", true)
            )
        ),
        Note(
            TextFieldValue(text = "Alphabets (set 2)"),
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


//@Preview(showBackground = true)
@Composable
fun ItemsCount(
    completedItemsCount: Int, pendingItemsCount: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = blue,
) {
    Row(
        modifier = modifier
            .padding(end = 4.dp, bottom = 4.dp)
            .size(60.dp, 20.dp)
            .background(color = backgroundColor, shape = CircleShape)
            .padding(start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .border(border = BorderStroke(1.0.dp, color = white)),
            contentAlignment = Alignment.Center
        ) {
        }

        Text(
            text = pendingItemsCount.toString(),
            color = white,
            fontSize = 10.sp
        )
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .border(BorderStroke(1.0.dp, color = white))
                .background(color = white),
            contentAlignment = Alignment.Center
        ) {

            Icon(Icons.Default.Check, contentDescription = "", tint = blue)
        }
        Text(
            text = completedItemsCount.toString(),
            color = white,
            fontSize = 10.sp
        )

    }
}
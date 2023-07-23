package `in`.v89bhp.checkablenotes.ui.home

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import `in`.v89bhp.checkablenotes.ui.theme.black
import `in`.v89bhp.checkablenotes.ui.theme.blue
import `in`.v89bhp.checkablenotes.ui.theme.light_green
import `in`.v89bhp.checkablenotes.ui.theme.light_grey_2
import `in`.v89bhp.checkablenotes.ui.theme.light_white
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

    val context = LocalContext.current

    LaunchedEffect(homeViewModel) {
        Log.i("LE", "Home load notes")
        delay(150) // Delay for NoteViewModel.saveNote() coroutine to complete. Useful when coming back after creating a new note.
        homeViewModel.loadNotesInitial(context)
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = if (homeViewModel.selectedFileNames.isEmpty()) {
            {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(110.dp)
                        .background(color = black)
                        .padding(20.dp)
                ) {
                    LargeFloatingActionButton(
                        shape = CircleShape,
                        containerColor = light_white,
                        contentColor = black,
                        onClick = { navigateToNote("${System.currentTimeMillis()}.json") }) {
                        Icon(
                            imageVector = Icons.Filled.Add, contentDescription = "New note",
                            modifier = Modifier.size(60.dp)
                        )
                    }
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
                            contentDescription = stringResource(R.string.delete),
                            tint = white
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
                            contentDescription = stringResource(if (homeViewModel.allSelected) R.string.deselect_all else R.string.select_all),
                            tint = white
                        )
                    }
                },
                onClose = { homeViewModel.selectedFileNames.clear() },
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(listOf(black, light_grey_2)))
        ) {

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
                            homeViewModel.longPressVibrate(context)
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
                        homeViewModel.deleteNotes(context, homeViewModel.selectedFileNames)
                    }
                    homeViewModel.openDeleteDialog = false
                })
        }
    }

    if (homeViewModel.selectedFileNames.isNotEmpty()) {
        BackHandler {
            homeViewModel.selectedFileNames.clear()
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
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
    LazyVerticalStaggeredGrid(
        modifier = modifier.padding(16.dp),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(notes) { index, note ->
            NoteCard(
                title = note.title.text,
                checkableList = note.list,
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
    checkableList: List<CheckableItem>,
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
            .heightIn(min = 40.dp)
            .width(110.dp)
            .semantics { selected = isSelected }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onClick() }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
            else if (pendingItemsCount == 0 && totalItemsCount > 0) light_green
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = black
        )
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Title:
            // Show it irrespective of whether title is available or not,
            // otherwise it will mess up the layout:
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            checkableList.sortedBy { checkableItem -> checkableItem.id } // Sort checkable list in the order entered by user
                .forEachIndexed { index, checkableItem ->
                    if (index < 10) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            // Checkbox:
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .border(BorderStroke(1.0.dp, color = black)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (checkableItem.isChecked) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "",
                                        tint = blue
                                    )
                                }
                            }
                            // Checkable list item:
                            Text(
                                text = checkableItem.name,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }


            // Blue checked/ pending items count row at bottom:
            ItemsCount(
                totalItemsCount - pendingItemsCount, pendingItemsCount,
                modifier = Modifier.align(
                    Alignment.End,
                ),
                backgroundColor = if (isSelected) light_grey_2 else blue
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
fun DynamicNoteCardPreview() {
    Card(
        modifier = Modifier
            .width(110.dp)
            .heightIn(min = 140.dp)
    ) {
        Column() {
            Text(text = "A\nB\nC\nD\nE\nF\nG\nH\nI\nJ")
        }

    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun NoteCardPreview() {
    NoteCard(
        title = "Title 1",
        checkableList = listOf(
            CheckableItem(id = 0, name = "Tomato", isChecked = false),
            CheckableItem(id = 1, name = "Potato", isChecked = true)
        ),
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
            .size(60.dp, 20.dp)
            .background(color = backgroundColor, shape = CircleShape)
            .padding(start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        // Empty checkbox:
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

        // Checked checkbox:
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
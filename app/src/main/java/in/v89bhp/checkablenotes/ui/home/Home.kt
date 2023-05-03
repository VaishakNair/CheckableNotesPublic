package `in`.v89bhp.checkablenotes.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.v89bhp.checkablenotes.Screen
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.data.Note

@Composable
fun Home(
    navigateToNote: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),

    ) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navigateToNote("newNote") }) {
                Icon(Icons.Filled.Add, "New note")
            }
        }
    ) { contentPadding ->
        NotesGrid(
            fileNames = viewModel.fileNamesList,
            notes = viewModel.notesList,
            navigateToNote = navigateToNote,
            modifier = modifier.padding(contentPadding)
        )
    }


}

@Composable
fun NotesGrid(
    fileNames: List<String>,
    notes: List<Note>,
    navigateToNote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier.padding(8.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(notes) { index, note ->
            NoteCard(note = note.text.text,
                onClick = { navigateToNote(fileNames[index]) })
        }
    }
}

@Composable
fun NoteCard(
    note: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .size(width = 100.dp, height = 100.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = note,
            modifier = Modifier.padding(8.dp),
            maxLines = 4
        )
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun NoteCardPreview() {
    NoteCard(
        note = "Tomato\nPotato\nDates",
        onClick = {},
        modifier = Modifier.padding(8.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun ScaffoldPreview() {
    Home({}, modifier = Modifier.padding(0.dp))
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
    NotesGrid(fileNames = listOf("3", "2", "1"), notes = notes, navigateToNote = {})
}
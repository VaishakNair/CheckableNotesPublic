package `in`.v89bhp.checkablenotes.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.v89bhp.checkablenotes.data.Note

@Composable
fun Home(
    navigateToNote: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),

    ) {
    NotesGrid(viewModel.notesList)
}

@Composable
fun NotesGrid(notes: List<Note>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2)
    ) {
        items(notes) { note ->
            NoteCard(note.text.text)
        }
    }
}

@Composable
fun NoteCard(note: String, modifier: Modifier = Modifier) {
    // TODO
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.size(width = 150.dp, height = 150.dp)
    ) {
        Text(
            text = note
        )
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun NoteCardPreview() {
    NoteCard(
        note = "Tomato\nPotato\nDates",
        modifier = Modifier.padding(8.dp)
    )
}
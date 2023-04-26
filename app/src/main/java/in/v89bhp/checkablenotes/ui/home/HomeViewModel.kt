package `in`.v89bhp.checkablenotes.ui.home

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import `in`.v89bhp.checkablenotes.data.Note
import `in`.v89bhp.checkablenotes.data.NotesRepository

class HomeViewModel(private val notesRepository: NotesRepository = Graph.notesRepository) : ViewModel() {
    var notesList = mutableListOf<Note>().toMutableStateList()


}
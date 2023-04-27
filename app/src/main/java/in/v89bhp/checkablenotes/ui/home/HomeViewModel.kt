package `in`.v89bhp.checkablenotes.ui.home

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.v89bhp.checkablenotes.Graph
import `in`.v89bhp.checkablenotes.data.Note
import `in`.v89bhp.checkablenotes.data.NotesRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val notesRepository: NotesRepository = Graph.notesRepository) : ViewModel() {
    var notesList = mutableListOf<Note>().toMutableStateList()

    init {
        viewModelScope.launch {
//            notesList.addAll(notesRepository.loadNotes(context)) // TODO How to get app context in viewmodel? Get from app state?
        }
    }
}
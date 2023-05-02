package `in`.v89bhp.checkablenotes.ui.home

import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import `in`.v89bhp.checkablenotes.Graph
import `in`.v89bhp.checkablenotes.data.Note
import `in`.v89bhp.checkablenotes.data.NotesRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application, private val notesRepository: NotesRepository = Graph.notesRepository) : AndroidViewModel(application) {
    var notesList = mutableListOf<Note>().toMutableStateList()
    var fileNamesList = mutableListOf<String>().toMutableStateList()

    init {
        viewModelScope.launch {
            val (fileNames, notes) = notesRepository.loadNotes(getApplication())
            fileNamesList.addAll(fileNames)
            notesList.addAll(notes) // TODO How to get app context in viewmodel? Get from app state?
        }
    }
}
package `in`.v89bhp.checkablenotes.ui.home

import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import `in`.v89bhp.checkablenotes.Graph
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.data.Note
import `in`.v89bhp.checkablenotes.data.NotesRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val TAG = "HomeViewModel"
    private val notesRepository: NotesRepository = Graph.notesRepository
    var notesList = mutableListOf<Note>().toMutableStateList()
    var fileNamesList = mutableListOf<String>().toMutableStateList()

    init {
        viewModelScope.launch {
            loadNotes()
        }

    }

    private suspend fun loadNotes() {
        val (fileNames, notes) = notesRepository.loadNotes(getApplication())
        fileNamesList.removeAll { true }
        fileNamesList.addAll(fileNames)

        notesList.removeAll { true }
        notesList.addAll(notes)
    }

    fun saveNote(fileName: String, text: TextFieldValue, list: List<CheckableItem>) {
        // TODO
        viewModelScope.launch {
            notesRepository.saveNote(
                context = getApplication(),
                note = Note(text = text, list = list),
                fileName = if (fileName == "newNote") null else fileName
            )

            loadNotes()
        }
    }
}
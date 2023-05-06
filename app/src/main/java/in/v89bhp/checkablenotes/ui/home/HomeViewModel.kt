package `in`.v89bhp.checkablenotes.ui.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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

    var firstTime = true

    var notesList = mutableListOf<Note>().toMutableStateList()
    var fileNamesList = mutableListOf<String>().toMutableStateList()

    var openDeleteDialog by mutableStateOf(false)
    var selectedFileNames = mutableListOf<String>().toMutableStateList()

    fun loadNotesInitial() {
        viewModelScope.launch {
            loadNotes()
        }

    }

    private suspend fun loadNotes() {
        val (fileNames, notes) = notesRepository.loadNotes(getApplication())
        fileNamesList.clear()
        fileNamesList.addAll(fileNames)

        notesList.clear()
        notesList.addAll(notes)

        selectedFileNames.clear()
    }

    fun saveNote(fileName: String, text: TextFieldValue, list: List<CheckableItem>) {
        viewModelScope.launch {

            notesRepository.saveNote(
                context = getApplication(),
                note = Note(text = text, list = list),
                fileName = fileName
            )

            loadNotes()
        }
    }

    fun deleteNotes(fileNames: List<String>) {
        viewModelScope.launch {
            notesRepository.deleteNotes(getApplication(), fileNames)
            selectedFileNames.clear()
            loadNotes()
        }
    }

    fun selectAll() {
        selectedFileNames.clear()
        selectedFileNames.addAll(fileNamesList)
    }

}
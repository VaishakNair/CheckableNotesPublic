package `in`.v89bhp.checkablenotes.ui.home

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
        Log.i("HomeViewModel", "Saving note: ${text.text}")
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

    fun deselectAll() {
        selectedFileNames.clear()
    }

    val allSelected: Boolean
        get() = selectedFileNames.size == fileNamesList.size

    fun longPressVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {// For Devices that support VibratorManager:
            val vibratorManager: VibratorManager =
                (getApplication() as Context).getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createPredefined(
                        VibrationEffect.EFFECT_CLICK
                    )
                )
            )
        } else {
            val vibrator =
                (getApplication() as Context).getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Vibrator (Deprecated) with VibrationEffect
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        1000, VibrationEffect.DEFAULT_AMPLITUDE

                    )
                )
            } else {
                vibrator.vibrate(1000) // Generic vibration based on time
            }
        }
    }
}
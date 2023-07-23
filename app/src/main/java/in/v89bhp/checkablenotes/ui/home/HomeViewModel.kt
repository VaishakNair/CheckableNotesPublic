package `in`.v89bhp.checkablenotes.ui.home

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.v89bhp.checkablenotes.Graph
import `in`.v89bhp.checkablenotes.data.Note
import `in`.v89bhp.checkablenotes.data.NotesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher

private const val TAG = "HomeViewModel"

class HomeViewModel(
    private val notesRepository: NotesRepository = Graph.notesRepository,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val start: CoroutineStart = CoroutineStart.DEFAULT
) : ViewModel() {

    var notesList = mutableListOf<Note>().toMutableStateList()
    var fileNamesList = mutableListOf<String>().toMutableStateList()

    var openDeleteDialog by mutableStateOf(false)
    var selectedFileNames = mutableListOf<String>().toMutableStateList()

    var isLoadingNotes by mutableStateOf(false)
    var noNotes by mutableStateOf(false)

    fun loadNotesInitial(context: Context) {
        viewModelScope.launch(
            context = coroutineDispatcher,
            start = start
        ) {
            loadNotes(context)
        }

    }

    private suspend fun loadNotes(context: Context) {
        isLoadingNotes = true

        val (fileNames, notes) = notesRepository.loadNotes(context)

        fileNamesList.clear()
        fileNamesList.addAll(fileNames)

        notesList.clear()
        notesList.addAll(notes)

        selectedFileNames.clear()

        isLoadingNotes = false
        noNotes = fileNamesList.isEmpty()
    }


    fun deleteNotes(context: Context, fileNames: List<String>) {
        viewModelScope.launch(
            context = coroutineDispatcher,
            start = start
        ) {
            notesRepository.deleteNotes(context, fileNames)
            selectedFileNames.clear()
            loadNotes(context)
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

    fun longPressVibrate(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {// For Devices that support VibratorManager:
            val vibratorManager: VibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createPredefined(
                        VibrationEffect.EFFECT_CLICK
                    )
                )
            )
        } else {
            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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
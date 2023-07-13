package `in`.v89bhp.checkablenotes.ui.note

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import `in`.v89bhp.checkablenotes.Graph
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.data.Note
import `in`.v89bhp.checkablenotes.data.NotesRepository
import `in`.v89bhp.checkablenotes.setDifference
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "NoteViewModel"

    private val notesRepository: NotesRepository = Graph.notesRepository

    var title by mutableStateOf(TextFieldValue("", TextRange(0)))

    var text by mutableStateOf(TextFieldValue("", TextRange(0)))

    val list = mutableListOf<CheckableItem>().toMutableStateList()

    var loadedNote: Note? = null

    var openDeleteDialog by mutableStateOf(false)


    val completedItemsCount: Int
        get() = list.size - pendingItemsCount
    val pendingItemsCount: Int
        get() = list.sumOf {
            if (!it.isChecked) 1 as Int else 0
        }

    fun loadNote(fileName: String) {
        viewModelScope.launch {
            try {
                loadedNote = notesRepository.loadNote(getApplication(), fileName)
                title = loadedNote!!.title
                text = loadedNote!!.text
                list.clear()
                list.addAll(loadedNote!!.list.map {
                    it.copy()
                })
            } catch (ex: FileNotFoundException) {
                Log.i(TAG, ex.message!!)
            }
        }
    }


    fun updateList(value: TextFieldValue) {
        var newList: List<CheckableItem> = if (value.text.trim() == "") {// Empty list
            emptyList()
        } else {
            var list = value.text.trim().split('\n').filter { it.trim() != "" }

            // Remove duplicate elements:
            list = mutableListOf<String>().apply { addAll(linkedSetOf(*(list.toTypedArray()))) }

            list.mapIndexed { index, item ->
                CheckableItem(id = index, name = item)
            }
        }


        Log.i(TAG, "NewList:")
        Log.i(TAG, newList.joinToString { "Id: ${it.id} Message: ${it.name}" })
        Log.i(TAG, "List")
        Log.i(TAG, list.joinToString { "Id: ${it.id} Message: ${it.name}" })

        /*
            User deletes one (or more) items.
            ------------------------------------
            Here we need to first do list - newList to find
            out the items deleted by the user. Then delete these items from list.
        */

        if (!list.isEmpty()) {
            if (newList.isEmpty()) {
                list.clear()
            } else {
                setDifference(list.toSet(), newList.toSet()).forEach { deletedItem ->
                    Log.i(
                        TAG,
                        "Removing item with Id: ${deletedItem.id} and message: ${deletedItem.name}"
                    )
                    list.removeAll { it == deletedItem }
                }
            }
        }


        /*
            User adds one (or more) items.
            ------------------------------------
         */
        if (newList.isNotEmpty()) {
            if (list.isEmpty()) {
                list.addAll(newList)
            } else {
                // To preserve the order of user input (the order of new list),
                // update just the checked status of list item from the current list:
                val listWithNewIds = newList.map { checkableItem ->
                    checkableItem.isChecked =
                        list.firstOrNull { it == checkableItem }?.isChecked ?: false
                    checkableItem
                }.toMutableList()
                arrangeItems(listWithNewIds)
                list.clear()
                list.addAll(listWithNewIds)
            }
        }
    }


    fun onCheckedChange(checkableItem: CheckableItem, isChecked: Boolean) {
        checkableItem.isChecked = isChecked
        arrangeItems(list)
    }

    private fun arrangeItems(list: MutableList<CheckableItem>) {

        // Remove unchecked items from the list, sort them in their natural order (the one input by user based on which their ids are assigned,
        // and add them to the top of the list:
        val currentlyUncheckedItems = list.filterNot { item -> item.isChecked }
        list.removeAll(currentlyUncheckedItems)
        list.addAll(0, currentlyUncheckedItems.sortedBy { item -> item.id })

        // Remove checked items from the list, sort them in their natural order (the one input by user based on which their ids are assigned,
        // and add them to the bottom of the list:
        val currentlyCheckedItems = list.filter { item -> item.isChecked }
        list.removeAll(currentlyCheckedItems)
        list.addAll(currentlyCheckedItems.sortedBy { item -> item.id })
    }

    fun showSharesheet(context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text.text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, null))
    }

    fun saveNote(
        fileName: String,
        title: TextFieldValue,
        text: TextFieldValue,
        list: List<CheckableItem>
    ) {
        Log.i("HomeViewModel", "Saving note: ${text.text}")
        viewModelScope.launch {

            notesRepository.saveNote(
                context = getApplication(),
                note = Note(title = title, text = text, list = list),
                fileName = fileName
            )
        }
    }

    fun deleteNotes(fileNames: List<String>) {
        viewModelScope.launch {
            notesRepository.deleteNotes(getApplication(), fileNames)
        }
    }
}


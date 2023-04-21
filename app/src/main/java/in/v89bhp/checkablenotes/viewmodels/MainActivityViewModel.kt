package `in`.v89bhp.checkablenotes.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import `in`.v89bhp.checkablenotes.composables.CheckableItem
import `in`.v89bhp.checkablenotes.composables.setDifference
import kotlinx.coroutines.MainScope

class MainActivityViewModel : ViewModel() {

    val TAG = "MainActivityViewModel"

    var text by mutableStateOf(TextFieldValue("", TextRange(0, 7)))

    val list = mutableListOf<CheckableItem>().toMutableStateList()


    fun updateList(value: TextFieldValue) {
        val newList = value.text.trim().split('\n').mapIndexed { index, value ->
            CheckableItem(index, value)
        }
        Log.i(TAG, "NewList:")
        Log.i(TAG, newList.joinToString() { "Id: ${it.id} Message: ${it.message}" })
        Log.i(TAG, "List")
        Log.i(TAG, list.joinToString() { "Id: ${it.id} Message: ${it.message}" })


        var maxId: Int = if (list.isEmpty()) 0 else list.maxOf { it.id }
        /*
            User deletes one (or more) items.
            ------------------------------------
            Here we need to first do list - newList to find
            out the items deleted by the user. Then delete these items from list.
        */

        if (!list.isEmpty()) {
            if (newList.isEmpty()) {
                list.removeAll { true }
            } else {
                setDifference(list.toSet(), newList.toSet()).forEach { deletedItem ->
                    Log.i(
                        TAG,
                        "Removing item with Id: ${deletedItem.id} and message: ${deletedItem.message}"
                    )
                    list.removeAll { it == deletedItem }
                }
            }
        }


        /*
            User adds one (or more) items.
            ------------------------------------
            First do newList - list to find out the items added by the user. Then add them to list.
            Find the maximum id in the current list and then set its increments as ids
            of the new items being added.
         */
        if (!newList.isEmpty()) {
            if (list.isEmpty()) {
                list.addAll(newList)
            } else {
                setDifference(newList.toSet(), list.toSet()).forEach { newItem ->
                    Log.i(
                        TAG,
                        "Adding new item with Id: ${newItem.id} and message>>${newItem.message}<<"
                    )
                    newItem.id = ++maxId
                    list.add(newItem)
                }
            }
        }

        rearrangeList()
    }

    private fun rearrangeList() {
        // TODO
    }

    fun onCheckedChange(checkableItem: CheckableItem, isChecked: Boolean) {
        val changedItemIndex = list.indexOfFirst { it.id == checkableItem.id }
        checkableItem.isChecked = isChecked

        if (isChecked) {
            list.getOrNull(changedItemIndex + 1)?.let { nextItem -> // There's a next item.
                if (!nextItem.isChecked) { // The next item is not checked. Move the checked item to the bottom of the list
                    list.removeIf { item -> item.id == checkableItem.id }
                    list.add(checkableItem)
                }
            }
        } else {
            list.getOrNull(changedItemIndex - 1)?.let { previousItem -> // There's a previous item.
                if (previousItem.isChecked) { // The previous item is checked. Move the unchecked item to the top of the list
                    list.removeIf { item -> item.id == checkableItem.id }
                    list.add(0, checkableItem)
                }
            }
        }
    }
}
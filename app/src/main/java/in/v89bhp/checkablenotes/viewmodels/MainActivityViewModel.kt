package `in`.v89bhp.checkablenotes.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import `in`.v89bhp.checkablenotes.composables.CheckableItem

class MainActivityViewModel : ViewModel() {

    var text by mutableStateOf(TextFieldValue("", TextRange(0, 7)))

    val list = mutableListOf<CheckableItem>().toMutableStateList()

    fun updateList(value: TextFieldValue) {
        list.removeAll { true }
        list.addAll(value.text.split('\n').mapIndexed { index, value ->
            CheckableItem(index, value)
        })
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
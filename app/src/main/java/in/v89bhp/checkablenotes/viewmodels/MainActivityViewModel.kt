package `in`.v89bhp.checkablenotes.viewmodels

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import `in`.v89bhp.checkablenotes.composables.CheckableItem

class MainActivityViewModel : ViewModel() {

    var text by mutableStateOf(TextFieldValue("", TextRange(0, 7)))


    val list = mutableListOf<CheckableItem>().toMutableStateList()

    fun updateList(value: TextFieldValue) {
        list.removeAll { true }
        list.addAll(value.text.split('\n').mapIndexed {
            index, value -> CheckableItem(index, value)
        })
    }

    fun onCheckedChange(checkableItem: CheckableItem, newValue: Boolean) {
        list.find { it.id == checkableItem.id }?.let { it.isChecked = newValue }
    }

}
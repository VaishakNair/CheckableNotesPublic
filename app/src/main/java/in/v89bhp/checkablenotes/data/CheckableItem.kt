package `in`.v89bhp.checkablenotes.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CheckableItem(var id: Int, val message: String, isChecked: Boolean = false) {
    var isChecked by mutableStateOf(isChecked)

    override fun equals(other: Any?): Boolean {
        return other?.let {
            if (it is CheckableItem) {
                it.message == message
            } else {
                false
            }
        } ?: false
    }

    override fun toString(): String {
        return "Id: $id Message: $message"
    }
}
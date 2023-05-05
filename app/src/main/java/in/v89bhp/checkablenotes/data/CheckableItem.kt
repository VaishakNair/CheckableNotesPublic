package `in`.v89bhp.checkablenotes.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CheckableItem(var id: Int, val name: String, isChecked: Boolean = false) {
    var isChecked by mutableStateOf(isChecked)

    override fun equals(other: Any?): Boolean {
        return other?.let {
            if (it is CheckableItem) {
                it.name == name
            } else {
                false
            }
        } ?: false
    }

    override fun toString(): String {
        return "Id: $id Message: $name"
    }

    fun copy(): CheckableItem = CheckableItem(id, name, isChecked)

}

/**
 * Custom extension function for checking equality of lists of checkable items as the overridden
 * equals() method of CheckableItem checks only for equality of names in the custom set difference
 * implementation in Utils.kt
 */
infix fun List<CheckableItem>.nameischeckedequals(other: List<CheckableItem>): Boolean {
    for ((thisItem, otherItem) in this zip other) {
        if ((thisItem.name != otherItem.name) || (thisItem.isChecked != otherItem.isChecked)) {
            return false
        }
    }
    return true
}
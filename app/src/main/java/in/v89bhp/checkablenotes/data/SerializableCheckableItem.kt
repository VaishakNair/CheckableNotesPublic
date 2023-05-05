package `in`.v89bhp.checkablenotes.data

import androidx.compose.runtime.Immutable

@Immutable
data class SerializableCheckableItem(val id: Int, val name: String, val isChecked: Boolean)

package `in`.v89bhp.checkablenotes.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue


@Immutable
data class Note(val text: TextFieldValue, val list: List<CheckableItem>)

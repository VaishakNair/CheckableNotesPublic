package `in`.v89bhp.checkablenotes.data

import android.content.Context
import com.google.gson.Gson
import `in`.v89bhp.checkablenotes.composables.CheckableItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.streams.toList

class NotesRepository(private val mainDispatcher: CoroutineDispatcher) {

    suspend fun loadNote(context: Context, fileName: String): Note {
        return withContext(mainDispatcher) {
            val jsonObjectString = context.openFileInput(fileName).bufferedReader().useLines() {

                it.toList()
                    .joinToString(separator = "\n")
            }
            Gson().fromJson(jsonObjectString, SerializableNote::class.java)
                .let { serializableNote ->
                    Note(
                        text = serializableNote.text,
                        list = serializableNote.list.map { serializableCheckableItem ->
                            CheckableItem(
                                id = serializableCheckableItem.id,
                                message = serializableCheckableItem.message,
                                isChecked = serializableCheckableItem.isChecked
                            )
                        })
                }
        }
    }

    suspend fun saveNote(context: Context, note: Note, fileName: String?) {
        val serializableNote = SerializableNote(
            text = note.text,
            list = note.list.map { checkableItem ->
                SerializableCheckableItem(
                    id = checkableItem.id,
                    message = checkableItem.message,
                    isChecked = checkableItem.isChecked
                )
            })
        val jsonObjectString = Gson().toJson(serializableNote)

        fileName?.let { context.deleteFile(it) }// File already exists. Delete it.
        context.openFileOutput("${System.currentTimeMillis()}.json", Context.MODE_PRIVATE)
            .use { it.write(jsonObjectString.toByteArray()) }
    }
}
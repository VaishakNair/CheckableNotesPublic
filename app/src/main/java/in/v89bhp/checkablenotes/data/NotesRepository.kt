package `in`.v89bhp.checkablenotes.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class NotesRepository(private val ioDispatcher: CoroutineDispatcher) {

    suspend fun loadNotes(context: Context): Pair<List<String>,List<Note>> {
        val fileNames: List<String> = context.fileList().toList().filter { fileName -> fileName.endsWith(".json") }
        val notesList = fileNames.map { fileName ->
            loadNote(context, fileName) }
        return Pair(fileNames, notesList)
    }


    suspend fun loadNote(context: Context, fileName: String): Note {
        return withContext(ioDispatcher) {
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
        withContext(ioDispatcher) {
            fileName?.let { context.deleteFile(it) }// File already exists. Delete it.
            context.openFileOutput("${System.currentTimeMillis()}.json", Context.MODE_PRIVATE)
                .use { it.write(jsonObjectString.toByteArray()) }
        }
    }
}
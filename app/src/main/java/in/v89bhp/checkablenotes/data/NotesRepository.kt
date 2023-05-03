package `in`.v89bhp.checkablenotes.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class NotesRepository(private val ioDispatcher: CoroutineDispatcher) {
    val TAG = "NotesRepository"
    suspend fun loadNotes(context: Context): Pair<List<String>,List<Note>> {
        val fileNames: List<String> = context.fileList().toList().filter { fileName -> fileName.endsWith(".json") }
        // TODO Sort file names by their last modified time (descending)
        val notesList = fileNames.map { fileName ->
            loadNote(context, fileName) }
        return Pair(fileNames, notesList)
    }


    suspend fun loadNote(context: Context, fileName: String): Note {
        Log.i(TAG, "Loading note from file $fileName")
        return withContext(ioDispatcher) {
            val jsonObjectString = context.openFileInput(fileName).bufferedReader().useLines() {

                it.toList()
                    .joinToString(separator = "\n")

            }
            Log.i(TAG, "JSON Object String (loaded from file): $jsonObjectString")
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

    suspend fun saveNote(context: Context, note: Note, fileName: String) {
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
        Log.i(TAG, "JSON Object string (to be saved): $jsonObjectString")
        withContext(ioDispatcher) {
            context.openFileOutput(fileName, Context.MODE_PRIVATE)
                .use { it.write(jsonObjectString.toByteArray()) }
        }
    }
}
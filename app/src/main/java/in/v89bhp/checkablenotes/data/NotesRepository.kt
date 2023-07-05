package `in`.v89bhp.checkablenotes.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

class NotesRepository(private val ioDispatcher: CoroutineDispatcher) {

    suspend fun loadNotes(context: Context): Pair<List<String>, List<Note>> {
        val fileNames: List<String> =
            context.fileList().toList().filter { fileName -> fileName.matches(Regex("[0-9]+\\.json")) }
                .sortedByDescending { fileName ->
                    File(context.filesDir, fileName).lastModified()
                }

        val notesList = fileNames.map { fileName ->
            loadNote(context, fileName)
        }
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
                        title = serializableNote.title,
                        text = serializableNote.text,
                        list = serializableNote.list.map { serializableCheckableItem ->
                            CheckableItem(
                                id = serializableCheckableItem.id,
                                name = serializableCheckableItem.name,
                                isChecked = serializableCheckableItem.isChecked
                            )
                        })
                }
        }
    }

    suspend fun saveNote(context: Context, note: Note, fileName: String) {
        val serializableNote = SerializableNote(
            title = note.title,
            text = note.text,
            list = note.list.map { checkableItem ->
                SerializableCheckableItem(
                    id = checkableItem.id,
                    name = checkableItem.name,
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

    suspend fun deleteNotes(context: Context, fileNames: List<String>) {
        withContext(ioDispatcher) {
            fileNames.forEach {fileName ->
                context.deleteFile(fileName)
            }
        }
    }

    companion object {
        const val TAG = "NotesRepository"
    }
}
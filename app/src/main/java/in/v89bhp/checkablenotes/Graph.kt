package `in`.v89bhp.checkablenotes

import `in`.v89bhp.checkablenotes.data.NotesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Graph {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    val notesRepository by lazy {
        NotesRepository(ioDispatcher)
    }
}
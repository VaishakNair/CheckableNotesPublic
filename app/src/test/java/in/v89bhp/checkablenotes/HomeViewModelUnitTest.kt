import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import `in`.v89bhp.checkablenotes.data.CheckableItem
import `in`.v89bhp.checkablenotes.data.Note
import `in`.v89bhp.checkablenotes.data.NotesRepository
import `in`.v89bhp.checkablenotes.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelUnitTest {

    @Mock
    private val mockContext = mock<Context>()


    @Test
    fun loadNotesInitial() {
        val homeViewModel = HomeViewModel(FakeNotesRepository)
    }
}

object FakeNotesRepository : NotesRepository(Dispatchers.IO) {
    val fileNamesList = listOf("File1.json", "File2.json")
    val notesList = listOf(
        Note(
            title = TextFieldValue("Note 1"), text = TextFieldValue("Item 1\nItem2"), list = listOf(
                CheckableItem(id = 1, name = "Item 1", isChecked = false),
                CheckableItem(id = 2, name = "Item 2", isChecked = true)
            )
        ),
        Note(
            title = TextFieldValue("Note 2"),
            text = TextFieldValue("Item 3\nItem 4"),
            list = listOf(
                CheckableItem(id = 1, name = "Item 3", isChecked = false),
                CheckableItem(id = 2, name = "Item 4", isChecked = false)
            )
        )
    )

    override suspend fun loadNotes(context: Context): Pair<List<String>, List<Note>> {
        return fileNamesList to notesList
    }
}
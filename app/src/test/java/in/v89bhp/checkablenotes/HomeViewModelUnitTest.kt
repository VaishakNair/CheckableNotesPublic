import android.content.Context
import `in`.v89bhp.checkablenotes.ui.home.HomeViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelUnitTest {

    @Mock private val mockContext = mock<Context>()


    @Test
    fun loadNotesInitial() {
        val homeViewModel = HomeViewModel(FakeNotesRepository)
    }
}
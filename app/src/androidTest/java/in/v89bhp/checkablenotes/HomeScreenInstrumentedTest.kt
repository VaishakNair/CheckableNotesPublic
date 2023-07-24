package `in`.v89bhp.checkablenotes

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

private const val TAG = "HomeScreenInstrumentedTest"
@RunWith(AndroidJUnit4::class)
class HomeScreenInstrumentedTest {
    @Test
    fun ensureFileNameFormat() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val fileNameFormat = Regex("[0-9]+\\.json")

        val appPrivateFileList: List<String> =  appContext.fileList().toList()

        appPrivateFileList.forEach { fileName ->
            assertTrue("Doesn't match for file name: >>$fileName<<", fileName.matches(fileNameFormat))
        }

//        assertEquals(appContext.packageName, "in.v89bhp.checkablenotes")
    }
}
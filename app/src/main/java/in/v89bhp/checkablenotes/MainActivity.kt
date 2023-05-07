package `in`.v89bhp.checkablenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.v89bhp.checkablenotes.ui.theme.CheckableNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                CheckableNotesApp()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    CheckableNotesTheme {
        Surface(tonalElevation = 5.dp) {
            content()
        }
    }
}


//@Preview(fontScale = 1.2f, showBackground = true)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        CheckableNotesApp()
    }
}
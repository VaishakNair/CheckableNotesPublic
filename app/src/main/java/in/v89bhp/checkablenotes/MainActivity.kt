package `in`.v89bhp.checkablenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
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
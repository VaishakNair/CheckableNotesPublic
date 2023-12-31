package `in`.v89bhp.checkablenotes

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


/**
 * List of screens for [CheckableNotesApp]
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")

    object Note : Screen("note/{fileName}") {
        fun createRoute(fileName: String) = "note/$fileName"
    }
}


@Composable
fun rememberCheckableNotesAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    CheckableNotesAppState(navController, context)
}

class CheckableNotesAppState(
    val navController: NavHostController,
    private val context: Context
) {

    fun navigateToNote(fileName: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.Note.createRoute(fileName))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }


}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.getLifecycle().currentState == Lifecycle.State.RESUMED
package `in`.v89bhp.checkablenotes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.v89bhp.checkablenotes.ui.home.Home
import `in`.v89bhp.checkablenotes.ui.note.Note

@Composable
fun CheckableNotesApp(
    appState: CheckableNotesAppState = rememberCheckableNotesAppState()
) {

    NavHost(
        navController = appState.navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) { backStackEntry ->
            Home(
                navigateToNote = { fileName ->
                    appState.navigateToNote(fileName, backStackEntry)
                }
            )
        }

        composable(Screen.Note.route) { backStackEntry ->
            Note(fileName = backStackEntry.arguments?.getString("fileName")!!,
                navigateBack = { appState.navigateBack() })
        }
    }

}
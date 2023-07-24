package `in`.v89bhp.checkablenotes

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.v89bhp.checkablenotes.ui.home.Home
import `in`.v89bhp.checkablenotes.ui.note.Note

@Composable
fun CheckableNotesApp(
    windowSizeClass: WindowSizeClass,
    appState: CheckableNotesAppState = rememberCheckableNotesAppState(),

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
            val showOnePane = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

            Note(fileName = backStackEntry.arguments?.getString("fileName")!!,
                showOnePane = showOnePane,
                navigateBack = { appState.navigateBack() })
        }
    }

}
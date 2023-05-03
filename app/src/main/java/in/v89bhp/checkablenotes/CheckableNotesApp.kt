package `in`.v89bhp.checkablenotes

import androidx.compose.runtime.Composable
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
            navigateBack = {appState.navigateBack()})
        }

//        composable(Screen.Player.route) { backStackEntry ->
//            val playerViewModel: PlayerViewModel = viewModel(
//                factory = PlayerViewModel.provideFactory(
//                    owner = backStackEntry,
//                    defaultArgs = backStackEntry.arguments
//                )
//            )
//            PlayerScreen(
//                playerViewModel,
//                windowSizeClass,
//                displayFeatures,
//                onBackPress = appState::navigateBack
//            )
//        }
    }

}
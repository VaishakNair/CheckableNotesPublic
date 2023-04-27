package `in`.v89bhp.checkablenotes

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.v89bhp.checkablenotes.ui.home.Home

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
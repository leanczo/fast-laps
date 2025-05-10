import androidx.compose.runtime.Composable
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel

import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fastlaps.presentation.presentation.screen.SessionResultsScreen
import androidx.compose.runtime.getValue

@Composable
fun AppNavigation(viewModel: RaceViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "sessionList") {
        composable("sessionList") {
            SessionListScreen(
                viewModel = viewModel,
                onSessionClick = { sessionKey ->
                    // 1. **Call loadSessionResults with the sessionKey**
                    viewModel.loadSessionData(sessionKey)
                    navController.navigate("sessionResults/$sessionKey") {
                        // Optional: Configure navigation options
                    }
                }
            )
        }
        composable(
            route = "sessionResults/{sessionKey}",
            arguments = listOf(navArgument("sessionKey") { type = NavType.IntType })
        ) { backStackEntry ->
            // You can optionally retrieve the sessionKey here if needed for the screen,
            // but the ViewModel should already be loading based on the click.
            val sessionKey = backStackEntry.arguments?.getInt("sessionKey")

            // Collect the final positions from the ViewModel
            val finalPositions by viewModel.finalPositions.collectAsState()

            SessionResultsScreen(
                finalPositions = finalPositions,
                onBack = { navController.popBackStack() },
                viewModel
            )
        }
    }
}
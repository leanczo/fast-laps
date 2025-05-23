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

    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            MainScreen(
                onCircuitsClick = { navController.navigate("sessionList") },
                onPilotsClick = { navController.navigate("pilotList") },
                onAboutClick = { navController.navigate("about") },
            )
        }

        composable("about") {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("sessionList") {
            SessionListScreen(
                viewModel = viewModel,
                onSessionClick = { sessionKey ->
                    viewModel.loadSessionData(sessionKey)
                    navController.navigate("sessionResults/$sessionKey")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("pilotList") {
            PilotsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "sessionResults/{sessionKey}",
            arguments = listOf(navArgument("sessionKey") { type = NavType.IntType })
        ) { backStackEntry ->
            val finalPositions by viewModel.finalPositions.collectAsState()
            SessionResultsScreen(
                finalPositions = finalPositions,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
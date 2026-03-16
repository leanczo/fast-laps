

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fastlaps.presentation.presentation.screen.SessionResultsScreen
import com.example.fastlaps.presentation.presentation.viewmodel.RaceViewModel

@Composable
fun AppNavigation(viewModel: RaceViewModel, currentLang: String, onLanguageChange: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            MainScreen(
                viewModel = viewModel,
                onCircuitsClick = { navController.navigate("sessionList") },
                onPilotsClick = { navController.navigate("pilotList") },
                onAboutClick = { navController.navigate("about") },
                onConstructorsClick = { navController.navigate("constructors") },
                onNewsClick = { navController.navigate("news") },
                currentLang = currentLang,
                onLanguageChange = onLanguageChange
            )
        }

        composable("about") {
            AboutScreen()
        }

        composable("sessionList") {
            SessionListScreen(
                viewModel = viewModel,
                onRaceClick = { round, raceName ->
                    viewModel.loadRaceResults(round, raceName)
                    navController.navigate("raceResults/$round")
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

        composable("constructors") {
            ConstructorsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("news") {
            NewsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                currentLang = currentLang
            )
        }

        composable(
            route = "raceResults/{round}",
            arguments = listOf(navArgument("round") { type = NavType.IntType })
        ) {
            val raceResults by viewModel.raceResults.collectAsState()
            val qualifyingResults by viewModel.qualifyingResults.collectAsState()
            val sprintResults by viewModel.sprintResults.collectAsState()
            val raceName by viewModel.currentRaceName.collectAsState()
            SessionResultsScreen(
                raceResults = raceResults,
                qualifyingResults = qualifyingResults,
                sprintResults = sprintResults,
                raceName = raceName,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}

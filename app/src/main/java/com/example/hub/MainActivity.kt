package com.example.hub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hub.Pages.HomeScreen
import com.example.hub.Pages.RepoDetailScreen
import com.example.hub.Pages.WebViewScreen
import com.example.hub.data.ItemsRepositoryImplementation
import com.example.hub.presentation.ItemsViewModel
import com.example.hub.ui.theme.HubTheme
import com.example.hub.room.AppDatabase
import com.example.hub.RetrofitInstance

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ItemsViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Get repoDao from AppDatabase
                val repoDao = AppDatabase.getDatabase(applicationContext).repoDao()

                // Initialize ItemsRepositoryImplementation with both API and repoDao
                val repository = ItemsRepositoryImplementation(RetrofitInstance.api, repoDao)

                return ItemsViewModel(repository) as T
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()  // If you are using edge-to-edge UI

        setContent {
            HubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home_screen") {
                        // Step 3: Define routes for your screens
                        composable("home_screen") {
                            HomeScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(
                            "item_detail_screen/{itemId}",
                            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            // Retrieve the itemId argument from the route
                            val itemId = backStackEntry.arguments?.getString("itemId")
                            if (itemId != null) {
                                RepoDetailScreen(itemId = itemId, viewModel = viewModel, navController = navController)
                            }
                        }

                        composable("webview_screen/{url}") { backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url") ?: ""
                            WebViewScreen(url = url) // Implement WebViewScreen to display the URL
                        }
                    }
                }
            }
        }
    }
}

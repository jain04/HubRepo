package com.example.hub.Pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.hub.Components.RepoDetailsContent
import com.example.hub.data.model.Item
import com.example.hub.data.model.RepoData
import com.example.hub.presentation.ItemsViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun RepoDetailScreen(itemId: String, viewModel: ItemsViewModel, navController: NavController) {
    // Observe the repoDetails state from the ViewModel
    val repoDetails by viewModel.repoDetails.collectAsState()
    val loading by viewModel.loadingRepoDetails.collectAsState()

    // Launch effect to fetch repository details when the itemId changes
    LaunchedEffect(itemId) {
        viewModel.fetchRepoDetails(itemId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            // Make the content scrollable using LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Only one item to display, so we use `item {}` block
                item {
                    if (loading) {
                        // Show a loading indicator while fetching data
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Display the repository details once loaded
                        repoDetails?.let { item ->
                            RepoDetailsContent(item = item, navController = navController)
                        } ?: run {
                            // Display a message if no repository details are available
                            Text(text = "Repository details not available.", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    )
}




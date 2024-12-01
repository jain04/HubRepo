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
import com.example.hub.presentation.ItemsViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun RepoDetailScreen(itemId: String, viewModel: ItemsViewModel,navController:NavController) {

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (loading) {
                    // Show a loading indicator while fetching data
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Display the repository details once loaded
                    repoDetails?.let { item ->

                        val imageState = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current).data(item.owner.avatar_url)
                                .size(Size.ORIGINAL).build()
                        ).state



                        Column(modifier = Modifier.padding(16.dp)) {
                            if(imageState is AsyncImagePainter.State.Error){
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp),
                                    contentAlignment= Alignment.Center){
                                    CircularProgressIndicator()
                                }
                            }
                            if(imageState is AsyncImagePainter.State.Success){
                                Image(
                                    modifier = Modifier.width(200.dp).height(200.dp),
                                    painter = imageState.painter,
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Name: ${item.name}", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Description: ${item.description ?: "No description available"}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Owner: ${item.owner.login}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Stars: ${item.stargazers_count}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Forks: ${item.forks_count}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Language: ${item.language ?: "Unknown"}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val encodedUrl = URLEncoder.encode(item.html_url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("webview_screen/${encodedUrl}")
                                }
                            ) {
                                Text("View Project Code")
                            }
                            // Add more fields as needed
                        }
                    } ?: run {
                        // Display a message if no repository details are available
                        Text(text = "Repository details not available.")
                    }
                }
            }
        }
    )
}

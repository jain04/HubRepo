package com.example.hub.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.hub.data.model.Item
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun RepoDetailsContent(item: Item, navController: NavController) {
    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(item.owner.avatar_url)
            .size(Size.ORIGINAL).build()
    ).state

    Column(modifier = Modifier.padding(16.dp)) {
        // Display repository owner image
        if (imageState is AsyncImagePainter.State.Error) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        if (imageState is AsyncImagePainter.State.Success) {
            Image(
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp),
                painter = imageState.painter,
                contentDescription = item.name,
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Repo Name: ${item.name}", style = MaterialTheme.typography.bodyLarge)
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

        // Button to navigate to the webview screen
        Button(
            onClick = {
                val encodedUrl = URLEncoder.encode(item.html_url, StandardCharsets.UTF_8.toString())
                navController.navigate("webview_screen/${encodedUrl}")
            }
        ) {
            Text("View Project Code")
        }
    }
}
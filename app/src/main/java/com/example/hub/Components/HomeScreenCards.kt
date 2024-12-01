package com.example.hub.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.hub.data.model.Item

@Composable
fun ItemRepo(item: Item,navController: NavController){
    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(item.owner.avatar_url)
            .size(Size.ORIGINAL).build()
    ).state

    Column (
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).height(300.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable {
                navController.navigate("item_detail_screen/${item.id}")
            }
    ){
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

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${item.name}",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${item.description}",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 12.sp,
            maxLines = 2
        )
    }
}
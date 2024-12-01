package com.example.hub.Pages

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WebViewScreen(url: String) {
    Scaffold(
        modifier = Modifier.fillMaxSize()) {
        // paddingValues is removed
        // WebView to display the URL
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient() // Opens links within the WebView itself
                    loadUrl(url)
                }
            }
        )
    }
}

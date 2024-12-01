package com.example.hub.Pages

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hub.Components.ItemRepo
import com.example.hub.presentation.ItemsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(viewModel: ItemsViewModel, navController: NavController) {
    val repoList = viewModel.items.collectAsState().value
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }  // Track loading state
    var hasMoreData by remember { mutableStateOf(true) }  // Track if there are more pages of data
    var page by remember { mutableStateOf(1) }  // Track the current page

    val lazyListState = rememberLazyListState()

    // Observe error state
    LaunchedEffect(key1 = viewModel.showErrorToastChannel) {
        viewModel.showErrorToastChannel.collectLatest { show ->
            if (show) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                loading = false  // Stop loading on error
            }
        }
    }

    // Function to load the next page when scrolling to the bottom
    fun loadNextPage() {
        if (!loading && hasMoreData) {
            loading = true
            viewModel.loadNextPage(searchQuery)  // Pass current search query to viewModel
        }
    }

    // Observe the scroll position to trigger pagination when reaching the end
    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        if (lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
            if (lastVisibleItem != null && lastVisibleItem.index == repoList.size - 1 && hasMoreData && !loading) {
                // User reached the end of the list, trigger loading of next page
                loadNextPage()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search bar section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.height(16.dp)) // space above the text field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        label = { Text("Search Repository") }
                    )
                    Button(
                        onClick = {
                            loading = true  // Start loading when button is clicked
                            page = 1  // Reset page to 1 for a new search
                            hasMoreData = true  // Reset hasMoreData flag on new search
                            viewModel.resetPagination()  // Reset pagination state in ViewModel
                            viewModel.fetchRepositories(searchQuery, page)
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Search")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // space below the search bar

                // Show CircularProgressIndicator when loading
                if (loading && repoList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (repoList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Repositories Found")
                    }
                } else {
                    LazyColumn(
                        state = lazyListState, // Attach LazyListState to detect scrolling
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.ime), // Handle keyboard insets
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(repoList.size) { index ->
                            ItemRepo(repoList[index], navController = navController)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Show progress indicator at the bottom if data is loading
                        if (loading && hasMoreData) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}





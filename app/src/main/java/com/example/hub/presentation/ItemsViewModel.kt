package com.example.hub.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hub.data.ItemsRepository
import com.example.hub.data.Result
import com.example.hub.data.model.Item
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData = _hasMoreData.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    private var currentPage = 1

    // Add a new state for repository details
    private val _repoDetails = MutableStateFlow<Item?>(null)
    val repoDetails = _repoDetails.asStateFlow()

    // Add a loading state for repo details
    private val _loadingRepoDetails = MutableStateFlow(false)
    val loadingRepoDetails = _loadingRepoDetails.asStateFlow()

    // Fetch repositories with pagination
    fun fetchRepositories(query: String, page: Int) {
        viewModelScope.launch {
            // Start loading
            _loading.value = true

            itemsRepository.getItemRepository(query, page).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _loading.value = false
                        _showErrorToastChannel.send(true)
                    }
                    is Result.Success -> {
                        result.data?.let { newItems ->
                            // Append new data to the list
                            _items.update { currentItems -> currentItems + newItems }

                            // If no new items are returned, set hasMoreData to false
                            if (newItems.isEmpty()) {
                                _hasMoreData.value = false
                            }
                        }
                        // Stop loading after fetching
                        _loading.value = false
                    }
                }
            }
        }
    }

    // Fetch details of a specific repository
    fun fetchRepoDetails(itemId: String) {
        viewModelScope.launch {
            _loadingRepoDetails.value = true

            itemsRepository.getRepoDetails(itemId).collect { result ->
                _loadingRepoDetails.value = false
                when (result) {
                    is Result.Error -> {
                        // Handle error (show an error message or log it)
                        _repoDetails.value = null
                    }
                    is Result.Success -> {
                        _repoDetails.value = result.data
                    }
                }
            }
        }
    }

    // Reset pagination and item list
    fun resetPagination() {
        currentPage = 1
        _hasMoreData.value = true
        _loading.value = false
        _items.value = emptyList()
    }

    // Load next page for pagination
    fun loadNextPage(query: String) {
        if (_loading.value || !_hasMoreData.value) return

        currentPage += 1
        fetchRepositories(query, currentPage)
    }
}



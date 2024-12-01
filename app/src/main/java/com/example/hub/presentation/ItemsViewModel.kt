package com.example.hub.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hub.data.ItemsRepository
import com.example.hub.data.Result
import com.example.hub.data.model.Item
import com.example.hub.data.toItem
import com.example.hub.room.MyApplication
import com.example.hub.room.data.toRepositoryEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData = _hasMoreData.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    private var currentPage = 1

    private val _repoDetails = MutableStateFlow<Item?>(null)
    val repoDetails = _repoDetails.asStateFlow()

    private val _loadingRepoDetails = MutableStateFlow(false)
    val loadingRepoDetails = _loadingRepoDetails.asStateFlow()

    // Fetch repositories from API and insert into Room Database
    fun fetchRepositories(query: String, page: Int) {
        viewModelScope.launch {
            _loading.value = true

            // Call API to fetch repositories
            itemsRepository.getItemRepository(query, page).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _loading.value = false
                        _showErrorToastChannel.send(true)
                    }
                    is Result.Success -> {
                        result.data?.let { newItems ->
                            // Save the fetched items to the database
                            val repositoryEntities = newItems.map { it.toRepositoryEntity() }
                            MyApplication.database.repoDao().insertAllRepositories(repositoryEntities)

                            // Update the state with the new items
                            _items.value = newItems
                            _loading.value = false

                            // Check if more data is available
                            if (newItems.isEmpty()) {
                                _hasMoreData.value = false
                            }
                        }
                    }
                }
            }
        }
    }

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

    // Observe repositories from the database using Flow
    fun getRepositoriesFromDb() {
        viewModelScope.launch {
            MyApplication.database.repoDao().getAllRepositories().collect { repositoryEntities ->
                _items.value = repositoryEntities.map { it.toItem() }
            }
        }
    }

    // Reset pagination
    fun resetPagination() {
        currentPage = 1
        _hasMoreData.value = true
        _loading.value = false
        _items.value = emptyList()
    }

    // Load the next page
    fun loadNextPage(query: String) {
        if (_loading.value || !_hasMoreData.value) return

        currentPage += 1
        fetchRepositories(query, currentPage)
    }
}

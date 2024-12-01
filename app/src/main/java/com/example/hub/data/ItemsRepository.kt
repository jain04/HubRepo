package com.example.hub.data

import com.example.hub.data.model.Item
import kotlinx.coroutines.flow.Flow


interface ItemsRepository {
    suspend fun getItemRepository(query: String, page: Int):Flow<Result<List<Item>>>

    suspend fun getRepoDetails(itemId: String): Flow<Result<Item>>
}
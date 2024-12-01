package com.example.hub.data

import com.example.hub.RetrofitInstance
import com.example.hub.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ItemsRepositoryImplementation(
    private val api: Api
):ItemsRepository {
    override suspend fun getItemRepository(query: String, page: Int): Flow<Result<List<Item>>> {
        return flow {
            val itemsFromApi = try {
                api.getRepositoryList(query = query, perPage = 10, page = page) // Use the 'page' argument
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading repository"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading repository"))
                return@flow
            }
            emit(Result.Success(itemsFromApi.items))
        }
    }


    override suspend fun getRepoDetails(itemId: String): Flow<Result<Item>> {
        return flow {
            try {
                // Make the network request to fetch repository details
                val response = RetrofitInstance.api.getRepositoryDetails(itemId)

                // Check if the response was successful (status code 200-299)
                if (response.isSuccessful) {
                    // Check if the response body is not null
                    response.body()?.let {
                        emit(Result.Success(it)) // Emit success with the repository details
                    } ?: emit(Result.Error("Repository details not available"))
                } else {
                    // Handle unsuccessful response with status code
                    emit(Result.Error("Failed to fetch repository details. Status code: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Catch network or other exceptions
                emit(Result.Error("Network error or invalid response"))
            }
        }
    }


}









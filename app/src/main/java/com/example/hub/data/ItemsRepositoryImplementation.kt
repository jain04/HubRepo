package com.example.hub.data

import com.example.hub.RetrofitInstance
import com.example.hub.data.model.Item
import com.example.hub.data.model.Owner
import com.example.hub.data.model.RepoData
import com.example.hub.room.RepoDao
import com.example.hub.room.data.RepositoryEntity
import com.example.hub.room.data.toRepositoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ItemsRepositoryImplementation(
    private val api: Api,
    private val repoDao: RepoDao
) : ItemsRepository {

    override suspend fun getItemRepository(query: String, page: Int): Flow<Result<List<Item>>> {
        return flow {
            var itemsFromApi: RepoData? = null

            try {
                // Try fetching data from the network (GitHub API)
                itemsFromApi = api.getRepositoryList(query = query, perPage = 15, page = page)

                // Cache the result in the database (Room)
                itemsFromApi.items.forEach { item ->
                    val repoEntity = item.toRepositoryEntity()

                    // Insert or replace the repository in the Room database
                    repoDao.insertAllRepositories(listOf(repoEntity))
                }

                // Emit the result from the API (online data)
                emit(Result.Success(itemsFromApi.items))
            } catch (e: IOException) {
                // Handle network errors (e.g., no internet connection)
                e.printStackTrace()

                // Emit cached data if available, otherwise emit an error
                val cachedItems = repoDao.getAllRepositories()
                cachedItems.collect { repositories ->
                    if (repositories.isNotEmpty()) {
                        emit(Result.Success(repositories.map { it.toItem() }))  // Map Room entities to domain models
                    } else {
                        emit(Result.Error(message = "Error loading repository and no cached data available"))
                    }
                }
            } catch (e: HttpException) {
                // Handle HTTP errors (e.g., 500, 404)
                e.printStackTrace()

                // Emit cached data if available, otherwise emit an error
                val cachedItems = repoDao.getAllRepositories()
                cachedItems.collect { repositories ->
                    if (repositories.isNotEmpty()) {
                        emit(Result.Success(repositories.map { it.toItem() }))  // Map Room entities to domain models
                    } else {
                        emit(Result.Error(message = "Error loading repository from server"))
                    }
                }
            }
        }
    }

    override suspend fun getRepoDetails(itemId: String): Flow<Result<Item>> {
        return flow {
            try {
                // Fetch repository details from the network
                val response = RetrofitInstance.api.getRepositoryDetails(itemId)

                // Check if the response was successful
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Result.Success(it))  // Emit success with the repository details
                    } ?: emit(Result.Error("Repository details not available"))
                } else {
                    emit(Result.Error("Failed to fetch repository details. Status code: ${response.code()}"))
                }
            } catch (e: Exception) {
                // Catch network or other exceptions
                emit(Result.Error("Network error or invalid response"))
            }
        }
    }
}
fun RepositoryEntity.toItem(): Item {
    return Item(
        id = this.id,
        name = this.name,
        description = this.description,
        forks_count = this.forks_count,
        language = this.language,
        stargazers_count = this.stargazers_count,
        html_url = this.html_url,
        collaborators_url = this.collaborators_url,
        owner = TODO(),
    )
}


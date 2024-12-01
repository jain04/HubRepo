package com.example.hub.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.hub.room.data.RepositoryEntity

@Dao
interface RepoDao {

    // Insert a list of repositories into the database (replaces existing entries)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRepositories(repositories: List<RepositoryEntity>)

    // Get all repositories as Flow (LiveData alternative)
    @Query("SELECT * FROM repositories")
    fun getAllRepositories(): Flow<List<RepositoryEntity>>
}


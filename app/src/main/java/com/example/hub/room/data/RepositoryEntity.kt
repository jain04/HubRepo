package com.example.hub.room.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hub.data.model.Item

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val forks_count: Int,
    val language: String,
    val stargazers_count: Int,
    val html_url: String,
    val collaborators_url: String
)

fun Item.toRepositoryEntity(): RepositoryEntity {
    return RepositoryEntity(
        id = this.id,
        name = this.name,
        description = this.description ?: "",  // Default to empty string if null
        forks_count = this.forks_count,
        language = this.language ?: "",  // Default to empty string if null
        stargazers_count = this.stargazers_count,
        html_url = this.html_url,
        collaborators_url = this.collaborators_url
    )
}


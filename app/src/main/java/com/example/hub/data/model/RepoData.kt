package com.example.hub.data.model

data class RepoData(
    val incomplete_results: Boolean,
    val items: List<Item>,
    val total_count: Int
)
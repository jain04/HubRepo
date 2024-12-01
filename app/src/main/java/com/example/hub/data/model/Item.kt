package com.example.hub.data.model

data class Item(
    val collaborators_url: String,
    val description: String,
    val forks_count: Int,
    val html_url: String,
    val id: Int,
    val language: String,
    val name: String,
    val owner: Owner,
    val stargazers_count: Int,

)
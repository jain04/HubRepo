package com.example.hub.data

import com.example.hub.data.model.Item
import com.example.hub.data.model.RepoData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("search/repositories")
    suspend fun getRepositoryList(
        @Query("q") query: String,
        @Query("per_page") perPage:Int=15,
        @Query("page") page:Int
    ):RepoData

    @GET("repositories/{itemId}")
    suspend fun getRepositoryDetails(
        @Path("itemId") itemId: String
    ): retrofit2.Response<Item>


    companion object{
        const val BASE_URL="https://api.github.com/"
    }
}
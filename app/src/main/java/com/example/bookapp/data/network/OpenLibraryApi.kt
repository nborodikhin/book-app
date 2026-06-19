package com.example.bookapp.data.network

import com.example.bookapp.data.network.models.SearchResponse
import com.example.bookapp.data.network.models.WorkDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20,
        @Query("fields") fields: String = "key,title,author_name,cover_i"
    ): SearchResponse

    @GET("works/{workId}.json")
    suspend fun getWork(@Path("workId") workId: String): WorkDetailResponse
}

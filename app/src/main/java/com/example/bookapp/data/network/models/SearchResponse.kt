package com.example.bookapp.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    @SerialName("numFound") val numFound: Int = 0,
    @SerialName("docs") val docs: List<SearchDoc> = emptyList()
)

@Serializable
data class SearchDoc(
    @SerialName("key") val key: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("author_name") val authorName: List<String>? = null,
    @SerialName("cover_i") val coverId: Long? = null
)

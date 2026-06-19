package com.example.bookapp.data.repository

data class SearchResult(
    val workId: String,
    val title: String,
    val authors: List<String>,
    val coverUrl: String?
)

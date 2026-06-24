package com.example.bookapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val workId: String,
    val title: String,
    val authors: String,
    val synopsis: String,
    val coverUrl: String?
) {
    val authorList: List<String> get() = authors.split(", ").filter { it.isNotBlank() }
}

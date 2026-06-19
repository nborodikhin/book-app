package com.example.bookapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: BookEntity)

    @Query("SELECT * FROM books WHERE workId = :workId")
    suspend fun getBook(workId: String): BookEntity?

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>
}

package com.example.bookapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.bookapp.data.local.AppDatabase
import com.example.bookapp.data.local.BookDao
import com.example.bookapp.data.local.BookEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: BookDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.bookDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    // 11.1 — insert and retrieve; entity not deleted after unbookmark (no delete method)
    @Test
    fun insertAndRetrieveBook() = runTest {
        val entity = BookEntity("OL1W", "Dune", "Frank Herbert", "A desert planet.", null)
        dao.insert(entity)

        val retrieved = dao.getBook("OL1W")
        assertNotNull(retrieved)
        assertEquals("Dune", retrieved?.title)
        assertEquals("Frank Herbert", retrieved?.authors)
    }

    @Test
    fun entityRetainedWithoutDeleteMethod() = runTest {
        val entity = BookEntity("OL1W", "Dune", "Frank Herbert", "A desert planet.", null)
        dao.insert(entity)
        // BookDao has no delete method by design; just verify it persists
        assertNotNull(dao.getBook("OL1W"))
    }

    // 11.2 — getBookmarkedBooks() Flow emits updated list when new entities inserted
    @Test
    fun getAllBooksFlowEmitsUpdatedList() = runTest {
        dao.getAllBooks().test {
            assertEquals(emptyList<BookEntity>(), awaitItem())

            dao.insert(BookEntity("OL1W", "Dune", "Frank Herbert", "", null))
            assertEquals(1, awaitItem().size)

            dao.insert(BookEntity("OL2W", "Foundation", "Asimov", "", null))
            assertEquals(2, awaitItem().size)

            cancelAndIgnoreRemainingEvents()
        }
    }
}

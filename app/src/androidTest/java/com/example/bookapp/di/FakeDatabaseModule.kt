package com.example.bookapp.di

import android.content.Context
import androidx.room.Room
import com.example.bookapp.data.local.AppDatabase
import com.example.bookapp.data.local.BookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object FakeDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

    @Provides
    @Singleton
    fun provideBookDao(db: AppDatabase): BookDao = db.bookDao()
}

package com.revelatestudio.simplifyspeechnotes.di

import android.content.Context
import androidx.room.Room
import com.revelatestudio.simplifyspeechnotes.data.local.NoteDatabase
import com.revelatestudio.simplifyspeechnotes.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Singleton
    @Provides
    fun provideNoteDatabase(
        @ApplicationContext app : Context
    ) = Room.databaseBuilder(
        app,
        NoteDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideNoteDao(db : NoteDatabase) = db.getNoteDao()


    @Singleton
    @Provides
    fun provideDispatcher() = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined

    }

    companion object {
        const val DATABASE_NAME = "note.db"
    }


}
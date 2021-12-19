package com.revelatestudio.simplifyspeechnotes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [Note::class],
    version = 1
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getNoteDao() : NoteDao
}
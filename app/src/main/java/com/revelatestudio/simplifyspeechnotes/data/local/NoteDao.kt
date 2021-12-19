package com.revelatestudio.simplifyspeechnotes.data.local

import androidx.room.*

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note) : Long

    @Update
    fun updateNote(vararg note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note_table")
    suspend fun getNotes() : List<Note>

    @Query("SELECT * FROM note_table WHERE id = :id")
    suspend fun getNote(id : Int) : Note?


}
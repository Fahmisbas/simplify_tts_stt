package com.revelatestudio.simplifyspeechnotes.data.repository

import com.revelatestudio.simplifyspeechnotes.data.local.Note
import com.revelatestudio.simplifyspeechnotes.data.local.NoteDao
import com.revelatestudio.simplifyspeechnotes.utils.Resource
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val dao: NoteDao
) {

    suspend fun insertNote(note: Note): Long {
        return dao.insertNote(note)
    }

    fun updateNote(note: Note) {
        dao.updateNote(note)
    }

    suspend fun getNotes(): Resource<List<Note>> {
        val notes = dao.getNotes()
        return if (!notes.isNullOrEmpty()) {
            Resource.Success(notes)
        } else Resource.Empty()
    }

    suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }
}
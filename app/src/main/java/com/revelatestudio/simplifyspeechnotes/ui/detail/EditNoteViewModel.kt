package com.revelatestudio.simplifyspeechnotes.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revelatestudio.simplifyspeechnotes.data.local.Note
import com.revelatestudio.simplifyspeechnotes.data.repository.MainRepository
import com.revelatestudio.simplifyspeechnotes.utils.DispatcherProvider
import com.revelatestudio.simplifyspeechnotes.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private val _note = MutableLiveData<RetrievalEvent>(RetrievalEvent.Empty)
    val note: LiveData<RetrievalEvent> = _note

    fun insertNote(note: Note, id : (Long) -> Unit) {
        viewModelScope.launch(dispatcher.io) {
            id.invoke(repository.insertNote(note))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(dispatcher.io) {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(dispatcher.io) {
            repository.deleteNote(note)
        }
    }

    sealed class RetrievalEvent {
        class Success(val note: Note) : RetrievalEvent()
        class Failure(val errorMsg: String) : RetrievalEvent()
        object Empty : RetrievalEvent()
    }


}
package com.revelatestudio.simplifyspeechnotes.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revelatestudio.simplifyspeechnotes.data.local.Note
import com.revelatestudio.simplifyspeechnotes.data.repository.MainRepository
import com.revelatestudio.simplifyspeechnotes.ui.detail.EditNoteViewModel
import com.revelatestudio.simplifyspeechnotes.utils.DispatcherProvider
import com.revelatestudio.simplifyspeechnotes.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository : MainRepository,
    private val dispatcher : DispatcherProvider
) : ViewModel() {

    private val _notes = MutableLiveData<RetrievalEvent>(RetrievalEvent.Empty)
    val notes: LiveData<RetrievalEvent> = _notes

    fun getNotes() {
        viewModelScope.launch(dispatcher.io) {
            _notes.postValue(RetrievalEvent.Loading)
            when (val notes = repository.getNotes()) {
                is Resource.Success -> {
                    if (notes.data != null) {
                        val arrayNote = arrayListOf<Note>()
                        arrayNote.addAll(notes.data)
                        _notes.postValue(RetrievalEvent.Success(arrayNote))
                    } else _notes.postValue(RetrievalEvent.Failure("Unexpected Error"))
                }
                is Resource.Empty -> _notes.postValue(RetrievalEvent.Empty)
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(dispatcher.io) {
            repository.deleteNote(note)
        }
    }

    sealed class RetrievalEvent {
        class Success(val notes: ArrayList<Note>) : RetrievalEvent()
        class Failure(val errorMsg: String) : RetrievalEvent()
        object Loading : RetrievalEvent()
        object Empty : RetrievalEvent()
    }
}
package com.example.randomstringapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.randomstringapp.database.GeneratedStringEntity
import com.example.randomstringapp.repository.RandomStringAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RandomStringAppViewModel(private val repository: RandomStringAppRepository) : ViewModel() {
    private var hasReadPermission by mutableStateOf(false)
    private var hasWritePermission by mutableStateOf(false)

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val generatedStrings: StateFlow<List<GeneratedStringEntity>> =
        repository.generatedStrings.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    /**
     * Queries the content provider for a random string of the given length
     * and saves it to the local database.
     *
     * If an error occurs, updates [_error] so the UI can show a message.
     */
    fun generateString(length: Int) {
        viewModelScope.launch {
            val result = repository.queryAndSaveGeneratedString(length)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    /**
     * Deletes a single generated string record from the local database.
     *
     * @param record The [GeneratedStringEntity] to delete.
     */
    fun deleteOneRecord(record: GeneratedStringEntity) {
        viewModelScope.launch {
            repository.deleteSingleRecord(record)
        }
    }

    /**
     * Clears all generated string records from the local database.
     */
    fun clearTable() {
        viewModelScope.launch {
            repository.clearAllRecords()
        }
    }

    fun setReadPermission(granted: Boolean) {
        hasReadPermission = granted
    }

    fun setWritePermission(granted: Boolean) {
        hasWritePermission = granted
    }

    fun clearError() {
        _error.value = null
    }
}

class RandomStringAppViewModelFactory(
    private val repository: RandomStringAppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RandomStringAppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RandomStringAppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
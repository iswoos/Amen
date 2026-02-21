package com.example.amen.presentation.ui.bible

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amen.domain.repository.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleBrowserViewModel @Inject constructor(
    private val bibleRepository: BibleRepository
) : ViewModel() {

    private val _books = MutableStateFlow<List<String>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredBooks: StateFlow<List<String>> = combine(_books, _searchQuery) { currentBooks, query ->
        if (query.isBlank()) currentBooks
        else currentBooks.filter { it.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedBook = MutableStateFlow<String?>(null)
    val selectedBook: StateFlow<String?> = _selectedBook.asStateFlow()

    private val _chapters = MutableStateFlow<List<Int>>(emptyList())
    val chapters: StateFlow<List<Int>> = _chapters.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _books.value = bibleRepository.getAllBooks()
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun selectBook(book: String) {
        _selectedBook.value = book
        _searchQuery.value = "" // 선택 시 검색어 초기화
        viewModelScope.launch {
            _chapters.value = bibleRepository.getChapters(book)
        }
    }

    fun clearBookSelection() {
        _selectedBook.value = null
        _chapters.value = emptyList()
    }
}

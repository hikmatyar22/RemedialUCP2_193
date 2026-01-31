package com.example.praktikum7.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktikum7.repositori.RepositoriPerpustakaan
import com.example.praktikum7.room.Buku
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repositoriPerpustakaan: RepositoriPerpustakaan): ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val homeUiState: StateFlow<HomeUiState> = repositoriPerpustakaan.getAllBuku().filterNotNull()
        .map { HomeUiState(listBuku = it.toList()) }
        .stateIn(scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState())

    fun getBooksByCategory(categoryId: Int): StateFlow<List<Buku>> {
         return repositoriPerpustakaan.getBooksByCategoryRecursive(categoryId)
             .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    data class HomeUiState(
        val listBuku: List<Buku> = listOf()
    )
}
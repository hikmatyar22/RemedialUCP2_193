package com.example.praktikum7.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktikum7.repositori.RepositoriPerpustakaan
import com.example.praktikum7.room.Buku
import com.example.praktikum7.room.Kategori
import com.example.praktikum7.view.route.DestinasiEditSiswa
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoriPerpustakaan: RepositoriPerpustakaan
) : ViewModel() {

    var uiStateBuku by mutableStateOf(UIStateBuku())
        private set


    val kategoriList: StateFlow<List<Kategori>> = repositoriPerpustakaan.getAllKategori()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val idBuku: Int =
        checkNotNull(savedStateHandle[DestinasiEditSiswa.itemIdArg])

    init {
        viewModelScope.launch {
            uiStateBuku = repositoriPerpustakaan.getBuku(idBuku)
                .filterNotNull()
                .first()
                .toUiStateBuku(true)
        }
    }

    fun updateUIState(detailBuku: DetailBuku) {
        uiStateBuku =
            uiStateBuku.copy(
                detailBuku = detailBuku,
                isEntryValid = validasiInput(detailBuku)
            )
    }

    private fun validasiInput(uiState: DetailBuku = uiStateBuku.detailBuku): Boolean {
        return with(uiState) {
            judul.isNotBlank() && deskripsi.isNotBlank()
        }
    }

    suspend fun updateBuku() {
        if (validasiInput(uiStateBuku.detailBuku)) {
            repositoriPerpustakaan.updateBuku(uiStateBuku.detailBuku.toBuku())
        }
    }
}
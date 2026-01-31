package com.example.praktikum7.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktikum7.repositori.RepositoriPerpustakaan
import com.example.praktikum7.room.Buku
import com.example.praktikum7.room.Kategori
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class EntryViewModel(private val repositoriPerpustakaan: RepositoriPerpustakaan): ViewModel() {
    var uiStateBuku by mutableStateOf(UIStateBuku())
        private set


    val kategoriList: StateFlow<List<Kategori>> = repositoriPerpustakaan.getAllKategori()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private fun validasiInput(uiState: DetailBuku = uiStateBuku.detailBuku): Boolean{
        return with(uiState){
            judul.isNotBlank() && deskripsi.isNotBlank() && kategoriId != null
        }
    }

    fun updateUiState(detailBuku: DetailBuku){
        uiStateBuku =
            UIStateBuku(detailBuku = detailBuku,
                isEntryValid = validasiInput(detailBuku))
    }

    suspend fun saveBuku() {
        if (validasiInput()) {
            try {
                repositoriPerpustakaan.insertBuku(uiStateBuku.detailBuku.toBuku())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class UIStateBuku(
    val detailBuku: DetailBuku = DetailBuku(),
    val isEntryValid: Boolean = false
)

data class DetailBuku(
    val id: Int = 0,
    val judul: String = "",
    val deskripsi: String = "",
    val kategoriId: Int? = null,
    val stok: Int = 0
)

fun DetailBuku.toBuku(): Buku = Buku(
    id = id,
    judul = judul,
    deskripsi = deskripsi,
    kategoriId = kategoriId,
    stok = stok
)

fun Buku.toUiStateBuku(isEntryValid: Boolean = false): UIStateBuku = UIStateBuku(
    detailBuku = this.toDetailBuku(),
    isEntryValid = isEntryValid
)

fun Buku.toDetailBuku(): DetailBuku = DetailBuku(
    id = id,
    judul = judul,
    deskripsi = deskripsi,
    kategoriId = kategoriId,
    stok = stok ?: 0
)
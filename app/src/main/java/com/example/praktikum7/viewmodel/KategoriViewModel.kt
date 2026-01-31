package com.example.praktikum7.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.praktikum7.repositori.RepositoriPerpustakaan
import com.example.praktikum7.room.Kategori

class KategoriViewModel(private val repositoriPerpustakaan: RepositoriPerpustakaan) : ViewModel() {
    var uiStateKategori by mutableStateOf(DetailKategori())
        private set

    fun updateUiState(detailKategori: DetailKategori) {
        uiStateKategori = detailKategori
    }

    suspend fun saveKategori() {
        if (uiStateKategori.nama.isNotBlank()) {
            repositoriPerpustakaan.insertKategori(uiStateKategori.toKategori())
        }
    }
}

data class DetailKategori(
    val id: Int = 0,
    val nama: String = "",
    val deskripsi: String = ""
)

fun DetailKategori.toKategori(): Kategori = Kategori(
    id = id,
    nama = nama,
    deskripsi = deskripsi
)

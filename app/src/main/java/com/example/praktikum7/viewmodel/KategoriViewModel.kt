package com.example.praktikum7.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktikum7.repositori.RepositoriPerpustakaan
import com.example.praktikum7.room.Kategori
import com.example.praktikum7.room.KategoriWithLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class KategoriViewModel(private val repositoriPerpustakaan: RepositoriPerpustakaan) : ViewModel() {
    var uiStateKategori by mutableStateOf(DetailKategori())
        private set

    var selectedCategoryId by mutableStateOf<Int?>(null)
        private set

    var categoryPath by mutableStateOf<List<KategoriWithLevel>>(emptyList())
        private set

    fun updateUiState(detailKategori: DetailKategori) {
        uiStateKategori = detailKategori
    }

    fun selectCategory(categoryId: Int?) {
        selectedCategoryId = categoryId
        viewModelScope.launch {
            categoryId?.let {
                categoryPath = repositoriPerpustakaan.getCategoryPath(it)
            } ?: run {
                categoryPath = emptyList()
            }
        }
    }

    suspend fun saveKategori() {
        if (uiStateKategori.nama.isNotBlank()) {
            repositoriPerpustakaan.insertKategori(uiStateKategori.toKategori())
        }
    }

    fun getAllKategori(): Flow<List<Kategori>> {
        return repositoriPerpustakaan.getAllKategori()
    }

    fun getHierarchicalCategories(): Flow<List<KategoriWithLevel>> {
        return repositoriPerpustakaan.getAllKategoriHierarchical()
    }

    fun getSubCategories(parentId: Int?): Flow<List<KategoriWithLevel>> {
        return repositoriPerpustakaan.getSubCategories(parentId)
    }

    fun getBooksByCategoryRecursive(categoryId: Int): Flow<List<com.example.praktikum7.room.Buku>> {
        return repositoriPerpustakaan.getBooksByCategoryRecursive(categoryId)
    }

    suspend fun getSubCategoryCount(categoryId: Int): Int {
        return repositoriPerpustakaan.getSubCategoryCount(categoryId)
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

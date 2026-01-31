package com.example.praktikum7.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktikum7.repositori.RepositoriPerpustakaan
import com.example.praktikum7.view.route.DestinasiDetailSiswa
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DetailViewModel (
    savedStateHandle: SavedStateHandle,
    private val repositoriPerpustakaan: RepositoriPerpustakaan
) : ViewModel(){

    private val idBuku: Int = checkNotNull(savedStateHandle[DestinasiDetailSiswa.itemIdArg])

    val uiDetailState: StateFlow<DetailBukuUiState> =
        repositoriPerpustakaan.getBuku(idBuku)
            .filterNotNull()
            .map {
                DetailBukuUiState(detailBuku = it.toDetailBuku())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DetailBukuUiState()
            )
    
    suspend fun deleteBuku(){
        repositoriPerpustakaan.deleteBuku(uiDetailState.value.detailBuku.toBuku())
    }
}

data class DetailBukuUiState(
    val detailBuku: DetailBuku = DetailBuku()
)
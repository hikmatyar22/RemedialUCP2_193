package com.example.praktikum7.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.praktikum7.repositori.AplikasiSiswa
import com.example.praktikum7.viewmodel.DetailViewModel
import com.example.praktikum7.viewmodel.EditViewModel
import com.example.praktikum7.viewmodel.EntryViewModel
import com.example.praktikum7.viewmodel.HomeViewModel
import com.example.praktikum7.viewmodel.KategoriViewModel

object PenyediaViewModel{
    val Factory = viewModelFactory{
        initializer {
            HomeViewModel(aplikasiSiswa().container.repositoriPerpustakaan)
        }
        initializer {
            EntryViewModel(aplikasiSiswa().container.repositoriPerpustakaan)
        }
        initializer {
            DetailViewModel(
                this.createSavedStateHandle(),
                aplikasiSiswa().container.repositoriPerpustakaan
            )
        }
        initializer {
            EditViewModel(
                this.createSavedStateHandle(),
                aplikasiSiswa().container.repositoriPerpustakaan
            )
        }
        initializer {
            KategoriViewModel(aplikasiSiswa().container.repositoriPerpustakaan)
        }
    }
}

fun CreationExtras.aplikasiSiswa(): AplikasiSiswa =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiSiswa)
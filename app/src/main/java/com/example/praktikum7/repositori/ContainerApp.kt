package com.example.praktikum7.repositori

import android.app.Application
import android.content.Context
import com.example.praktikum7.room.DatabasePerpustakaan

interface ContainerApp {
    val repositoriPerpustakaan : RepositoriPerpustakaan
}

class ContainerDataApp(private val context: Context):
    ContainerApp {
    override val repositoriPerpustakaan: RepositoriPerpustakaan by lazy {
        OfflineRepositoriPerpustakaan(
            DatabasePerpustakaan.getDatabase(context).perpustakaanDao())
    }
}

class AplikasiSiswa : Application() {

    lateinit var container: ContainerApp

    override fun onCreate() {
        super.onCreate()
        container = ContainerDataApp(this)
    }
}
package com.example.praktikum7.view.route
import com.example.praktikum7.R

object DestinasiEntry: DestinasiNavigasi {
    override val route = "item_entry"
    override val titleRes = R.string.entry_siswa
}

object DestinasiEntryKategori : DestinasiNavigasi {
    override val route = "item_entry_kategori"
    override val titleRes = R.string.entry_kategori // We'll assume this string resource or use a hardcoded title in UI if resource missing, but ideally referencing R.string
}
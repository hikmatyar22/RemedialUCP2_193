package com.example.praktikum7.view.route

import com.example.praktikum7.R

object DestinasiDetailSiswa: DestinasiNavigasi {
    override val route = "detail_siswa"
    override val titleRes = R.string.detail_siswa
    const val itemIdArg = "idSiswa"
    val routeWithArgs = "$route/{$itemIdArg}"
}

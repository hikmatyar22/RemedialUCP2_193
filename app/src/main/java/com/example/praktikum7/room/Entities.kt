package com.example.praktikum7.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


enum class StatusBuku {
    TERSEDIA, DIPINJAM, HILANG, RUSAK
}

@Entity(tableName = "tblKategori")
data class Kategori(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val deskripsi: String,
    val parentId: Int? = null,
    val isDeleted: Boolean = false
)

@Entity(tableName = "tblPenulis")
data class Penulis(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val biografi: String,
    val isDeleted: Boolean = false
)

@Entity(
    tableName = "tblBuku",
    foreignKeys = [
        ForeignKey(
            entity = Kategori::class,
            parentColumns = ["id"],
            childColumns = ["kategoriId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["kategoriId"])]
)
data class Buku(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val deskripsi: String,
    val kategoriId: Int?,
    val stok: Int = 0,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


@Entity(
    tableName = "tblBukuPenulis",
    primaryKeys = ["bukuId", "penulisId"],
    foreignKeys = [
        ForeignKey(entity = Buku::class, parentColumns = ["id"], childColumns = ["bukuId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Penulis::class, parentColumns = ["id"], childColumns = ["penulisId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("bukuId"), Index("penulisId")]
)
data class BukuPenulisCrossRef(
    val bukuId: Int,
    val penulisId: Int
)

@Entity(
    tableName = "tblAsetBuku",
    foreignKeys = [
        ForeignKey(entity = Buku::class, parentColumns = ["id"], childColumns = ["bukuId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("bukuId")]
)
data class AsetBuku(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bukuId: Int,
    val kodeUnik: String,
    val status: StatusBuku = StatusBuku.TERSEDIA,
    val isDeleted: Boolean = false
)

@Entity(tableName = "tblAuditLog")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tableName: String,
    val recordId: Int,
    val action: String,
    val oldValue: String? = null,
    val newValue: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

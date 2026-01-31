package com.example.praktikum7.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Kategori::class,
        Buku::class,
        Penulis::class,
        BukuPenulisCrossRef::class,
        AsetBuku::class,
        AuditLog::class
    ],
    version = 3,
    exportSchema = false
)
abstract class DatabasePerpustakaan : RoomDatabase() {
    abstract fun perpustakaanDao(): PerpustakaanDao

    companion object {
        @Volatile
        private var Instance: DatabasePerpustakaan? = null

        fun getDatabase(context: Context): DatabasePerpustakaan {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    DatabasePerpustakaan::class.java,
                    "perpustakaan_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("INSERT INTO tblKategori (id, nama, deskripsi, isDeleted) VALUES (1, 'Umum', 'Kategori Default', 0)")
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
            }
        }
    }
}

package com.example.praktikum7.repositori

import com.example.praktikum7.room.AuditLog
import com.example.praktikum7.room.Buku
import com.example.praktikum7.room.Kategori
import com.example.praktikum7.room.KategoriWithLevel
import com.example.praktikum7.room.PerpustakaanDao
import kotlinx.coroutines.flow.Flow

interface RepositoriPerpustakaan {

    fun getAllKategori(): Flow<List<Kategori>>
    fun getKategori(id: Int): Flow<Kategori?>
    suspend fun insertKategori(kategori: Kategori)
    suspend fun updateKategori(kategori: Kategori)
    suspend fun deleteKategori(categoryId: Int, deleteBooks: Boolean) : Boolean

    fun getAllKategoriHierarchical(): Flow<List<KategoriWithLevel>>
    fun getSubCategories(parentId: Int?): Flow<List<KategoriWithLevel>>
    suspend fun getCategoryPath(categoryId: Int): List<KategoriWithLevel>
    suspend fun getSubCategoryCount(categoryId: Int): Int

    fun getAllBuku(): Flow<List<Buku>>
    fun getBooksByCategoryRecursive(categoryId: Int): Flow<List<Buku>>
    fun getBuku(id: Int): Flow<Buku?>
    suspend fun insertBuku(buku: Buku)
    suspend fun updateBuku(buku: Buku)
    suspend fun deleteBuku(buku: Buku)
    

    fun getAllLogs(): Flow<List<AuditLog>>
}

class OfflineRepositoriPerpustakaan(
    private val perpustakaanDao: PerpustakaanDao
) : RepositoriPerpustakaan {

    override fun getAllKategori(): Flow<List<Kategori>> = perpustakaanDao.getAllKategori()
    override fun getKategori(id: Int): Flow<Kategori?> = perpustakaanDao.getKategori(id)

    override suspend fun insertKategori(kategori: Kategori) {
        perpustakaanDao.insertKategori(kategori)
        logAction("tblKategori", 0, "INSERT", "New Category: ${kategori.nama}")
    }

    override suspend fun updateKategori(kategori: Kategori) {
        if (kategori.parentId != null) {
            val isCyclic = perpustakaanDao.isAncestor(targetAncestorId = kategori.id, childId = kategori.parentId)
            if (isCyclic > 0) {
                throw Exception("Cyclic reference detected! Cannot set a descendant as parent.")
            }
        }
        perpustakaanDao.updateKategori(kategori)
        logAction("tblKategori", kategori.id, "UPDATE", "Updated Category")
    }

    override suspend fun deleteKategori(categoryId: Int, deleteBooks: Boolean): Boolean {

        val bookIds = perpustakaanDao.getBookIdsByCategoryRecursive(categoryId)
        for (bookId in bookIds) {
            val borrowedCount = perpustakaanDao.getBorrowedCountForBook(bookId)
            if (borrowedCount > 0) {

                throw Exception("Gagal Hapus: Kategori ini atau turunannya memiliki buku yang sedang dipinjam.")
            }
        }


        if (deleteBooks) {

            for (bookId in bookIds) {
                perpustakaanDao.softDeleteBuku(bookId)
                logAction("tblBuku", bookId, "SOFT_DELETE", "Book soft deleted due to category deletion")
            }
        } else {

            for (bookId in bookIds) {
                perpustakaanDao.updateBukuKategori(bookId, null)
                logAction("tblBuku", bookId, "UPDATE", "Book category set to NULL (No Category)")
            }
        }


        perpustakaanDao.softDeleteKategori(categoryId)
        logAction("tblKategori", categoryId, "SOFT_DELETE", "Soft Deleted Category")
        
        return true
    }

    override fun getAllKategoriHierarchical(): Flow<List<KategoriWithLevel>> = 
        perpustakaanDao.getAllKategoriHierarchical()

    override fun getSubCategories(parentId: Int?): Flow<List<KategoriWithLevel>> = 
        perpustakaanDao.getSubCategories(parentId)

    override suspend fun getCategoryPath(categoryId: Int): List<KategoriWithLevel> = 
        perpustakaanDao.getCategoryPath(categoryId)

    override suspend fun getSubCategoryCount(categoryId: Int): Int = 
        perpustakaanDao.getSubCategoryCount(categoryId)

    override fun getAllBuku(): Flow<List<Buku>> = perpustakaanDao.getAllBuku()

    override fun getBooksByCategoryRecursive(categoryId: Int): Flow<List<Buku>> {
        return perpustakaanDao.getBooksByCategoryRecursive(categoryId)
    }

    override fun getBuku(id: Int): Flow<Buku?> = perpustakaanDao.getBuku(id)

    override suspend fun insertBuku(buku: Buku) {
        val id = perpustakaanDao.insertBuku(buku)
        logAction("tblBuku", id.toInt(), "INSERT", "New Book: ${buku.judul}")
    }

    override suspend fun updateBuku(buku: Buku) {
        perpustakaanDao.updateBuku(buku)
        logAction("tblBuku", buku.id, "UPDATE", "Updated Book: ${buku.judul}")
    }

    override suspend fun deleteBuku(buku: Buku) {
        perpustakaanDao.softDeleteBuku(buku.id)
        logAction("tblBuku", buku.id, "SOFT_DELETE", "Soft Deleted Book: ${buku.judul}")
    }
    
    override fun getAllLogs(): Flow<List<AuditLog>> = perpustakaanDao.getAllAuditLogs()

    private suspend fun logAction(table: String, id: Int, action: String, details: String) {
        perpustakaanDao.insertAuditLog(
            AuditLog(
                tableName = table,
                recordId = id,
                action = action,
                newValue = details
            )
        )
    }
}

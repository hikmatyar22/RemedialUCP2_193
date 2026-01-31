package com.example.praktikum7.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PerpustakaanDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertKategori(kategori: Kategori)

    @Update
    suspend fun updateKategori(kategori: Kategori)

    @Delete
    suspend fun deleteKategori(kategori: Kategori)

    @Query("SELECT * FROM tblKategori WHERE id = :id")
    fun getKategori(id: Int): Flow<Kategori>

    @Query("SELECT * FROM tblKategori WHERE isDeleted = 0 ORDER BY nama ASC")
    fun getAllKategori(): Flow<List<Kategori>>


    @Query("""
        WITH RECURSIVE Ancestors AS (
            SELECT id, parentId FROM tblKategori WHERE id = :childId
            UNION ALL
            SELECT k.id, k.parentId FROM tblKategori k
            INNER JOIN Ancestors a ON k.id = a.parentId
        )
        SELECT COUNT(*) FROM Ancestors WHERE id = :targetAncestorId
    """)
    suspend fun isAncestor(targetAncestorId: Int, childId: Int): Int


    @Query("""
        WITH RECURSIVE CategoryHierarchy AS (
            SELECT id FROM tblKategori WHERE id = :categoryId
            UNION ALL
            SELECT k.id FROM tblKategori k
            INNER JOIN CategoryHierarchy ch ON k.parentId = ch.id
        )
        SELECT * FROM tblBuku WHERE kategoriId IN (SELECT id FROM CategoryHierarchy) AND isDeleted = 0
    """)
    fun getBooksByCategoryRecursive(categoryId: Int): Flow<List<Buku>>



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBuku(buku: Buku): Long

    @Update
    suspend fun updateBuku(buku: Buku)

    @Delete
    suspend fun deleteBuku(buku: Buku)

    @Query("SELECT * FROM tblBuku WHERE id = :id")
    fun getBuku(id: Int): Flow<Buku>

    @Query("SELECT * FROM tblBuku WHERE isDeleted = 0 ORDER BY judul ASC")
    fun getAllBuku(): Flow<List<Buku>>



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPenulis(penulis: Penulis)

    @Update
    suspend fun updatePenulis(penulis: Penulis)

    @Query("SELECT * FROM tblPenulis WHERE isDeleted = 0 ORDER BY nama ASC")
    fun getAllPenulis(): Flow<List<Penulis>>



    @Insert
    suspend fun insertAsetBuku(aset: AsetBuku)

    @Update
    suspend fun updateAsetBuku(aset: AsetBuku)


    @Query("SELECT COUNT(*) FROM tblAsetBuku WHERE bukuId = :bukuId AND status = 'DIPINJAM' AND isDeleted = 0")
    suspend fun getBorrowedCountForBook(bukuId: Int): Int


    @Query("""
        WITH RECURSIVE CategoryHierarchy AS (
            SELECT id FROM tblKategori WHERE id = :categoryId
            UNION ALL
            SELECT k.id FROM tblKategori k
            INNER JOIN CategoryHierarchy ch ON k.parentId = ch.id
        )
        SELECT id FROM tblBuku WHERE kategoriId IN (SELECT id FROM CategoryHierarchy) AND isDeleted = 0
    """)
    suspend fun getBookIdsByCategoryRecursive(categoryId: Int): List<Int>



    @Insert
    suspend fun insertAuditLog(log: AuditLog)
    
    @Query("SELECT * FROM tblAuditLog ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>


    @Query("UPDATE tblKategori SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteKategori(id: Int)

    @Query("UPDATE tblBuku SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteBuku(id: Int)

    @Query("UPDATE tblBuku SET kategoriId = :newKategoriId WHERE id = :bookId")
    suspend fun updateBukuKategori(bookId: Int, newKategoriId: Int?)

    @Query("UPDATE tblAsetBuku SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteAsetBuku(id: Int)

    // Additional recursive category methods for hierarchical navigation
    @Query("""
        WITH RECURSIVE CategoryTree AS (
            SELECT id, nama, deskripsi, parentId, 0 as level 
            FROM tblKategori 
            WHERE parentId IS NULL AND isDeleted = 0
            UNION ALL
            SELECT k.id, k.nama, k.deskripsi, k.parentId, ct.level + 1
            FROM tblKategori k
            INNER JOIN CategoryTree ct ON k.parentId = ct.id
            WHERE k.isDeleted = 0
        )
        SELECT * FROM CategoryTree ORDER BY level, nama
    """)
    fun getAllKategoriHierarchical(): Flow<List<KategoriWithLevel>>

    @Query("""
        WITH RECURSIVE CategoryTree AS (
            SELECT id, nama, deskripsi, parentId, 0 as level 
            FROM tblKategori 
            WHERE parentId IS NULL AND isDeleted = 0
            UNION ALL
            SELECT k.id, k.nama, k.deskripsi, k.parentId, ct.level + 1
            FROM tblKategori k
            INNER JOIN CategoryTree ct ON k.parentId = ct.id
            WHERE k.isDeleted = 0
        )
        SELECT * FROM CategoryTree WHERE parentId = :parentId ORDER BY nama
    """)
    fun getSubCategories(parentId: Int?): Flow<List<KategoriWithLevel>>

    @Query("""
        WITH RECURSIVE CategoryTree AS (
            SELECT id, nama, deskripsi, parentId, 0 as level 
            FROM tblKategori 
            WHERE id = :categoryId AND isDeleted = 0
            UNION ALL
            SELECT k.id, k.nama, k.deskripsi, k.parentId, ct.level + 1
            FROM tblKategori k
            INNER JOIN CategoryTree ct ON k.parentId = ct.id
            WHERE k.isDeleted = 0
        )
        SELECT COUNT(*) FROM CategoryTree WHERE level > 0
    """)
    suspend fun getSubCategoryCount(categoryId: Int): Int

    @Query("""
        WITH RECURSIVE CategoryPath AS (
            SELECT id, nama, deskripsi, parentId, 0 as depth
            FROM tblKategori 
            WHERE id = :categoryId AND isDeleted = 0
            UNION ALL
            SELECT k.id, k.nama, k.deskripsi, k.parentId, cp.depth + 1
            FROM tblKategori k
            INNER JOIN CategoryPath cp ON k.id = cp.parentId
            WHERE k.isDeleted = 0
        )
        SELECT * FROM CategoryPath ORDER BY depth DESC
    """)
    suspend fun getCategoryPath(categoryId: Int): List<KategoriWithLevel>
}

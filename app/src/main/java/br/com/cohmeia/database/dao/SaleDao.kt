package br.com.cohmeia.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.cohmeia.database.entities.SaleEntity

@Dao
interface SaleDao {

    @Query("SELECT * FROM sale")
    fun getAll(): List<SaleEntity>

    @Query("SELECT * FROM sale WHERE syncDate IS NULL OR syncDate <= 0 LIMIT :limit")
    fun getAllNotSynced(limit: Int): List<SaleEntity>

    @Insert
    fun insert(saleEntity: SaleEntity): Long

    @Update
    fun update(vararg entities: SaleEntity)

    @Query("SELECT * FROM sale WHERE id IS :saleId")
    fun getById(saleId: Long) : SaleEntity

}
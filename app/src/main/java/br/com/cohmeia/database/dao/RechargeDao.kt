package br.com.cohmeia.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.cohmeia.database.entities.RechargeEntity

@Dao
interface RechargeDao {

    @Query("SELECT * FROM recharge")
    fun getAll(): List<RechargeEntity>

    @Query("SELECT * FROM recharge WHERE syncDate IS NULL OR syncDate <= 0 LIMIT :limit")
    fun getAllNotSynced(limit: Int): List<RechargeEntity>

    @Insert
    fun insert(rechargeEntity: RechargeEntity)

    @Update
    fun update(vararg entities: RechargeEntity)

}
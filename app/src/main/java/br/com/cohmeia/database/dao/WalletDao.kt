package br.com.cohmeia.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.cohmeia.database.entities.WalletEntity

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet")
    fun getAll(): List<WalletEntity>

    @Insert
    fun insert(vararg walletEntity: WalletEntity)
}
package br.com.cohmeia.database.entities

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.dao.WalletDao
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WalletEntityTest {

    private lateinit var walletDao: WalletDao
    private lateinit var db: CohmeiaDatabase

    private val walletEntity = WalletEntity(tagId = "teste1", externalId = "externalId", employeeId = 1)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, CohmeiaDatabase::class.java
        ).build()
        walletDao = db.walletDao()
        EntityHelper.createEmployee(db)
    }

    @Test
    fun readAndWrite() {
        val walletEntity = WalletEntity(tagId = "teste1", externalId = "externalId", employeeId = 1)
        walletDao.insert(walletEntity)
        assertEquals(1, walletDao.getAll().size)
    }

    @Test
    fun addMultipleWallets() {
        val walletEntity1 = WalletEntity(tagId = "teste1", employeeId = 1)
        val walletEntity2 = WalletEntity(tagId = "teste2", employeeId = 1)
        val walletEntity3 = WalletEntity(tagId = "teste3", employeeId = 2)
        val walletEntity4 = WalletEntity(tagId = "teste1", employeeId = 2)
        walletDao.insert(walletEntity1)
        walletDao.insert(walletEntity2)
        walletDao.insert(walletEntity3)
        walletDao.insert(walletEntity4)
        assertEquals(4, walletDao.getAll().size)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun duplicatedExternalIdShouldNotCompleteDbOperation() {
        walletDao.insert(walletEntity)
        walletDao.insert(walletEntity)
    }

    @After
    fun tearDown() {
        db.close()
    }
}
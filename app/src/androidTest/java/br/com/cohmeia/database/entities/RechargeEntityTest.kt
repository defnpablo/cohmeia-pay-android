package br.com.cohmeia.database.entities

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.dao.RechargeDao
import br.com.cohmeia.database.dao.WalletDao
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RechargeEntityTest {

    private lateinit var rechargeDao: RechargeDao
    private lateinit var db: CohmeiaDatabase

    private val rechargeEntity = RechargeEntity(
        externalId = "externalId",
        employeeId = 1,
        valueInCents = 3500,
        tagId = "asdasd",
        paymentMethod = "cash"
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, CohmeiaDatabase::class.java
        ).build()
        rechargeDao = db.rechargeDao()
        EntityHelper.createEmployee(db)
    }

    @Test
    fun readAndWrite() {
        rechargeDao.insert(rechargeEntity)
        assertEquals(1, rechargeDao.getAll().size)
    }

    @Test
    fun addMultipleRecharges() {
        val rechargeEntity1 =
            RechargeEntity(employeeId = 1, valueInCents = 3500, tagId = "asdasd", paymentMethod = "cash")
        val rechargeEntity2 =
            RechargeEntity(employeeId = 2, valueInCents = 2500, tagId = "asdasd", paymentMethod = "cash")
        val rechargeEntity3 =
            RechargeEntity(employeeId = 3, valueInCents = 100, tagId = "asdasd", paymentMethod = "cash")
        val rechargeEntity4 =
            RechargeEntity(employeeId = 1, valueInCents = 1700, tagId = "asdasd", paymentMethod = "cash")
        rechargeDao.insert(rechargeEntity1)
        rechargeDao.insert(rechargeEntity2)
        rechargeDao.insert(rechargeEntity3)
        rechargeDao.insert(rechargeEntity4)
        assertEquals(4, rechargeDao.getAll().size)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun duplicatedExternalIdShouldNotCompleteDbOperation() {
        rechargeDao.insert(rechargeEntity)
        rechargeDao.insert(rechargeEntity)
    }

    @After
    fun tearDown() {
        db.close()
    }

}
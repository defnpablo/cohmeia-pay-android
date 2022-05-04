package br.com.cohmeia.database.entities

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.dao.SaleDao
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaleEntityTest {

    private lateinit var saleDao: SaleDao
    private lateinit var db: CohmeiaDatabase

    private val salesEntity = SaleEntity(
        externalId = "externalId",
        employeeId = 1,
        valueInCents = 3500,
        tagId = "asdasd"
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, CohmeiaDatabase::class.java
        ).build()
        saleDao = db.saleDao()
        EntityHelper.createEmployee(db)
    }

    @Test
    fun readAndWrite() {
        saleDao.insert(salesEntity)
        Assert.assertEquals(1, saleDao.getAll().size)
    }

    @Test
    fun addMultipleSales() {
        val saleEntity1 = SaleEntity(employeeId = 1, valueInCents = 3500, tagId = "asdasd")
        val saleEntity2 = SaleEntity(employeeId = 2, valueInCents = 2500, tagId = "asdasd")
        val saleEntity3 = SaleEntity(employeeId = 3, valueInCents = 100, tagId = "asdasd")
        val saleEntity4 = SaleEntity(employeeId = 1, valueInCents = 1700, tagId = "asdasd")
        saleDao.insert(saleEntity1)
        saleDao.insert(saleEntity2)
        saleDao.insert(saleEntity3)
        saleDao.insert(saleEntity4)
        Assert.assertEquals(4, saleDao.getAll().size)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun duplicatedExternalIdShouldNotCompleteDbOperation() {
        saleDao.insert(salesEntity)
        saleDao.insert(salesEntity)
    }

    @After
    fun tearDown() {
        db.close()
    }

}
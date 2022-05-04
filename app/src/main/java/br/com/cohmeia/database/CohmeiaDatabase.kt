package br.com.cohmeia.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.cohmeia.BuildConfig
import br.com.cohmeia.common.onlyLetters
import br.com.cohmeia.database.dao.*
import br.com.cohmeia.database.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.Executors

@Database(
    entities = [
        EmployeeEntity::class,
        ProductSaleEntity::class,
        RechargeEntity::class,
        SaleEntity::class,
        WalletEntity::class,
        ProductEntity::class,
        SyncEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class CohmeiaDatabase : RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao
    abstract fun productDao(): ProductDao
    abstract fun productSaleDao(): ProductSaleDao
    abstract fun rechargeDao(): RechargeDao
    abstract fun saleDao(): SaleDao
    abstract fun walletDao(): WalletDao
    abstract fun syncDao(): SyncDao

    companion object {
        @Volatile
        private var INSTANCE: CohmeiaDatabase? = null

        fun getDatabase(context: Context): CohmeiaDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CohmeiaDatabase::class.java,
                    "Cohmeia_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            addDefaultUser(context)
                            //If need to mock data to test uncomment
//                            Executors.newSingleThreadExecutor().execute {
//                                testDb(context)
//                            }
                        }
                    })
                    .build()

                //FORCE DB ONCREATE TO PRE POPULATE DATA
                instance.query("select 1", null)

                INSTANCE = instance
                return instance
            }
        }

        private fun addDefaultUser(context: Context) {
            Timber.d("Adding default user...")
            Executors.newSingleThreadExecutor().execute {
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    val employeeDao = database.employeeDao()
                    employeeDao.insert(
                        EmployeeEntity(
                            name = BuildConfig.DEFAULT_ADMIN_NAME,
                            document = BuildConfig.DEFAULT_ADMIN_DOC.onlyLetters(),
                            profile = 0,
                            syncDate = 0,
                            createdBy = BuildConfig.DEFAULT_ADMIN_DOC.onlyLetters()
                        )
                    )
                    Timber.d("Default user ADDED")
                }
            }
        }

        fun testDb(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = getDatabase(context)
                    val employeeDao = database.employeeDao()
                    employeeDao.insert(EmployeeEntity(name = "Name employee 1", document = "111.111.111-11", profile = 1, syncDate = 0, createdBy = "-1"))
                    employeeDao.insert(EmployeeEntity(name = "Name employee 2", document = "222.222.222-22", profile = 2, syncDate = 0, createdBy = "-1"))
                    Timber.d("employees inserted: ${employeeDao.getAll()}")

                    Timber.d("creating wallet...")
                    val walletDao = database.walletDao()
                    Timber.d("before insert ${walletDao.getAll()}")
                    walletDao.insert(WalletEntity(tagId = "qwerty", employeeId = "2", syncDate = 0))
                    Timber.d("after insert ${walletDao.getAll()}")

                    Timber.d("test recharge...")
                    val rechargeDao = database.rechargeDao()
                    Timber.d("recharge before insert ${rechargeDao.getAll()}")
                    rechargeDao.insert(
                        RechargeEntity(
                            employeeId = "-1",
                            valueInCents = 3500,
                            tagId = "asdasd",
                            paymentMethod = "cash",
                            syncDate = 0
                        )
                    )
                    Timber.d("recharge after insert ${rechargeDao.getAll()}")

                    Timber.d("creating sale...")
                    val saleDao = database.saleDao()
                    Timber.d("before insert ${saleDao.getAll()}")
                    saleDao.insert(SaleEntity(employeeId = "-1", valueInCents = 12345, tagId = "testestes", syncDate = 0))
                    Timber.d("after insert ${saleDao.getAll()}")

                    Timber.d("creating productsale...")
                    val productSaleDao = database.productSaleDao()
                    Timber.d("before insert ${productSaleDao.getAll()}")
                    productSaleDao.insert(ProductSaleEntity("1", 1, "1", syncDate = 0))
                    Timber.d("after insert ${productSaleDao.getAll()}")
                } catch (e: Exception) {
                    Timber.e("well something failed in dev")
                }
            }
        }
    }
}
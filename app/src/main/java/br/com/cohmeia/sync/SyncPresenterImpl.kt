package br.com.cohmeia.sync

import br.com.cohmeia.common.MoneyConverter
import br.com.cohmeia.common.toFormat
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.entities.*
import br.com.cohmeia.sync.usecase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*


class SyncPresenterImpl(
    private val view: SyncContract.View,
    private val db: CohmeiaDatabase,
    private val getLastSyncDate: GetLastSyncDate,
    private val saveLastSyncDateWithStatusUseCase: SaveLastSyncDateWithStatusUseCase,
    private val getDbSyncDataUseCase: GetDbSyncDataUseCase,
    private val syncDataUseCase: SyncDataUseCase,
    private val updateSyncDateOfEntitiesUseCase: UpdateSyncDateOfEntitiesUseCase,
) : SyncContract.Presenter {

    override fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {

            val employees = db.employeeDao().getAll()
            val products = db.productDao().getAll()
            val recharges = db.rechargeDao().getAll()
            val sales = db.saleDao().getAll()
            val lastSyncDate = getLastSyncDate.execute()
            val date = lastSyncDate?.date?.toFormat("dd/MM/yyyy - hh:mm:ss") ?: "Não encontrada"

            withContext(Dispatchers.Main) {
                view.updateProductQnt(products.size)
                view.updateEmployeeQnt(employees.size)
                view.updateRechargesQnt(recharges.size)
                view.updateRechargesValue(MoneyConverter.formatValueInCents(recharges.sumBy { it.valueInCents }))
                view.updateSalesQnt(sales.size)
                view.updateSalesValue(MoneyConverter.formatValueInCents(sales.sumBy { it.valueInCents }))
                view.updateLastSyncDate(date, lastSyncDate?.success)
            }
        }
    }

    override fun syncData() {
        view.disableSyncButton()
        view.showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val data = getDbSyncDataUseCase.execute()
            val (_, response, result) =  syncDataUseCase.execute(data)

            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                updateSyncDateOfEntitiesUseCase.execute(data)
                withContext(Dispatchers.Main) {
                    view.hideLoading()
                    view.enableSyncButton()
                    Timber.d("Sync success ${result.component1()}")
                    updateLastSuccessfulSyncDate(true)
                    view.showSuccess()

                }
            } else {
                withContext(Dispatchers.Main) {
                    Timber.e("Sync fail ${result.component2()}")
                    updateLastSuccessfulSyncDate(false)
                    view.hideLoading()
                    view.enableSyncButton()
                    view.showError()
                }
            }
        }
    }

    private fun updateLastSuccessfulSyncDate(success: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val date = Date()
            saveLastSyncDateWithStatusUseCase.execute(date, success)
            withContext(Dispatchers.Main) {
                view.updateLastSyncDate(date.toFormat("dd/MM/yyyy - hh:mm:ss"), success)
            }
        }
    }


}



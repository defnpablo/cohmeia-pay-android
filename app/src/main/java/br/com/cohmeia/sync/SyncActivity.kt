package br.com.cohmeia.sync

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.cohmeia.R
import br.com.cohmeia.common.hide
import br.com.cohmeia.common.rawColor
import br.com.cohmeia.common.show
import br.com.cohmeia.database.CohmeiaDatabase
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_sync.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.lang.Exception
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*

class SyncActivity : AppCompatActivity(), SyncContract.View {

    private val presenter: SyncContract.Presenter by inject { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        presenter.loadData()
        sync_button.setOnClickListener {
            presenter.syncData()
        }
    }

    //region SyncContract.View

    override fun updateProductQnt(prodQnt: Int) {
        total_product_sync_resume_textView.text = prodQnt.toString()
    }

    override fun updateEmployeeQnt(employeeQnt: Int) {
        total_employes_sync_resume_textView.text = employeeQnt.toString()
    }

    override fun updateRechargesQnt(rechargesQnt: Int) {
        total_recharges_qnt_sync_resume_textView.text = rechargesQnt.toString()
    }

    override fun updateRechargesValue(rechargesValue: String) {
        total_recharges_value_sync_resume_textView.text = rechargesValue
    }

    override fun updateSalesQnt(salesQnt: Int) {
        total_sales_qnt_sync_resume_textView.text = salesQnt.toString()
    }

    override fun updateSalesValue(salesValue: String) {
        total_sales_value_sync_resume_textView.text = salesValue
    }

    override fun disableSyncButton() {
        sync_button.isEnabled = false
    }

    override fun enableSyncButton() {
        sync_button.isEnabled = true
    }

    override fun showLoading() {
        sync_progressBar.show()
    }

    override fun hideLoading() {
        sync_progressBar.hide()
    }

    override fun showSuccess() {
        Toast.makeText(this, "Sincronização concluida com sucesso!", Toast.LENGTH_LONG).show()
    }

    override fun showError() {
        Toast.makeText(this, "Erro durante a sincronização", Toast.LENGTH_LONG).show()
    }

    override fun updateLastSyncDate(formattedDate: String, success: Boolean?) {
        last_sync_date_textView.text = formattedDate
        success?.let {
            if (it) {
                syncDateStatusSuccess()
            } else {
                syncDateStatusError()
            }
        } ?: syncDateStatusNotFound()
    }

    private fun syncDateStatusError() {
        last_sync_date_title_textView.setText(R.string.title_last_sync_error)
        last_sync_date_title_textView.setTextColor(rawColor(R.color.error))
    }

    private fun syncDateStatusNotFound() {
        last_sync_date_title_textView.setText(R.string.title_last_sync_missing)
        last_sync_date_title_textView.setTextColor(rawColor(R.color.colorPrimaryDark))
    }

    private fun syncDateStatusSuccess() {
        last_sync_date_title_textView.setText(R.string.title_last_sync_success)
        last_sync_date_title_textView.setTextColor(rawColor(R.color.success))
    }
    //endregion
}

package br.com.cohmeia.sync

interface SyncContract {

    interface View {
        fun updateProductQnt(prodQnt: Int)
        fun updateEmployeeQnt(employeeQnt: Int)
        fun updateRechargesQnt(rechargesQnt: Int)
        fun updateRechargesValue(rechargesValue: String)
        fun updateSalesQnt(salesQnt: Int)
        fun updateSalesValue(salesValue: String)
        fun disableSyncButton()
        fun enableSyncButton()
        fun showLoading()
        fun hideLoading()
        fun updateLastSyncDate(formattedDate: String, success: Boolean?)
        fun showSuccess()
        fun showError()
    }

    interface Presenter {
        fun loadData()
        fun syncData()
    }

}

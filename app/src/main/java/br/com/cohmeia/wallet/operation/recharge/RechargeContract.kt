package br.com.cohmeia.wallet.operation.recharge

interface RechargeContract {

    interface View {
        fun updateCashView(cash: Int)
        fun startNfcRecharge(cash: Int, paymentType: RechargePaymentType)
        fun showInvalidRechargeMessage()
        fun showPaymentMethodChooser()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun addCash(cash: Int)
        fun clear()
        fun validateRecharge()
        fun doRecharge(paymentType: RechargePaymentType)
    }

}

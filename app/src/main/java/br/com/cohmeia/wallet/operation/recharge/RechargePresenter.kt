package br.com.cohmeia.wallet.operation.recharge

class RechargePresenter
    : RechargeContract.Presenter {

    private var view: RechargeContract.View? = null

    private var cashValue = 0

    override fun attachView(view: RechargeContract.View) {
        this.view = view
        view.updateCashView(cashValue)
    }

    override fun detachView() {
        view = null
    }

    override fun addCash(cash: Int) {
        cashValue += cash
        view?.updateCashView(cashValue)
    }

    override fun clear() {
        cashValue = 0
        view?.updateCashView(cashValue)
    }

    override fun validateRecharge() {
        if (cashValue > 0) {
            view?.showPaymentMethodChooser()
        } else {
            view?.showInvalidRechargeMessage()
        }
    }

    override fun doRecharge(paymentType: RechargePaymentType) {
        if (cashValue > 0) {
            view?.startNfcRecharge(cashValue, paymentType)
        } else {
            view?.showInvalidRechargeMessage()
        }
    }

}
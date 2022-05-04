package br.com.cohmeia.wallet.operation.read

class ReadWalletPresenter : ReadWalletContract.Presenter {

    private var view: ReadWalletContract.View? = null

    override fun attachView(view: ReadWalletContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun readWallet(balance: Int) {
        if (balance < 0) {
            view?.showCreateWalletDialog()
        } else {
            view?.showBalanceDialog(balance)
        }
    }
}
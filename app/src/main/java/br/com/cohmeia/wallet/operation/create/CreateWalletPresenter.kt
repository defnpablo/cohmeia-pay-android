package br.com.cohmeia.wallet.operation.create

class CreateWalletPresenter(private val view: CreateWalletContract.View) : CreateWalletContract.Presenter {

    override fun createWallet() {
        view.startNfcCreation()
    }
}
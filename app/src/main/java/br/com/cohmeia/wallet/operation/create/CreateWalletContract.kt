package br.com.cohmeia.wallet.operation.create

interface CreateWalletContract {

    interface View {
        fun startNfcCreation()
    }

    interface Presenter {
        fun createWallet()
    }
}
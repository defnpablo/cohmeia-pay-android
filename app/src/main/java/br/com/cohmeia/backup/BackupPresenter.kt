package br.com.cohmeia.backup

import br.com.cohmeia.backup.usecase.GetBackupNfcDataUseCase

class BackupPresenter(
    private val view: BackupContract.View,
    private val getBackupNfcDataUseCase: GetBackupNfcDataUseCase
) : BackupContract.Presenter {

    override fun loadBackup() {
        val nfcData = getBackupNfcDataUseCase.execute()
        if (nfcData.tagId.isNotEmpty()) {
            view.showBackupData(nfcData)
        } else {
            view.showNoBackupFound()
        }
    }

    override fun restoreBackup() {

    }

}

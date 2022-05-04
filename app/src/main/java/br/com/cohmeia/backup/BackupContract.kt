package br.com.cohmeia.backup

import br.com.cohmeia.nfc.core.NfcData

interface BackupContract {

    interface View {
        fun showBackupData(data: NfcData)
        fun showNoBackupFound()
        fun showRestoreLoading()
        fun hideRestoreLoading()
        fun showRestoreSuccess()
        fun showRestoreError()
    }

    interface Presenter {
        fun loadBackup()
        fun restoreBackup()
    }

}

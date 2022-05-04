package br.com.cohmeia.backup.usecase

import br.com.cohmeia.backup.BackupNfcDataRepository
import br.com.cohmeia.nfc.core.NfcData
import br.com.cohmeia.nfc.core.NfcSecurity
import timber.log.Timber

class BackupNfcDataUseCaseImpl(val repository: BackupNfcDataRepository) : BackupNfcDataUseCase {

    override fun execute(nfcData: NfcData?) {
        nfcData?.let {
            Timber.d("Backing up nfc data to encrypt: $nfcData")
            val encrypted = NfcSecurity.encrypt(it.toString())
            repository.saveData(encrypted)
        }
    }

}

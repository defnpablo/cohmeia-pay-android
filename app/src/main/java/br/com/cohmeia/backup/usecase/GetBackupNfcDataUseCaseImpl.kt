package br.com.cohmeia.backup.usecase

import br.com.cohmeia.backup.BackupNfcDataRepository
import br.com.cohmeia.nfc.core.NfcData
import br.com.cohmeia.nfc.core.NfcSecurity
import br.com.cohmeia.nfc.core.toNfcData
import timber.log.Timber

class GetBackupNfcDataUseCaseImpl(val repository: BackupNfcDataRepository) : GetBackupNfcDataUseCase {

    override fun execute(): NfcData {
        val encryptedNfcData = repository.retrieveData()
        val nfcData = NfcSecurity.decrypt(encryptedNfcData).toNfcData()
        Timber.d("Retrieve nfc data decrypted: $nfcData")
        return nfcData ?: NfcData("", "")
    }

}

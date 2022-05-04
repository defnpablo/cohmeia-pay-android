package br.com.cohmeia.backup.usecase

import br.com.cohmeia.nfc.core.NfcData

interface GetBackupNfcDataUseCase {

    fun execute() : NfcData

}

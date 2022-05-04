package br.com.cohmeia.backup.usecase

import br.com.cohmeia.nfc.core.NfcData

interface BackupNfcDataUseCase {

    fun execute(nfcData: NfcData?)

}

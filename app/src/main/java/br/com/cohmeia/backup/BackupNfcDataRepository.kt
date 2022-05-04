package br.com.cohmeia.backup


interface BackupNfcDataRepository {

    fun saveData(encryptedNfcData: String)
    fun retrieveData(): String

}

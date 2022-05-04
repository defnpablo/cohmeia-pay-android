package br.com.cohmeia.backup

import android.content.Context
import androidx.preference.PreferenceManager
import timber.log.Timber

private const val KEY_BACKUP = "BACKUP_NFC_DATA"

class BackupNfcDataRepositoryImpl(context: Context) : BackupNfcDataRepository {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun saveData(encryptedNfcData: String) {
        Timber.d("Backing up nfc data : $encryptedNfcData")
        prefs.edit().putString("BACKUP_NFC_DATA", encryptedNfcData).apply()
    }

    override fun retrieveData(): String {
        val dataString = prefs.getString(KEY_BACKUP, "")
        Timber.d("Retrieving nfc data : $dataString")
        return dataString ?: ""
    }

}
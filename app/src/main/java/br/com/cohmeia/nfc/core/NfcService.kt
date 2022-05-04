package br.com.cohmeia.nfc.core

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import br.com.cohmeia.backup.BackupNfcDataRepository
import br.com.cohmeia.backup.usecase.BackupNfcDataUseCase
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

import java.io.IOException

object NfcService : KoinComponent {

    private val backupNfcDataUseCase: BackupNfcDataUseCase by inject()

    fun isEnabled(ctx: Context) = NfcAdapter.getDefaultAdapter(ctx).isEnabled

    fun createNFCMessage(payload: String, intent: Intent?): NfcData? {
        intent?.let {
            val realTag = it.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val realTagId = getTagId(realTag)
            val nfcData = retrieveNFCMessage(it)
            val tagIdInCurrentPayload = nfcData?.tagId

            //First checking if tagId is the same
            if (tagIdInCurrentPayload != null && tagIdInCurrentPayload != realTagId) {
                return null
            }

            val encryptedPayload = NfcSecurity.encrypt("$realTagId|$payload")
            Timber.tag("NFC_DEBUG_SERVICE").i("NFC tag write msg: $encryptedPayload")
            val nfcMessage = NdefMessage(
                arrayOf(
                    NdefRecord.createExternal(
                        "br.com.cohmeia",
                        "wallet",
                        encryptedPayload.toByteArray()
                    )
                )
            )
            backupLastDataBeforeWrite(nfcData)
            return writeMessageToTag(nfcMessage, realTag)
        }
        return null
    }

    private fun backupLastDataBeforeWrite(nfcData: NfcData?) {
        nfcData?.let {
            backupNfcDataUseCase.execute(it)
        }
    }

    fun retrieveNFCMessage(intent: Intent?): NfcData? {
        intent?.let {

            Timber.tag("NFC_DEBUG_SERVICE").i("READ NFC tag intent action ${intent.action}")
            var i = 0
            getNDefMessages(intent).map {
                Timber.tag("NFC_DEBUG_SERVICE").i("READ NFC tag msg[$i] = $it")
                i += 1
            }
            val msg = getNDefMessages(intent)
                .first()
                .records
                .first()
                ?.payload

            val expectedTagId = getTagId(it.getParcelableExtra(NfcAdapter.EXTRA_TAG))
            Timber.tag("NFC_DEBUG_SERVICE").d("READ nfc tag id: $expectedTagId")
            if (msg != null) {
                val nfcData = NfcSecurity.decrypt(String(msg)).toNfcData()
                return if (nfcData?.tagId == expectedTagId) {
                    nfcData
                } else {
                    null
                }
            }
        }
        return null
    }

    private fun getTagId(tag: Tag?): String {
        return NfcHelper.bytesToHexString(tag?.id)
    }

    private fun getNDefMessages(intent: Intent): Array<NdefMessage> {
        val rawMessage = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        rawMessage?.let {
            return rawMessage.map {
                it as NdefMessage
            }.toTypedArray()
        }
        // Unknown tag type
        val empty = byteArrayOf()
        val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty)
        val msg = NdefMessage(arrayOf(record))
        return arrayOf(msg)
    }

    fun disableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity) {
        nfcAdapter.disableForegroundDispatch(activity)
    }

    fun <T> enableNFCInForeground(nfcAdapter: NfcAdapter, activity: Activity, classType: Class<T>) {
        val pendingIntent = getActivity(
            activity, 0,
            Intent(activity, classType).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        val nfcIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val filters = arrayOf(nfcIntentFilter)
        val techLists = arrayOf(arrayOf(Ndef::class.java.name), arrayOf(NdefFormatable::class.java.name))
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techLists)
    }

    private fun writeMessageToTag(nfcMessage: NdefMessage, tag: Tag?): NfcData? {
        try {
            tag?.let {
                val nDefTag = Ndef.get(tag)
                nDefTag?.let {
                    Timber.tag("NFC_DEBUG_SERVICE").i("Connecting to nDefTag")
                    it.connect()
                    if (it.maxSize < nfcMessage.toByteArray().size) {
                        //Message to large to write to NFC tag
                        return null
                    }
                    return if (it.isWritable) {
                        Timber.tag("NFC_DEBUG_SERVICE").i("Writing to nDefTag %s", nfcMessage)
                        it.writeNdefMessage(nfcMessage)
                        Timber.tag("NFC_DEBUG_SERVICE").i("Writing to nDefTag done")
                        it.close()
                        Timber.tag("NFC_DEBUG_SERVICE").i("Writing to nDefTag closed")
                        //Message is written to tag
                        Timber.tag("NFC_DEBUG_SERVICE").i("Data write done")
                        NfcSecurity.decrypt(String(nfcMessage.firstPayload())).toNfcData()
                    } else {
                        //NFC tag is read-only
                        Timber.tag("NFC_DEBUG_SERVICE").i("NFC tag is read-only")
                        null
                    }
                }
                val nDefFormatableTag = NdefFormatable.get(tag)
                nDefFormatableTag?.let {
                    return try {
                        Timber.tag("NFC_DEBUG_SERVICE").i("Connecting to nDefFormatableTag")
                        it.connect()
                        Timber.tag("NFC_DEBUG_SERVICE").i("Writing to nDefFormatableTag %s", nfcMessage)
                        it.format(nfcMessage)
                        Timber.tag("NFC_DEBUG_SERVICE").i("Writing to nDefFormatableTag done")
                        it.close()
                        Timber.tag("NFC_DEBUG_SERVICE").i("Writing to nDefFormatableTag closed")
                        //The data is written to the tag
                        Timber.tag("NFC_DEBUG_SERVICE").i("Data write done")
                        NfcSecurity.decrypt(String(nfcMessage.firstPayload())).toNfcData()
                    } catch (e: IOException) {
                        //Failed to format tag
                        Timber.tag("NFC_DEBUG_SERVICE").i("NFC write error inner" + e.message)
                        null
                    }
                }
                //NDEF is not supported
                return null
            }
        } catch (e: Exception) {
            //Write operation has failed
            Timber.tag("NFC_DEBUG_SERVICE").i("NFC write error generic" + e.message)
        }
        return null
    }
}

fun NdefMessage.firstPayload(): ByteArray {
    return records?.first()?.payload ?: ByteArray(0)
}

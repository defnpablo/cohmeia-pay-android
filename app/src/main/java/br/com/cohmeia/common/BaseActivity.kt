package br.com.cohmeia.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.NfcManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.cohmeia.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder


open class BaseActivity : AppCompatActivity() {

    private lateinit var alertBuilder: MaterialAlertDialogBuilder
    private var alert: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alertBuilder = MaterialAlertDialogBuilder(this, R.style.AlertDialog_Cohmeia)
            .setTitle("Atenção")
            .setMessage("Nfc está desabilitado!")
            .setPositiveButton("Ir para configurações") { _, _ ->
                openNfcSettings()
            }
            .setCancelable(false)
    }

    override fun onResume() {
        super.onResume()

        if (nfcEnabled().not()) {
//            showNfcMissing()
        }
    }

    private fun nfcEnabled(): Boolean {
        val manager = getSystemService(Context.NFC_SERVICE) as? NfcManager
        return manager?.defaultAdapter?.isEnabled == true
    }

    private fun showNfcMissing() {
        alertBuilder.show()
    }

    private fun openNfcSettings() {
        val nfcIntent = Intent(Settings.ACTION_NFC_SETTINGS)
        startActivityForResult(nfcIntent, 786)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 786 && resultCode == Activity.RESULT_OK) {
            if (nfcEnabled().not()) {
                showNfcMissing()
            } else {
                alert?.dismiss()
            }
        }
    }
}

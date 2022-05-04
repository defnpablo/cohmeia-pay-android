package br.com.cohmeia.backup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import br.com.cohmeia.R
import br.com.cohmeia.common.MoneyConverter
import br.com.cohmeia.navigation.Navigation
import br.com.cohmeia.nfc.core.NfcData
import kotlinx.android.synthetic.main.fragment_backup.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BackupFragment : Fragment(), BackupContract.View {

    private val presenter: BackupContract.Presenter by inject { parametersOf(this) }

    private var navigator: Navigation? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Navigation) {
            navigator = context
        } else {
            throw RuntimeException(context.toString() + " must implement Navigation")
        }
    }

    override fun onDetach() {
        super.onDetach()
        navigator = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_backup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadBackup()
    }

    //region BackupContract.View
    override fun showBackupData(data: NfcData) {
        label_last_tag_data_value_textView.text =
            "ID: ${data.tagId} - ${MoneyConverter.formatValueInCents(data.content.toInt())}"
        restore_backup_button.setOnClickListener {
            navigator?.openRestoreOperation(data)
        }
    }

    override fun showNoBackupFound() {
        label_last_tag_data_value_textView.text = "Dados não encontrados"
    }

    override fun showRestoreLoading() {
        Toast.makeText(requireContext(), "show restore loading", Toast.LENGTH_SHORT).show()
    }

    override fun hideRestoreLoading() {
        Toast.makeText(requireContext(), "hide restore loading", Toast.LENGTH_SHORT).show()
    }

    override fun showRestoreSuccess() {
        Toast.makeText(requireContext(), "restore loading SUCCESS", Toast.LENGTH_SHORT).show()
    }

    override fun showRestoreError() {
        Toast.makeText(requireContext(), "restore loading FAIL", Toast.LENGTH_SHORT).show()
    }
    //endregion

    companion object {

        const val TAG = "backup_frag_tag"

        @JvmStatic
        fun newInstance() = BackupFragment()
    }

}
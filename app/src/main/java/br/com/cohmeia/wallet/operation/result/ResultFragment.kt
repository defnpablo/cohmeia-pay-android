package br.com.cohmeia.wallet.operation.result

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.cohmeia.R
import br.com.cohmeia.barman.TemporaryCart
import br.com.cohmeia.common.MoneyConverter
import br.com.cohmeia.common.hide
import br.com.cohmeia.navigation.Navigation
import br.com.cohmeia.wallet.repository.*
import kotlinx.android.synthetic.main.fragment_success.*
import kotlinx.android.synthetic.main.layout_result_error.*
import kotlinx.android.synthetic.main.layout_result_success.*

class ResultFragment : Fragment() {

    private var navigator: Navigation? = null
    private lateinit var result: Result

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getSerializable(RESULT_PARAM) as Result
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (result) {
            is Success -> processSuccess(result as Success)
            is Error -> processError(result as Error)
        }

        result_root_view.setOnClickListener {
            navigator?.goHome()
        }
    }

    private fun processSuccess(success: Success) {
        TemporaryCart.clear()
        result_error_frameLayout.hide()
        result_success_textView.text = success.payload
        result_root_view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.success
            )
        )
    }

    private fun processError(error: Error) {
        result_success_frameLayout.hide()
        val errorType = error.errorType

        when (errorType) {
            WalletNotCreated, WalletAlreadyCreated, WalletCorrupted -> {
                result_success_balance_label_textView.hide()
                result_success_debit_label_textView.hide()
                result_success_remaining_label_textView.hide()
                result_error_remaining_value_textView.hide()
            }
            is InsufficientBalance -> {
                result_error_balance_textView.text = MoneyConverter.formatValueInCents(errorType.balanceInCents)
                result_error_debit_value_textView.text =
                    MoneyConverter.formatValueInCents(errorType.checkoutValueInCents)
                result_error_remaining_value_textView.text =
                    MoneyConverter.formatValueInCents(errorType.checkoutValueInCents - errorType.balanceInCents)
            }
        }

        result_error_title_textView.text = getString(errorType.messageError)
        result_root_view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.error
            )
        )
    }

    companion object {

        const val TAG = "ResultFragment"
        const val RESULT_PARAM = "resultParam"

        @JvmStatic
        fun newInstance(result: Result) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(RESULT_PARAM, result)
                }
            }
    }

}

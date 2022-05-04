package br.com.cohmeia.login

import android.content.Intent
import android.os.Bundle
import br.com.cohmeia.BuildConfig
import br.com.cohmeia.R
import br.com.cohmeia.admin.AdminActivity
import br.com.cohmeia.common.*
import br.com.cohmeia.common.EmployeeProfile.ADMIN
import br.com.cohmeia.common.EmployeeProfile.BARMAN
import br.com.cohmeia.common.EmployeeProfile.CASHIER
import br.com.cohmeia.navigation.NAVIGATE_BARMAN
import br.com.cohmeia.navigation.NAVIGATE_CASHIER
import br.com.cohmeia.nfc.NfcActivity
import br.com.cohmeia.sync.SyncActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class LoginActivity : BaseActivity() {

    private val loginRepository: LoginRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        cpf_editText.addTextChangedListener(MaskTextWatcher("###.###.###-##", cpf_editText))
        login_button.setOnClickListener {
            login()
        }

        when (loginRepository.getCurrentUser()) {
            is Barman -> logBarman()
            is Cashier -> logCashier()
            is Admin -> logAdmin()
        }

    }

    private fun login() {

        var cpfValid = CpfValidator.isCpfValid(cpf_editText.text.toString())
        if (cpfValid) {
            val cpf = cpf_editText.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val employee = loginRepository.userExists(cpf.onlyLetters())
                withContext(Dispatchers.Main) {
                    when (employee?.profile) {
                        CASHIER -> {
                            loginRepository.saveUser(Cashier(cpf, employee.id, employee.name))
                            logCashier()
                            finish()
                        }
                        BARMAN -> {
                            loginRepository.saveUser(Barman(cpf, employee.id, employee.name))
                            logBarman()
                            finish()
                        }
                        ADMIN -> {
                            loginRepository.saveUser(Admin(cpf, employee.id, employee.name))
                            logAdmin()
                        }
                        else -> cpf_textInputLayout.error = "Usuário não encontrado"
                    }
                }
            }
        } else {
            cpf_textInputLayout.error = "CPF inválido"
        }
    }

    private fun logAdmin() {
        startActivity(Intent(this, AdminActivity::class.java))
    }

    private fun logBarman() {
        startActivity(
            Intent(this, NfcActivity::class.java)
                .putExtra(EXTRA_NAVIGATE, NAVIGATE_BARMAN)
        )
        finish()
    }

    private fun logCashier() {
        startActivity(
            Intent(this, NfcActivity::class.java)
                .putExtra(EXTRA_NAVIGATE, NAVIGATE_CASHIER)
        )
        finish()
    }

}

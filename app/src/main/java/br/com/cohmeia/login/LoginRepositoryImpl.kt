package br.com.cohmeia.login

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import androidx.work.WorkManager
import br.com.cohmeia.common.onlyLetters
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.entities.EmployeeEntity
import org.koin.core.KoinComponent
import org.koin.core.inject

class LoginRepositoryImpl(private val context: Context) : LoginRepository, KoinComponent {

    private val db: CohmeiaDatabase by inject()
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun saveUser(user: User) {
        prefs.edit {
            putString("user_login", user.userCpf.onlyLetters())
            putString("user_name", user.employeeName)
            putInt("user_id", user.userId)
            putString(
                "user_type", when (user) {
                    is Barman -> "user_type_barman"
                    is Cashier -> "user_type_cashier"
                    is Admin -> "user_type_admin"
                }
            )
        }
    }

    override fun getCurrentUser(): User? {
        val userLogin = prefs.getString("user_login", "")!!
        val userName = prefs.getString("user_name", "")!!
        val userId = prefs.getInt("user_id", -1)
        return when (prefs.getString("user_type", "")) {
            "user_type_barman" -> Barman(userLogin.onlyLetters(), userId, userName)
            "user_type_cashier" -> Cashier(userLogin.onlyLetters(), userId, userName)
            "user_type_admin" -> Admin(userLogin.onlyLetters(), userId, userName)
            else -> null
        }
    }

    override fun logout() {
        prefs.edit { clear() }
    }

    override suspend fun userExists(cpf: String): EmployeeEntity? {
        return db.employeeDao().getByCpf(cpf)
    }
}
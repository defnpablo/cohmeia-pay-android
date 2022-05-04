package br.com.cohmeia.admin.employee

import android.database.sqlite.SQLiteConstraintException
import br.com.cohmeia.common.CpfValidator
import br.com.cohmeia.common.onlyLetters
import br.com.cohmeia.login.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmployeePresenter(
    private val employeeRepository: EmployeeRepository,
    private val loginRepository: LoginRepository,
) : EmployeeContract.Presenter {

    private var view: EmployeeContract.View? = null

    override fun attachView(view: EmployeeContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun selectedAddNewEmployee() {
        view?.showRegister()
    }

    override suspend fun registerEmployee(profile: Int, userName: String, cpf: String) {
        val validProfile = validProfile(profile)
        val validUsername = validUsername(userName)
        val validCpf = validCpf(cpf)

        val validData = validProfile && validUsername && validCpf
        if (validData) {
            try {
                val employee = Employee(userName, cpf.onlyLetters(), profile)
                val createdByUserDocument = loginRepository.getCurrentUser()?.userCpf ?: ""
                employeeRepository.saveEmployee(employee, createdByUserDocument)
                withContext(Dispatchers.Main) {
                    view?.hideRegister()
                    view?.clearRegister()
                    view?.showNewEmployee(employee)
                }
            } catch (e: SQLiteConstraintException) {
                view?.showCpfAlreadyExists()
            }
        }
    }

    private fun validProfile(profile: Int): Boolean {
        if (profile < 0) {
            view?.showProfileToastError()
            return false
        }
        return true
    }

    private fun validUsername(userName: String): Boolean {
        if (userName.length < 3) {
            view?.showNameError()
            return false
        }
        return true
    }

    private fun validCpf(cpf: String): Boolean {
        if (CpfValidator.isCpfValid(cpf).not()) {
            view?.showCpfError()
            return false
        }
        return true
    }

    override fun cancelEmployeeRegister() {
        view?.hideRegister()
    }

    override suspend fun loadUsers() {
        view?.showLoading()
        withContext(Dispatchers.IO) {
            val repoEmployees = employeeRepository.getEmployees()
            withContext(Dispatchers.Main) {
                view?.hideLoading()
                if (repoEmployees.isNotEmpty()) {
                    view?.showEmployees(repoEmployees)
                } else {
                    view?.showEmptyMessage()
                }
            }
        }
    }

    override suspend fun removeEmployee(employee: Employee) {
        employeeRepository.removeEmployee(employee)
        withContext(Dispatchers.Main) {
            view?.removeEmployeeFromList(employee)
        }
    }
}
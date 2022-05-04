package br.com.cohmeia.admin.employee

interface EmployeeContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showEmptyMessage()
        fun showEmployees(employees: List<Employee>)
        fun showRegister()
        fun hideRegister()
        fun showProfileToastError()
        fun showNameError()
        fun showCpfError()
        fun removeEmployeeFromList(employee: Employee)
        fun showNewEmployee(employee: Employee)
        fun clearRegister()
        fun showCpfAlreadyExists()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun selectedAddNewEmployee()
        fun cancelEmployeeRegister()
        suspend fun registerEmployee(profile: Int, userName: String, cpf: String)
        suspend fun loadUsers()
        suspend fun removeEmployee(employee: Employee)
    }

}
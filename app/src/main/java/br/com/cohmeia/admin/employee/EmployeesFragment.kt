package br.com.cohmeia.admin.employee

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.cohmeia.R
import br.com.cohmeia.common.MaskTextWatcher
import br.com.cohmeia.common.hide
import br.com.cohmeia.common.show
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_employees.*
import kotlinx.android.synthetic.main.layout_employee_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import androidx.recyclerview.widget.DividerItemDecoration
import br.com.cohmeia.common.EmployeeProfile.ADMIN
import br.com.cohmeia.common.EmployeeProfile.BARMAN
import br.com.cohmeia.common.EmployeeProfile.CASHIER
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.withContext


class EmployeesFragment : Fragment(), EmployeeContract.View {

    private val presenter: EmployeeContract.Presenter by inject()
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private val employeeAdapter = EmployeeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_employees, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.attachView(this)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detachView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            presenter.loadUsers()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(employee_bottomSheet)
        bottomSheetBehavior?.apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        fab.setOnClickListener {
            presenter.selectedAddNewEmployee()
        }

        cancel_employee_register_button.setOnClickListener {
            presenter.cancelEmployeeRegister()
        }

        employee_register_button.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                presenter.registerEmployee(
                    selectedProfile(),
                    employee_name_editText.text.toString(),
                    employee_cpf_editText.text.toString()
                )
            }
        }

        employee_cpf_editText.addTextChangedListener(MaskTextWatcher("###.###.###-##", employee_cpf_editText))
        employee_cpf_editText.addTextChangedListener { employee_cpf_textInputLayout.error = null }
        employee_name_editText.addTextChangedListener { employee_name_textInputLayout.error = null }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(employee_recyclerView) {
            adapter = employeeAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        employeeAdapter.onRemoveSelected {
            askToRemoveEmployee(it) {
                CoroutineScope(Dispatchers.IO).launch {
                    presenter.removeEmployee(it)
                }
            }
        }
    }

    private fun selectedProfile(): Int {
        return when (profile_toggleGroup.checkedButtonId) {
            admin_profile_button.id -> ADMIN
            cashier_profile_button.id -> CASHIER
            barman_profile_button.id -> BARMAN
            else -> -1
        }
    }

    //region EmployeeContract.View
    override fun showLoading() {
        employee_progressBar.show()
    }

    override fun hideLoading() {
        employee_progressBar.hide()
    }

    override fun showEmptyMessage() {
        empty_textView.show()
    }

    override fun showEmployees(employees: List<Employee>) {
        empty_textView.hide()
        employeeAdapter.addAll(employees)
    }

    override fun showRegister() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        fab.hide()
        footer_view.show()
    }

    override fun hideRegister() {
        hideKeyboard()
        Handler().postDelayed({
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            fab.show()
            footer_view.hide()
        }, 100)
    }

    override fun clearRegister() {
        employee_cpf_editText.setText("")
        employee_name_editText.setText("")
    }

    override fun showCpfAlreadyExists() {
        employee_cpf_textInputLayout.error = "Cpf já cadastrado"
    }

    override fun showProfileToastError() {
        Toast.makeText(context, "Selecione um perfil", Toast.LENGTH_SHORT).show()
    }

    override fun showNameError() {
        employee_name_textInputLayout.error = "Nome inválido"
    }

    override fun showCpfError() {
        employee_cpf_textInputLayout.error = "Cpf inválido"
    }

    override fun removeEmployeeFromList(employee: Employee) {
        employeeAdapter.remove(employee)
        if (employeeAdapter.itemCount <= 0) {
            showEmptyMessage()
        }
    }

    private fun askToRemoveEmployee(employee: Employee, removeAction: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog_Cohmeia)
            .setTitle("Atenção")
            .setMessage("Deseja remover o funcionário: ${employee.name} - ${employee.cpf}")
            .setNeutralButton("Cancelar"){ dialog, _ -> dialog.dismiss()}
            .setPositiveButton("Remover") {_,_ -> removeAction()}
            .show()
    }

    override fun showNewEmployee(employee: Employee) {
        employeeAdapter.add(employee)
        empty_textView.hide()
    }
    //endregion

    fun hideKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    companion object {
        fun newInstance() = EmployeesFragment()
    }
}

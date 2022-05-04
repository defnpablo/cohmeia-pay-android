package br.com.cohmeia.admin.employee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cohmeia.R
import br.com.cohmeia.login.profileForType
import kotlinx.android.synthetic.main.cell_employee.view.*

class EmployeeAdapter : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    private val employeeList = mutableListOf<Employee>()
    private var removeAction: (Employee) -> Unit = {}

    fun onRemoveSelected(action: (Employee) -> Unit) {
        removeAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_employee, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = employeeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val employee = employeeList[position]
        holder.bind(employee)
        holder.view.remove_employee_button.setOnClickListener {
            removeAction(employee)
        }
    }

    fun addAll(newEmployeeList: List<Employee>) {
        employeeList.addAll(newEmployeeList)
        notifyDataSetChanged()
    }

    fun remove(employee: Employee) {
        val position = employeeList.indexOf(employee)
        employeeList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add(employee: Employee) {
        employeeList.add(employee)
        notifyItemInserted(employeeList.size - 1)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(employee: Employee) {
            view.cell_employee_name_textView.text = employee.name
            view.cell_employee_profile_textView.text = profileForType(employee.profile)
            view.cell_employee_cpf_textView.text = employee.cpf
        }
    }
}
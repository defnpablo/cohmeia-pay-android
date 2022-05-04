package br.com.cohmeia.admin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.cohmeia.admin.employee.EmployeesFragment
import br.com.cohmeia.admin.inventory.InventoryFragment

class AdminViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val frags = listOf(
        EmployeesFragment.newInstance(),
        InventoryFragment.newInstance()
    )

    override fun createFragment(position: Int): Fragment {
        return frags[position]
    }

    override fun getItemCount(): Int {
        return 2
    }

}

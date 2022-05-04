package br.com.cohmeia.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import br.com.cohmeia.R
import br.com.cohmeia.common.tintIn
import br.com.cohmeia.login.LoginActivity
import br.com.cohmeia.login.LoginRepository
import br.com.cohmeia.sync.SyncActivity
import br.com.cohmeia.sync.SyncRepository
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_admin.employee_cpf_textView
import kotlinx.android.synthetic.main.activity_admin.employee_name_textView
import kotlinx.android.synthetic.main.activity_admin.toolbar
import kotlinx.android.synthetic.main.activity_admin.sync_status_imageView
import kotlinx.android.synthetic.main.fragment_barman.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class AdminActivity : AppCompatActivity() {

    private val loginRepository: LoginRepository by inject()
    private val syncRepository: SyncRepository by inject()

    private val viewPagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            tabs.getTabAt(position)?.select()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        setupToolbar()
        createTabs()
        view_pager.registerOnPageChangeCallback(viewPagerCallback)
        view_pager.adapter = AdminViewPagerAdapter(this)

        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
        setupSyncIcon()
    }

    private fun setupToolbar() {
        title = ""
        loginRepository.getCurrentUser()?.let {
            employee_name_textView.text = it.employeeName
            employee_cpf_textView.text = it.userCpf
        }
        sync_status_imageView.setOnClickListener { syncData() }
    }

    private fun setupSyncIcon() {
        CoroutineScope(Dispatchers.IO).launch {
            val lastSyncDate = syncRepository.getLastSyncDate()
            withContext(Dispatchers.Main) {
                with(sync_status_imageView) {
                    lastSyncDate?.let {
                        if (it.success) tintIn(R.color.success) else tintIn(R.color.error)
                    } ?: tintIn(R.color.colorControlActivated)
                }
            }
        }
    }

    private fun createTabs() {
        tabs.addTab(tabs.newTab().setText("Funcionarios"), 0, true)
        tabs.addTab(tabs.newTab().setText("Produtos"), 1, false)
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    view_pager.setCurrentItem(it.position, true)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sync -> syncData()
            R.id.menu_logout -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun syncData() {
        startActivity(Intent(this, SyncActivity::class.java))
    }

    private fun logout() {
        loginRepository.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
package com.trecobat.pointagetrecopro.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.trecobat.pointagetrecopro.R
import com.trecobat.pointagetrecopro.data.local.AppDatabase
import com.trecobat.pointagetrecopro.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        val db = AppDatabase.getDatabase(this) // Obtenir une instance de la base de données
        val myDao = db.myDao() // Obtenir une instance du DAO

        val hasToken = myDao.getToken().value?.token != null
        if (!hasToken) {
            navController.navigate(
                R.id.authFragment
            )
        }

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.inflateMenu(R.menu.menu)
        binding.toolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    System.clearProperty("token")
                    GlobalScope.launch(Dispatchers.Main) {
                        myDao.deleteAllToken()
                    }
                    navController.navigate(
                        R.id.authFragment
                    )
                    true
                }
                R.id.add_marche -> {
                    navController.navigate(
                        R.id.addMarcheFragment
                    )
                    true
                }
                R.id.add_ts -> {
                    Toast.makeText(this, "add_ts", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.repas -> {
                    Toast.makeText(this, "repas", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.voir_pointages -> {
                    Toast.makeText(this, "voir_pointages", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}

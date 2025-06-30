package com.example.talentara.view.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.talentara.R
import com.example.talentara.databinding.ActivityMainBinding
import com.example.talentara.view.ui.portfolio.add.NewPortfolioActivity
import com.example.talentara.view.ui.project.add.NewProjectActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        binding.btnNewProject.visibility = View.GONE

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_home) {
                binding.btnNewProject.visibility = View.VISIBLE
                binding.btnNewProject.setOnClickListener {
                    val intent = Intent(this, NewProjectActivity::class.java)
                    startActivity(intent)
                }
            } else if (destination.id == R.id.navigation_profile) {
                binding.btnNewProject.visibility = View.VISIBLE
                binding.btnNewProject.setOnClickListener {
                    val intent =
                        Intent(this, NewPortfolioActivity::class.java).apply {
                            putExtra(NewPortfolioActivity.STATE, "NewPortfolio")
                        }
                    startActivity(intent)
                }
            } else {
                binding.btnNewProject.visibility = View.GONE
            }
        }
    }
}
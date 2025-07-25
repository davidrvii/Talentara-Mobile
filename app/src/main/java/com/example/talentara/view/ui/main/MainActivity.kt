package com.example.talentara.view.ui.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.talentara.R
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.ActivityMainBinding
import com.example.talentara.view.ui.portfolio.add.NewPortfolioActivity
import com.example.talentara.view.ui.profile.ProfileViewModel
import com.example.talentara.view.ui.project.add.NewProjectActivity
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private val profileViewModel: ProfileViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private var userIsOnProject: Int = 0
    private var userIsTalentAccess: Int = 0

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Notifications permission granted")
            } else {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        profileViewModel.getUserDetail()
        getUserDetailObserver(navController)
    }

    private fun getUserDetailObserver(navController: androidx.navigation.NavController) {
        profileViewModel.getUserDetail.observe(this) { result ->
            when (result) {
                is Results.Loading -> {}
                is Results.Success -> {
                    val user = result.data.userDetail?.firstOrNull()
                    userIsOnProject = user?.isOnProject ?: 0
                    userIsTalentAccess = user?.talentAccess ?: 0

                    navController.addOnDestinationChangedListener { _, destination, _ ->
                        when (destination.id) {
                            R.id.navigation_home -> {
                                showBtnForProject(userIsOnProject)
                            }
                            R.id.navigation_profile -> {
                                if (userIsTalentAccess == 1) {
                                    showBtnForPortfolio()
                                } else {
                                    hideBtn()
                                }
                            }
                            else -> hideBtn()
                        }
                    }
                }

                is Results.Error -> {}
            }
        }
    }

    private fun showBtnForPortfolio() {
        binding.btnNewProject.visibility = View.VISIBLE
        binding.btnNewProject.apply {
            isEnabled = true
            alpha = 1f
        }
        binding.btnNewProject.setOnClickListener {
            val intent = Intent(this, NewPortfolioActivity::class.java)
            intent.putExtra(NewPortfolioActivity.STATE, "NewPortfolio")
            startActivity(intent)
        }
    }

    private fun showBtnForProject(isOnProject: Int) {
        binding.btnNewProject.visibility = View.VISIBLE
        if (isOnProject == 0) {
            binding.btnNewProject.setOnClickListener {
                val intent = Intent(this, NewProjectActivity::class.java)
                startActivity(intent)
            }
        } else {
            binding.btnNewProject.apply {
                isEnabled = false
                alpha = 0.5f
            }
        }
    }

    private fun hideBtn() {
        binding.btnNewProject.visibility = View.GONE
    }

}

package com.example.talentara.view.ui.main

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.setupWithNavController
import com.example.talentara.R
import com.example.talentara.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment
import com.example.talentara.view.ui.project.add.NewProjectActivity
import kotlin.jvm.java


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

        binding.btnNewProject.setOnClickListener {
            val intent = Intent(this, NewProjectActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.example.talentara.view.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.talentara.R
import com.example.talentara.databinding.ActivityAuthenticationBinding
import com.example.talentara.view.ui.authentication.signin.SignInFragment
import com.example.talentara.view.ui.authentication.signup.SignUpFragment
import com.example.talentara.view.ui.main.MainActivity
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.tabs.TabLayoutMediator

class AuthenticationActivity : AppCompatActivity() {

    private val authViewModel: AuthenticationViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        authViewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                setupUI()
            }
        }

        setupViewPagerWithTabs()
    }

    private fun setupUI() {
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.authentication)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun switchPage(toPosition: Int) {
        binding.viewPager.currentItem = toPosition
    }

    private fun setupViewPagerWithTabs() {
        val adapter = object : FragmentStateAdapter(this) {
            private val pages = listOf(
                SignInFragment(),
                SignUpFragment()
            )

            override fun getItemCount() = pages.size
            override fun createFragment(position: Int) = pages[position]
        }
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = if (pos == 0)
                getString(R.string.sign_in)
            else
                getString(R.string.sign_up)
        }.attach()

        binding.viewPager.currentItem = 1
    }
}
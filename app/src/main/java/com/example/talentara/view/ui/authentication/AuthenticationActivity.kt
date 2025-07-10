package com.example.talentara.view.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.talentara.R
import com.example.talentara.data.local.preference.UserPreference
import com.example.talentara.data.local.preference.dataStore
import com.example.talentara.data.model.result.Results
import com.example.talentara.databinding.ActivityAuthenticationBinding
import com.example.talentara.view.ui.authentication.signin.SignInFragment
import com.example.talentara.view.ui.authentication.signup.SignUpFragment
import com.example.talentara.view.ui.main.MainActivity
import com.example.talentara.view.utils.FactoryViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class AuthenticationActivity : AppCompatActivity() {

    private val authViewModel: AuthenticationViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val preference: UserPreference by lazy {
        UserPreference.getInstance(this.dataStore)
    }
    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        authViewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    if (!token.isNullOrBlank()) {
                        authViewModel.saveFcmToken(token)
                        lifecycleScope.launch {
                            preference.saveFcmToken(token)
                        }
                    }
                }

                authViewModel.saveFcmTokenResponse.observe(this) { result ->
                    when (result) {
                        is Results.Loading -> {}
                        is Results.Success -> {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        is Results.Error -> Log.e("MainActivity", "Error saving FCM token: ${result.error}")
                    }
                }
            } else {
                setupUI()
            }
        }
    }

    private fun setupUI() {
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.authentication)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewPagerWithTabs()
    }

    fun switchPage(toPosition: Int) {
        binding.viewPager.currentItem = toPosition
    }

    private fun setupViewPagerWithTabs() {
        val adapter = object : FragmentStateAdapter(this) {
            private val pages = listOf(
                SignUpFragment(),
                SignInFragment()
            )

            override fun getItemCount() = pages.size
            override fun createFragment(position: Int) = pages[position]
        }
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = if (pos == 0)
                getString(R.string.sign_up)
            else
                getString(R.string.sign_in)
        }.attach()

        binding.viewPager.currentItem = 0
    }
}
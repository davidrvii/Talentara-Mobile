package com.example.talentara.view.ui.waiting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.talentara.R
import com.example.talentara.databinding.ActivityWaitingPageBinding
import com.example.talentara.view.ui.main.MainActivity

class WaitingPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaitingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaitingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupActionButton()

        binding.tvWaiting.text = intent.getStringExtra(MESSAGE)
    }

    private fun setupActionButton() {
        with(binding) {
            btnHome.setOnClickListener {
                val intent = Intent(this@WaitingPageActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    companion object {
        const val MESSAGE = "MESSAGE"
    }
}
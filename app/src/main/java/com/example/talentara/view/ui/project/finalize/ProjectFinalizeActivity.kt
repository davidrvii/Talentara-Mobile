package com.example.talentara.view.ui.project.finalize

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.talentara.R
import com.example.talentara.databinding.ActivityProjectFinalizeBinding
import com.example.talentara.view.ui.project.detail.ProjectDetailViewModel
import com.example.talentara.view.utils.FactoryViewModel

class ProjectFinalizeActivity : AppCompatActivity() {

    private val projectDetailViewModel: ProjectDetailViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private val projectFinalizeViewModel: ProjectFinalizeViewModel by viewModels {
        FactoryViewModel.getInstance(this)
    }
    private lateinit var binding: ActivityProjectFinalizeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectFinalizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
package com.example.talentara.view.ui.project.finalize

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.talentara.R
import com.example.talentara.databinding.ActivityProjectFinalizeBinding
import com.google.android.material.textfield.TextInputEditText

class ProjectFinalizeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectFinalizeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProjectFinalizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addFeatureField()

        binding.btnAddFeature.setOnClickListener {
            addFeatureField()
        }
    }

    private fun addFeatureField() {
        // Inflate item_feature.xml
        val item = layoutInflater.inflate(
            R.layout.feature_item,
            binding.featuresContainer,
            false
        )

        // Tombol X di dalam item
        val btnRemove = item.findViewById<ImageButton>(R.id.btnRemove)
        btnRemove.setOnClickListener {
            binding.featuresContainer.removeView(item)
        }

        // Tambahkan ke container
        binding.featuresContainer.addView(item)
    }

    private fun collectFeatures(): List<String> {
        val features = mutableListOf<String>()
        for (i in 0 until binding.featuresContainer.childCount) {
            val item = binding.featuresContainer.getChildAt(i)
            val et = item.findViewById<TextInputEditText>(R.id.etProjectFeature)
            et.text?.toString()?.takeIf { it.isNotBlank() }?.let(features::add)
        }
        return features
    }
}
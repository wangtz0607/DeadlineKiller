package com.deadlinekiller.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import com.deadlinekiller.databinding.ActivityMainBinding
import com.deadlinekiller.viewmodels.MainViewModel
import com.deadlinekiller.widgets.DeadlineWidget
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseContext.resources.configuration.setLocale(Locale("en"))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        baseContext.resources.configuration.setLocale(Locale("en"))
    }
}
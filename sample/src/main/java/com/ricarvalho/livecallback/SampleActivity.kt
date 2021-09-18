package com.ricarvalho.livecallback

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ricarvalho.livecallback.databinding.ActivitySampleBinding

class SampleActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

package com.strongbulb.hsv_extraction

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.strongbulb.hsv_extraction.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.run {
            btnFindGallery.setOnClickListener {
                startActivity(Intent(this@MainActivity, GalleryActivity::class.java))
            }
        }
    }

}
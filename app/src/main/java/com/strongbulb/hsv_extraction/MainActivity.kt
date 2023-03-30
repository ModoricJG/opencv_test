package com.strongbulb.hsv_extraction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.strongbulb.hsv_extraction.constants.SharedPreferenceKeys
import com.strongbulb.hsv_extraction.databinding.ActivityMainBinding
import com.strongbulb.hsv_extraction.extension.getSharedPreferenceInt
import com.strongbulb.hsv_extraction.extension.putSharedPreferenceInt


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
        setOnEditTextChangeListener()
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView() {
        binding.run {
            val hRow =  this@MainActivity.getSharedPreferenceInt(SharedPreferenceKeys.HRowValue.name)
            val hHigh = this@MainActivity.getSharedPreferenceInt(SharedPreferenceKeys.HHighValue.name, defaultValue = 255)
            val sRow =  this@MainActivity.getSharedPreferenceInt(SharedPreferenceKeys.SRowValue.name)
            val sHigh = this@MainActivity.getSharedPreferenceInt(SharedPreferenceKeys.SHighValue.name, defaultValue = 255)
            val vRow =  this@MainActivity.getSharedPreferenceInt(SharedPreferenceKeys.VRowValue.name)
            val vHigh = this@MainActivity.getSharedPreferenceInt(SharedPreferenceKeys.VHighValue.name, defaultValue = 255)

            layoutH.etRow.setText("$hRow")
            layoutH.etHigh.setText("$hHigh")
            layoutS.etRow.setText("$sRow")
            layoutS.etHigh.setText("$sHigh")
            layoutV.etRow.setText("$vRow")
            layoutV.etHigh.setText("$vHigh")
        }
    }

    private fun setOnClickListener() {
        binding.run {
            btnFindGallery.setOnClickListener {
                startActivity(Intent(this@MainActivity, GalleryActivity::class.java))
            }
        }
    }

    private fun setOnEditTextChangeListener() {
        binding.run {
            layoutH.etRow.addTextChangedListener {
                if(it?.toString().isNullOrBlank()) return@addTextChangedListener
                this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.HRowValue.name, it?.toString()?.toInt() ?: 0)
            }
            layoutH.etHigh.addTextChangedListener {
                if(it?.toString().isNullOrBlank()) return@addTextChangedListener
                this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.HHighValue.name, it?.toString()?.toInt() ?: 0)
            }
            layoutS.etRow.addTextChangedListener {
                if(it?.toString().isNullOrBlank()) return@addTextChangedListener
                this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.SRowValue.name, it?.toString()?.toInt() ?: 0)
            }
            layoutS.etHigh.addTextChangedListener {
                if(it?.toString().isNullOrBlank()) return@addTextChangedListener
                this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.SHighValue.name, it?.toString()?.toInt() ?: 0)
            }
            layoutV.etRow.addTextChangedListener {
                if(it?.toString().isNullOrBlank()) return@addTextChangedListener
                this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.VRowValue.name, it?.toString()?.toInt() ?: 0)
            }
            layoutV.etHigh.addTextChangedListener {
                if(it?.toString().isNullOrBlank()) return@addTextChangedListener
                this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.VHighValue.name, it?.toString()?.toInt() ?: 0)
            }
        }

    }

}
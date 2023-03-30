package com.strongbulb.hsv_extraction

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
        removeEditTextListener()
        initView()
        setOnEditTextChangeListener()
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
            btnFindCamera.setOnClickListener {
                startActivity(Intent(this@MainActivity, CameraActivity::class.java))
            }
        }
    }

    private val hEtRowWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(s?.toString().isNullOrBlank()) return
            this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.HRowValue.name, s?.toString()?.toInt() ?: 0)
        }
    }

    private val hEtHighWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(s?.toString().isNullOrBlank()) return
            this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.HHighValue.name, s?.toString()?.toInt() ?: 0)
        }
    }

    private val sEtRowWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(s?.toString().isNullOrBlank()) return
            this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.SRowValue.name, s?.toString()?.toInt() ?: 0)
        }
    }

    private val sEtHighWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(s?.toString().isNullOrBlank()) return
            this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.SHighValue.name, s?.toString()?.toInt() ?: 0)
        }
    }

    private val vEtRowWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(s?.toString().isNullOrBlank()) return
            this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.VRowValue.name, s?.toString()?.toInt() ?: 0)
        }
    }

    private val vEtHighWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(s?.toString().isNullOrBlank()) return
            this@MainActivity.putSharedPreferenceInt(SharedPreferenceKeys.VHighValue.name, s?.toString()?.toInt() ?: 0)
        }
    }

    private fun setOnEditTextChangeListener() {
        binding.run {
            layoutH.etRow.addTextChangedListener(hEtRowWatcher)
            layoutH.etHigh.addTextChangedListener(hEtHighWatcher)
            layoutS.etRow.addTextChangedListener(sEtRowWatcher)
            layoutS.etHigh.addTextChangedListener(sEtHighWatcher)
            layoutV.etRow.addTextChangedListener(vEtRowWatcher)
            layoutV.etHigh.addTextChangedListener(vEtHighWatcher)
        }
    }

    private fun removeEditTextListener() {
        binding.run {
            layoutH.etRow.removeTextChangedListener(hEtRowWatcher)
            layoutH.etHigh.removeTextChangedListener(hEtHighWatcher)
            layoutS.etRow.removeTextChangedListener(sEtRowWatcher)
            layoutS.etHigh.removeTextChangedListener(sEtHighWatcher)
            layoutV.etRow.removeTextChangedListener(vEtRowWatcher)
            layoutV.etHigh.removeTextChangedListener(vEtHighWatcher)
        }
    }

}
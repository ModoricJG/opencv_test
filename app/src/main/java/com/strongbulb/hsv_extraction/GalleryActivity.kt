package com.strongbulb.hsv_extraction

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.slider.RangeSlider
import com.strongbulb.hsv_extraction.constants.SharedPreferenceKeys
import com.strongbulb.hsv_extraction.databinding.ActivityGalleryBinding
import com.strongbulb.hsv_extraction.extension.getSharedPreferenceInt
import com.strongbulb.hsv_extraction.extension.putSharedPreferenceInt
import com.strongbulb.hsv_extraction.module.FilterModule
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvException
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc


class GalleryActivity : AppCompatActivity() {

    private val defaultGalleryRequestCode = 10
    private lateinit var binding: ActivityGalleryBinding
    private var currentImageUri: Uri? = null

    init {
        System.loadLibrary("opencv_java4")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setOnClickListener()
        setOnRangeListener()
    }

    private fun initView() {
        binding.run {
            val hRow =  this@GalleryActivity.getSharedPreferenceInt(SharedPreferenceKeys.HRowValue.name)
            val hHigh = this@GalleryActivity.getSharedPreferenceInt(SharedPreferenceKeys.HHighValue.name, defaultValue = 255)
            val sRow =  this@GalleryActivity.getSharedPreferenceInt(SharedPreferenceKeys.SRowValue.name)
            val sHigh = this@GalleryActivity.getSharedPreferenceInt(SharedPreferenceKeys.SHighValue.name, defaultValue = 255)
            val vRow =  this@GalleryActivity.getSharedPreferenceInt(SharedPreferenceKeys.VRowValue.name)
            val vHigh = this@GalleryActivity.getSharedPreferenceInt(SharedPreferenceKeys.VHighValue.name, defaultValue = 255)
            binding.rangeH.setValues(hRow?.toFloat(), hHigh?.toFloat())
            binding.rangeS.setValues(sRow?.toFloat(), sHigh?.toFloat())
            binding.rangeV.setValues(vRow?.toFloat(), vHigh?.toFloat())
            setRecordText()
        }
    }

    private fun setOnClickListener() {
        binding.run {
            btnOpenGallery.setOnClickListener {
                startDefaultGalleryApp()
            }
        }
    }

    private fun setOnRangeListener() {
        binding.run {
            rangeH.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {}

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    this@GalleryActivity.putSharedPreferenceInt(SharedPreferenceKeys.HRowValue.name, slider.values[0].toInt())
                    this@GalleryActivity.putSharedPreferenceInt(SharedPreferenceKeys.HHighValue.name, slider.values[1].toInt())
                    setRecordText()
                    imgProcessing()
                }
            })

            rangeS.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {}

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    this@GalleryActivity.putSharedPreferenceInt(SharedPreferenceKeys.SRowValue.name, slider.values[0].toInt())
                    this@GalleryActivity.putSharedPreferenceInt(SharedPreferenceKeys.SHighValue.name, slider.values[1].toInt())
                    setRecordText()
                    imgProcessing()
                }
            })
            rangeV.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {}

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    this@GalleryActivity.putSharedPreferenceInt(SharedPreferenceKeys.VRowValue.name, slider.values[0].toInt())
                    this@GalleryActivity.putSharedPreferenceInt(SharedPreferenceKeys.VHighValue.name, slider.values[1].toInt())
                    setRecordText()
                    imgProcessing()
                }
            })
        }
    }

    private fun setRecordText() {
        val h = binding.rangeH.values // RangeSlider를 통해 값의 범위를 가져옴
        val s = binding.rangeS.values
        val v = binding.rangeV.values
        binding.layoutH.etRow.setText("${h[0].toInt()}")
        binding.layoutH.etHigh.setText("${h[1].toInt()}")
        binding.layoutS.etRow.setText("${s[0].toInt()}")
        binding.layoutS.etHigh.setText("${s[1].toInt()}")
        binding.layoutV.etRow.setText("${v[0].toInt()}")
        binding.layoutV.etHigh.setText("${v[1].toInt()}")
    }

    private fun imgProcessing() {
        binding.ivDstImg.isVisible = true
        currentImageUri?.run {
            val originMat = FilterModule.uri2Mat(this@GalleryActivity, this)
            val hsvMat = FilterModule.matCvtHsv(originMat)
            val rangeMat = matRange(hsvMat)
            val mask = FilterModule.matMask(originMat, rangeMat)
            val dstBitmap = FilterModule.convertMatToBitMap(mask)
            binding.ivDstImg.setImageBitmap(dstBitmap)
        }
    }

    /**
     * 지정 범위 색상 출력
     */
    private fun matRange(mat: Mat) : Mat {
        val h = binding.rangeH.values // RangeSlider를 통해 값의 범위를 가져옴
        val s = binding.rangeS.values
        val v = binding.rangeV.values
        val lowerb = Scalar(h[0].toDouble(),s[0].toDouble(),v[0].toDouble())
        val upperb = Scalar(h[1].toDouble(),s[1].toDouble(),v[1].toDouble())
        return FilterModule.matRange(mat, lowerb, upperb)
    }

    private fun startDefaultGalleryApp() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, defaultGalleryRequestCode)
    }

    // 갤러리 화면에서 이미지를 선택한 경우 현재 화면에 보여준다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            defaultGalleryRequestCode -> {
                currentImageUri = data?.data
                imgProcessing()
                setRecordText()
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
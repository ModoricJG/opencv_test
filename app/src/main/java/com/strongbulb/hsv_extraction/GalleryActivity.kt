package com.strongbulb.hsv_extraction

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.RangeSlider
import com.strongbulb.hsv_extraction.databinding.ActivityGalleryBinding
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvException
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc


class GalleryActivity : AppCompatActivity() {

    val defaultGalleryRequestCode = 10
    private lateinit var binding: ActivityGalleryBinding
    private var currentImageUri: Uri? = null

    init {
        System.loadLibrary("opencv_java4")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
        setOnRangeListener()
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
                override fun onStartTrackingTouch(slider: RangeSlider) {
                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    Log.i("mylog", "v[0] = ${slider.values[0]}, v[1] = ${slider.values[1]}")
                    imgProcessing()
                }
            })

            rangeS.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {
                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    imgProcessing()
                }
            })
            rangeV.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {
                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    imgProcessing()
                }
            })
        }
    }

    private fun imgProcessing() {
        currentImageUri?.run {
            val originMat = uri2Mat(this)
            val hsvMat = matCvtHsv(originMat)
            val rangeMat = matRange(hsvMat)
            val mask = matMask(originMat, rangeMat)
            val dstBitmap = convertMatToBitMap(mask)
            binding.ivDstImg.setImageBitmap(dstBitmap)
        }
    }

    private fun uri2Mat(uri: Uri) : Mat {
        val bmpFactoryOptions = BitmapFactory.Options()
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val dst = Mat()
        Utils.bitmapToMat(bmp, dst)
        return dst
    }

    /**
     * HSV 변환
     */
    private fun matCvtHsv(mat: Mat) : Mat {
        val dst = Mat()
        Imgproc.cvtColor(mat, dst, Imgproc.COLOR_RGB2HSV)
        return dst
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
        val dst = Mat()
        Core.inRange(mat, lowerb, upperb, dst)
        return dst
    }

    private fun matMask(originMat: Mat, mat: Mat) : Mat {
        val dst = Mat()
        Core.bitwise_and(originMat, originMat, dst, mat)
        return dst
    }

    private fun convertMatToBitMap(input: Mat): Bitmap? {
        var bmp: Bitmap? = null
        val rgb = Mat()
        Imgproc.cvtColor(input, rgb, Imgproc.CV_RGBA2mRGBA)
        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(rgb, bmp)
        } catch (e: CvException) {
            Log.d("Exception", e.message ?: "")
        }
        return bmp
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
                Log.i("mylog", "data?.data = ${currentImageUri}")

                try{
                    currentImageUri?.let {
                        if(Build.VERSION.SDK_INT < 28) {
                            val matBitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                currentImageUri
                            )
                            binding.ivMatImg.setImageBitmap(matBitmap)
                        } else {
                            val source = ImageDecoder.createSource(this.contentResolver, it)
                            val matBitmap = ImageDecoder.decodeBitmap(source)
                            binding.ivMatImg.setImageBitmap(matBitmap)
                        }
                    }

                }catch(e: java.lang.Exception)
                {
                    e.printStackTrace()
                }
            }

            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
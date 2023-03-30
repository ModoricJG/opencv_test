package com.strongbulb.hsv_extraction

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import java.util.Collections


class CameraActivity : AppCompatActivity(), CvCameraViewListener2 {
    private var mOpenCvCameraView: CameraBridgeViewBase? = null

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                SUCCESS -> {
                    mOpenCvCameraView?.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        setContentView(R.layout.activity_camera)
        mOpenCvCameraView = findViewById<View>(R.id.activity_surface_view) as CameraBridgeViewBase
        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView!!.setCvCameraViewListener(this)
        mOpenCvCameraView!!.setCameraIndex(0) // front-camera(1),  back-camera(0)
    }

    public override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback)
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {}
    override fun onCameraViewStopped() {}
    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        return inputFrame.rgba()
    }

    private val cameraViewList: List<CameraBridgeViewBase?>
        get() = Collections.singletonList(mOpenCvCameraView)

    private fun onCameraPermissionGranted() {
        val cameraViews = cameraViewList
        for (cameraBridgeViewBase in cameraViews) {
            cameraBridgeViewBase?.setCameraPermissionGranted()
        }
    }

    override fun onStart() {
        super.onStart()
        var havePermission = true
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            havePermission = false
        }
        if (havePermission) {
            onCameraPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted()
        } else {
            showDialogForPermission("카메라 권한을 허용해 주세요.")
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showDialogForPermission(msg: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@CameraActivity)
        builder.setTitle("알림")
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton("예") { dialog, which ->
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
        builder.setNegativeButton("아니오", { dialog, which ->  finish() })
        builder.create().show()
    }

    companion object {
        private const val TAG = "opencv"

        init {
            System.loadLibrary("opencv_java4")
        }

        //여기서부턴 퍼미션 관련 메소드
        private const val CAMERA_PERMISSION_REQUEST_CODE = 200
    }
}
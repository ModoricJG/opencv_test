package com.strongbulb.hsv_extraction.module

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvException
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

object FilterModule {

    fun uri2Mat(context: Context, uri: Uri) : Mat {
        val bmpFactoryOptions = BitmapFactory.Options()
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bmp = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val dst = Mat()
        Utils.bitmapToMat(bmp, dst)
        return dst
    }

    /**
     * HSV 변환
     */
    fun matCvtHsv(mat: Mat) : Mat {
        val dst = Mat()
        Imgproc.cvtColor(mat, dst, Imgproc.COLOR_RGB2HSV)
        return dst
    }

    /**
     *  범위내 색상 추출
     */
    fun matRange(mat: Mat, lowerb: Scalar, upperb: Scalar) : Mat {
        val dst = Mat()
        Core.inRange(mat, lowerb, upperb, dst)
        return dst
    }

    fun matMask(originMat: Mat, mat: Mat) : Mat {
        val dst = Mat()
        Core.bitwise_and(originMat, originMat, dst, mat)
        return dst
    }

    fun convertMatToBitMap(input: Mat): Bitmap? {
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
}
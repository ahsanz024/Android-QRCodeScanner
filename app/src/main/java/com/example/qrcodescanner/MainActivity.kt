package com.example.qrcodescanner

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.core.util.valueIterator
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    private lateinit var detector: BarcodeDetector

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buildQrDetector()
    }

    private fun buildQrDetector() {
        detector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.QR_CODE)
//            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
    }

    fun startCamera(v: View) {
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            detectQrCode(imageBitmap)
        }
    }

    private fun detectQrCode(image: Bitmap) {
        if(!detector.isOperational){
            Log.e(TAG, "Could not setup detector")
            textView.text = "Could not set up the detector!";
            return;
        }
        val frame: Frame = Frame.Builder().setBitmap(image).build()
        val barcodes: SparseArray<Barcode> = detector.detect(frame)
        barcodes.valueIterator().forEach { barcode ->
            Log.e(TAG, "QRCode result ${barcode.rawValue}")
            textView.text = barcode.rawValue
        }
    }
}

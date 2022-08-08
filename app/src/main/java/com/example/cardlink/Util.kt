package com.example.cardlink

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.URLUtil.isValidUrl
import android.widget.ImageView
import android.widget.Toast
import com.google.common.base.Ascii.toLowerCase
import com.google.firebase.storage.FirebaseStorage

class Util {
    companion object {
        fun asString(text: Any?): String {
            if(text != null)
                return text as String
            return ""
        }

        // https://stackoverflow.com/questions/16465686/android-resizing-bitmaps-without-losing-quality
        fun resize(imaged: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
            var image = imaged
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > 1) {
                finalWidth = Math.round(maxHeight.toFloat() * ratioBitmap)
            } else {
                finalHeight = Math.round(maxWidth.toFloat() / ratioBitmap)
            }
            return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, false)
        }

        fun downloadUserImage(userId: String, imageView: ImageView) {
            Thread(Runnable {
                val mainHandler = Handler(Looper.getMainLooper())
                val storageReference = FirebaseStorage.getInstance().reference
                val photoReference = storageReference.child("images/${userId}/profile.jpg")
                val ONE_MEGABYTE = (1024 * 1024 * 10).toLong()
                photoReference.metadata.addOnSuccessListener {
                    println("debug: onSuccess $it")
                    photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        var myRunnable = Runnable() {
                            imageView.setImageBitmap(bmp)
                        };
                        mainHandler.post(myRunnable);
                    }
                }.addOnFailureListener {
                    println("debug: image not found")
                }
            }).start()
        }

        // appends "https://" prefix
        fun getUri(url: String): Uri {
            val lowerCaseUrl = toLowerCase(url)
            var uri: Uri = Uri.parse(lowerCaseUrl)

            // Reference: https://stackoverflow.com/questions/5882656/no-activity-found-to-handle-intent-android-intent-action-view
            if (!lowerCaseUrl.startsWith("http://") && !lowerCaseUrl.startsWith("https://")) {
                uri = Uri.parse("https://${lowerCaseUrl}")
            }

            return uri
        }
    }
}
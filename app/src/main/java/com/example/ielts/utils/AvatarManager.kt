package com.example.ielts.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.min

object AvatarManager {

    private const val PREFS_NAME = "avatar_user_prefs"
    private const val KEY_AVATAR_PATH = "key_custom_avatar_path"
    private const val AVATAR_FILE_NAME = "user_avatar.png"

    /**
     * Lưu ảnh đại diện từ Uri thư viện vào bộ nhớ nội bộ (internal storage) của ứng dụng
     */
    fun saveAvatarFromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // Resize ảnh về kích thước chuẩn (tối đa 512x512) để tối ưu dung lượng & bộ nhớ
            val maxDimension = 512
            val scale = min(maxDimension.toFloat() / originalBitmap.width, maxDimension.toFloat() / originalBitmap.height)
            val finalWidth = if (scale < 1.0f) (originalBitmap.width * scale).toInt() else originalBitmap.width
            val finalHeight = if (scale < 1.0f) (originalBitmap.height * scale).toInt() else originalBitmap.height
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, finalWidth, finalHeight, true)

            // Cắt ảnh thành hình tròn
            val circularBitmap = getCircularBitmap(scaledBitmap)

            // Lưu file vào filesDir
            val avatarFile = File(context.filesDir, AVATAR_FILE_NAME)
            val out = FileOutputStream(avatarFile)
            circularBitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
            out.flush()
            out.close()

            // Lưu đường dẫn file vào SharedPreferences
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_AVATAR_PATH, avatarFile.absolutePath)
                .apply()

            // Đồng bộ sang ielts_prefs để tương thích
            context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString("user_avatar_path", avatarFile.absolutePath)
                .apply()

            avatarFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Lấy Bitmap ảnh đại diện tùy chỉnh (nếu có)
     */
    fun getCustomAvatarBitmap(context: Context): Bitmap? {
        val path = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_AVATAR_PATH, null)
            ?: context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
                .getString("user_avatar_path", null)
            ?: return null

        val file = File(path)
        if (!file.exists() || file.length() == 0L) return null

        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Tự động hiển thị ảnh đại diện lên ImageView hoặc fallback về chữ cái đầu
     */
    fun loadAvatar(
        context: Context,
        imageView: ImageView,
        fallbackInitialView: TextView? = null,
        initialText: String = "U"
    ) {
        val bitmap = getCustomAvatarBitmap(context)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
            imageView.visibility = View.VISIBLE
            fallbackInitialView?.visibility = View.GONE
        } else {
            imageView.visibility = View.GONE
            fallbackInitialView?.apply {
                visibility = View.VISIBLE
                text = initialText
            }
        }
    }

    /**
     * Xóa ảnh đại diện tùy chỉnh
     */
    fun clearAvatar(context: Context) {
        val file = File(context.filesDir, AVATAR_FILE_NAME)
        if (file.exists()) {
            file.delete()
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_AVATAR_PATH)
            .apply()
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = min(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
        }
        val rect = Rect(0, 0, size, size)
        val srcRect = Rect(
            (bitmap.width - size) / 2,
            (bitmap.height - size) / 2,
            (bitmap.width + size) / 2,
            (bitmap.height + size) / 2
        )
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, srcRect, rect, paint)
        return output
    }
}


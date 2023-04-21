import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.math.min

// From: https://stackoverflow.com/questions/54813010/android-can-we-actually-save-imagereaders-acquirelatestimage

class ImagePreprocessor {
    private var rgbFrameBitmap = Bitmap.createBitmap(2, 2,
            Bitmap.Config.ARGB_8888)

    fun preprocessImage(image: Image?, width: Int, height: Int): Bitmap? {
        rgbFrameBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        if (image == null) {
            return null
        }

        check(rgbFrameBitmap!!.width ==  image.width) { "Invalid size width" }
        check(rgbFrameBitmap!!.height == image.height) { "Invalid size height" }

        if (rgbFrameBitmap != null) {
            val bb = image.planes[0].buffer
            rgbFrameBitmap = BitmapFactory.decodeStream(ByteBufferBackedInputStream(bb))
        }

        return rgbFrameBitmap
    }

    private class ByteBufferBackedInputStream(internal var buf: ByteBuffer) : InputStream() {

        @Throws(IOException::class)
        override fun read(): Int {
            return if (!buf.hasRemaining()) {
                -1
            } else (buf.get() and 0xFF.toByte()).toInt()
        }

        @Throws(IOException::class)
        override fun read(bytes: ByteArray, off: Int, len: Int): Int {
            var length = len
            if (!buf.hasRemaining()) {
                return -1
            }

            length = min(length, buf.remaining())
            buf.get(bytes, off, length)
            return length
        }
    }
}
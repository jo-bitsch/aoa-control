package io.github.jo_bitsch.aoa_control.service

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Objects

class DirectedStream(
    inStream: InputStream,
    outStream: OutputStream,
    daemon: Boolean = false
) : Thread(handler(inStream, outStream)) {

    init {
        isDaemon = daemon
    }

    companion object {
        private fun handler(inStream: InputStream, outStream: OutputStream): Runnable {
            return Runnable {
                Log.i("DirectedStream","start running")
                try {
                    Objects.requireNonNull(outStream, "out")
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (inStream.read(buffer, 0, 8192)
                            .also { read = it } >= 0
                    ) {
                        Log.i("DirectedStream","read \"${buffer.toString().substring(0,read)}\"")
                        if(Thread.interrupted())
                            break
                        outStream.write(buffer, 0, read)
                        outStream.flush()
                    }
                    Log.i("DirectedStream","stop running")
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException){
                    e.printStackTrace()
                }
            }
        }
    }
}
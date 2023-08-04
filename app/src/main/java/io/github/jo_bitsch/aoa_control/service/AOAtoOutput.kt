package io.github.jo_bitsch.aoa_control.service

import android.util.Log
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException
import java.net.Socket
import java.net.SocketException

class AOAtoOutput(
    fd: FileDescriptor,
    socket: Socket,
) : Thread(handler(fd, socket)) {

    companion object {
        private fun handler(fd: FileDescriptor, socket: Socket): Runnable {
            return Runnable {
                Log.i("DirectedStream","start running")
                val inStream = FileInputStream(fd)
                val outStream = socket.getOutputStream()
                try {
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (inStream.read(buffer, 0, 8192)
                            .also { read = it } >= 0
                    ) {
                        Log.i(TAG, "read \"${buffer.decodeToString(0, read)}\"")
                        if (socket.isClosed or socket.isInputShutdown) {
                            Log.i(TAG, "shutting down")
                            socket.shutdownOutput()
                            outStream.close()
                            break
                        }
                        outStream.write(buffer, 0, read)
                        outStream.flush()
                    }
                } catch (e: SocketException){
                    outStream.close()
                    inStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException){
                    e.printStackTrace()
                }
                Log.i(TAG, "stop running")
            }
        }

        val TAG = "AOAtoOutput"
    }

}
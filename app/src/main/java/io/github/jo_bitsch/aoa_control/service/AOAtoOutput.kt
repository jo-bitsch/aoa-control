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
    otherThread: Thread?,
) : Thread(handler(fd, socket, otherThread)) {

    companion object {
        private fun handler(fd: FileDescriptor, socket: Socket, otherThread: Thread?): Runnable {
            return Runnable {
                Log.i(TAG,"start running")
                val inStream = FileInputStream(fd)
                val outStream = socket.getOutputStream()
                try {
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (inStream.read(buffer, 0, 8192)
                            .also { read = it } >= 0
                    ) {
                        if (socket.isClosed or socket.isInputShutdown) {
                            Log.i(TAG, "shutting down")
                            socket.shutdownOutput()
                            outStream.close()
                            break
                        }
                        Log.i("DirectedStream","write \"${buffer.decodeToString(0,read)}\"")
                        outStream.write(buffer, 0, read)
                        outStream.flush()
                    }
                } catch (_: SocketException){
                    outStream.close()
                    inStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    outStream.close()
                    inStream.close()
                } catch (e: NullPointerException){
                    e.printStackTrace()
                }
                Log.i(TAG, "stop running")
            }
        }

        private const val TAG = "AOAtoOutput"
    }

}
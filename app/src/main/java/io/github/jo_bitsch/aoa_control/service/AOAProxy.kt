package io.github.jo_bitsch.aoa_control.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.IBinder
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.core.app.NotificationCompat
import io.github.jo_bitsch.aoa_control.R
import java.io.FileOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicBoolean


class AOAProxy : Service() {
    private val manager: UsbManager by lazy {
        getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private val working = AtomicBoolean(true)
    private var thread: Thread? = null
    private val runnable = Runnable {
        try {
            Log.i("Runnable", "Starting thread for proxying AOA stuff")
        //    val aoAtoOutput = AOAtoOutput(manager, System.out)
        //    aoAtoOutput.start()
            val pfd =  manager.openAccessory(manager.accessoryList?.first())
//            Log.i(TAG, "Opening Server Socket")
            val serverSocket = ServerSocket(PORT)
            // we explicitly only ever allow one single connection to be handled as afterwards
            // the state of the AOA Socket is ill defined
            val socket = serverSocket.accept()
            val socketOut = socket.getOutputStream()
            val socketIn = socket.getInputStream()

      //      Log.i("Runner", "pfd is $pfd")
            val localFd = pfd.fileDescriptor
            val aoAtoOutput = AOAtoOutput(localFd,socket)
            aoAtoOutput.start()
            val outputStream = FileOutputStream(localFd)
            var read = 0
            val buffer = ByteArray(8192)
            while (socketIn.read(buffer, 0, 8192)
                    .also { read = it } >= 0
            ) {
                Log.i("DirectedStream","read \"${buffer.decodeToString(0,read)}\"")
                outputStream.write(buffer, 0, read)
                outputStream.flush()
            }
            aoAtoOutput.interrupt()
            Log.i("DirectedStream","stop running")
            Log.i("Runner", "Done")
            Log.i(TAG, "Will not accept new server connections")
            aoAtoOutput.join()
            outputStream.close()
            Log.i(TAG, "Connection joined")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        StrictMode.setVmPolicy(
            VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMeForeground()
//        fd = intent?.getIntExtra("fd", 0) ?: 0
        thread =  Thread(runnable)
        thread?.priority = Thread.MAX_PRIORITY
        thread?.name = "aoa data transfer"
        thread?.isDaemon = true
        thread?.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        working.set(false)
    }

    private fun startMeForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG, "start notification")
            val notificationChannelId = packageName
            val channelName = "AOA Background Service"
            val chan = NotificationChannel(
                notificationChannelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                notificationBuilder.foregroundServiceBehavior = Notification.FOREGROUND_SERVICE_IMMEDIATE
            }
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Android Open Accessory connected")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    3,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
                )
            } else {
                startForeground(2, notification)
            }
        } else {
            startForeground(1, Notification())
        }
    }

    companion object {
        private val TAG = AOAProxy::class.java.simpleName
        private const val PORT = 9876
    }
}
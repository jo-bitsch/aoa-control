package io.github.jo_bitsch.aoa_control.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.IBinder
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.github.jo_bitsch.aoa_control.ACTION_USB_PERMISSION
import io.github.jo_bitsch.aoa_control.R
import java.io.FileOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicBoolean


class AOAProxy : Service() {
    private val manager: UsbManager by lazy {
        getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private val working = AtomicBoolean(true)
    var tcpToAOAThread: Thread? = null
    private var aoaToTCPThread: Thread? = null
    private val runnable = Runnable {
        try {
            Log.i("Runnable", "Starting thread for proxying AOA stuff")
            val aoaDevice = manager.accessoryList?.first()
            if (aoaDevice == null){
                Log.i(TAG, "no AOA device connected")
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return@Runnable
            }
            val pfd =  manager.openAccessory(manager.accessoryList?.first())
            val serverSocket = ServerSocket(PORT,1, InetAddress.getLocalHost())

            // we explicitly only ever allow one single connection to be handled as afterwards
            // the state of the AOA Socket is ill defined
            val socket = serverSocket.accept()
            val socketIn = socket.getInputStream()

            val localFd = pfd.fileDescriptor
            aoaToTCPThread = AOAtoOutput(localFd,socket, tcpToAOAThread)
            aoaToTCPThread?.start()
            val outputStream = FileOutputStream(localFd)
            var read: Int
            val buffer = ByteArray(8192)
            while (socketIn.read(buffer, 0, 8192)
                    .also { read = it } >= 0
            ) {
                Log.i("DirectedStream","read \"${buffer.decodeToString(0,read)}\"")
                outputStream.write(buffer, 0, read)
                outputStream.flush()
            }
            aoaToTCPThread?.interrupt()
            Log.i("DirectedStream","stop running")
            Log.i("Runner", "Done")
            Log.i(TAG, "Will not accept new server connections")
            aoaToTCPThread?.join()
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
        tcpToAOAThread =  Thread(runnable)
        tcpToAOAThread?.priority = Thread.MAX_PRIORITY
        tcpToAOAThread?.name = "aoa data transfer"
        tcpToAOAThread?.isDaemon = true
        tcpToAOAThread?.start()


        val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if (UsbManager.ACTION_USB_ACCESSORY_DETACHED == intent.action) {
                    tcpToAOAThread?.interrupt()
                    aoaToTCPThread?.interrupt()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
        ContextCompat.registerReceiver(applicationContext,usbReceiver,filter,ContextCompat.RECEIVER_NOT_EXPORTED)
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
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Android Open Accessory connected")
                .setPriority(NotificationManager.IMPORTANCE_LOW)
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
        private const val PORT = 0xA0A0  // almost spells out aoa0, currently unassigned port according to IANA and wikipedia
    }
}
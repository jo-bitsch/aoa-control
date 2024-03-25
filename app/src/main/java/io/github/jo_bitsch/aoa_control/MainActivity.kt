package io.github.jo_bitsch.aoa_control

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.github.jo_bitsch.aoa_control.service.AOAProxy
import io.github.jo_bitsch.aoa_control.ui.DeviceCard
import io.github.jo_bitsch.aoa_control.ui.theme.AOAControlTheme
import io.github.jo_bitsch.aoa_control.utils.UsbAccessoryData
import io.github.jo_bitsch.aoa_control.utils.UsbAccessoryData.Companion.usbAccessoryData


const val ACTION_USB_PERMISSION = "io.github.jo_bitsch.USB_PERMISSION"

class MainActivity : ComponentActivity() {

    private val manager: UsbManager by lazy {
        getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private var usbAccessory: UsbAccessory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build()
        )

        val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (UsbManager.ACTION_USB_ACCESSORY_DETACHED == intent.action) {
                    finishAndRemoveTask()
                }
            }
        }
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
        ContextCompat.registerReceiver(applicationContext,usbReceiver,filter,ContextCompat.RECEIVER_NOT_EXPORTED)



        if (intent.hasExtra(UsbManager.EXTRA_ACCESSORY)) {
            usbAccessory = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    intent.getParcelableExtra(
                        UsbManager.EXTRA_ACCESSORY,
                        UsbAccessory::class.java
                    )

                else -> @Suppress("DEPRECATION")
                intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY)
            }
            Log.i("MainActivity", "starting service 1")
            val serviceIntent = Intent(applicationContext, AOAProxy::class.java)
            ContextCompat.startForegroundService(applicationContext, serviceIntent)
        } else {
            val accessoryList: Array<out UsbAccessory>? = manager.accessoryList
            if (accessoryList != null && accessoryList.isNotEmpty()) {
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    Intent(ACTION_USB_PERMISSION),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        PendingIntent.FLAG_MUTABLE
                    else
                        0
                )
                val intentFilter = IntentFilter(ACTION_USB_PERMISSION)
                ContextCompat.registerReceiver(
                    applicationContext, usbReceiver, intentFilter,
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )

                usbAccessory = accessoryList.first()
                manager.requestPermission(usbAccessory, pendingIntent)
            } else {
                usbAccessory = null
            }
        }
        setDiscoveredContent()
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                unregisterReceiver(this)
            } catch (_: IllegalArgumentException) {
            }
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val accessory: UsbAccessory? = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                            intent.getParcelableExtra(
                                UsbManager.EXTRA_ACCESSORY,
                                UsbAccessory::class.java
                            )

                        else -> @Suppress("DEPRECATION")
                        intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY)
                    }


                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        accessory?.apply {
                            setDiscoveredContent()

                            Log.d(
                                this.javaClass.simpleName,
                                "permission granted for accessory $accessory"
                            )
                            Log.i("MainActivity", "starting service 2")
                            val serviceIntent = Intent(applicationContext, AOAProxy::class.java)
                            ContextCompat.startForegroundService(applicationContext, serviceIntent)
                        }
                    } else {
                        Log.d(
                            this.javaClass.simpleName,
                            "permission denied for accessory $accessory"
                        )
                    }
                }
            }
        }
    }

    private fun setDiscoveredContent() {
        setContent {
            MainScreen(usbAccessory?.usbAccessoryData)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainScreen(usbAccessory: UsbAccessoryData? = null) {
    val context = LocalContext.current
    AOAControlTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.safeContent)
                    .padding(horizontal = 4.dp)
            ) {
                DeviceCard(usbAccessory = usbAccessory)
                TextButton(
                    onClick =
                    {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "https://github.com/jo-bitsch/aoa-control/blob/" +
                                            "6f8507c2cb0cf40d6e98ba4b8493d0a271d9853a/" +
                                            "privacy-policy-2023-07-21.md"
                                )
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.End),
                ) {
                    Text(
                        text = "Privacy Policy",
                    )
                }
            }
        }
    }
}
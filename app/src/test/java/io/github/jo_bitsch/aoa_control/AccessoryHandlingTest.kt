package io.github.jo_bitsch.aoa_control

import android.content.Context
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbManager
import androidx.test.core.app.ApplicationProvider
import io.github.jo_bitsch.aoa_control.utils.UsbAccessoryData
import io.github.jo_bitsch.aoa_control.utils.UsbAccessoryData.Companion.usbAccessoryData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertFailsWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class AccessoryHandlingTest {

    @Test
    fun testAccessoryAnalyzer() {
        val usbManager =
            ApplicationProvider.getApplicationContext<Context>()
                .getSystemService(Context.USB_SERVICE) as UsbManager
        val l: Array<UsbAccessory>? = usbManager.accessoryList

        assertNull(l)
        assertFailsWith(SecurityException::class) {
            throw SecurityException()
        }
        val uA = mock<UsbAccessory> {
            on(it.manufacturer) doReturn ("aoa-proxy")
            on(it.model) doReturn ("test")
        }
        val usbAccessoryData = uA.usbAccessoryData
        assert(usbAccessoryData.isAOAProxy())
        assertEquals(null, usbAccessoryData.serial)

        assert(usbAccessoryData.services.isEmpty())
    }

    @Test
    fun testGetServices() {
        val usbAccessoryData = UsbAccessoryData(
            "aoa-proxy",
            "Raspberry Pi 4 Model B\nSense HAT\nServices:ssh,rfb"
        )
        assert(
            usbAccessoryData.services.containsAll(
                listOf(
                    UsbAccessoryData.ServiceTypes.SSH,
                    UsbAccessoryData.ServiceTypes.RFB
                )
            )
        )
    }


}
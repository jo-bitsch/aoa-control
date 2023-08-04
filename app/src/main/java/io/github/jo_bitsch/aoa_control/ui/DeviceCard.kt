package io.github.jo_bitsch.aoa_control.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.github.jo_bitsch.aoa_control.utils.UsbAccessoryData

@Preview(widthDp = 300)
@Composable
fun DeviceCard(@PreviewParameter(SampleAccessoryProvider::class) usbAccessory: UsbAccessoryData?){
    if(usbAccessory == null){
        NoAccessoryDetectedCard()
    }else{
        if (usbAccessory.isAOAProxy()){
            AOAProxyCard(usbAccessory = usbAccessory)
        }else{
            UnsupportedAOADeviceCard(usbAccessory = usbAccessory)
        }

    }
}
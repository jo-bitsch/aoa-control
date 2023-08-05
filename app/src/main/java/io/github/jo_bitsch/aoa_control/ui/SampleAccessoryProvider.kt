package io.github.jo_bitsch.aoa_control.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.jo_bitsch.aoa_control.utils.UsbAccessoryData

/**
 * aoa-proxy --port 3-1 --announce --manufacturer MangoPi --model "MPi-MQ1PL"  \
 *     --model-version "V1.4" --description "$(echo -e "MangoPi\nYour Tiny Tiny Tiny SBC")" \
 *     --url "https://mangopi.org/mqpro" --serial "123456U5678"
 */
class SampleAccessoryProvider : PreviewParameterProvider<UsbAccessoryData?> {
    override val values: Sequence<UsbAccessoryData?>
        get() {
            return sequenceOf(
                UsbAccessoryData(
                    "aoa-proxy",
                    "Raspberry Pi v4\nconsole",
                    "Ubuntu 23.04\nFirmware 4711",
                    "description\nIP: 192.168.0.1",
                    "uri",
                    "123456U5678"
                ),
                UsbAccessoryData(
                    "aoa-proxy",
                    "Raspberry Pi v4"
                ),
                /**
                 * aoa-proxy --port 3-1 --announce \
                 *     --manufacturer MangoPi \
                 *     --model "MPi-MQ1PL"  \
                 *     --model-version "V1.4" \
                 *     --description "$(echo -e "MangoPi\nYour Tiny Tiny Tiny SBC")" \
                 *     --url "https://mangopi.org/mqpro" \
                 *     --serial "123456U5678"
                 */
                UsbAccessoryData(
                    "MangoPi",
                    "MPi-MQ1PL",
                    "V1.4",
                    "MangoPi\n" +
                            "Your Tiny Tiny Tiny SBC",
                    "https://mangopi.org/mqpro",
                    "123456U5678"
                ),
                UsbAccessoryData(
                    "Raspberry Pi Ltd",
                    "Raspberry Pi 4 Model B",
                    null,
                    "Raspberry Pi 4\n" +
                            "Your tiny, dual-display, desktop computer\n" +
                            "\n" +
                            "…and robot brains, smart home hub, media centre, networked AI core," +
                            "factory controller, and much more",
                    "https://www.raspberrypi.com/",
                    null
                ),
                null
            )
        }
}
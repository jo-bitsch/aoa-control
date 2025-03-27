package io.github.jo_bitsch.aoa_control.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.jo_bitsch.aoa_control.R
import io.github.jo_bitsch.aoa_control.utils.UsbAccessoryData

class UnsupportedAOADeviceCardSampleProvider : PreviewParameterProvider<UsbAccessoryData> {
    override val values: Sequence<UsbAccessoryData>
        get() {
            return sequenceOf(
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
                /**
                 *
                 *  aoa-proxy --port 3-1 --announce \
                 *      --manufacturer Android \
                 *      --model "Android Auto" \
                 *      --model-version "2.0.1" \
                 *      --description "Android Auto" \
                 *      --url "https://fixstudio.com" \
                 *      --serial "HU-AAAAAA001"
                 *
                 * or DHU
                 * https://developer.android.com/training/cars/testing/dhu
                 *
                 */
                UsbAccessoryData(
                    "Android",
                    "Android Auto",
                    "2.0.1",
                    "Android Auto",
                    "https://fixstudio.com/",
                    "HU-AAAAAA001"
                ),
            )
        }
}

@Preview(widthDp = 300)
@Composable
fun UnsupportedAOADeviceCard(@PreviewParameter(UnsupportedAOADeviceCardSampleProvider::class) usbAccessory: UsbAccessoryData){
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Min)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            val color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            Image(
                painterResource(id = R.drawable.usb_3),
                null,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(.7f)
                    .matchParentSize()
                    .align(Alignment.CenterEnd),
                colorFilter = ColorFilter.tint(color)
            )

            Column(
                modifier = Modifier
                    .padding(all = 4.dp)
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.generic_aoa_device),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                HorizontalDivider()
                Text(
                    text = stringResource(R.string.manufacturer).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = usbAccessory.manufacturer,
                    softWrap = true,
                    style = MaterialTheme.typography.bodySmall,
                )
                HorizontalDivider(thickness = Dp.Hairline)
                Text(
                    text = stringResource(R.string.model).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = usbAccessory.model,
                    softWrap = true,
                    style = MaterialTheme.typography.bodySmall,
                )
                HorizontalDivider(thickness = Dp.Hairline)
                Text(
                    text = stringResource(R.string.version).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = usbAccessory.version ?: stringResource(R.string.none_provided),
                    softWrap = true,
                    style = MaterialTheme.typography.bodySmall,
                )

                HorizontalDivider(thickness = Dp.Hairline)
                Text(
                    text = stringResource(R.string.description).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = usbAccessory.description ?: stringResource(R.string.none_provided),
                    softWrap = true,
                    style = MaterialTheme.typography.bodySmall,
                )
                HorizontalDivider(thickness = Dp.Hairline)
                Text(
                    text = stringResource(R.string.uri).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (usbAccessory.uri!=null){
                    Text(
                        text = usbAccessory.uri,
                        softWrap = true,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(usbAccessory.uri)
                                )
                            )
                        }
                    )
                }else{
                    Text(
                        text = stringResource(R.string.none_provided),
                        softWrap = true,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                HorizontalDivider(thickness = Dp.Hairline)
                Text(
                    text = stringResource(R.string.serial).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = usbAccessory.serial ?: stringResource(R.string.none_provided),
                    softWrap = true,
                    style = MaterialTheme.typography.bodySmall,
                )


            }

        }
    }

}
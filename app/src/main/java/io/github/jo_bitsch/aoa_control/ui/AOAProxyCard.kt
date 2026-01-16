package io.github.jo_bitsch.aoa_control.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.SystemSecurityUpdateGood
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.core.net.toUri
import io.github.jo_bitsch.aoa_control.R
import io.github.jo_bitsch.aoa_control.utils.UsbAccessoryData

class AOAProxyDeviceCardSampleProvider : PreviewParameterProvider<UsbAccessoryData?> {
    override val values: Sequence<UsbAccessoryData?>
        get() {
            return sequenceOf<UsbAccessoryData?>(
                UsbAccessoryData(
                    "aoa-proxy",
                    "Raspberry Pi v4\nconsole\nssh\nrfb",
                    "Raspberry Pi OS (Trixie)\nLinux 6.12.y",
                    "Raspberry Pi v4\nIP: 192.168.0.1\n",
                    "uri",
                    "100000007184bc7e\ndc:a6:32:01:23:45"
                ),
                UsbAccessoryData(
                    "aoa-proxy",
                    "Raspberry Pi v4"
                ),
            )
        }
}

@Preview
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AOAProxyCard(@PreviewParameter(AOAProxyDeviceCardSampleProvider::class) usbAccessory: UsbAccessoryData){
    Card(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Min)
            .padding(10.dp)
    ) {
        val context = LocalContext.current
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            val color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
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
                        text = usbAccessory.modelName,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                HorizontalDivider()
                Text(
                    text = stringResource(id = R.string.version).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (usbAccessory.version != null) {
                        for (version in usbAccessory.version.split("\n").filter { it.isNotEmpty() }) {
                            AssistChip(
                                leadingIcon = { Icon(Icons.Default.SystemSecurityUpdateGood, null, Modifier.size(18.dp)) },
                                onClick = {
                                          },
                                label = {
                                    Text(text = version)
                                })
                        }
                    } else {
                        Text(text = stringResource(R.string.none_provided))
                    }
                }
                HorizontalDivider(thickness = Dp.Hairline)
                Text(
                    text = stringResource(R.string.identifiers).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (usbAccessory.serial != null) {
                        for (ids in usbAccessory.serial.split("\n").filter { it.isNotEmpty() }) {
                            AssistChip(
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.SimCard,
                                        null,
                                        Modifier
                                            .size(18.dp)
                                    )},
                                onClick = { /*TODO*/ },
                                label = {
                                    Text(text = ids)
                                },
                            )
                        }
                    } else {
                        Text(text = stringResource(R.string.none_provided))
                    }
                }
                HorizontalDivider(thickness = Dp.Hairline)
                Text(
                    text = stringResource(R.string.ip_addresses).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (usbAccessory.ipAddresses.isNotEmpty()) {
                        for (ipAddress in usbAccessory.ipAddresses) {
                            val expanded = remember {
                                mutableStateOf(false)
                            }
                            AssistChip(
                                onClick = {
                                    expanded.value = true
                                },
                                leadingIcon = { Icon(Icons.Default.Lan, "LAN", Modifier.size(18.dp)) },
                                label = {
                                    Text(text = ipAddress)
                                    DropdownMenu(
                                        expanded = expanded.value,
                                        onDismissRequest = { expanded.value = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                    Text(text = "http")
                                                   },
                                            onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        "http://${ipAddress}/".toUri()
                                                    )
                                                )
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Text(text = "https")
                                            },
                                            onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        "https://${ipAddress}/".toUri()
                                                    )
                                                )
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Text(text = "cockpit")
                                            },
                                            onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        "http://${ipAddress}:9090/".toUri()
                                                    )
                                                )
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Text(text = "SSH")
                                            },
                                            onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        "ssh://${ipAddress}:22/#aoa".toUri()
                                                    )
                                                )
                                            }
                                        )
                                    }
                                })
                        }
                    } else {
                        Text(text = stringResource(R.string.none_provided))
                    }
                }
                if (usbAccessory.services.isNotEmpty()){
                    HorizontalDivider()
                    FlowRow (
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        for(service in usbAccessory.services) {
                            Button(onClick = {
                                when (service) {
                                    UsbAccessoryData.ServiceTypes.Console -> {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                "telnet://127.0.0.1:41120/#aoa-telnet".toUri()
                                            )
                                        )
                                    }
                                    UsbAccessoryData.ServiceTypes.SSH -> {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                "ssh://jo@127.0.0.1:41120/#aoa".toUri()
                                            )
                                        )
                                    }

                                    else -> {/* TODO */}
                                }
                            }) {
                                Text(text = service.name)
                            }
                        }
                    }
                }


            }

        }
    }

}

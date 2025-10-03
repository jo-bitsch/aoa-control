package io.github.jo_bitsch.aoa_control.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.github.jo_bitsch.aoa_control.R

@Preview
@Composable
fun NoAccessoryDetectedCard() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Min)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp)
        ) {
            val color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            Image(
                painterResource(id = R.drawable.usb),
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
                    .fillMaxWidth()
                    .align(Alignment.Center),
            ) {
                Text(
                    text = stringResource(R.string.connect_a_device),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                HorizontalDivider()

                TextButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick =
                    {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://jo-bitsch.github.io/aoa-control/".toUri()
                            )
                        )

                    }) {
                    Text(text = stringResource(R.string.learn_more))
                }

            }

        }
    }

}
package ru.nto.guestmanagement.mobile.task.a2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.nto.guestmanagement.mobile.R
import ru.nto.guestmanagement.mobile.data.TimeProvider
import ru.nto.guestmanagement.mobile.model.PassData
import ru.nto.guestmanagement.mobile.model.PassStatus
import ru.nto.guestmanagement.mobile.ui.PassQr
import ru.nto.guestmanagement.mobile.ui.statusColor

/** Метка статуса и QR-код либо сообщение о недоступности. */
@Composable
fun PassAccessPanel(data: PassData, timeProvider: TimeProvider, modifier: Modifier = Modifier) {
    // Read on every composition so a changed TimeProvider or PassData updates the panel.
    val status = calculatePassStatus(data.pass, data.scans, timeProvider.now())
    val (label, colorRes) = when (status) {
        PassStatus.ACTIVE -> R.string.pass_status_active to R.color.status_active
        PassStatus.NOT_STARTED -> R.string.pass_status_not_started to R.color.status_not_started
        PassStatus.EXPIRED -> R.string.pass_status_expired to R.color.status_expired
        PassStatus.EXHAUSTED -> R.string.pass_status_exhausted to R.color.status_exhausted
        PassStatus.BLOCKED -> R.string.pass_status_blocked to R.color.status_blocked
    }
    val statusColor = colorResource(colorRes)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(label),
            color = statusColor,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .statusColor(statusColor)
                .testTag("pass_status"),
        )
        if (status == PassStatus.ACTIVE) {
            PassQr(content = data.pass.qrCode)
        } else {
            Text(
                text = stringResource(R.string.qr_unavailable),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("pass_qr_hidden"),
            )
        }
    }
}


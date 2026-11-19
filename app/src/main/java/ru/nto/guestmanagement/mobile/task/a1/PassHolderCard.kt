package ru.nto.guestmanagement.mobile.task.a1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.nto.guestmanagement.mobile.R
import ru.nto.guestmanagement.mobile.model.PassDetails
import java.time.format.DateTimeFormatter
import java.util.Locale

/** A1. Карточка владельца: ФИО, организация, срок действия и лимит проходов. */
@Composable
fun PassHolderCard(pass: PassDetails, modifier: Modifier = Modifier) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT)
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ROOT)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)
    val sameDay = pass.validFrom.toLocalDate() == pass.validTo.toLocalDate()
    val validPeriod = if (sameDay) {
        stringResource(
            R.string.pass_valid_same_day,
            pass.validFrom.format(dateFormatter),
            pass.validFrom.format(timeFormatter),
            pass.validTo.format(timeFormatter),
        )
    } else {
        stringResource(
            R.string.pass_valid_range,
            pass.validFrom.format(dateTimeFormatter),
            pass.validTo.format(dateTimeFormatter),
        )
    }
    val entriesLimit = when {
        pass.maxEntries == 1 -> stringResource(R.string.pass_single_entry)
        else -> stringResource(entriesLimitString(pass.maxEntries), pass.maxEntries)
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = pass.visitor.fullName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.testTag("pass_holder"),
            )
            if (!pass.visitor.organization.isNullOrBlank()) {
                Text(
                    text = pass.visitor.organization,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("pass_organization"),
                )
            }
            Text(
                text = validPeriod,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("pass_valid_period"),
            )
            Text(
                text = entriesLimit,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("pass_entries_limit"),
            )
        }
    }
}

private fun entriesLimitString(maxEntries: Int): Int = when {
    maxEntries % 10 == 1 && maxEntries % 100 != 11 -> R.string.pass_entries_limit_one
    maxEntries % 10 in 2..4 && maxEntries % 100 !in 12..14 -> R.string.pass_entries_limit_few
    else -> R.string.pass_entries_limit_many
}

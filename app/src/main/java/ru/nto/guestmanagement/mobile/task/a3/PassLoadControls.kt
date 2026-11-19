package ru.nto.guestmanagement.mobile.task.a3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.nto.guestmanagement.mobile.R
import ru.nto.guestmanagement.mobile.model.PassScreenState

/** Индикатор загрузки, сообщения об ошибке и кнопки управления запросами. */
@Composable
fun PassLoadControls(
    state: PassScreenState,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (state) {
            PassScreenState.InitialLoading -> {
                CircularProgressIndicator(modifier = Modifier.testTag("pass_loading"))
            }

            is PassScreenState.InitialError -> {
                Text(text = state.message, modifier = Modifier.testTag("pass_error"))
                Button(onClick = onRetry, modifier = Modifier.testTag("pass_retry")) {
                    Text(text = stringResource(R.string.action_retry))
                }
            }

            is PassScreenState.Content -> {
                if (state.isRefreshing) {
                    CircularProgressIndicator(modifier = Modifier.testTag("pass_loading"))
                }
                Button(
                    onClick = onRefresh,
                    enabled = !state.isRefreshing,
                    modifier = Modifier.testTag("pass_refresh"),
                ) {
                    Text(text = stringResource(R.string.action_refresh))
                }
                if (state.isOffline && !state.isRefreshing) {
                    Text(text = stringResource(R.string.offline), modifier = Modifier.testTag("pass_offline"))
                }
            }
        }
    }
}

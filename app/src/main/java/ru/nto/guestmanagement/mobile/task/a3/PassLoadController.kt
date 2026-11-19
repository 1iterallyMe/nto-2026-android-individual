package ru.nto.guestmanagement.mobile.task.a3

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.nto.guestmanagement.mobile.data.PassRepository
import ru.nto.guestmanagement.mobile.model.PassData
import ru.nto.guestmanagement.mobile.model.PassScreenState

/** Логика первой загрузки, повтора и обновления пропуска. */
class PassLoadController(
    private val repository: PassRepository,
    private val scope: CoroutineScope,
) {
    private val mutableState = MutableStateFlow<PassScreenState>(PassScreenState.InitialLoading)
    private val lock = Any()
    private var hasStarted = false
    private var requestInProgress = false

    val state: StateFlow<PassScreenState> = mutableState

    fun start() {
        synchronized(lock) {
            if (hasStarted || requestInProgress) return
            hasStarted = true
            requestInProgress = true
        }
        load(previousData = null)
    }

    fun refresh() {
        val previousData = synchronized(lock) {
            val content = mutableState.value as? PassScreenState.Content ?: return
            if (requestInProgress) return
            requestInProgress = true
            mutableState.value = content.copy(isRefreshing = true)
            content.data
        }
        load(previousData = previousData)
    }

    fun retry() {
        synchronized(lock) {
            if (mutableState.value !is PassScreenState.InitialError || requestInProgress) return
            requestInProgress = true
            mutableState.value = PassScreenState.InitialLoading
        }
        load(previousData = null)
    }

    private fun load(previousData: PassData?) {
        scope.launch {
            try {
                val result = repository.load()
                synchronized(lock) {
                    mutableState.value = result.fold(
                        onSuccess = { data ->
                            PassScreenState.Content(data, isRefreshing = false, isOffline = false)
                        },
                        onFailure = { error ->
                            if (previousData != null) {
                                PassScreenState.Content(
                                    data = previousData,
                                    isRefreshing = false,
                                    isOffline = true,
                                )
                            } else {
                                PassScreenState.InitialError(
                                    error.message?.takeIf { it.isNotBlank() }
                                        ?: "Не удалось загрузить пропуск",
                                )
                            }
                        },
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                synchronized(lock) {
                    mutableState.value = if (previousData != null) {
                        PassScreenState.Content(previousData, isRefreshing = false, isOffline = true)
                    } else {
                        PassScreenState.InitialError(
                            error.message?.takeIf { it.isNotBlank() }
                                ?: "Не удалось загрузить пропуск",
                        )
                    }
                }
            } finally {
                synchronized(lock) {
                    requestInProgress = false
                }
            }
        }
    }
}

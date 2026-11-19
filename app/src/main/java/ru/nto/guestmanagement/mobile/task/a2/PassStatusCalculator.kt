package ru.nto.guestmanagement.mobile.task.a2

import ru.nto.guestmanagement.mobile.model.PassDetails
import ru.nto.guestmanagement.mobile.model.PassStatus
import ru.nto.guestmanagement.mobile.model.Scan
import ru.nto.guestmanagement.mobile.model.ScanDirection
import java.time.LocalDateTime

/** Чистая функция: статус пропуска на момент [now] по приоритетам задания. */
fun calculatePassStatus(pass: PassDetails, scans: List<Scan>, now: LocalDateTime): PassStatus {
    if (pass.revokedAt != null) return PassStatus.BLOCKED
    if (now < pass.validFrom) return PassStatus.NOT_STARTED
    if (now > pass.validTo) return PassStatus.EXPIRED

    val entriesUsed = scans.count { it.direction == ScanDirection.ENTRY && it.scannedAt <= now }
    if (entriesUsed >= pass.maxEntries) return PassStatus.EXHAUSTED
    return PassStatus.ACTIVE
}

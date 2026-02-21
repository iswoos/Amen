package com.example.amen.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.amen.domain.entity.StreakInfo
import com.example.amen.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "amen_streak_prefs")

class StreakRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StreakRepository {

    companion object {
        val KEY_STREAK_DAYS = intPreferencesKey("streak_days")
        val KEY_LAST_VISITED_DATE = stringPreferencesKey("last_visited_date")
    }

    override fun getStreakInfo(): Flow<StreakInfo> {
        return context.dataStore.data.map { prefs ->
            StreakInfo(
                currentStreakDays = prefs[KEY_STREAK_DAYS] ?: 0,
                lastVisitedDate = prefs[KEY_LAST_VISITED_DATE]
            )
        }
    }

    override suspend fun recordVisitAndGetStreak(todayDateString: String): StreakInfo {
        val prefs = context.dataStore.data.first()
        val lastVisited = prefs[KEY_LAST_VISITED_DATE]
        var currentStreak = prefs[KEY_STREAK_DAYS] ?: 0

        if (lastVisited == null) {
            currentStreak = 1
        } else if (lastVisited != todayDateString) {
            val today = LocalDate.parse(todayDateString, DateTimeFormatter.ISO_LOCAL_DATE)
            val lastDate = LocalDate.parse(lastVisited, DateTimeFormatter.ISO_LOCAL_DATE)
            val daysBetween = ChronoUnit.DAYS.between(lastDate, today)

            if (daysBetween == 1L) {
                // 어제 오고 오늘 또 온 경우 추가
                currentStreak += 1
            } else if (daysBetween > 1L) {
                // 끊긴 경우 초기화
                currentStreak = 1
            }
        }

        // 갱신 저장
        context.dataStore.edit { preferences ->
            preferences[KEY_STREAK_DAYS] = currentStreak
            preferences[KEY_LAST_VISITED_DATE] = todayDateString
        }

        return StreakInfo(currentStreak, todayDateString)
    }
}

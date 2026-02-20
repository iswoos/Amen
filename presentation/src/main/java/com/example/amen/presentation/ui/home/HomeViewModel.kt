package com.example.amen.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amen.domain.entity.StreakInfo
import com.example.amen.domain.usecase.RecordVisitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recordVisitUseCase: RecordVisitUseCase
) : ViewModel() {

    private val _streakState = MutableStateFlow<StreakInfo?>(null)
    val streakState: StateFlow<StreakInfo?> = _streakState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun recordTodayVisit() {
        viewModelScope.launch {
            val todayString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val info = recordVisitUseCase(todayString)
            _streakState.value = info
        }
    }
}

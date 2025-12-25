package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.dao.FlashcardProgressDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(private val dao: FlashcardProgressDao) : ViewModel() {

    private val _streak = MutableLiveData<Int>(0)
    val streak: LiveData<Int> = _streak

    private val _totalLearned = MutableLiveData<Int>(0)
    val totalLearned: LiveData<Int> = _totalLearned

    fun loadStats() {
        viewModelScope.launch {
            // 1. Calculate Streak
            val dates = dao.getUniqueStudyDates()
            _streak.postValue(calculateStreak(dates))

            // 2. Get Total Learned Words
            _totalLearned.postValue(dao.getTotalKnownWords())
        }
    }

    private fun calculateStreak(dates: List<String>): Int {
        if (dates.isEmpty()) return 0
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()
        val todayStr = sdf.format(today.time)
        
        var streak = 0
        var checkDate = today
        
        // If they haven't studied today, check if they studied yesterday
        if (dates[0] != todayStr) {
            checkDate.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(checkDate.time)
            if (dates[0] != yesterdayStr) return 0
        }

        // Loop back through dates
        for (dateStr in dates) {
            val formattedCheck = sdf.format(checkDate.time)
            if (dateStr == formattedCheck) {
                streak++
                checkDate.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}

package com.example.amen.data.local.util

import android.content.Context
import com.example.amen.data.local.dao.BibleDao
import com.example.amen.data.local.entity.BibleVerseEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleDataSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bibleDao: BibleDao
) {
    suspend fun seedIfNeeded() {
        withContext(Dispatchers.IO) {
            val count = bibleDao.getTotalVersesCount()
            // 데이터가 아예 없거나, 이전 샘플 데이터만 있는 경우(8개 미만) 재시딩 수행
            // 성경 전권(약 3.1만 절) 시딩을 위해 임계값을 대폭 상향합니다.
            if (count < 30000) {
                try {
                    if (count > 0) {
                        bibleDao.deleteAllVerses()
                    }
                    val jsonString = context.assets.open("bible_data.json")
                        .bufferedReader()
                        .use { it.readText() }
                    
                    val listType = object : TypeToken<List<BibleVerseEntity>>() {}.type
                    val verses: List<BibleVerseEntity> = Gson().fromJson(jsonString, listType)
                    
                    bibleDao.insertAll(verses)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

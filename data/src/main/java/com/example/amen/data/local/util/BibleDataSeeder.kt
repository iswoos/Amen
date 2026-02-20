package com.example.amen.data.local.util

import android.content.Context
import com.example.amen.data.local.dao.BibleDao
import com.example.amen.data.local.entity.BibleVerseEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject

class BibleDataSeeder @Inject constructor(
    private val bibleDao: BibleDao,
    private val context: Context,
    private val gson: Gson
) {
    suspend fun seedDatabaseIfNeeded() {
        withContext(Dispatchers.IO) {
            val count = bibleDao.getTotalVersesCount()
            // DB가 비어있을 때만 assets/bible_krv.json 파일을 읽어서 저장
            if (count == 0) {
                try {
                    val inputStream = context.assets.open("bible_krv.json")
                    val reader = InputStreamReader(inputStream)
                    val listType = object : TypeToken<List<BibleVerseEntity>>() {}.type
                    
                    // JSON -> List<BibleVerseEntity> 파싱
                    val verses: List<BibleVerseEntity> = gson.fromJson(reader, listType)
                    
                    // Room Database 대량 삽입
                    bibleDao.insertAll(verses)
                    reader.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // 실제 환경에서는 로그에 에러를 기록해야 합니다.
                }
            }
        }
    }
}

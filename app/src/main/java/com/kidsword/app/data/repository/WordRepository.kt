package com.kidsword.app.data.repository

import com.kidsword.app.data.database.AppDatabase
import com.kidsword.app.data.model.LearningProgress
import com.kidsword.app.data.model.Word
import com.kidsword.app.data.model.WrongRecord
import kotlinx.coroutines.flow.Flow

class WordRepository(private val database: AppDatabase) {
    
    private val wordDao = database.wordDao()
    private val progressDao = database.progressDao()
    private val wrongRecordDao = database.wrongRecordDao()
    
    // Word operations
    fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()
    
    fun getWordsByLevel(level: Int): Flow<List<Word>> = wordDao.getWordsByLevel(level)
    
    suspend fun getUnlearnedWords(limit: Int): List<Word> = wordDao.getUnlearnedWords(limit)
    
    fun getMasteredWords(): Flow<List<Word>> = wordDao.getMasteredWords()
    
    fun getWrongWords(): Flow<List<Word>> = wordDao.getWrongWords()
    
    suspend fun getWordById(id: Long): Word? = wordDao.getWordById(id)
    
    suspend fun insertWords(words: List<Word>) = wordDao.insertWords(words)
    
    suspend fun markAsLearned(id: Long) {
        wordDao.markAsLearned(id)
        updateLearnedCount()
    }
    
    suspend fun markAsMastered(id: Long) {
        wordDao.markAsMastered(id)
        updateMasteredCount()
    }
    
    suspend fun incrementWrongCount(id: Long) {
        wordDao.incrementWrongCount(id)
    }
    
    private suspend fun updateLearnedCount() {
        val count = wordDao.getLearnedCount()
        progressDao.updateLearnedCount(count)
    }
    
    private suspend fun updateMasteredCount() {
        val count = wordDao.getMasteredCount()
        progressDao.updateMasteredCount(count)
    }
    
    // Progress operations
    fun getProgress(): Flow<LearningProgress?> = progressDao.getProgress()
    
    suspend fun initProgress(totalWords: Int) {
        val existing = progressDao.getProgressSync()
        if (existing == null) {
            progressDao.insertProgress(LearningProgress(totalWords = totalWords))
        }
    }
    
    suspend fun addQuizScore(score: Int) {
        progressDao.addQuizScore(score)
    }
    
    suspend fun updateCurrentLevel(level: Int) {
        progressDao.updateCurrentLevel(level)
    }
    
    suspend fun incrementChallengePassed() {
        progressDao.incrementChallengePassed()
    }
    
    // Wrong record operations
    fun getAllWrongRecords(): Flow<List<WrongRecord>> = wrongRecordDao.getAllWrongRecords()
    
    suspend fun addWrongRecord(wordId: Long, english: String, chinese: String, quizType: String) {
        wrongRecordDao.insertWrongRecord(
            WrongRecord(
                wordId = wordId,
                wordEnglish = english,
                wordChinese = chinese,
                wrongTime = System.currentTimeMillis(),
                quizType = quizType
            )
        )
    }
    
    suspend fun clearAllWrongRecords() {
        wrongRecordDao.clearAllWrongRecords()
    }
}

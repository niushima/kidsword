package com.kidsword.app.data.database

import androidx.room.*
import com.kidsword.app.data.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    
    @Query("SELECT * FROM words ORDER BY id")
    fun getAllWords(): Flow<List<Word>>
    
    @Query("SELECT * FROM words WHERE level = :level ORDER BY id")
    fun getWordsByLevel(level: Int): Flow<List<Word>>
    
    @Query("SELECT * FROM words WHERE isLearned = 0 ORDER BY id LIMIT :limit")
    suspend fun getUnlearnedWords(limit: Int): List<Word>
    
    @Query("SELECT * FROM words WHERE isMastered = 1 ORDER BY id")
    fun getMasteredWords(): Flow<List<Word>>
    
    @Query("SELECT * FROM words WHERE wrongCount > 0 ORDER BY wrongCount DESC, lastReviewTime ASC")
    fun getWrongWords(): Flow<List<Word>>
    
    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?
    
    @Query("SELECT * FROM words WHERE category = :category ORDER BY id")
    fun getWordsByCategory(category: String): Flow<List<Word>>
    
    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE isLearned = 1")
    suspend fun getLearnedCount(): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE isMastered = 1")
    suspend fun getMasteredCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>)
    
    @Update
    suspend fun updateWord(word: Word)
    
    @Query("UPDATE words SET isLearned = 1, lastReviewTime = :time WHERE id = :id")
    suspend fun markAsLearned(id: Long, time: Long = System.currentTimeMillis())
    
    @Query("UPDATE words SET isMastered = 1 WHERE id = :id")
    suspend fun markAsMastered(id: Long)
    
    @Query("UPDATE words SET wrongCount = wrongCount + 1, lastReviewTime = :time WHERE id = :id")
    suspend fun incrementWrongCount(id: Long, time: Long = System.currentTimeMillis())
    
    @Query("DELETE FROM words")
    suspend fun deleteAllWords()
}

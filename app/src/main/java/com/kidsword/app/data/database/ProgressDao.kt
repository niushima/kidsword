package com.kidsword.app.data.database

import android.content.Context
import androidx.room.*
import com.kidsword.app.data.model.LearningProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    
    @Query("SELECT * FROM progress WHERE id = 1")
    fun getProgress(): Flow<LearningProgress?>
    
    @Query("SELECT * FROM progress WHERE id = 1")
    suspend fun getProgressSync(): LearningProgress?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LearningProgress)
    
    @Update
    suspend fun updateProgress(progress: LearningProgress)
    
    @Query("UPDATE progress SET learnedWords = :count, lastStudyTime = :time WHERE id = 1")
    suspend fun updateLearnedCount(count: Int, time: Long = System.currentTimeMillis())
    
    @Query("UPDATE progress SET masteredWords = :count WHERE id = 1")
    suspend fun updateMasteredCount(count: Int)
    
    @Query("UPDATE progress SET currentLevel = :level WHERE id = 1")
    suspend fun updateCurrentLevel(level: Int)
    
    @Query("UPDATE progress SET totalQuizScore = totalQuizScore + :score, totalQuizCount = totalQuizCount + 1 WHERE id = 1")
    suspend fun addQuizScore(score: Int)
    
    @Query("UPDATE progress SET totalChallengePassed = totalChallengePassed + 1 WHERE id = 1")
    suspend fun incrementChallengePassed()
}

package com.kidsword.app.data.database

import android.content.Context
import androidx.room.*
import com.kidsword.app.data.model.WrongRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface WrongRecordDao {
    
    @Query("SELECT * FROM wrong_records ORDER BY wrongTime DESC")
    fun getAllWrongRecords(): Flow<List<WrongRecord>>
    
    @Query("SELECT * FROM wrong_records WHERE wordId = :wordId ORDER BY wrongTime DESC")
    suspend fun getWrongRecordsByWord(wordId: Long): List<WrongRecord>
    
    @Query("SELECT DISTINCT wordId FROM wrong_records")
    suspend fun getWrongWordIds(): List<Long>
    
    @Query("SELECT COUNT(*) FROM wrong_records WHERE wordId = :wordId")
    suspend fun getWrongCountForWord(wordId: Long): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrongRecord(record: WrongRecord)
    
    @Query("DELETE FROM wrong_records WHERE wordId = :wordId")
    suspend fun deleteWrongRecordsByWord(wordId: Long)
    
    @Query("DELETE FROM wrong_records")
    suspend fun clearAllWrongRecords()
    
    @Query("DELETE FROM wrong_records WHERE id IN (SELECT id FROM wrong_records GROUP BY wordId HAVING COUNT(*) <= 3)")
    suspend fun clearEasyWrongRecords()
}

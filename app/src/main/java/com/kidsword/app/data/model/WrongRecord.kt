package com.kidsword.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 错题记录实体类
 */
@Entity(tableName = "wrong_records")
data class WrongRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,               // 单词ID
    val wordEnglish: String,        // 单词英文
    val wordChinese: String,        // 单词中文
    val wrongTime: Long,           // 错误时间
    val quizType: String           // 测验类型
)

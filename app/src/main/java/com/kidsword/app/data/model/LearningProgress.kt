package com.kidsword.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 学习进度实体类
 */
@Entity(tableName = "progress")
data class LearningProgress(
    @PrimaryKey
    val id: Int = 1,               // 固定ID，只有单条记录
    val totalWords: Int = 0,       // 总单词数
    val learnedWords: Int = 0,     // 已学习单词数
    val masteredWords: Int = 0,    // 已掌握单词数
    val totalQuizScore: Int = 0,   // 总测验得分
    val totalQuizCount: Int = 0,   // 总测验次数
    val currentLevel: Int = 1,     // 当前关卡
    val totalChallengePassed: Int = 0, // 已通关数
    val lastStudyTime: Long = 0    // 最后学习时间
)

package com.kidsword.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 单词实体类
 */
@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val english: String,           // 英文单词
    val chinese: String,           // 中文翻译
    val phonetic: String,          // 发音音标
    val category: String,          // 分类（如：动物、颜色、食物等）
    val level: Int = 1,             // 难度等级 1-6
    val isLearned: Boolean = false, // 是否已学习
    val isMastered: Boolean = false, // 是否已掌握
    val wrongCount: Int = 0,       // 错误次数
    val lastReviewTime: Long = 0    // 上次复习时间
)

package com.kidsword.app.ui.progress

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kidsword.app.data.database.AppDatabase
import com.kidsword.app.data.repository.WordRepository
import com.kidsword.app.databinding.ActivityProgressBinding
import com.kidsword.app.util.PreferencesHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProgressActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProgressBinding
    private lateinit var repository: WordRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val database = AppDatabase.getDatabase(this)
        repository = WordRepository(database)
        
        binding.btnBack.setOnClickListener { finish() }
        
        loadProgress()
    }
    
    private fun loadProgress() {
        lifecycleScope.launch {
            repository.getProgress().collectLatest { progress ->
                if (progress != null) {
                    // 计算掌握率
                    val masterRate = if (progress.totalWords > 0) {
                        (progress.masteredWords.toFloat() / progress.totalWords * 100).toInt()
                    } else 0
                    
                    binding.tvMasterRate.text = "${masterRate}%"
                    binding.progressTotal.progress = masterRate
                    
                    binding.tvTotalWords.text = progress.totalWords.toString()
                    binding.tvLearnedWords.text = progress.learnedWords.toString()
                    binding.tvMasteredWords.text = "${progress.masteredWords} 个单词"
                    
                    // 测验统计
                    binding.tvQuizCount.text = "${progress.totalQuizCount} 次"
                    val avgScore = if (progress.totalQuizCount > 0) {
                        progress.totalQuizScore / progress.totalQuizCount
                    } else 0
                    binding.tvAvgScore.text = "$avgScore 分"
                    
                    // 闯关统计
                    binding.tvCurrentLevel.text = "第 ${progress.currentLevel} 关"
                    binding.tvChallengePassed.text = "${progress.totalChallengePassed} 关"
                }
            }
        }
    }
}

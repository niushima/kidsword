package com.kidsword.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kidsword.app.data.database.AppDatabase
import com.kidsword.app.data.repository.WordRepository
import com.kidsword.app.databinding.ActivityMainBinding
import com.kidsword.app.ui.progress.ProgressActivity
import com.kidsword.app.ui.quiz.QuizActivity
import com.kidsword.app.ui.study.StudyActivity
import com.kidsword.app.ui.wrong.WrongBookActivity
import com.kidsword.app.ui.challenge.ChallengeActivity
import com.kidsword.app.ui.import.ImportActivity
import com.kidsword.app.ui.challenge.ChallengeActivity
import com.kidsword.app.util.InitialDataProvider
import com.kidsword.app.util.PreferencesHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: WordRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val database = AppDatabase.getDatabase(this)
        repository = WordRepository(database)
        
        // 初始化数据（首次启动）
        initData()
        
        setupClickListeners()
    }
    
    private fun initData() {
        if (PreferencesHelper.isFirstLaunch(this)) {
            lifecycleScope.launch {
                // 插入初始单词数据
                val words = InitialDataProvider.getInitialWords()
                repository.insertWords(words)
                repository.initProgress(words.size)
                PreferencesHelper.setFirstLaunchComplete(this@MainActivity)
                Toast.makeText(this@MainActivity, "已加载 ${words.size} 个初始单词", Toast.LENGTH_SHORT).show()
            }
        } else {
            lifecycleScope.launch {
                repository.initProgress(repository.getUnlearnedWords(Int.MAX_VALUE).size)
            }
        }
    }
    
    private fun setupClickListeners() {
        // 学习单词
        binding.cardStudy.setOnClickListener {
            startActivity(Intent(this, StudyActivity::class.java))
        }
        
        // 开始测验
        binding.cardQuiz.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }
        
        // 闯关模式
        binding.cardChallenge.setOnClickListener {
            startActivity(Intent(this, ChallengeActivity::class.java))
        }
        
        // 学习进度
        binding.cardProgress.setOnClickListener {
            startActivity(Intent(this, ProgressActivity::class.java))
        }
        
        // 错题本
        binding.cardWrongBook.setOnClickListener {
            startActivity(Intent(this, WrongBookActivity::class.java))
        }
        
        // 批量导入
        binding.cardImport.setOnClickListener {
            startActivity(Intent(this, ImportActivity::class.java))
        }
    }
}

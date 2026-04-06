package com.kidsword.app.ui.study

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kidsword.app.R
import com.kidsword.app.data.database.AppDatabase
import com.kidsword.app.data.model.Word
import com.kidsword.app.data.repository.WordRepository
import com.kidsword.app.databinding.ActivityStudyBinding
import com.kidsword.app.util.SpeechHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StudyActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStudyBinding
    private lateinit var repository: WordRepository
    private lateinit var speechHelper: SpeechHelper
    
    private var words = listOf<Word>()
    private var currentIndex = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val database = AppDatabase.getDatabase(this)
        repository = WordRepository(database)
        speechHelper = SpeechHelper(this)
        
        loadWords()
        setupClickListeners()
    }
    
    private fun loadWords() {
        lifecycleScope.launch {
            words = repository.getAllWords().first()
            if (words.isNotEmpty()) {
                showWord(words[currentIndex])
            } else {
                Toast.makeText(this@StudyActivity, "没有单词数据，请先导入", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    private fun showWord(word: Word) {
        binding.tvWord.text = word.english
        binding.tvPhonetic.text = word.phonetic
        binding.tvChinese.text = word.chinese
        binding.tvProgress.text = "${currentIndex + 1} / ${words.size}"
        
        // 自动发音
        speechHelper.speakWithRate(word.english)
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnSpeak.setOnClickListener {
            if (words.isNotEmpty()) {
                speechHelper.speakWithRate(words[currentIndex].english)
            }
        }
        
        binding.btnPrevious.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                showWord(words[currentIndex])
            }
        }
        
        binding.btnNext.setOnClickListener {
            if (currentIndex < words.size - 1) {
                currentIndex++
                showWord(words[currentIndex])
            }
        }
        
        // 认识了
        binding.btnKnown.setOnClickListener {
            lifecycleScope.launch {
                if (words.isNotEmpty()) {
                    repository.markAsLearned(words[currentIndex].id)
                    Toast.makeText(this@StudyActivity, "太棒了！", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // 不认识
        binding.btnUnknown.setOnClickListener {
            lifecycleScope.launch {
                if (words.isNotEmpty()) {
                    val word = words[currentIndex]
                    repository.incrementWrongCount(word.id)
                    Toast.makeText(this@StudyActivity, "继续加油！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechHelper.shutdown()
    }
}

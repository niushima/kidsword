package com.kidsword.app.ui.quiz

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.kidsword.app.R
import com.kidsword.app.data.database.AppDatabase
import com.kidsword.app.data.model.Word
import com.kidsword.app.data.repository.WordRepository
import com.kidsword.app.databinding.ActivityQuizBinding
import com.kidsword.app.util.SpeechHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityQuizBinding
    private lateinit var repository: WordRepository
    private lateinit var speechHelper: SpeechHelper
    
    private var allWords = listOf<Word>()
    private var quizWords = mutableListOf<Word>()
    private var currentIndex = 0
    private var score = 0
    private var selectedAnswer: String? = null
    private var isAnswered = false
    
    private val optionButtons: List<MaterialButton> by lazy {
        listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val database = AppDatabase.getDatabase(this)
        repository = WordRepository(database)
        speechHelper = SpeechHelper(this)
        
        loadQuizData()
        setupClickListeners()
    }
    
    private fun loadQuizData() {
        lifecycleScope.launch {
            allWords = repository.getAllWords().first()
            if (allWords.size >= 4) {
                // 随机选10个单词
                quizWords = allWords.shuffled().take(10).toMutableList()
                showQuestion()
            } else {
                binding.tvQuestion.text = "单词数不足"
                optionButtons.forEach { it.isEnabled = false }
            }
        }
    }
    
    private fun showQuestion() {
        if (currentIndex >= quizWords.size) {
            showFinalResult()
            return
        }
        
        isAnswered = false
        selectedAnswer = null
        binding.layoutResult.visibility = View.GONE
        
        val currentWord = quizWords[currentIndex]
        binding.tvQuestion.text = currentWord.english
        binding.tvScore.text = "$score/$currentIndex"
        
        // 生成选项（1个正确 + 3个随机）
        val options = mutableSetOf(currentWord.chinese)
        while (options.size < 4) {
            val randomWord = allWords.random()
            if (randomWord.id != currentWord.id) {
                options.add(randomWord.chinese)
            }
        }
        
        // 打乱选项
        val shuffledOptions = options.shuffled()
        optionButtons.forEachIndexed { index, button ->
            button.text = shuffledOptions[index]
            button.isEnabled = true
            button.setBackgroundColor(getColor(R.color.option_default))
        }
    }
    
    private fun selectAnswer(button: MaterialButton) {
        if (isAnswered) return
        
        selectedAnswer = button.text.toString()
        isAnswered = true
        
        val currentWord = quizWords[currentIndex]
        val isCorrect = button.text == currentWord.chinese
        
        // 显示结果
        optionButtons.forEach { btn ->
            btn.isEnabled = false
            when {
                btn.text == currentWord.chinese -> {
                    btn.setBackgroundColor(getColor(R.color.option_correct))
                }
                btn == button && !isCorrect -> {
                    btn.setBackgroundColor(getColor(R.color.option_wrong))
                }
            }
        }
        
        // 记录结果
        lifecycleScope.launch {
            if (isCorrect) {
                score++
                binding.tvResult.text = "回答正确！✓"
                binding.tvResult.setTextColor(getColor(R.color.success))
            } else {
                repository.addWrongRecord(
                    currentWord.id,
                    currentWord.english,
                    currentWord.chinese,
                    "quiz"
                )
                repository.incrementWrongCount(currentWord.id)
                binding.tvResult.text = "正确答案是：${currentWord.chinese}"
                binding.tvResult.setTextColor(getColor(R.color.error))
            }
            binding.tvScore.text = "$score/${currentIndex + 1}"
        }
        
        binding.layoutResult.visibility = View.VISIBLE
    }
    
    private fun showFinalResult() {
        binding.tvTitle.text = "测验完成！"
        binding.tvQuestion.text = ""
        optionButtons.forEach { it.visibility = View.GONE }
        
        lifecycleScope.launch {
            repository.addQuizScore(score)
            
            binding.layoutResult.visibility = View.VISIBLE
            binding.tvResult.text = "最终得分：$score / ${quizWords.size}"
            binding.tvResult.setTextColor(getColor(R.color.primary))
            
            binding.btnNextQuestion.text = "再来一次"
            binding.btnNextQuestion.setOnClickListener {
                currentIndex = 0
                score = 0
                quizWords = allWords.shuffled().take(10).toMutableList()
                optionButtons.forEach { it.visibility = View.VISIBLE }
                binding.tvTitle.text = "开始测验"
                showQuestion()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnSpeaker.setOnClickListener {
            if (quizWords.isNotEmpty() && currentIndex < quizWords.size) {
                speechHelper.speakWithRate(quizWords[currentIndex].english)
            }
        }
        
        optionButtons.forEach { button ->
            button.setOnClickListener { selectAnswer(button) }
        }
        
        binding.btnNextQuestion.setOnClickListener {
            currentIndex++
            showQuestion()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechHelper.shutdown()
    }
}

package com.kidsword.app.ui.challenge

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.kidsword.app.R
import com.kidsword.app.data.database.AppDatabase
import com.kidsword.app.data.model.Word
import com.kidsword.app.data.repository.WordRepository
import com.kidsword.app.databinding.ActivityChallengeBinding
import com.kidsword.app.util.PreferencesHelper
import com.kidsword.app.util.SpeechHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChallengeActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChallengeBinding
    private lateinit var repository: WordRepository
    private lateinit var speechHelper: SpeechHelper
    
    private var allWords = listOf<Word>()
    private var levelWords = mutableListOf<Word>()
    private var currentLevel = 1
    private var currentIndex = 0
    private var correctCount = 0
    
    // Letter game state
    private var shuffledLetters = listOf<Char>()
    private var selectedLetters = mutableListOf<Char>()
    private var letterButtons = mutableListOf<MaterialButton>()
    private var currentWord: Word? = null
    
    companion object {
        private const val WORDS_PER_LEVEL = 10
        private const val PASS_THRESHOLD = 7
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val database = AppDatabase.getDatabase(this)
        repository = WordRepository(database)
        speechHelper = SpeechHelper(this)
        
        currentLevel = PreferencesHelper.getCurrentLevel(this)
        loadLevelData()
        setupClickListeners()
    }
    
    private fun loadLevelData() {
        lifecycleScope.launch {
            allWords = repository.getAllWords().first()
            if (allWords.isNotEmpty()) {
                // 获取当前关卡的单词
                val levelWordsList = allWords.filter { it.level == currentLevel }
                levelWords = if (levelWordsList.size >= WORDS_PER_LEVEL) {
                    levelWordsList.shuffled().take(WORDS_PER_LEVEL).toMutableList()
                } else {
                    // 如果当前等级单词不够，从所有单词中补
                    (levelWordsList + allWords.shuffled()).distinctBy { it.id }.take(WORDS_PER_LEVEL).toMutableList()
                }
                binding.tvLevel.text = "第${currentLevel}关"
                showQuestion()
            } else {
                binding.tvChinese.text = "没有单词数据"
            }
        }
    }
    
    private fun showQuestion() {
        if (currentIndex >= levelWords.size) {
            showLevelResult()
            return
        }
        
        currentWord = levelWords[currentIndex]
        binding.tvChinese.text = currentWord!!.chinese
        binding.tvProgressCount.text = "${currentIndex + 1}/$WORDS_PER_LEVEL"
        binding.progressBar.progress = currentIndex + 1
        
        // 清空选择
        selectedLetters.clear()
        
        // 生成打乱的字母
        val word = currentWord!!.english.lowercase()
        shuffledLetters = word.toList().shuffled()
        
        // 创建字母按钮
        createLetterButtons()
        
        // 创建答案槽
        createAnswerSlots()
        
        // 隐藏结果
        binding.layoutResult.visibility = View.GONE
    }
    
    private fun createLetterButtons() {
        binding.layoutLetters.removeAllViews()
        letterButtons.clear()
        
        val params = LinearLayout.LayoutParams(
            dpToPx(50),
            dpToPx(50)
        ).apply {
            setMargins(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
        }
        
        shuffledLetters.forEachIndexed { index, letter ->
            val button = MaterialButton(this).apply {
                text = letter.uppercase()
                textSize = 22f
                setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.option_default)
                cornerRadius = dpToPx(10)
                layoutParams = params
                setOnClickListener { onLetterClicked(index) }
            }
            letterButtons.add(button)
            binding.layoutLetters.addView(button)
        }
    }
    
    private fun createAnswerSlots() {
        binding.layoutAnswerSlots.removeAllViews()
        
        currentWord?.english?.forEachIndexed { index, _ ->
            // 答案槽
            val slot = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(45), dpToPx(55)).apply {
                    setMargins(dpToPx(3), 0, dpToPx(3), 0)
                }
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                background = ContextCompat.getDrawable(context, R.drawable.bg_answer_slot)
            }
            
            // 字母显示
            val letterView = androidx.appcompat.widget.AppCompatTextView(this).apply {
                id = View.generateViewId()
                textSize = 28f
                gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                tag = "letter_$index"
            }
            
            // 删除按钮
            val deleteBtn = MaterialButton(this, null, 0).apply {
                id = View.generateViewId()
                layoutParams = LinearLayout.LayoutParams(dpToPx(30), dpToPx(20)).apply {
                    topMargin = dpToPx(2)
                }
                text = "×"
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.error))
                backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
                setPadding(0, 0, 0, 0)
                minWidth = 0
                minHeight = 0
                insetTop = 0
                insetBottom = 0
                tag = "delete_$index"
                setOnClickListener { onSlotClicked(index) }
            }
            
            slot.addView(letterView)
            slot.addView(deleteBtn)
            binding.layoutAnswerSlots.addView(slot)
        }
        
        // 添加空格分隔
        updateAnswerDisplay()
    }
    
    private fun onLetterClicked(buttonIndex: Int) {
        if (selectedLetters.size >= currentWord!!.english.length) return
        
        val letter = shuffledLetters[buttonIndex]
        selectedLetters.add(letter)
        
        // 禁用该按钮
        letterButtons[buttonIndex].isEnabled = false
        letterButtons[buttonIndex].alpha = 0.3f
        
        updateAnswerDisplay()
        
        // 如果填满了，自动提交
        if (selectedLetters.size == currentWord!!.english.length) {
            checkAnswer()
        }
    }
    
    private fun onSlotClicked(slotIndex: Int) {
        if (slotIndex >= selectedLetters.size) return
        
        // 把这个位置之后的字母移回去
        val removedLetter = selectedLetters.removeAt(slotIndex)
        
        // 找到对应的按钮并恢复
        val buttonIndex = shuffledLetters.indexOf(removedLetter)
        letterButtons[buttonIndex].isEnabled = true
        letterButtons[buttonIndex].alpha = 1f
        
        updateAnswerDisplay()
    }
    
    private fun updateAnswerDisplay() {
        currentWord?.english?.forEachIndexed { index, _ ->
            val slot = binding.layoutAnswerSlots.getChildAt(index)
            val letterView = slot.findViewWithTag<androidx.appcompat.widget.AppCompatTextView>("letter_$index")
            
            if (index < selectedLetters.size) {
                letterView.text = selectedLetters[index].uppercase()
                letterView.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            } else {
                letterView.text = "_"
                letterView.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
            }
        }
    }
    
    private fun checkAnswer() {
        val userAnswer = selectedLetters.joinToString("").lowercase()
        val correctAnswer = currentWord!!.english.lowercase()
        
        lifecycleScope.launch {
            if (userAnswer == correctAnswer) {
                correctCount++
                showResult(true, correctAnswer)
            } else {
                // 记录错题
                repository.addWrongRecord(
                    currentWord!!.id,
                    currentWord!!.english,
                    currentWord!!.chinese,
                    "challenge"
                )
                repository.incrementWrongCount(currentWord!!.id)
                showResult(false, correctAnswer)
            }
        }
    }
    
    private fun showResult(isCorrect: Boolean, correctWord: String) {
        binding.layoutResult.visibility = View.VISIBLE
        
        if (isCorrect) {
            binding.tvResultEmoji.text = "🎉"
            binding.tvResultText.text = "太棒了！"
            binding.tvResultText.setTextColor(ContextCompat.getColor(this, R.color.success))
            binding.tvResultWord.text = correctWord
            binding.tvResultWord.setTextColor(ContextCompat.getColor(this, R.color.success))
        } else {
            binding.tvResultEmoji.text = "😢"
            binding.tvResultText.text = "答错了！"
            binding.tvResultText.setTextColor(ContextCompat.getColor(this, R.color.error))
            binding.tvResultWord.text = "正确答案：$correctWord"
            binding.tvResultWord.setTextColor(ContextCompat.getColor(this, R.color.secondary))
        }
    }
    
    private fun showLevelResult() {
        binding.layoutResult.visibility = View.VISIBLE
        
        if (correctCount >= PASS_THRESHOLD) {
            binding.tvResultEmoji.text = "🏆"
            binding.tvResultText.text = "🎊 恭喜过关！\n得分：$correctCount / $WORDS_PER_LEVEL"
            binding.tvResultText.setTextColor(ContextCompat.getColor(this, R.color.success))
            binding.tvResultWord.text = "准备进入第 ${currentLevel + 1} 关"
            binding.tvResultWord.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            binding.btnContinue.text = "下一关 →"
            
            lifecycleScope.launch {
                currentLevel++
                PreferencesHelper.setCurrentLevel(this@ChallengeActivity, currentLevel)
                repository.incrementChallengePassed()
                repository.updateCurrentLevel(currentLevel)
            }
        } else {
            binding.tvResultEmoji.text = "💪"
            binding.tvResultText.text = "😔 挑战失败\n得分：$correctCount / $WORDS_PER_LEVEL\n需要答对 $PASS_THRESHOLD 题才能过关"
            binding.tvResultText.setTextColor(ContextCompat.getColor(this, R.color.error))
            binding.tvResultWord.text = ""
            binding.btnContinue.text = "再试一次"
            
            // 重置当前关卡
            currentIndex = 0
            correctCount = 0
            levelWords = levelWords.shuffled().toMutableList()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnSpeaker.setOnClickListener {
            currentWord?.let {
                speechHelper.speakWithRate(it.english)
            }
        }
        
        binding.btnClear.setOnClickListener {
            // 把所有选中的字母移回去
            selectedLetters.forEach { letter ->
                val buttonIndex = shuffledLetters.indexOf(letter)
                letterButtons[buttonIndex].isEnabled = true
                letterButtons[buttonIndex].alpha = 1f
            }
            selectedLetters.clear()
            updateAnswerDisplay()
        }
        
        binding.btnSubmit.setOnClickListener {
            if (selectedLetters.size == currentWord?.english?.length) {
                checkAnswer()
            }
        }
        
        binding.btnContinue.setOnClickListener {
            // 检查是否需要重置关卡
            if (correctCount == 0 && binding.tvResultText.text.toString().contains("失败")) {
                // 失败后重试
            } else {
                // 下一题
                currentIndex++
            }
            binding.layoutResult.visibility = View.GONE
            showQuestion()
        }
    }
    
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechHelper.shutdown()
    }
}

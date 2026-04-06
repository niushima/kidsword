package com.kidsword.app.ui.import

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kidsword.app.R
import com.kidsword.app.data.database.AppDatabase
import com.kidsword.app.data.model.Word
import com.kidsword.app.data.repository.WordRepository
import com.kidsword.app.databinding.ActivityImportBinding
import kotlinx.coroutines.launch

class ImportActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityImportBinding
    private lateinit var repository: WordRepository
    private var parsedWords = mutableListOf<Word>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val database = AppDatabase.getDatabase(this)
        repository = WordRepository(database)
        
        setupClickListeners()
        setupTextWatcher()
        setupQuickTemplates()
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnPreview.setOnClickListener {
            parseAndPreview()
        }
        
        binding.btnClear.setOnClickListener {
            binding.etInput.text?.clear()
            binding.cardPreview.visibility = View.GONE
            parsedWords.clear()
        }
        
        binding.btnImport.setOnClickListener {
            importWords()
        }
    }
    
    private fun setupTextWatcher() {
        binding.etInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // 自动预览
                if (!s.isNullOrBlank() && s.contains("\n")) {
                    parseAndPreview()
                }
            }
        })
    }
    
    private fun setupQuickTemplates() {
        binding.chipAnimals.setOnClickListener {
            pasteTemplate(getAnimalsTemplate())
        }
        binding.chipFruits.setOnClickListener {
            pasteTemplate(getFruitsTemplate())
        }
        binding.chipColors.setOnClickListener {
            pasteTemplate(getColorsTemplate())
        }
        binding.chipNumbers.setOnClickListener {
            pasteTemplate(getNumbersTemplate())
        }
        binding.chipFamily.setOnClickListener {
            pasteTemplate(getFamilyTemplate())
        }
        binding.chipBody.setOnClickListener {
            pasteTemplate(getBodyTemplate())
        }
    }
    
    private fun pasteTemplate(template: String) {
        val current = binding.etInput.text?.toString() ?: ""
        val newText = if (current.isBlank()) template else "$current\n$template"
        binding.etInput.setText(newText)
        Toast.makeText(this, "已粘贴模板，请编辑后导入", Toast.LENGTH_SHORT).show()
    }
    
    private fun parseAndPreview() {
        parsedWords.clear()
        val input = binding.etInput.text?.toString() ?: return
        
        if (input.isBlank()) {
            binding.cardPreview.visibility = View.GONE
            return
        }
        
        val lines = input.split("\n").filter { it.isNotBlank() }
        val errors = mutableListOf<String>()
        
        lines.forEachIndexed { index, line ->
            try {
                val parts = line.split(",").map { it.trim() }
                if (parts.size >= 2) {
                    val english = parts[0]
                    val chinese = parts[1]
                    val phonetic = parts.getOrNull(2)?.takeIf { it.isNotBlank() } ?: ""
                    val category = parts.getOrNull(3)?.takeIf { it.isNotBlank() } ?: "未分类"
                    val level = parts.getOrNull(4)?.toIntOrNull() ?: 1
                    
                    parsedWords.add(Word(
                        english = english,
                        chinese = chinese,
                        phonetic = phonetic,
                        category = category,
                        level = level
                    ))
                } else if (parts.size == 1 && parts[0].isNotBlank()) {
                    errors.add("第${index + 1}行：缺少中文翻译")
                }
            } catch (e: Exception) {
                errors.add("第${index + 1}行：格式错误")
            }
        }
        
        // 显示预览
        binding.cardPreview.visibility = View.VISIBLE
        val previewText = buildString {
            append("解析到 ${parsedWords.size} 个单词\n\n")
            parsedWords.take(5).forEach { word ->
                append("• ${word.english} = ${word.chinese}\n")
            }
            if (parsedWords.size > 5) {
                append("... 还有 ${parsedWords.size - 5} 个\n")
            }
            if (errors.isNotEmpty()) {
                append("\n⚠️ 警告：\n")
                errors.take(3).forEach { append("$it\n") }
                if (errors.size > 3) {
                    append("... 还有 ${errors.size - 3} 个错误")
                }
            }
        }
        binding.tvPreview.text = previewText
        binding.tvPreview.parent.javaClass.getDeclaredMethod("invalidate").apply {
            isAccessible = true
            invoke(binding.cardPreview)
        }
    }
    
    private fun importWords() {
        if (parsedWords.isEmpty()) {
            parseAndPreview()
        }
        
        if (parsedWords.isEmpty()) {
            Toast.makeText(this, "没有可导入的单词", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            repository.insertWords(parsedWords)
            
            // 更新进度
            val totalCount = parsedWords.size
            repository.initProgress(totalCount)
            
            Toast.makeText(this@ImportActivity, "成功导入 ${parsedWords.size} 个单词！", Toast.LENGTH_LONG).show()
            
            // 清空输入
            binding.etInput.text?.clear()
            parsedWords.clear()
            binding.cardPreview.visibility = View.GONE
        }
    }
    
    // ========== 快速模板 ==========
    
    private fun getAnimalsTemplate() = """
lion,狮子,/laɪən/,动物,1
tiger,老虎,/ˈtaɪɡə/,动物,1
elephant,大象,/ˈelɪfənt/,动物,1
monkey,猴子,/ˈmʌŋki/,动物,1
rabbit,兔子,/ˈræbɪt/,动物,1
panda,熊猫,/ˈpændə/,动物,1
bear,熊,/beə/,动物,1
giraffe,长颈鹿,/dʒəˈrɑːf/,动物,1
zebra,斑马,/ˈziːbrə/,动物,2
kangaroo,袋鼠,/ˌkæŋɡəˈruː/,动物,2
    """.trimIndent()
    
    private fun getFruitsTemplate() = """
strawberry,草莓,/ˈstrɔːbəri/,水果,1
grape,葡萄,/ɡreɪp/,水果,2
watermelon,西瓜,/ˈwɔːtəmelən/,水果,2
peach,桃子,/piːtʃ/,水果,2
pear,梨,/peə/,水果,1
cherry,樱桃,/ˈtʃeri/,水果,2
mango,芒果,/ˈmæŋɡəʊ/,水果,3
pineapple,菠萝,/ˈpaɪnæpl/,水果,3
lemon,柠檬,/ˈlemən/,水果,3
cherry,樱桃,/ˈtʃeri/,水果,2
    """.trimIndent()
    
    private fun getColorsTemplate() = """
pink,粉色,/pɪŋk/,颜色,1
purple,紫色,/ˈpɜːpl/,颜色,2
brown,棕色,/braʊn/,颜色,2
gray,灰色,/ɡreɪ/,颜色,2
orange,橙色,/ˈɒrɪndʒ/,颜色,1
    """.trimIndent()
    
    private fun getNumbersTemplate() = """
seven,七,/ˈsevn/,数字,1
eight,八,/eɪt/,数字,1
nine,九,/naɪn/,数字,1
ten,十,/ten/,数字,1
eleven,十一,/ɪˈlevn/,数字,2
twelve,十二,/twelv/,数字,2
twenty,二十,/ˈtwenti/,数字,2
hundred,百,/ˈhʌndrəd/,数字,3
    """.trimIndent()
    
    private fun getFamilyTemplate() = """
grandma,奶奶,/ˈɡrænmɑː/,家庭,1
grandpa,爷爷,/ˈɡrænpɑː/,家庭,1
aunt,阿姨,/ɑːnt/,家庭,2
uncle,叔叔,/ˈʌŋkl/,家庭,2
cousin,堂兄妹,/ˈkʌzn/,家庭,2
baby,宝宝,/ˈbeɪbi/,家庭,1
    """.trimIndent()
    
    private fun getBodyTemplate() = """
nose,鼻子,/nəʊz/,身体,1
ear,耳朵,/ɪə/,身体,1
hair,头发,/heə/,身体,1
arm,手臂,/ɑːm/,身体,1
leg,腿,/leɡ/,身体,1
foot,脚,/fʊt/,身体,1
finger,手指,/ˈfɪŋɡə/,身体,2
toe,脚趾,/təʊ/,身体,2
knee,膝盖,/niː/,身体,2
elbow,手肘,/ˈelbəʊ/,身体,2
    """.trimIndent()
}

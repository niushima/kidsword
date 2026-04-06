package com.kidsword.app.ui.wrong

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kidsword.app.data.database.AppDatabase
import com.kidsword.app.data.model.Word
import com.kidsword.app.data.repository.WordRepository
import com.kidsword.app.databinding.ActivityWrongBookBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WrongBookActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityWrongBookBinding
    private lateinit var repository: WordRepository
    private lateinit var adapter: WrongWordAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWrongBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val database = AppDatabase.getDatabase(this)
        repository = WordRepository(database)
        
        setupRecyclerView()
        setupClickListeners()
        loadWrongWords()
    }
    
    private fun setupRecyclerView() {
        adapter = WrongWordAdapter { word ->
            // 点击复习单个单词
            showWordDetail(word)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.btnClear.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("清空错题本")
                .setMessage("确定要清空所有错题记录吗？")
                .setPositiveButton("确定") { _, _ ->
                    lifecycleScope.launch {
                        repository.clearAllWrongRecords()
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }
    
    private fun loadWrongWords() {
        lifecycleScope.launch {
            repository.getWrongWords().collectLatest { words ->
                if (words.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.submitList(words)
                }
            }
        }
    }
    
    private fun showWordDetail(word: Word) {
        AlertDialog.Builder(this)
            .setTitle(word.english)
            .setMessage("中文：${word.chinese}\n音标：${word.phonetic}\n错误次数：${word.wrongCount}")
            .setPositiveButton("知道了", null)
            .show()
    }
}

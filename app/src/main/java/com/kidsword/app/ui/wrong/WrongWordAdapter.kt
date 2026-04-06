package com.kidsword.app.ui.wrong

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kidsword.app.data.model.Word
import com.kidsword.app.databinding.ItemWrongWordBinding

class WrongWordAdapter(
    private val onItemClick: (Word) -> Unit
) : ListAdapter<Word, WrongWordAdapter.ViewHolder>(WordDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWrongWordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(
        private val binding: ItemWrongWordBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(word: Word) {
            binding.tvEnglish.text = word.english
            binding.tvChinese.text = word.chinese
            binding.tvWrongCount.text = "错误 ${word.wrongCount} 次"
            
            binding.root.setOnClickListener { onItemClick(word) }
            binding.btnReview.setOnClickListener { onItemClick(word) }
        }
    }
    
    class WordDiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }
}

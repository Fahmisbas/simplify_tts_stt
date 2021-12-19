package com.revelatestudio.simplifyspeechnotes.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.revelatestudio.simplifyspeechnotes.data.local.Note
import com.revelatestudio.simplifyspeechnotes.databinding.ItemNoteBinding

class NoteListAdapter : ListAdapter<Note, NoteListAdapter.NoteViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener : OnItemClickListener? = null
    private var onItemLongClickListener : OnItemLongClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = currentList[position]

        if (note != null) {
            holder.bind(note, position)
        }
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, position: Int) {
            with(binding) {
                tvTitle.text = note.title
                tvContent.text = note.content

                if (note.title.isEmpty()) {
                    tvTitle.text = note.content
                }

                if (note.content.isEmpty()) {
                    tvContent.text = note.title
                }

                itemView.setOnClickListener {
                    onItemClickListener?.onItemLongClick(note,position)
                }

                itemView.setOnLongClickListener {
                    onItemLongClickListener?.onItemLongClick(note, position)
                    true
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItemResponse: Note, newItemResponse: Note): Boolean {
                return oldItemResponse.id == newItemResponse.id
            }

            override fun areContentsTheSame(oldItemResponse: Note, newItemResponse: Note): Boolean {
                return oldItemResponse == newItemResponse
            }
        }
    }

    interface OnItemClickListener {
        fun onItemLongClick(note: Note, position : Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(note: Note, position: Int)
    }

}
package com.revelatestudio.simplifyspeechnotes.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.revelatestudio.simplifyspeechnotes.R
import com.revelatestudio.simplifyspeechnotes.data.local.Note
import com.revelatestudio.simplifyspeechnotes.ui.adapter.NoteListAdapter
import com.revelatestudio.simplifyspeechnotes.ui.detail.EditNoteFragment.Companion.EXTRA_NOTE_ID
import com.revelatestudio.simplifyspeechnotes.ui.detail.EditNoteFragment.Companion.EXTRA_NOTE
import com.revelatestudio.simplifyspeechnotes.utils.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.LinearLayoutManager
import com.revelatestudio.simplifyspeechnotes.databinding.FragmentHomeBinding


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listAdapter = initListAdapter()
        observeNotes(listAdapter)

        binding.fab.setOnClickListener {
            val bundle = bundleOf(
                EXTRA_NOTE to null,
                EXTRA_NOTE_ID to -1
            )
            it.findNavController().navigate(R.id.action_homeFragment_to_editNoteFragment, bundle)
        }
    }

    private fun initListAdapter() : NoteListAdapter{
        val adapter = NoteListAdapter()
        binding.rvNote.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            true
        )
        binding.rvNote.adapter = adapter
        return adapter
    }

    private fun observeNotes(adapter: NoteListAdapter) {
        viewModel.getNotes()
        viewModel.notes.observe(viewLifecycleOwner, { result ->
            when (result) {
                is HomeViewModel.RetrievalEvent.Success -> {
                    displayNotes(result.notes, adapter)
                }
                is HomeViewModel.RetrievalEvent.Loading -> {}
                else -> {}
            }
        })
    }


    private fun displayNotes(notes: ArrayList<Note>, adapter: NoteListAdapter) {
        adapter.submitList(notes)
        onItemNoteClick(adapter)
        onItemLongClickListener(adapter, notes)
    }

    private fun onItemNoteClick(adapter: NoteListAdapter) {
        adapter.setOnItemClickListener(object : NoteListAdapter.OnItemClickListener {
            override fun onItemLongClick(note: Note, position: Int) {
                val bundle = bundleOf(
                    EXTRA_NOTE to note,
                    EXTRA_NOTE_ID to note.id
                )
                findNavController().navigate(R.id.action_homeFragment_to_editNoteFragment, bundle)
            }
        })
    }

    private fun onItemLongClickListener(adapter: NoteListAdapter, notes: ArrayList<Note>) {
        adapter.setOnItemLongClickListener(object : NoteListAdapter.OnItemLongClickListener {
            override fun onItemLongClick(note: Note, position: Int) {
                requireContext().showAlertDialog(getString(R.string.delete_prompt_title), getString(R.string.delete_prompt_msg), positiveButton = {
                    viewModel.deleteNote(note)
                    notes.remove(note)
                    adapter.notifyItemRemoved(position)
                }, negativeButton = {
                    return@showAlertDialog
                }).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
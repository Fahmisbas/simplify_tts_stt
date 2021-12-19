package com.revelatestudio.simplifyspeechnotes.ui.detail

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import com.revelatestudio.simplifyspeechnotes.MainActivity
import com.revelatestudio.simplifyspeechnotes.R
import com.revelatestudio.simplifyspeechnotes.data.local.Note
import com.revelatestudio.simplifyspeechnotes.databinding.FragmentEditNoteBinding
import com.revelatestudio.simplifyspeechnotes.utils.getCurrentDate
import com.revelatestudio.simplifyspeechnotes.utils.shareTextContent
import com.revelatestudio.simplifyspeechnotes.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class EditNoteFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditNoteViewModel by viewModels()

    private var note: Note? = null
    private var noteId: Int = -1
    private var isNewNote = false

    private var textToSpeech: TextToSpeech? = null

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                binding.edtContent.setText(getString(R.string.speech_to_text, binding.edtContent.text.toString(), matches?.get(0).toString())
                )
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = binding.toolbarEditNote
        (activity as MainActivity).setSupportActionBar(toolbar.root)
        (activity as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.tvToolbarTitle.text = requireContext().getString(R.string.note)

        note = arguments?.getParcelable(EXTRA_NOTE)
        noteId = arguments?.getInt(EXTRA_NOTE_ID) ?: -1

        isNewNote = note == null && noteId == -1

        textToSpeech = TextToSpeech(requireContext(), this)

        if (!isNewNote) {
            with(binding) {
                edtTitle.setText(note?.title)
                edtContent.setText(note?.content)
                date.text = note?.date
            }
        }

        binding.btnRecord.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text")
            }
            resultLauncher.launch(intent)
        }

        binding.btnPlay.setOnClickListener {
            val text = binding.edtContent.text.toString()
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_note, menu)
        menu.getItem(1).isVisible = !isNewNote
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveNote()
            }
            R.id.delete -> {
                deleteNote()
            }
            R.id.share -> {
                val title = binding.edtTitle.text.toString()
                val content = binding.edtContent.text.toString()
                share(title, content)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun share(title: String, content: String) {
        if (content.isNotEmpty()) {
            shareTextContent(
                requireContext(),
                title = title,
                content = content
            )
        } else requireContext().showToast("The content is empty")

    }

    private fun deleteNote() {
        val note = note
        if (note != null) {
            viewModel.deleteNote(note)
            val intent = activity?.intent
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            activity?.apply {
                overridePendingTransition(0, 0)
                finish()
                overridePendingTransition(0, 0)
            }
            startActivity(intent)
        }
    }

    private fun saveNote() {
        with(binding) {
            val title = edtTitle.text.toString()
            val content = edtContent.text.toString()
            val date = getCurrentDate()

            val newNote = Note(title = title, content = content, date = date)

            val id = noteId

            if (id > 0 && !isNewNote) {
                newNote.id = id
                viewModel.updateNote(newNote)
                requireContext().showToast("Updated!")
            }

            if (id < 0 && isNewNote){
                isNewNote = false
                 viewModel.insertNote(newNote) { insertedNoteId ->
                     noteId = insertedNoteId.toInt()
                }
                requireContext().showToast("Saved!")
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.getDefault())

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                requireContext().showToast("The Language specified is not supported!")
            }
        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    companion object {
        const val EXTRA_NOTE = "note"
        const val EXTRA_NOTE_ID = "id"
    }
}
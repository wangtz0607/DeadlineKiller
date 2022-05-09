package com.deadlinekiller.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.deadlinekiller.R
import com.deadlinekiller.data.Deadline
import com.deadlinekiller.databinding.FragmentEditDeadlineBinding
import com.deadlinekiller.utils.buildMaterialDatePicker
import com.deadlinekiller.utils.buildMaterialTimePicker
import com.deadlinekiller.viewmodels.FormViewModel
import com.deadlinekiller.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@AndroidEntryPoint
class EditDeadlineFragment : Fragment() {
    private var _binding: FragmentEditDeadlineBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val mainViewModel by viewModels<MainViewModel>()
    private val formViewModel by viewModels<FormViewModel>()
    private var deadline: Deadline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deadline = arguments?.getParcelable(ARG_DEADLINE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDeadlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onBackPressed = {
            if (formViewModel.hasFormChanged) {
                ConfirmDialogFragment(
                    getString(R.string.confirm_discarding_changes),
                    getString(R.string.button_discard),
                    getString(R.string.button_cancel)
                ) {
                    findNavController().navigateUp()
                }.show(childFragmentManager, null)
            } else {
                findNavController().navigateUp()
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }
        if (deadline == null) {
            binding.topAppBar.setTitle(R.string.create_new_deadline_page_title)
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_done -> {
                    if (validateForm()) {
                        Deadline(
                            id = deadline?.id ?: 0,
                            title = binding.editTextTitle.text.toString(),
                            dateTime = LocalDateTime.parse(
                                "${binding.editTextDate.text} ${binding.editTextTime.text}",
                                DateTimeFormatter.ofPattern("y/MM/dd HH:mm")
                            ).atZone(ZoneId.systemDefault()).toInstant(),
                            notes = binding.editTextNotes.text.toString(),
                            deadline?.isDone ?: false,
                        ).let {
                            if (deadline == null) {
                                mainViewModel.insertDeadline(it)
                            } else {
                                mainViewModel.updateDeadline(it)
                            }
                        }
                        findNavController().navigateUp()
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }
        deadline?.let { deadline ->
            binding.editTextTitle.setText(deadline.title)
            deadline.dateTime.atZone(ZoneId.systemDefault()).let {
                binding.editTextDate.setText(
                    it.format(DateTimeFormatter.ofPattern("y/MM/dd"))
                )
                binding.editTextTime.setText(
                    it.format(DateTimeFormatter.ofPattern("HH:mm"))
                )
            }
            binding.editTextNotes.setText(deadline.notes)
        }
        binding.editTextDate.apply {
            if (text.isEmpty()) {
                setText(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("y/MM/dd"))
                )
            }
        }
        binding.editTextTime.apply {
            if (text.isEmpty()) {
                setText(
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                )
            }
        }
        binding.editTextDate.setOnClickListener {
            buildMaterialDatePicker(it as EditText).show(parentFragmentManager, null)
        }
        binding.editTextTime.setOnClickListener {
            buildMaterialTimePicker(it as EditText).show(parentFragmentManager, null)
        }
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                formViewModel.hasFormChanged = true
            }

            override fun afterTextChanged(s: Editable?) {}
        }.let {
            binding.editTextTitle.addTextChangedListener(it)
            binding.editTextDate.addTextChangedListener(it)
            binding.editTextTime.addTextChangedListener(it)
            binding.editTextNotes.addTextChangedListener(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateForm(): Boolean {
        var isValid = true
        binding.editTextTitle.let {
            if (it.text.isEmpty()) {
                it.error = getString(R.string.empty_title)
                isValid = false
            }
        }
        binding.editTextDate.let {
            try {
                LocalDate.parse(it.text, DateTimeFormatter.ofPattern("y/MM/dd"))
            } catch (e: DateTimeParseException) {
                it.error = getString(R.string.invalid_date)
                isValid = false
            }
        }
        binding.editTextTime.let {
            try {
                LocalTime.parse(it.text, DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: DateTimeParseException) {
                it.error = getString(R.string.invalid_time)
                isValid = false
            }
        }
        return isValid
    }

    companion object {
        const val ARG_DEADLINE = "deadline"
    }
}
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
import com.deadlinekiller.data.Reminder
import com.deadlinekiller.data.ReminderType
import com.deadlinekiller.databinding.FragmentEditReminderBinding
import com.deadlinekiller.utils.buildMaterialDatePicker
import com.deadlinekiller.utils.buildMaterialTimePicker
import com.deadlinekiller.viewmodels.FormViewModel
import com.deadlinekiller.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@AndroidEntryPoint
class EditReminderFragment : Fragment() {
    private var _binding: FragmentEditReminderBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val mainViewModel by viewModels<MainViewModel>()
    private val formViewModel by viewModels<FormViewModel>()
    private var deadlineId: Int? = null
    private var reminder: Reminder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deadlineId = arguments?.getInt(ARG_DEADLINE_ID)
        reminder = arguments?.getParcelable(ARG_REMINDER)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReminderBinding.inflate(layoutInflater, container, false)
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
        if (reminder == null) {
            binding.topAppBar.setTitle(R.string.create_new_reminder_page_title)
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_done -> {
                    if (validateForm()) {
                        val dateTime = LocalDateTime.parse(
                            "${binding.editTextDate.text} ${binding.editTextTime.text}",
                            DateTimeFormatter.ofPattern("y/MM/dd HH:mm")
                        ).atZone(ZoneId.systemDefault()).toInstant()
                        Reminder(
                            id = reminder?.id ?: 0,
                            deadlineId = reminder?.deadlineId ?: requireNotNull(deadlineId),
                            type = if (binding.chipNotification.isChecked) {
                                ReminderType.NOTIFICATION
                            } else {
                                ReminderType.ALARM
                            },
                            dateTime = dateTime,
                            isEnabled = reminder?.isEnabled ?: true,
                            isInvoked = Instant.now() > dateTime,
                        ).let {
                            if (reminder == null) {
                                mainViewModel.insertReminder(it)
                            } else {
                                mainViewModel.updateReminder(it)
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
        reminder?.let { reminder ->
            when (reminder.type) {
                ReminderType.NOTIFICATION -> binding.chipNotification.isChecked = true
                ReminderType.ALARM -> binding.chipAlarm.isChecked = true
            }
            reminder.dateTime.atZone(ZoneId.systemDefault()).let {
                binding.editTextDate.setText(
                    it.format(DateTimeFormatter.ofPattern("y/MM/dd"))
                )
                binding.editTextTime.setText(
                    it.format(DateTimeFormatter.ofPattern("HH:mm"))
                )
            }
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
            binding.editTextDate.addTextChangedListener(it)
            binding.editTextTime.addTextChangedListener(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateForm(): Boolean {
        var isValid = true
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
        const val ARG_DEADLINE_ID = "deadline_id"
        const val ARG_REMINDER = "reminder"
    }
}
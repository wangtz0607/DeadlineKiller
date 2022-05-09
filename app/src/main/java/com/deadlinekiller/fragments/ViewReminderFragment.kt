package com.deadlinekiller.fragments

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.deadlinekiller.R
import com.deadlinekiller.databinding.FragmentViewReminderBinding
import com.deadlinekiller.data.ReminderType
import com.deadlinekiller.utils.getDurationDescription
import com.deadlinekiller.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

@AndroidEntryPoint
class ViewReminderFragment : Fragment() {
    private var _binding: FragmentViewReminderBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel by viewModels<MainViewModel>()
    private var reminderId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reminderId = requireNotNull(requireArguments().getInt(ARG_REMINDER_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewReminderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        viewModel.getReminderById(reminderId)
            .observe(viewLifecycleOwner) { fullReminder ->
                binding.topAppBar.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.item_edit -> {
                            findNavController().navigate(
                                R.id.action_view_reminder_fragment_to_edit_reminder_fragment,
                                bundleOf(EditReminderFragment.ARG_REMINDER to fullReminder.toReminder())
                            )
                            true
                        }
                        R.id.item_delete -> {
                            ConfirmDialogFragment(
                                getString(R.string.confirm_deletion),
                                getString(R.string.button_delete),
                                getString(R.string.button_cancel)
                            ) {
                                findNavController().navigateUp()
                                viewModel.deleteReminderById(fullReminder.id)
                            }.show(childFragmentManager, null)
                            true
                        }
                        else -> false
                    }
                }
                binding.imageViewReminderType.setImageResource(
                    when (fullReminder.type) {
                        ReminderType.NOTIFICATION -> R.drawable.ic_baseline_chat_bubble_outline_24
                        ReminderType.ALARM -> R.drawable.ic_baseline_alarm_24
                    }
                )
                fullReminder.dateTime.atZone(ZoneId.systemDefault()).apply {
                    binding.textViewDate.text = format(DateTimeFormatter.ofPattern("y/MM/dd"))
                    binding.textViewTime.text = format(DateTimeFormatter.ofPattern("HH:mm"))
                }
                if (fullReminder.isInvoked) {
                    binding.textViewDate.apply {
                        setTextColor(requireContext().getColor(R.color.gray_600))
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    binding.textViewTime.apply {
                        setTextColor(requireContext().getColor(R.color.gray_600))
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                } else {
                    binding.textViewDate.apply {
                        setTextColor(requireContext().getColor(R.color.black))
                        paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                    binding.textViewTime.apply {
                        setTextColor(requireContext().getColor(R.color.black))
                        paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }
                binding.textViewInvocationFailed.visibility =
                    if (
                        fullReminder.isEnabled
                        && !fullReminder.isInvoked
                        && Instant.now() > fullReminder.dateTime
                    ) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                if (Instant.now() > fullReminder.deadlineDateTime) {
                    binding.textViewDeadlineTitle.apply {
                        setTextColor(requireContext().getColor(R.color.gray_600))
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                } else {
                    binding.textViewDeadlineTitle.apply {
                        setTextColor(requireContext().getColor(R.color.black))
                        paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }
                binding.textViewDeadlineTitle.text = fullReminder.deadlineTitle
                binding.textViewDeadlineDateTime.text =
                    fullReminder.deadlineDateTime.atZone(ZoneId.systemDefault()).format(
                        DateTimeFormatter.ofPattern("y/MM/dd HH:mm")
                    )
                if (fullReminder.deadlineIsDone) {
                    binding.textViewDeadlineTitle.apply {
                        setTextColor(context.getColor(R.color.gray_600))
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                } else {
                    binding.textViewDeadlineTitle.apply {
                        setTextColor(context.getColor(R.color.black))
                        paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }
                binding.linearLayoutDeadline.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_view_reminder_fragment_to_view_deadline_fragment,
                        bundleOf(ViewDeadlineFragment.ARG_DEADLINE_ID to fullReminder.deadlineId)
                    )
                }
                binding.textViewRelativeReminderTimeNow.text =
                    getDurationDescription(
                        requireContext(),
                        Duration.between(Instant.now(), fullReminder.dateTime)
                    )
                binding.textViewRelativeReminderTimeDeadline.text =
                    getDurationDescription(
                        requireContext(),
                        Duration.between(fullReminder.dateTime, fullReminder.deadlineDateTime)
                    )
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_REMINDER_ID = "reminder_id"
    }
}
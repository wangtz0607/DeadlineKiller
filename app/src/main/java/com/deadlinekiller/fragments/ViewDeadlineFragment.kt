package com.deadlinekiller.fragments

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlinekiller.R
import com.deadlinekiller.adapters.SimpleReminderListAdapter
import com.deadlinekiller.data.Reminder
import com.deadlinekiller.data.ReminderType
import com.deadlinekiller.databinding.FragmentViewDeadlineBinding
import com.deadlinekiller.services.AlarmService
import com.deadlinekiller.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.properties.Delegates

@AndroidEntryPoint
class ViewDeadlineFragment : Fragment() {
    private var _binding: FragmentViewDeadlineBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel by viewModels<MainViewModel>()
    private var deadlineId by Delegates.notNull<Int>()
    private var comingFromReminder: Reminder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deadlineId = requireNotNull(requireArguments().getInt(ARG_DEADLINE_ID))
        comingFromReminder = arguments?.getParcelable(ARG_COMING_FROM_REMINDER)
        comingFromReminder?.let {
            if (it.type == ReminderType.ALARM) {
                requireContext().stopService(Intent(context, AlarmService::class.java))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewDeadlineBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        viewModel.getDeadlineById(deadlineId)
            .observe(viewLifecycleOwner) { deadline ->
                binding.topAppBar.menu.findItem(R.id.item_mark_as_done_or_undone).apply {
                    if (deadline.isDone) {
                        setTitle(R.string.item_mark_as_undone)
                        setOnMenuItemClickListener {
                            viewModel.setDeadlineDoneById(deadline.id, false)
                            true
                        }
                    } else {
                        setTitle(R.string.item_mark_as_done)
                        setOnMenuItemClickListener {
                            ConfirmDialogFragment(
                                getString(R.string.confirm_deadline_done, deadline.title),
                                getString(R.string.button_yes),
                                getString(R.string.button_no)
                            ) {
                                viewModel.setDeadlineDoneById(deadline.id, true)
                                viewModel.setReminderEnabledByDeadlineId(deadline.id, false)
                                findNavController().navigate(
                                    R.id.action_view_deadline_fragment_to_congratulation_fragment,
                                    bundleOf(CongratulationFragment.ARG_DEADLINE to deadline)
                                )
                            }.show(childFragmentManager, null)
                            true
                        }
                    }
                }
                binding.topAppBar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_edit -> {
                            findNavController().navigate(
                                R.id.action_view_deadline_fragment_to_edit_deadline_fragment,
                                bundleOf(EditDeadlineFragment.ARG_DEADLINE to deadline)
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
                                viewModel.deleteDeadlineById(deadlineId)
                            }.show(childFragmentManager, null)
                            true
                        }
                        else -> false
                    }
                }
                binding.textViewTitle.text = deadline.title
                binding.textViewDateTime.text =
                    deadline.dateTime.atZone(ZoneId.systemDefault()).format(
                        DateTimeFormatter.ofPattern("y/MM/dd HH:mm")
                    )
                ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    deadline.dateTime.atZone(ZoneId.systemDefault()).toLocalDate(),
                ).toInt().let { days ->
                    binding.textViewDaysRemaining.text = days.toString()
                    binding.textViewDaysPlurals.text =
                        resources.getQuantityString(R.plurals.days, days)
                }
                if (deadline.isDone) {
                    binding.textViewTitle.apply {
                        setTextColor(context.getColor(R.color.gray_600))
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    binding.textViewDaysRemaining.visibility = View.GONE
                    binding.textViewDaysPlurals.visibility = View.GONE
                } else {
                    binding.textViewTitle.apply {
                        setTextColor(context.getColor(R.color.black))
                        paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                    binding.textViewDaysRemaining.visibility = View.VISIBLE
                    binding.textViewDaysPlurals.visibility = View.VISIBLE
                }
                if (Instant.now() > deadline.dateTime) {
                    requireContext().getColor(R.color.red_500).let {
                        binding.textViewDaysRemaining.setTextColor(it)
                        binding.textViewDaysPlurals.setTextColor(it)
                    }
                } else {
                    requireContext().getColor(R.color.blue_700).let {
                        binding.textViewDaysRemaining.setTextColor(it)
                        binding.textViewDaysPlurals.setTextColor(it)
                    }
                }
                binding.textViewNotes.text = deadline.notes
            }
        val adapter = SimpleReminderListAdapter(
            requireActivity().menuInflater,
            isReminderHighlighted = { reminder ->
                comingFromReminder?.let { reminder.id == it.id } ?: false
            },
            onClickReminderItem = { reminder ->
                findNavController().navigate(
                    R.id.action_view_deadline_fragment_to_view_reminder_fragment,
                    bundleOf(ViewReminderFragment.ARG_REMINDER_ID to reminder.id)
                )
            },
            onReminderEnabledChange = { reminder, isEnabled ->
                viewModel.setReminderEnabledById(reminder.id, isEnabled)
            },
            onEditReminder = { reminder ->
                findNavController().navigate(
                    R.id.action_view_deadline_fragment_to_edit_reminder_fragment,
                    bundleOf(EditReminderFragment.ARG_REMINDER to reminder)
                )
            },
            onDeleteReminder = { reminder ->
                ConfirmDialogFragment(
                    getString(R.string.confirm_deletion),
                    getString(R.string.button_delete),
                    getString(R.string.button_cancel)
                ) {
                    viewModel.deleteReminderById(reminder.id)
                }.show(childFragmentManager, null)
            }
        )
        binding.recyclerViewSimpleReminderList.adapter = adapter
        binding.recyclerViewSimpleReminderList.layoutManager = LinearLayoutManager(context)
        viewModel.getRemindersByDeadlineId(deadlineId).observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                R.id.action_view_deadline_fragment_to_edit_reminder_fragment,
                bundleOf(EditReminderFragment.ARG_DEADLINE_ID to deadlineId)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_DEADLINE_ID = "deadline_id"
        const val ARG_COMING_FROM_REMINDER = "coming_from_reminder"
    }
}
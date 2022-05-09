package com.deadlinekiller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlinekiller.R
import com.deadlinekiller.data.FullReminder
import com.deadlinekiller.adapters.ReminderListAdapter
import com.deadlinekiller.databinding.FragmentReminderListBinding
import com.deadlinekiller.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderListFragment : Fragment() {
    private var _binding: FragmentReminderListBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ReminderListAdapter(
            requireActivity().menuInflater,
            onClickReminderItem = { fullReminder ->
                findNavController().navigate(
                    R.id.action_home_fragment_to_view_reminder_fragment,
                    bundleOf(ViewReminderFragment.ARG_REMINDER_ID to fullReminder.id)
                )
            },
            onReminderEnabledChange = { fullReminder, isEnabled ->
                viewModel.setReminderEnabledById(fullReminder.id, isEnabled)
            },
            onEditReminder = { fullReminder ->
                findNavController().navigate(
                    R.id.action_home_fragment_to_edit_reminder_fragment,
                    bundleOf(EditReminderFragment.ARG_REMINDER to fullReminder.toReminder())
                )
            },
            onDeleteReminder = { fullReminder ->
                ConfirmDialogFragment(
                    getString(R.string.confirm_deletion),
                    getString(R.string.button_delete),
                    getString(R.string.button_cancel)
                ) {
                    viewModel.deleteReminderById(fullReminder.id)
                }.show(childFragmentManager, null)
            }
        )

        binding.recyclerViewReminderList.adapter = adapter
        binding.recyclerViewReminderList.layoutManager = LinearLayoutManager(context)
        val observer = Observer<List<FullReminder>> {
            adapter.submitList(it)
            binding.linearLayoutNoReminders.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_filter -> true
                R.id.item_all, R.id.item_only_upcoming -> {
                    menuItem.isChecked = true
                    viewModel.allReminders.removeObserver(observer)
                    viewModel.upcomingReminders.removeObserver(observer)
                    when (menuItem.itemId) {
                        R.id.item_all -> {
                            viewModel.allReminders.observe(viewLifecycleOwner, observer)
                            preferences.edit().apply {
                                putInt(KEY_REMINDER_LIST_FILTER, REMINDER_LIST_FILTER_ALL)
                                apply()
                            }
                        }
                        R.id.item_only_upcoming -> {
                            viewModel.upcomingReminders.observe(viewLifecycleOwner, observer)
                            preferences.edit().apply {
                                putInt(
                                    KEY_REMINDER_LIST_FILTER,
                                    REMINDER_LIST_FILTER_ONLY_UPCOMING
                                )
                                apply()
                            }
                        }
                    }
                    true
                }
                R.id.item_settings -> {
                    findNavController().navigate(R.id.action_home_fragment_to_settings_fragment)
                    true
                }
                else -> false
            }
        }
        binding.topAppBar.menu.apply {
            when (preferences.getInt(KEY_REMINDER_LIST_FILTER, REMINDER_LIST_FILTER_ALL)) {
                REMINDER_LIST_FILTER_ALL -> {
                    performIdentifierAction(R.id.item_all, 0)
                }
                REMINDER_LIST_FILTER_ONLY_UPCOMING -> {
                    performIdentifierAction(R.id.item_only_upcoming, 0)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_REMINDER_LIST_FILTER = "reminder_list_filter"
        private const val REMINDER_LIST_FILTER_ALL = 0
        private const val REMINDER_LIST_FILTER_ONLY_UPCOMING = 1
    }
}

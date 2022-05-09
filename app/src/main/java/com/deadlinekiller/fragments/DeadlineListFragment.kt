package com.deadlinekiller.fragments

import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.deadlinekiller.R
import com.deadlinekiller.data.Deadline
import com.deadlinekiller.databinding.FragmentDeadlineListBinding
import com.deadlinekiller.adapters.DeadlineListAdapter
import com.deadlinekiller.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeadlineListFragment : Fragment() {
    private var _binding: FragmentDeadlineListBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeadlineListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_home_fragment_to_edit_deadline_fragment)
        }
        val adapter = DeadlineListAdapter(
            requireActivity().menuInflater,
            onClickDeadlineItem = {
                findNavController().navigate(
                    R.id.action_home_fragment_to_view_deadline_fragment,
                    bundleOf(ViewDeadlineFragment.ARG_DEADLINE_ID to it.id)
                )
            },
            onEditDeadline = {
                findNavController().navigate(
                    R.id.action_home_fragment_to_edit_deadline_fragment,
                    bundleOf(EditDeadlineFragment.ARG_DEADLINE to it)
                )
            },
            onDeleteDeadline = {
                ConfirmDialogFragment(
                    getString(R.string.confirm_deletion),
                    getString(R.string.button_delete),
                    getString(R.string.button_cancel)
                ) {
                    viewModel.deleteDeadlineById(it.id)
                }.show(childFragmentManager, null)
            },
            onDeadlineDoneChange = { deadline, isDone ->
                if (isDone) {
                    ConfirmDialogFragment(
                        getString(R.string.confirm_deadline_done, deadline.title),
                        getString(R.string.button_yes),
                        getString(R.string.button_no)
                    ) {
                        viewModel.setDeadlineDoneById(deadline.id, true)
                        viewModel.setReminderEnabledByDeadlineId(deadline.id, false)
                        findNavController().navigate(
                            R.id.action_home_fragment_to_congratulation_fragment,
                            bundleOf(CongratulationFragment.ARG_DEADLINE to deadline)
                        )
                    }.show(childFragmentManager, null)
                } else {
                    viewModel.setDeadlineDoneById(deadline.id, false)
                }
            }
        )
        binding.recyclerViewDeadlineList.adapter = adapter
        binding.recyclerViewDeadlineList.layoutManager = LinearLayoutManager(context)
        val observer = Observer<List<Deadline>> {
            adapter.submitList(it)
            binding.linearLayoutNoDeadlines.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_filter -> true
                R.id.item_all, R.id.item_only_undone -> {
                    menuItem.isChecked = true
                    viewModel.allDeadlines.removeObserver(observer)
                    viewModel.undoneDeadlines.removeObserver(observer)
                    when (menuItem.itemId) {
                        R.id.item_all -> {
                            viewModel.allDeadlines.observe(viewLifecycleOwner, observer)
                            preferences.edit().apply {
                                putInt(KEY_DEADLINE_LIST_FILTER, DEADLINE_LIST_FILTER_ALL)
                                apply()
                            }
                        }
                        R.id.item_only_undone -> {
                            viewModel.undoneDeadlines.observe(viewLifecycleOwner, observer)
                            preferences.edit().apply {
                                putInt(KEY_DEADLINE_LIST_FILTER, DEADLINE_LIST_FILTER_ONLY_UNDONE)
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
            when (preferences.getInt(KEY_DEADLINE_LIST_FILTER, DEADLINE_LIST_FILTER_ALL)) {
                DEADLINE_LIST_FILTER_ALL -> {
                    performIdentifierAction(R.id.item_all, 0)
                }
                DEADLINE_LIST_FILTER_ONLY_UNDONE -> {
                    performIdentifierAction(R.id.item_only_undone, 0)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_DEADLINE_LIST_FILTER = "deadline_list_filter"
        private const val DEADLINE_LIST_FILTER_ALL = 0
        private const val DEADLINE_LIST_FILTER_ONLY_UNDONE = 1
    }
}
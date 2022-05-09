package com.deadlinekiller.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.deadlinekiller.fragments.DeadlineListFragment
import com.deadlinekiller.fragments.ReminderListFragment

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int) = when (position) {
        0 -> DeadlineListFragment()
        1 -> ReminderListFragment()
        else -> throw IllegalArgumentException()
    }
}

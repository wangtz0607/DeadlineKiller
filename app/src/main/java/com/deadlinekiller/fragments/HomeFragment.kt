package com.deadlinekiller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.deadlinekiller.R
import com.deadlinekiller.adapters.PagerAdapter
import com.deadlinekiller.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = PagerAdapter(this)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavigation.menu.findItem(
                    when (position) {
                        0 -> R.id.item_deadlines
                        1 -> R.id.item_reminders
                        else -> throw IllegalArgumentException()
                    }
                ).isChecked = true
            }
        })
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            binding.viewPager.currentItem = when (item.itemId) {
                R.id.item_deadlines -> 0
                R.id.item_reminders -> 1
                else -> throw IllegalArgumentException()
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

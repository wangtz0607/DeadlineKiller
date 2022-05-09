package com.deadlinekiller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.deadlinekiller.R
import com.deadlinekiller.data.Deadline
import com.deadlinekiller.databinding.FragmentCongratulationBinding
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class CongratulationFragment : Fragment() {
    private var _binding: FragmentCongratulationBinding? = null
    private val binding get() = checkNotNull(_binding)
    private lateinit var deadline: Deadline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deadline = requireNotNull(requireArguments().getParcelable(ARG_DEADLINE))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCongratulationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ChronoUnit.DAYS.between(
            LocalDate.now(),
            deadline.dateTime.atZone(ZoneId.systemDefault()).toLocalDate()
        ).toInt().let { days ->
            binding.textViewCongratulationBody.text = getString(
                R.string.congratulation_body,
                deadline.title,
                days,
                resources.getQuantityString(R.plurals.days, days)
            )
        }
        binding.buttonOk.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_DEADLINE = "deadline"
    }
}

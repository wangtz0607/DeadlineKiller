package com.deadlinekiller.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.deadlinekiller.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val deadlineRepository: DeadlineRepository,
    private val reminderRepository: ReminderRepository,
) : ViewModel() {
    val allDeadlines: LiveData<List<Deadline>> = deadlineRepository.all.asLiveData()
    val undoneDeadlines: LiveData<List<Deadline>> = deadlineRepository.undone.asLiveData()
    val allReminders: LiveData<List<FullReminder>> =
        reminderRepository.all.asLiveData()
    val upcomingReminders: LiveData<List<FullReminder>> =
        reminderRepository.upcoming.asLiveData()

    fun getDeadlineById(id: Int): LiveData<Deadline> = deadlineRepository.getById(id).asLiveData()

    fun setDeadlineDoneById(id: Int, isDone: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        deadlineRepository.setDoneById(id, isDone)
    }

    fun insertDeadline(deadline: Deadline) = viewModelScope.launch(Dispatchers.IO) {
        deadlineRepository.insert(deadline)
    }

    fun updateDeadline(deadline: Deadline) = viewModelScope.launch(Dispatchers.IO) {
        deadlineRepository.update(deadline)
    }

    fun deleteDeadlineById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        deadlineRepository.deleteById(id)
    }

    fun getReminderById(id: Int): LiveData<FullReminder> =
        reminderRepository.getById(id).asLiveData()

    fun getRemindersByDeadlineId(deadlineId: Int): LiveData<List<Reminder>> =
        reminderRepository.getByDeadlineId(deadlineId).asLiveData()

    fun insertReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        reminderRepository.insert(reminder)
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        reminderRepository.update(reminder)
    }

    fun setReminderEnabledById(id: Int, isEnabled: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepository.setEnabledById(id, isEnabled)
        }

    fun setReminderEnabledByDeadlineId(deadlineId: Int, isEnabled: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepository.setEnabledByDeadlineId(deadlineId, isEnabled)
        }

    fun deleteReminderById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        reminderRepository.deleteById(id)
    }
}

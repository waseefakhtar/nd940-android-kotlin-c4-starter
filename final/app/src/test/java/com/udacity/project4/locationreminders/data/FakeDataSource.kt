package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.mockito.Mockito.mock

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source
    private val remindersDao: RemindersDao = mock(RemindersDao::class.java)
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val remindersLocalRepository: RemindersLocalRepository
        get() = RemindersLocalRepository(remindersDao, ioDispatcher)


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return remindersLocalRepository.getReminders()
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersLocalRepository.saveReminder(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return remindersLocalRepository.getReminder(id)
    }

    override suspend fun deleteAllReminders() {
        remindersLocalRepository.deleteAllReminders()
    }
}
package com.udacity.project4.locationreminders.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.utils.TestUtil

import org.junit.Before;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt
    private lateinit var remindersDao: RemindersDao
    private lateinit var remindersDatabase: RemindersDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            context, RemindersDatabase::class.java).build()
        remindersDao = remindersDatabase.reminderDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        remindersDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun saveReminderAndGetById() {
        val reminderDTO: ReminderDTO = TestUtil.createReminderDTO(
            TestUtil.createReminderDataItem())

        runBlocking { remindersDao.saveReminder(reminderDTO) }

        val savedReminder = runBlocking { remindersDao.getReminderById(reminderDTO.id) }
        assertThat(savedReminder, equalTo(reminderDTO))
    }

    @Test
    @Throws(Exception::class)
    fun saveDeleteRemindersAndGetAll() {
        val reminderDTO: ReminderDTO = TestUtil.createReminderDTO(
            TestUtil.createReminderDataItem())
        runBlocking { remindersDao.saveReminder(reminderDTO) }

        runBlocking { remindersDao.deleteAllReminders() }

        val savedReminders = runBlocking { remindersDao.getReminders() }
        assertThat(savedReminders, `is`(emptyList()))
    }

    @Test
    @Throws(Exception::class)
    fun deleteRemindersAndGetAll() {
        runBlocking { remindersDao.deleteAllReminders() }

        val savedReminders = runBlocking { remindersDao.getReminders() }
        assertThat(savedReminders, `is`(emptyList()))
    }

    @Test
    @Throws(Exception::class)
    fun getReminders() {
        val savedReminders = runBlocking { remindersDao.getReminders() }
        assertThat(savedReminders, `is`(emptyList()))
    }
}
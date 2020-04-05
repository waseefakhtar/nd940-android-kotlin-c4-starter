package com.udacity.project4.locationreminders.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt
    private lateinit var remindersDao: RemindersDao
    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun init() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            context, RemindersDatabase::class.java).build()
        remindersDao = remindersDatabase.reminderDao()
        remindersLocalRepository = RemindersLocalRepository(remindersDao)
    }

    @After
    @Throws(IOException::class)
    fun close() {
        remindersDatabase.close()
    }

    @Test
    fun test() {
    }
}
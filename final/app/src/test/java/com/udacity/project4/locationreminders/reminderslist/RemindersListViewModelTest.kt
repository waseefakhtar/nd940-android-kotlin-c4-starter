package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.utils.blockingObserveValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    private val app: Application = Mockito.mock(Application::class.java)
    private val dataSource: FakeDataSource = Mockito.mock(FakeDataSource::class.java)
    private var remindersListViewModel: RemindersListViewModel = Mockito.mock(RemindersListViewModel::class.java)

    @Before
    fun setUp() {
        remindersListViewModel = RemindersListViewModel(app, dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `Should load reminders`() {
        remindersListViewModel.loadReminders()

        assertEquals(remindersListViewModel.showLoading.blockingObserveValue(), false)
        assertEquals(remindersListViewModel.showNoData.blockingObserveValue(), remindersListViewModel.remindersList.blockingObserveValue() == null || remindersListViewModel.remindersList.blockingObserveValue()!!.isEmpty())
    }
}
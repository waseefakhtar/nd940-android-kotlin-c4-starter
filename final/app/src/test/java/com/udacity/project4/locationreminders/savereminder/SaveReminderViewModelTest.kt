package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.TestUtil.Companion.createReminderDataItem
import com.udacity.project4.utils.TestUtil.Companion.randomString
import com.udacity.project4.utils.blockingObserveValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito.*
import java.util.concurrent.ThreadLocalRandom

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private val app: Application = mock(Application::class.java)
    private val dataSource: FakeDataSource = mock(FakeDataSource::class.java)
    private var saveReminderViewModel: SaveReminderViewModel = mock(SaveReminderViewModel::class.java)


    //TODO: provide testing to the SaveReminderView and its live data objects
    @Before
    fun setUp() {
        saveReminderViewModel = SaveReminderViewModel(app, dataSource)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun onClear() {
        saveReminderViewModel.onClear()

        assertEquals(saveReminderViewModel.reminderTitle.blockingObserveValue(), null)
        assertEquals(saveReminderViewModel.reminderDescription.blockingObserveValue(), null)
        assertEquals(saveReminderViewModel.reminderSelectedLocationStr.blockingObserveValue(), null)
        assertEquals(saveReminderViewModel.selectedPOI.blockingObserveValue(), null)
        assertEquals(saveReminderViewModel.latitude.blockingObserveValue(), null)
        assertEquals(saveReminderViewModel.longitude.blockingObserveValue(), null)
    }

    @Test
    fun validateAndSaveReminder() {
            val reminderDataItem: ReminderDataItem = createReminderDataItem()

            saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

            assertEquals(saveReminderViewModel.showLoading.blockingObserveValue(), false)
            assertEquals(saveReminderViewModel.showToast.blockingObserveValue(), app.getString(R.string.reminder_saved))
            assertEquals(saveReminderViewModel.navigationCommand.blockingObserveValue(), NavigationCommand.Back)
    }

    @Test
    fun saveReminder() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem()

        saveReminderViewModel.saveReminder(reminderDataItem)


        assertEquals(saveReminderViewModel.showLoading.blockingObserveValue(), false)
        assertEquals(saveReminderViewModel.showToast.blockingObserveValue(), app.getString(R.string.reminder_saved))
        assertEquals(saveReminderViewModel.navigationCommand.blockingObserveValue(), NavigationCommand.Back)
    }


    @Test
    fun validateEnteredData() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem()

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.blockingObserveValue(), null)
    }

    @Test
    fun `Should validate empty title`() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem("")

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.blockingObserveValue(), R.string.err_enter_title)
    }

    @Test
    fun `Should validate null title`() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem(null)

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.blockingObserveValue(), R.string.err_enter_title)
    }

    @Test
    fun `Should validate empty location`() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem(randomString(), randomString(), "")

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.blockingObserveValue(), R.string.err_select_location)
    }

    @Test
    fun `Should validate null location`() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem(randomString(), randomString(), null)

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.blockingObserveValue(), R.string.err_select_location)
    }
}
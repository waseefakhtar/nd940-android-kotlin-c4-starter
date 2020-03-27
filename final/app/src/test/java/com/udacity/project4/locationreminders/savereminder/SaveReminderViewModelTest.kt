package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.base.Verify
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Suspendable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.stubbing.OngoingStubbing
import java.util.concurrent.ThreadLocalRandom
import javax.sql.DataSource
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private val app: Application = mock(Application::class.java)
    private val dataSource: FakeDataSource = mock(FakeDataSource::class.java)
    private var saveReminderViewModel: SaveReminderViewModel = mock(SaveReminderViewModel::class.java)

    private val job = Job()

    val coroutineContext: CoroutineContext
        get() = Dispatchers.Unconfined + job

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

        assertEquals(saveReminderViewModel.reminderTitle.value, null)
        assertEquals(saveReminderViewModel.reminderDescription.value, null)
        assertEquals(saveReminderViewModel.reminderSelectedLocationStr.value, null)
        assertEquals(saveReminderViewModel.selectedPOI.value, null)
        assertEquals(saveReminderViewModel.latitude.value, null)
        assertEquals(saveReminderViewModel.longitude.value, null)
    }

    @Test
    fun validateAndSaveReminder() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem()

        saveReminderViewModel.saveReminder(reminderDataItem)

        assertEquals(saveReminderViewModel.showLoading.value, false)
        assertEquals(saveReminderViewModel.showToast.value, app.getString(R.string.reminder_saved))
        assertEquals(saveReminderViewModel.navigationCommand.value, NavigationCommand.Back)
    }

    @Test
    fun saveReminder() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem()

        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)


        assertEquals(saveReminderViewModel.showLoading.value, false)
        assertEquals(saveReminderViewModel.showToast.value, app.getString(R.string.reminder_saved))
        assertEquals(saveReminderViewModel.navigationCommand.value, NavigationCommand.Back)
    }


    @Test
    fun validateEnteredData() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem()

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.value, null)
    }

    @Test
    fun `Should validate empty title`() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem("")

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.value, R.string.err_enter_title)
    }

    @Test
    fun `Should validate null title`() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem(null)

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.value, R.string.err_enter_title)
    }

    @Test
    fun `Should validate empty location`() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem(randomString(), randomString(), "")

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.value, R.string.err_select_location)
    }

    @Test
    fun `Should validate null location`() {
        val reminderDataItem: ReminderDataItem = createReminderDataItem(randomString(), randomString(), null)

        saveReminderViewModel.validateEnteredData(reminderDataItem)

        assertEquals(saveReminderViewModel.showSnackBarInt.value, R.string.err_select_location)
    }
}

fun createReminderDataItem(
    title: String? = randomString(),
    description: String? = randomString(),
    location: String? = randomString(),
    latitude: Double? = positiveRandomDouble(),
    longitude: Double?  = positiveRandomDouble(),
    id: String = randomString()) =
    ReminderDataItem(
        title,
        description,
        location,
        latitude,
        longitude,
        id)

fun createReminderDTO(reminderDataItem: ReminderDataItem) =
    ReminderDTO(
        reminderDataItem.title,
        reminderDataItem.description,
        reminderDataItem.location,
        reminderDataItem.latitude,
        reminderDataItem.longitude,
        reminderDataItem.id)

fun positiveRandomInt(maxInt: Int = Int.MAX_VALUE-1): Int = random.nextInt(maxInt+1).takeIf { it > 0 } ?: positiveRandomInt(maxInt)
fun positiveRandomLong(maxLong: Long = Long.MAX_VALUE-1): Long = random.nextLong(maxLong+1).takeIf { it > 0 } ?: positiveRandomLong(maxLong)
fun positiveRandomDouble(maxDouble: Double = Double.MAX_VALUE-1): Double = random.nextDouble(maxDouble + 1).takeIf { it > 0 } ?: positiveRandomDouble(maxDouble)
fun randomInt() = random.nextInt()
fun randomIntBetween(min: Int, max: Int) = random.nextInt(max - min) + min
fun randomLong() = random.nextLong()
fun randomBoolean() = random.nextBoolean()
fun randomString(size: Int = 20): String = (0..size)
    .map { charPool[random.nextInt(0, charPool.size)] }
    .joinToString()

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
val random
    get() = ThreadLocalRandom.current()


interface SuspendableMock {
    suspend fun suspendFunctionMock()
}

class SuspendableImpl(val dataSource: FakeDataSource, val reminderDTO: ReminderDTO) : SuspendableMock {
    override suspend fun suspendFunctionMock() = dataSource.saveReminder(reminderDTO)
}

class CallsSuspendable(val suspendableMock: SuspendableMock) {
    fun callSuspendable() {
        runBlocking {
            suspendableMock.suspendFunctionMock()
        }
    }
}
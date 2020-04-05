package com.udacity.project4.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

class TestUtil {

    companion object {
        fun createReminderDataItem(
            title: String? = randomString(),
            description: String? = randomString(),
            location: String? = randomString(),
            latitude: Double? = positiveRandomDouble(),
            longitude: Double?  = positiveRandomDouble(),
            id: String = randomString()
        ) =
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

        fun positiveRandomInt(maxInt: Int = Int.MAX_VALUE-1): Int = random.nextInt(maxInt+1).takeIf { it > 0 } ?: positiveRandomInt(
            maxInt
        )
        fun positiveRandomLong(maxLong: Long = Long.MAX_VALUE-1): Long = random.nextLong(maxLong+1).takeIf { it > 0 } ?: positiveRandomLong(
            maxLong
        )
        fun positiveRandomDouble(maxDouble: Double = Double.MAX_VALUE-1): Double = random.nextDouble(maxDouble + 1).takeIf { it > 0 } ?: positiveRandomDouble(
            maxDouble
        )
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
    }
}

fun <T> LiveData<T>.blockingObserveValue(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)

    val observer = Observer<T> { t ->
        value = t
        latch.countDown()
    }

    observeForever(observer)

    latch.await(2, TimeUnit.SECONDS)
    return value
}
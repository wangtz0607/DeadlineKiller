package com.deadlinekiller.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.deadlinekiller.TestUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ReminderDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var deadlineDao: DeadlineDao
    private lateinit var reminderDao: ReminderDao
    private val testDeadlines = TestUtils.createDeadlines(DEADLINES_COUNT)
    private val testReminders = TestUtils.createReminders(REMINDERS_COUNT, testDeadlines)
    private val testDeadlineIndex = testDeadlines.associateBy { it.id }

    @Before
    fun setUp() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        deadlineDao = database.deadlineDao()
        reminderDao = database.reminderDao()
        deadlineDao.insertAll(testDeadlines)
        reminderDao.insertAll(testReminders)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testGetAll() = runBlocking {
        assertEquals(
            testReminders
                .map { it.toFullReminder(testDeadlineIndex.getValue(it.deadlineId)) }
                .sortedBy { it.dateTime },
            reminderDao.getAll().first()
        )
    }

    @Test
    fun testGetUpcoming() = runBlocking {
        assertEquals(
            testReminders
                .filter { it.dateTime > Instant.now() }
                .map { it.toFullReminder(testDeadlineIndex.getValue(it.deadlineId)) }
                .sortedBy { it.dateTime },
            reminderDao.getUpcoming().first()
        )
    }

    @Test
    fun testGetAwaitingInvocation() = runBlocking {
        assertEquals(
            testReminders
                .filter { it.isEnabled && !it.isInvoked }
                .map { it.toFullReminder(testDeadlineIndex.getValue(it.deadlineId)) }
                .sortedBy { it.dateTime },
            reminderDao.getAwaitingInvocation().first()
        )
    }

    @Test
    fun testGetById() = runBlocking {
        assertEquals(
            testReminders[0].toFullReminder(testDeadlineIndex.getValue(testReminders[0].deadlineId)),
            reminderDao.getById(testReminders[0].id).first()
        )
    }

    @Test
    fun testGetByDeadlineId() = runBlocking {
        assertEquals(
            testReminders.filter { it.deadlineId == testDeadlines[0].id }.sortedBy { it.dateTime },
            reminderDao.getByDeadlineId(testDeadlines[0].id).first()
        )
    }

    @Test
    fun testSetEnabledById() = runBlocking {
        reminderDao.setEnabledById(testReminders[0].id, !testReminders[0].isEnabled)
        assertEquals(
            !testReminders[0].isEnabled,
            reminderDao.getById(testReminders[0].id).first().isEnabled
        )
    }

    @Test
    fun testSetEnabledByDeadlineId() = runBlocking {
        reminderDao.setEnabledByDeadlineId(testReminders[0].deadlineId, !testReminders[0].isEnabled)
        reminderDao.getByDeadlineId(testReminders[0].deadlineId).first().forEach {
            assertEquals(it.isEnabled, !testReminders[0].isEnabled)
        }
    }

    @Test
    fun testSetInvokedById() = runBlocking {
        reminderDao.setInvokedById(testReminders[0].id, !testReminders[0].isInvoked)
        assertEquals(
            reminderDao.getById(testReminders[0].id).first().isInvoked,
            !testReminders[0].isInvoked
        )
    }

    @Test
    fun testDeleteById() = runBlocking {
        reminderDao.deleteById(testReminders[0].id)
        assertEquals(
            testReminders
                .filter { it != testReminders[0] }
                .map { it.toFullReminder(testDeadlineIndex.getValue(it.deadlineId)) }
                .sortedBy { it.dateTime },
            reminderDao.getAll().first()
        )
    }

    companion object {
        private const val DEADLINES_COUNT = 5
        private const val REMINDERS_COUNT = 50
    }
}
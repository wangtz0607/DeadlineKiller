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

class DeadlineDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var deadlineDao: DeadlineDao
    private val testDeadlines = TestUtils.createDeadlines(DEADLINES_COUNT)

    @Before
    fun setUp() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        deadlineDao = database.deadlineDao()
        deadlineDao.insertAll(testDeadlines)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testGetAll() = runBlocking {
        assertEquals(testDeadlines.sortedBy { it.dateTime }, deadlineDao.getAll().first())
    }

    @Test
    fun testGetUndone() = runBlocking {
        assertEquals(
            testDeadlines.filter { !it.isDone }.sortedBy { it.dateTime },
            deadlineDao.getUndone().first()
        )
    }

    @Test
    fun testGetById() = runBlocking {
        assertEquals(testDeadlines[0], deadlineDao.getById(testDeadlines[0].id).first())
    }

    @Test
    fun testSetDoneById() = runBlocking {
        deadlineDao.setDoneById(testDeadlines[0].id, !testDeadlines[0].isDone)
        assertEquals(
            !testDeadlines[0].isDone,
            deadlineDao.getById(testDeadlines[0].id).first().isDone
        )
    }

    @Test
    fun testDeleteById() = runBlocking {
        deadlineDao.deleteById(testDeadlines[0].id)
        assertEquals(
            testDeadlines.filter { it != testDeadlines[0] }.sortedBy { it.dateTime },
            deadlineDao.getAll().first()
        )
    }

    companion object {
        private const val DEADLINES_COUNT = 50
    }
}

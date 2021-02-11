package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.Entity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowTransformingTest : BaseTest() {

    private val flowTransforming = Mockito.spy(FlowTransforming())

    @Test
    fun `test transform int to string`() = runBlockingTest {
        val given = listOf(0, 1, 2, 3)
        val expected = listOf("0", "1", "2", "3")

        flowTransforming.transformIntToString(given.asFlow())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @FlowPreview
    @Test
    fun `test get pair by id`() = runBlockingTest {
        val given = listOf(0, 1, 2, 3)
        val expected = listOf(
            Entity(0, "0"),
            Entity(1, "1"),
            Entity(2, "2"),
            Entity(3, "3"),
        )

        flowTransforming.getPairById(given.asFlow())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test collect ints to list`() = runBlockingTest {
        val given = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val expected = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(9, 10)
        )

        flowTransforming.collectsIntsToLists(given.asFlow(), 3)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test distribute names by first letter`() = runBlockingTest {
        val given = listOf("00", "11", "11123", "22", "33", "34", "35")
        val expected = mapOf(
            '0' to listOf("00"),
            '1' to listOf("11", "11123"),
            '2' to listOf("22"),
            '3' to listOf("33", "34", "35"),
        )

        flowTransforming.distributeNamesByFirstLetter(given.asFlow())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }
}
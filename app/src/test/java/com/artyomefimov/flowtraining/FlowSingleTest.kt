package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.ExpectedException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowSingleTest : BaseTest() {

    private val flowSingle = FlowSingle()

    @Test
    fun `test only one element no error`() = runBlockingTest {
        val given = 1

        flowSingle.onlyOneElement(given)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(given, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test only one element with error`() = runBlockingTest {
        val given = -1

        flowSingle.onlyOneElement(given)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(given, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.hasException(ExpectedException::class))
    }

    @Test
    fun `test only one element of sequence success`() = runBlockingTest {
        val given = listOf(1, 2, 3)
        val expected = given.first()

        flowSingle.firstElementOfSequence(given.asFlow())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test only one element of sequence error`() = runBlockingTest {
        flowSingle.firstElementOfSequence(flowOf())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.hasException(ExpectedException::class))
    }

    @Test
    fun `test calculate sum of values`() = runBlockingTest {
        val given = flowOf(1, 2, 3)
        val expected = 6

        flowSingle.calculateSumOfValues(given)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test collection of values`() = runBlockingTest {
        val given = listOf(1, 2, 3)

        flowSingle.collectionOfValues(given.asFlow())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(given, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test all elements are positive`() = runBlockingTest {
        val given = listOf(1, 2, 3)
        val expected = true

        flowSingle.allElementsArePositive(given.asFlow())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test not all elements are positive`() = runBlockingTest {
        val given = listOf(1, 2, -3)
        val expected = false

        flowSingle.allElementsArePositive(given.asFlow())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }
}
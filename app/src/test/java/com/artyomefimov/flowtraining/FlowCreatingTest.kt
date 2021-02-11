package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.ExpectedException
import com.artyomefimov.flowtraining.model.FlowCreatingEntity
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.Mockito

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowCreatingTest : BaseTest() {

    @Test
    fun `test value to flow`() = runBlockingTest {
        val expected = 1

        valueToFlow(expected)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test vararg to flow`() = runBlockingTest {
        val expected = listOf(1, 2, 3)

        varargToFlow(1, 2, 3)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()
            .also { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test list to flow`() = runBlockingTest {
        val expected = listOf("1", "2", "3")

        listToFlow(expected)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()
            .also { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test expensive method result`() = runBlockingTest {
        val entity = Mockito.spy(FlowCreatingEntity())
        val flow = expensiveMethodResult(entity)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }

        verify(entity, never()).expensiveMethod()

        flow.collect { assertEquals(Int.MAX_VALUE, it) }

        verify(entity).expensiveMethod()

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test increasing sequence with delays`() = runBlockingTest {
        val initialDelay = 1000L
        val period = 500L
        val result = mutableListOf<Long>()

        val job = launch {
            increasingSequenceWithDelays(initialDelay, period)
                .onCompletion { testStatusController.noticeCompletion() }
                .catch { testStatusController.noticeException(it) }
                .toList(result)
        }

        advanceTimeBy(initialDelay)
        assertEquals(listOf(0L), result)

        advanceTimeBy(period)
        assertEquals(listOf(0L, 1L), result)

        advanceTimeBy(period)
        assertEquals(listOf(0L, 1L, 2L), result)

        job.cancel()

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test delayed zero`() = runBlockingTest {
        val delay = 1000L
        val result = mutableListOf<Long>()

        val job = launch {
            delayedZero(delay)
                .onCompletion { testStatusController.noticeCompletion() }
                .catch { testStatusController.noticeException(it) }
                .toList(result)
        }

        advanceTimeBy(delay - 1)
        assertEquals(0, result.size)

        advanceTimeBy(1L)
        job.cancel()
        assertEquals(1, result.size)
        assertEquals(0L, result.first())
        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test methods combination with error`() = runBlockingTest {
        val entity = Mockito.spy(FlowCreatingEntity())
        val flow = combinationExpensiveMethods(entity = entity, condition = true)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
        val result = mutableListOf<Int>()

        verify(entity, never()).expensiveMethod()
        verify(entity, never()).anotherExpensiveMethod()
        verify(entity, never()).unstableMethod(anyBoolean())

        flow.toList(result)

        verify(entity).expensiveMethod()
        verify(entity).anotherExpensiveMethod()
        verify(entity).unstableMethod(anyBoolean())

        assertEquals(2, result.size)
        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.hasException(ExpectedException::class))
    }

    @Test
    fun `test methods combination without error`() = runBlockingTest {
        val entity = Mockito.spy(FlowCreatingEntity())
        val flow = combinationExpensiveMethods(entity = entity, condition = false)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
        val result = mutableListOf<Int>()

        verify(entity, never()).expensiveMethod()
        verify(entity, never()).anotherExpensiveMethod()
        verify(entity, never()).unstableMethod(anyBoolean())

        flow.toList(result)

        verify(entity).expensiveMethod()
        verify(entity).anotherExpensiveMethod()
        verify(entity).unstableMethod(anyBoolean())

        assertEquals(3, result.size)
        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test without any events`() = runBlockingTest {
        val result = mutableListOf<Int>()
        val job = launch {
            withoutAnyEvents()
                .onCompletion { testStatusController.noticeCompletion() }
                .catch { testStatusController.noticeException(it) }
                .toList(result)
        }

        assertTrue(testStatusController.isCompleted().not())
        assertTrue(testStatusController.noExceptions())
        assertTrue(result.isEmpty())
        job.cancel()
    }

    @Test
    fun `test only complete`() = runBlockingTest {
        onlyComplete()
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test only error`() = runBlockingTest {
        onlyError()
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.hasException(ExpectedException::class))
    }
}
package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.ExpectedException
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

    private val flowCreating = Mockito.spy(FlowCreating())

    @Test
    fun `test value to flow`() = runBlockingTest {
        val expected = 1

        flowCreating.valueToFlow(expected)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test vararg to flow`() = runBlockingTest {
        val expected = listOf(1, 2, 3)

        flowCreating.varargToFlow(1, 2, 3)
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

        flowCreating.listToFlow(expected)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()
            .also { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test expensive method result`() = runBlockingTest {
        val flow = flowCreating.expensiveMethodResult()
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }

        verify(flowCreating, never()).expensiveMethod()

        flow.collect { assertEquals(Int.MAX_VALUE, it) }

        verify(flowCreating).expensiveMethod()

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test increasing sequence with delays`() = runBlockingTest {
        val initialDelay = 1000L
        val period = 500L
        val result = mutableListOf<Long>()

        val job = launch {
            flowCreating.increasingSequenceWithDelays(initialDelay, period)
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
            flowCreating.delayedZero(delay)
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
        val flow = flowCreating.combinationExpensiveMethods(condition = true)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
        val result = mutableListOf<Int>()

        verify(flowCreating, never()).expensiveMethod()
        verify(flowCreating, never()).anotherExpensiveMethod()
        verify(flowCreating, never()).unstableMethod(anyBoolean())

        flow.toList(result)

        verify(flowCreating).expensiveMethod()
        verify(flowCreating).anotherExpensiveMethod()
        verify(flowCreating).unstableMethod(anyBoolean())

        assertEquals(2, result.size)
        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.hasException(ExpectedException::class))
    }

    @Test
    fun `test methods combination without error`() = runBlockingTest {
        val flow = flowCreating.combinationExpensiveMethods(condition = false)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
        val result = mutableListOf<Int>()

        verify(flowCreating, never()).expensiveMethod()
        verify(flowCreating, never()).anotherExpensiveMethod()
        verify(flowCreating, never()).unstableMethod(anyBoolean())

        flow.toList(result)

        verify(flowCreating).expensiveMethod()
        verify(flowCreating).anotherExpensiveMethod()
        verify(flowCreating).unstableMethod(anyBoolean())

        assertEquals(3, result.size)
        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test without any events`() = runBlockingTest {
        val result = mutableListOf<Int>()
        val job = launch {
            flowCreating.withoutAnyEvents()
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
        flowCreating.onlyComplete()
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test only error`() = runBlockingTest {
        flowCreating.onlyError()
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.hasException(ExpectedException::class))
    }
}
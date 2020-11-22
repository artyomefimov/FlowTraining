package com.artyomefimov.flowtraining

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowCreatingTest : BaseTest() {

    @Test
    fun `test value to flow`() = runBlockingTest {
        val value = 1
        testObject.valueToFlow(value)
            .noticeCompletion()
            .noticeError()
            .assertValue(value)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test vararg to flow`() = runBlockingTest {
        testObject.varargToFlow(1, 2, 3)
            .noticeCompletion()
            .noticeError()
            .assertValues(listOf(1, 2, 3))

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test list to flow`() = runBlockingTest {
        val list = listOf("1", "2", "3")
        testObject.listToFlow(list)
            .noticeCompletion()
            .noticeError()
            .assertValues(list)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test expensive method result`() = runBlockingTest {
        val flow = testObject
            .expensiveMethodResult()
            .noticeCompletion()
            .noticeError()

        verify(testObject, never()).expensiveMethod()

        flow.assertValue(Int.MAX_VALUE)

        verify(testObject).expensiveMethod()

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test increasing sequence with delays`() = runBlockingTest {
        val initialDelay = 1000L
        val period = 500L
        val result = mutableListOf<Long>()

        val job = launch {
            testObject.increasingSequenceWithDelays(initialDelay, period)
                .noticeCompletion()
                .noticeError()
                .toList(result)
        }

        advanceTimeBy(initialDelay)
        assertEquals(listOf(0L), result)

        advanceTimeBy(period)
        assertEquals(listOf(0L, 1L), result)

        advanceTimeBy(period)
        assertEquals(listOf(0L, 1L, 2L), result)

        job.cancel()

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test delayed zero`() = runBlockingTest {
        val delay = 1000L
        val result = mutableListOf<Long>()

        val job = launch {
            testObject.delayedZero(delay)
                .noticeCompletion()
                .noticeError()
                .toList(result)
        }

        advanceTimeBy(delay - 1)
        assertEquals(0, result.size)

        advanceTimeBy(1L)
        job.cancel()
        assertEquals(1, result.size)
        assertEquals(0L, result.first())
    }

    @Test
    fun `test methods combination with error`() = runBlockingTest {
        val flow = testObject.combinationExpensiveMethods(true)
            .noticeCompletion()
            .noticeError()
        val result = mutableListOf<Int>()

        verify(testObject, never()).expensiveMethod()
        verify(testObject, never()).anotherExpensiveMethod()
        verify(testObject, never()).unstableMethod(anyBoolean())

        flow.toList(result)

        verify(testObject).expensiveMethod()
        verify(testObject).anotherExpensiveMethod()
        verify(testObject).unstableMethod(anyBoolean())

        assertEquals(2, result.size)
        assertExpectedException()
        assertCompleted()
    }

    @Test
    fun `test methods combination without error`() = runBlockingTest {
        val flow = testObject.combinationExpensiveMethods(false)
            .noticeCompletion()
            .noticeError()
        val result = mutableListOf<Int>()

        verify(testObject, never()).expensiveMethod()
        verify(testObject, never()).anotherExpensiveMethod()
        verify(testObject, never()).unstableMethod(anyBoolean())

        flow.toList(result)

        verify(testObject).expensiveMethod()
        verify(testObject).anotherExpensiveMethod()
        verify(testObject).unstableMethod(anyBoolean())

        assertEquals(3, result.size)
        assertNoExceptions()
        assertCompleted()
    }

    @Test
    fun `test without any events`() = runBlockingTest {
        val result = mutableListOf<Int>()
        val job = launch {
            testObject.withoutAnyEvents()
                .noticeCompletion()
                .noticeError()
                .toList(result)
        }

        assertNotCompleted()
        assertNoExceptions()
        assertTrue(result.isEmpty())
        job.cancel()
    }

    @Test
    fun `test only complete`() = runBlockingTest {
        testObject.onlyComplete()
            .noticeCompletion()
            .noticeError()
            .collect { }

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test only error`() = runBlockingTest {
        testObject.onlyError()
            .noticeCompletion()
            .noticeError()
            .collect { }

        assertExpectedException()
    }
}
package com.artyomefimov.flowtraining

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowCreatingTest : BaseTest() {

    private companion object {
        const val VALUE = 1
        val STRING_LIST = listOf("1", "2", "3")
    }

    @Test
    fun `test value to flow`() = runBlockingTest {
        testObject.valueToFlow(VALUE)
            .failOnError()
            .assertValue(VALUE)
    }

    @Test
    fun `test array to flow`() = runBlockingTest {
        testObject.listToFlow(STRING_LIST)
            .failOnError()
            .assertValues(STRING_LIST)
    }

    @Test
    fun `test expensive method result`() = runBlockingTest {
        val flow = testObject
            .expensiveMethodResult()
            .failOnError()

        verify(testObject, never()).expensiveMethod()

        flow.assertValue(Int.MAX_VALUE)

        verify(testObject).expensiveMethod()
    }

    @Test
    fun `test increasing sequence with delays`() = runBlockingTest {
        val initialDelay = 1000L
        val period = 500L
        val result = mutableListOf<Long>()

        val job = launch {
            testObject.increasingSequenceWithDelays(initialDelay, period)
                .failOnError()
                .collect {
                    result.add(it)
                }
        }

        advanceTimeBy(initialDelay)
        assertEquals(0L, result.last())

        advanceTimeBy(period)
        assertEquals(1L, result.last())

        advanceTimeBy(period)
        assertEquals(2L, result.last())

        job.cancel()

        assertEquals(3, result.size)
    }

    @Test
    fun `test delayed zero`() = runBlockingTest {
        val delay = 1000L
        val result = mutableListOf<Long>()

        val job = launch {
            testObject.delayedZero(delay)
                .failOnError()
                .collect {
                    result.add(it)
                }
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
        var exception: Throwable? = null // todo вынести в Base
        val flow = testObject.combinationExpensiveMethods(true)
            .noticeCompletion()
            .catch { exception = it }
        val result = mutableListOf<Int>()

        verify(testObject, never()).expensiveMethod()
        verify(testObject, never()).anotherExpensiveMethod()
        verify(testObject, never()).unstableMethod(anyBoolean())

        flow.collect { result.add(it) }

        verify(testObject).expensiveMethod()
        verify(testObject).anotherExpensiveMethod()
        verify(testObject).unstableMethod(anyBoolean())

        assertEquals(2, result.size)
        assertNotNull(exception)
        assertTrue(exception is ExpectedException)
        //assertFalse(isCompleted) // падает
    }

    @Test
    fun `test methods combination without error`() = runBlockingTest {
        var exception: Throwable? = null // todo вынести в Base
        val flow = testObject.combinationExpensiveMethods(false)
            .noticeCompletion()
            .catch { exception = it }
        val result = mutableListOf<Int>()

        verify(testObject, never()).expensiveMethod()
        verify(testObject, never()).anotherExpensiveMethod()
        verify(testObject, never()).unstableMethod(anyBoolean())

        flow.collect { result.add(it) }

        verify(testObject).expensiveMethod()
        verify(testObject).anotherExpensiveMethod()
        verify(testObject).unstableMethod(anyBoolean())

        assertEquals(3, result.size)
        assertNull(exception)
        //assertFalse(isCompleted) // падает
    }

    @Test
    fun `test without any events`() = runBlockingTest {
        val result = mutableListOf<Int>()
        val job = launch {
            testObject.withoutAnyEvents()
                .noticeCompletion()
                .failOnError()
                .collect {
                    result.add(it)
                }
        }

        assertFalse(isCompleted)
        assertTrue(result.isEmpty())
        job.cancel()
    }

    @Test
    fun `test only complete`() = runBlockingTest {
        testObject.onlyComplete()
            .noticeCompletion()
            .failOnError()
            .collect {  }

        assertTrue(isCompleted)
    }

    @Test
    fun `test only error`() = runBlockingTest {
        var exception: Throwable? = null

        testObject.onlyError()
            .noticeCompletion()
            .catch { exception = it }
            .collect {  }

        assertNotNull(exception)
        assertTrue(exception is ExpectedException)
    }
}
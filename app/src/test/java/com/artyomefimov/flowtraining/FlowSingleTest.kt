package com.artyomefimov.flowtraining

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowSingleTest : BaseTest() {

    @Test
    fun `test only one element no error`() = runBlockingTest {
        val value = 1

        testObject.onlyOneElement(value)
            .noticeError()
            .noticeCompletion()
            .assertValue(value)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test only one element with error`() = runBlockingTest {
        val value = -1

        testObject.onlyOneElement(value)
            .noticeError()
            .noticeCompletion()
            .assertValue(value)

        assertCompleted()
        assertExpectedException()
    }

    @Test
    fun `test only one element of sequence success`() = runBlockingTest {
        val data = listOf(1, 2, 3)

        testObject
            .firstElementOfSequence(data.asFlow())
            .noticeError()
            .noticeCompletion()
            .assertValue(data.first())

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test only one element of sequence error`() = runBlockingTest {
        val data = listOf<Int>()

        testObject
            .firstElementOfSequence(data.asFlow())
            .noticeError()
            .noticeCompletion()
            .toList()

        assertCompleted()
        assertExpectedException()
    }

    @Test
    fun `test calculate sum of values`() = runBlockingTest {
        testObject.calculateSumOfValues(flowOf(1, 2, 3))
            .noticeError()
            .noticeCompletion()
            .assertValue(6)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test collection of values`() = runBlockingTest {
        val data = listOf(1, 2, 3)

        testObject.collectionOfValues(data.asFlow())
            .noticeError()
            .noticeCompletion()
            .assertValue(data)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test all elements are positive`() = runBlockingTest {
        val data = listOf(1, 2, 3)

        testObject.allElementsArePositive(data.asFlow())
            .noticeError()
            .noticeCompletion()
            .assertValue(true)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test not all elements are positive`() = runBlockingTest {
        val data = listOf(1, 2, -3)

        testObject.allElementsArePositive(data.asFlow())
            .noticeError()
            .noticeCompletion()
            .assertValue(false)

        assertCompleted()
        assertNoExceptions()
    }
}
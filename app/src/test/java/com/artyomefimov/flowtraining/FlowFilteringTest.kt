package com.artyomefimov.flowtraining

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowFilteringTest : BaseTest() {

    @Test
    fun `test only positive numbers`() = runBlockingTest {
        val given = listOf(-20, 0, Int.MIN_VALUE, 10, Int.MAX_VALUE)
        val expected = listOf(10, Int.MAX_VALUE)

        testObject.onlyPositiveNumbers(given.asFlow())
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test only last values more than values`() = runBlockingTest {
        val given = listOf(1, 2, 3, 4, 5)

        testObject.onlyLastValues(given.asFlow(), 100)
            .noticeCompletion()
            .noticeError()
            .assertValue(given)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test only last values less than values`() = runBlockingTest {
        val given = listOf(1, 2, 3, 4, 5)
        val expected = listOf(4, 5)

        testObject.onlyLastValues(given.asFlow(), 2)
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test only first values more than values`() = runBlockingTest {
        val given = listOf(1, 2, 3, 4, 5)

        testObject.onlyFirstValues(given.asFlow(), 100)
            .noticeCompletion()
            .noticeError()
            .assertValue(given)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test only first values less than values`() = runBlockingTest {
        val given = listOf(1, 2, 3, 4, 5)
        val expected = listOf(1, 2)

        testObject.onlyFirstValues(given.asFlow(), 2)
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test ignore first values more than values`() = runBlockingTest {
        val given = listOf(1, 2, 3, 4, 5)

        testObject.ignoreFirstValues(given.asFlow(), 100)
            .noticeCompletion()
            .noticeError()
            .assertValue(emptyList())

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test ignore first values less than values`() = runBlockingTest {
        val given = listOf(1, 2, 3, 4, 5)
        val expected = listOf(3, 4, 5)

        testObject.ignoreFirstValues(given.asFlow(), 2)
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }
    
    @FlowPreview
    @Test
    fun `test only last per interval`() = runBlockingTest {
        val expected = listOf(4, 5)
        val period = 500L
        val intFlow = flow {
            emit(1)
            delay(period)
            emit(2)
            emit(3)
            emit(4)
            delay(period)
            emit(5)
            delay(period)
            delay(period)
            emit(6)
            emit(7)
        }

        testObject.onlyLastPerInterval(intFlow, period)
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test error if long wait no error`() = runBlockingTest {
        val expected = listOf(1, 2, 3, 4, 5, 6, 7)
        val period = 500L
        val intFlow = flow {
            emit(1)
            delay(period - 1)
            emit(2)
            emit(3)
            emit(4)
            delay(period - 1)
            emit(5)
            delay(period - 1)
            emit(6)
            emit(7)
        }

        testObject.errorIfLongWait(intFlow, period)
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test error if long wait with timeout`() = runBlockingTest {
        val expected = listOf(1, 2, 3, 4, 5)
        val period = 500L
        val intFlow = flow {
            emit(1)
            delay(period - 1)
            emit(2)
            emit(3)
            emit(4)
            delay(period - 1)
            emit(5)
            delay(period)
            emit(6)
            emit(7)
        }

        testObject.errorIfLongWait(intFlow, period)
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test ignore duplicates`() = runBlockingTest {
        val given = listOf(2, 1, 2, 3, 6, 4, 5, 5, 6)
        val expected = listOf(2, 1, 3, 6, 4, 5)

        testObject.ignoreDuplicates(given.asFlow())
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test only changed values`() = runBlockingTest {
        val given = listOf(2, 1, 1, 2, 3, 6, 4, 5, 5, 6)
        val expected = listOf(2, 1, 2, 3, 6, 4, 5, 6)

        testObject.onlyChangedValues(given.asFlow())
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }
}
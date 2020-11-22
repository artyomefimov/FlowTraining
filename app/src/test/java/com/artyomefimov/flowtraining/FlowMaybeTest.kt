package com.artyomefimov.flowtraining

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertNull
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowMaybeTest : BaseTest() {

    private companion object {
        const val POSITIVE_VALUE = 1
        const val NEGATIVE_VALUE = -1
        var expectedResult: Int? = null
    }

    @Test
    fun `test positive or empty success`() = runBlockingTest {
        testObject.positiveOrEmpty(POSITIVE_VALUE)
            .noticeCompletion()
            .noticeError()
            .assertValue(POSITIVE_VALUE)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test positive or empty fail`() = runBlockingTest {
        testObject.positiveOrEmpty(NEGATIVE_VALUE)
            .noticeCompletion()
            .noticeError()
            .collect { expectedResult = it }

        assertCompleted()
        assertNoExceptions()
        assertNull(expectedResult)
    }

    @Test
    fun `test positive or empty from flow success`() = runBlockingTest {
        testObject.positiveOrEmptyFromFlow(flowOf(POSITIVE_VALUE))
            .noticeCompletion()
            .noticeError()
            .assertValue(POSITIVE_VALUE)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test positive or empty from flow fail`() = runBlockingTest {
        testObject.positiveOrEmptyFromFlow(flowOf(NEGATIVE_VALUE))
            .noticeCompletion()
            .noticeError()
            .collect { expectedResult = it }

        assertCompleted()
        assertNoExceptions()
        assertNull(expectedResult)
    }

    @Test
    fun `test only one element has values`() = runBlockingTest {
        val defaultValue = 2
        testObject.onlyOneElement(flowOf(POSITIVE_VALUE), defaultValue)
            .noticeCompletion()
            .noticeError()
            .assertValue(POSITIVE_VALUE)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test only one element no values`() = runBlockingTest {
        val defaultValue = 2

        testObject.onlyOneElement(flowOf(), defaultValue)
            .noticeCompletion()
            .noticeError()
            .assertValue(defaultValue)

        assertCompleted()
        assertNoExceptions()
    }
}
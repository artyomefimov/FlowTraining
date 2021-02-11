package com.artyomefimov.flowtraining

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
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
        positiveOrEmpty(POSITIVE_VALUE)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(POSITIVE_VALUE, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test positive or empty fail`() = runBlockingTest {
        positiveOrEmpty(NEGATIVE_VALUE)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { expectedResult = it }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
        assertNull(expectedResult)
    }

    @Test
    fun `test positive or empty from flow success`() = runBlockingTest {
        positiveOrEmptyFromFlow(flowOf(POSITIVE_VALUE))
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(POSITIVE_VALUE, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test positive or empty from flow fail`() = runBlockingTest {
        positiveOrEmptyFromFlow(flowOf(NEGATIVE_VALUE))
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { expectedResult = it }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
        assertNull(expectedResult)
    }

    @Test
    fun `test only one element has values`() = runBlockingTest {
        val defaultValue = 2
        onlyOneElement(flowOf(POSITIVE_VALUE), defaultValue)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(POSITIVE_VALUE, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test only one element no values`() = runBlockingTest {
        val defaultValue = 2

        onlyOneElement(flowOf(), defaultValue)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(defaultValue, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }
}
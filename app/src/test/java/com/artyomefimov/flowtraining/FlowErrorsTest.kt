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
class FlowErrorsTest : BaseTest() {

    private companion object {
        val testList = listOf(1, 2, 3)
        const val defaultValue = 4
        val intFlow = flow {
            testList.forEach {
                emit(it)
            }
        }
    }

    @Test
    fun `test handle errors with default value no errors`() = runBlockingTest {
        handleErrorsWithDefaultValue(intFlow, defaultValue)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()
            .also { assertEquals(testList, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test handle errors with default value with error`() = runBlockingTest {
        val flowWithError = intFlow.onCompletion { throw ExpectedException() }
        handleErrorsWithDefaultValue(flowWithError, defaultValue)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()
            .also { assertEquals(testList + defaultValue, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `handle errors with fallback flow without error`() = runBlockingTest {
        handleErrorsWithFallbackFlow(intFlow, flowOf(defaultValue))
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()
            .also { assertEquals(testList, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test handle errors with fallback flow with error`() = runBlockingTest {
        val flowWithError = intFlow.onCompletion { throw ExpectedException() }
        handleErrorsWithFallbackFlow(flowWithError, flowOf(defaultValue))
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .toList()
            .also { assertEquals(testList + defaultValue, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }
}
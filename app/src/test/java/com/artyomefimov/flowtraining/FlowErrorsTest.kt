package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.ExpectedException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.runBlockingTest
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
        testObject
            .handleErrorsWithDefaultValue(intFlow, defaultValue)
            .noticeCompletion()
            .noticeError()
            .assertValues(testList)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test handle errors with default value with error`() = runBlockingTest {
        val flowWithError = intFlow.onCompletion { throw ExpectedException() }
        testObject
            .handleErrorsWithDefaultValue(flowWithError, defaultValue)
            .noticeCompletion()
            .noticeError()
            .assertValues(testList + defaultValue)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `handle errors with fallback flow without error`() = runBlockingTest {
        testObject
            .handleErrorsWithFallbackFlow(intFlow, flowOf(defaultValue))
            .noticeCompletion()
            .noticeError()
            .assertValues(testList)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test handle errors with fallback flow with error`() = runBlockingTest {
        val flowWithError = intFlow.onCompletion { throw ExpectedException() }
        testObject
            .handleErrorsWithFallbackFlow(flowWithError, flowOf(defaultValue))
            .noticeCompletion()
            .noticeError()
            .assertValues(testList + defaultValue)

        assertCompleted()
        assertNoExceptions()
    }
}
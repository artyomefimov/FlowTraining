package com.artyomefimov.flowtraining

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowCombiningTest : BaseTest() {

    @Test
    fun `test summation`() = runBlockingTest {
        val given1 = listOf(1, 2, 3, 4, 5)
        val given2 = listOf(10, 20, 30, 40, 50)
        val expected = listOf(11, 22, 33, 44, 55)

        summation(given1.asFlow(), given2.asFlow())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test request items`() = runBlockingTest {
        val searchData = listOf("a", "ab", "abc")
        val categoryData = listOf(1, 2, 3, 4, 5)
        val expected =
            listOf("a -> 1", "ab -> 1", "ab -> 2", "abc -> 2", "abc -> 3", "abc -> 4", "abc -> 5")

        requestItems(
            searchData.asFlow().onEach { delay(20L) },
            categoryData.asFlow().onEach { delay(30L) }
        )
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test composition`() = runBlockingTest {
        val given1 = listOf(1, 2, 3, 4, 5)
        val given2 = listOf(10, 20, 30, 40, 50)
        var result = listOf<Int>()

        composition(given1.asFlow(), given2.asFlow())
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { result = it }

        assertEquals(given1.size + given2.size, result.size)
        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }

    @Test
    fun `test additional first item`() = runBlockingTest {
        val firstItem = 0
        val given = listOf(1, 2, 3)
        val expected = listOf(0, 1, 2, 3)

        additionalFirstItem(given.asFlow(), firstItem)
            .onCompletion { testStatusController.noticeCompletion() }
            .catch { testStatusController.noticeException(it) }
            .collect { assertEquals(expected, it) }

        assertTrue(testStatusController.isCompleted())
        assertTrue(testStatusController.noExceptions())
    }
}
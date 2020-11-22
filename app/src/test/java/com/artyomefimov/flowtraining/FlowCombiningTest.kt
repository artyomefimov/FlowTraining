package com.artyomefimov.flowtraining

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowCombiningTest: BaseTest() {

    @Test
    fun `test summation`() = runBlockingTest {
        val given1 = listOf(1, 2, 3, 4, 5)
        val given2 = listOf(10, 20, 30, 40, 50)
        val expected = listOf(11, 22, 33, 44, 55)

        testObject.summation(given1.asFlow(), given2.asFlow())
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test request items`() = runBlockingTest {
        val searchData = listOf("a", "ab", "abc")
        val categoryData = listOf(1, 2, 3, 4, 5)
        val expected = listOf("a -> 1", "ab -> 1", "ab -> 2", "abc -> 2", "abc -> 3", "abc -> 4", "abc -> 5")

       val result = testObject.requestItems(
            searchData.asFlow().onEach { delay(20L) },
            categoryData.asFlow().onEach { delay(30L) }
        )
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test composition`() = runBlockingTest {
        val given1 = listOf(1, 2, 3, 4, 5)
        val given2 = listOf(10, 20, 30, 40, 50)
        var result = listOf<Int>()

        testObject.composition(given1.asFlow(), given2.asFlow())
            .noticeCompletion()
            .noticeError()
            .collect { result = it }

        assertEquals(given1.size + given2.size, result.size)
        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test additional first item`() = runBlockingTest {
        val firstItem = 0
        val given = listOf(1, 2, 3)
        val expected = listOf(0, 1, 2, 3)

        testObject.additionalFirstItem(given.asFlow(), firstItem)
            .noticeError()
            .noticeCompletion()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }
}
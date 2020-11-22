package com.artyomefimov.flowtraining

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FlowTransformingTest : BaseTest() {

    @Test
    fun `test transform int to string`() = runBlockingTest {
        val given = listOf(0, 1, 2, 3)
        val expected = listOf("0", "1", "2", "3")

        testObject.transformIntToString(given.asFlow())
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @FlowPreview
    @Test
    fun `test get pair by id`() = runBlockingTest {
        val given = listOf(0, 1, 2, 3)
        val expected = listOf(
            0 to "0",
            1 to "1",
            2 to "2",
            3 to "3"
        )

        testObject.getPairById(given.asFlow())
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test collect ints to list`() = runBlockingTest {
        val given = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val expected = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(9, 10)
        )

        testObject.collectsIntsToLists(given.asFlow(), 3)
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }

    @Test
    fun `test distribute names by first letter`() = runBlockingTest {
        val given = listOf("00", "11", "11123", "22", "33", "34", "35")
        val expected = mapOf(
            '0' to listOf("00"),
            '1' to listOf("11", "11123"),
            '2' to listOf("22"),
            '3' to listOf("33", "34", "35"),
        )

        testObject.distributeNamesByFirstLetter(given.asFlow())
            .noticeCompletion()
            .noticeError()
            .assertValue(expected)

        assertCompleted()
        assertNoExceptions()
    }
}
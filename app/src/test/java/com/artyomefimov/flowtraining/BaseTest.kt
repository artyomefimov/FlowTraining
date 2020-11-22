package com.artyomefimov.flowtraining

import org.junit.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.mockito.Mockito
import org.mockito.Mockito.reset

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
open class BaseTest {

    protected val testObject: TestObject = Mockito.spy(TestObject())

    private var isCompleted = false
    private var exception: Throwable? = null

    private val mainThreadSurrogate =
        newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        reset(testObject)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        isCompleted = false
        exception = null
    }

    fun <T> Flow<T>.noticeCompletion() = onCompletion { isCompleted = true }

    fun <T> Flow<T>.noticeError() = catch { exception = it }

    suspend fun <T> Flow<T>.assertValue(value: T) = collect {
        assertEquals(value, it)
    }

    suspend fun <T> Flow<T>.assertValues(values: List<T>) {
        assertEquals(
            values,
            toList()
        )
    }

    fun assertExpectedException() {
        assertNotNull(exception)
        assertTrue(exception is ExpectedException)
    }

    fun assertNoExceptions() {
        assertNull(exception)
    }

    fun assertCompleted() {
        assertTrue(isCompleted)
    }

    fun assertNotCompleted() {
        assertFalse(isCompleted)
    }
}
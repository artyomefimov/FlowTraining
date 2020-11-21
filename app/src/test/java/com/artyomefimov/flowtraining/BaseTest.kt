package com.artyomefimov.flowtraining

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.mockito.Mockito
import org.mockito.Mockito.reset

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
open class BaseTest {

    protected val testObject: TestObject = Mockito.spy(TestObject())
    protected var isCompleted = false
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
    }

    fun <T> Flow<T>.noticeCompletion() = onCompletion { isCompleted = true }
}
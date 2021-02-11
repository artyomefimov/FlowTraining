package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.ExpectedException
import kotlin.reflect.KClass

class TestStatusController {
    private var isCompleted = false
    private var exception: Throwable? = null

    fun noticeException(exception: Throwable) {
        this.exception = exception
    }

    fun hasException(exceptionClass: KClass<ExpectedException>): Boolean {
        return exception != null && exceptionClass.isInstance(exception)
    }

    fun noExceptions() = exception == null

    fun noticeCompletion() {
        isCompleted = true
    }

    fun isCompleted() = isCompleted

    fun reset() {
        isCompleted = false
        exception = null
    }
}
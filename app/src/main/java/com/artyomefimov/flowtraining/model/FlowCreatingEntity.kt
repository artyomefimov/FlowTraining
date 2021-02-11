package com.artyomefimov.flowtraining.model

import kotlinx.coroutines.delay

open class FlowCreatingEntity {
    open suspend fun expensiveMethod(): Int {
        delay(3000L)
        return Int.MAX_VALUE
    }

    open suspend fun anotherExpensiveMethod(): Int {
        delay(3000L)
        return Int.MAX_VALUE
    }

    open fun unstableMethod(condition: Boolean): Int {
        if (condition)
            throw ExpectedException()
        return Int.MAX_VALUE
    }
}
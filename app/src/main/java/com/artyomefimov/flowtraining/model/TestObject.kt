package com.artyomefimov.flowtraining.model

import kotlinx.coroutines.delay

open class TestObject {

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

    open fun requestApiEntity(id: Int) = Entity(id, id.toString())
}
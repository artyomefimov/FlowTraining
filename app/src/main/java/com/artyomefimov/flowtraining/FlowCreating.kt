package com.artyomefimov.flowtraining

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

fun TestObject.valueToFlow(value: Int) = flow {
    emit(value)
}

fun TestObject.listToFlow(list: List<String>) = flow {
    list.forEach {
        emit(it)
    }
}

fun TestObject.expensiveMethodResult() = flow {
    emit(expensiveMethod())
}

fun TestObject.increasingSequenceWithDelays(
    initialDelay: Long,
    period: Long) = flow {
    delay(initialDelay)
    for (i in 0L..Long.MAX_VALUE) {
        emit(i)
        delay(period)
    }
}

fun TestObject.delayedZero(delay: Long) = flow {
    delay(delay)
    emit(0L)
}

fun TestObject.combinationExpensiveMethods(condition: Boolean) = flow<Int> {
    emit(expensiveMethod())
    emit(anotherExpensiveMethod())
    emit(unstableMethod(condition))
}

fun TestObject.withoutAnyEvents() = flow<Int> {
    delay(Long.MAX_VALUE)
}

fun TestObject.onlyComplete() = flow<Int> {  }

fun TestObject.onlyError() = flow<Nothing> {
    throw ExpectedException()
}
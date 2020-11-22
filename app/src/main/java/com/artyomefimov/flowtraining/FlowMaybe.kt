package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.TestObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

fun TestObject.positiveOrEmpty(value: Int) = flow {
    if (value > 0) emit(value)
}

fun TestObject.positiveOrEmptyFromFlow(intFlow: Flow<Int>) = flow {
    intFlow.firstOrNull()?.let { emitted ->
        if (emitted > 0)
            emit(emitted)
    }
}

fun TestObject.onlyOneElement(intFlow: Flow<Int>, defaultValue: Int) = flow {
    emit(intFlow.toList().firstOrNull() ?: defaultValue)
}
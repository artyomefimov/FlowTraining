package com.artyomefimov.flowtraining

import kotlinx.coroutines.flow.*

fun TestObject.handleErrorsWithDefaultValue(intFlow: Flow<Int>, defaultValue: Int) = flow {
    intFlow
        .catch { emit(defaultValue) }
        .onEach { emit(it) }
        .toList()
}

fun TestObject.handleErrorsWithFallbackFlow(intFlow: Flow<Int>, fallbackFlow: Flow<Int>) = flow {
    intFlow
        .catch { emitAll(fallbackFlow) }
        .onEach { emit(it) }
        .toList()
}

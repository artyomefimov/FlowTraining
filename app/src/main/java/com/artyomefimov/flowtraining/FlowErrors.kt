package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.TestObject
import kotlinx.coroutines.flow.*

/**
 * В случае ошибки передавать значение по умолчанию
 *
 * @param intFlow       [Flow] с произвольным количеством рандомных чисел, который
 *                      может передавать ошибку
 * @param defaultValue  значение по умолчанию, передавать в случае, если последовательность
 *                      intFlow завершилась с ошибкой
 * @return [Flow] который эммитит значения из intFlow, либо
 * defaultValue
 */
fun TestObject.handleErrorsWithDefaultValue(intFlow: Flow<Int>, defaultValue: Int) = flow {
    intFlow
        .catch { emit(defaultValue) }
        .onEach { emit(it) }
        .toList()
}

/**
 * В случае ошибки переключаться на другую последовательность
 *
 * @param intFlow      [Flow] с произвольным количеством рандомных чисел, который
 *                           может передавать ошибку
 * @param fallbackFlow [Flow] последовательность, на которую нужно
 *                           переключиться в случае ошибки
 * @return [Flow] который эммитит значения из intFlow, либо fallbackFlow
 */
fun TestObject.handleErrorsWithFallbackFlow(intFlow: Flow<Int>, fallbackFlow: Flow<Int>) = flow {
    intFlow
        .catch { emitAll(fallbackFlow) }
        .onEach { emit(it) }
        .toList()
}

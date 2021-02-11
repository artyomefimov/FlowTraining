package com.artyomefimov.flowtraining

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

/**
 * Эммит только 1 положительного элемента либо пустая последовательность
 *
 * @param value любое произвольное число
 * @return [Flow] который эммитит значение value если оно положительное,
 * либо не эммитит ничего, если value отрицательное
 */
fun positiveOrEmpty(value: Int) = flow {
    if (value > 0) emit(value)
}

/**
 * Эммит только 1 положительного элемента либо пустая последовательность
 *
 * @param intFlow [Flow] который эммитит любое произвольное число
 * @return [Flow] который эммитит значение из intFlow если оно эммитит
 * положительное число, иначе не эммитит ничего
 */
fun positiveOrEmptyFromFlow(intFlow: Flow<Int>) = flow {
    intFlow.firstOrNull()?.let { emitted ->
        if (emitted > 0)
            emit(emitted)
    }
}

/**
 * Если intFlow не эммитит элемент, то возвращать defaultValue
 *
 * @param defaultValue произвольное число
 * @return [Flow] который эммитит значение из intFlow, либо
 * defaultValue если последовательность пустая
 */
fun onlyOneElement(intFlow: Flow<Int>, defaultValue: Int) = flow {
    emit(intFlow.toList().firstOrNull() ?: defaultValue)
}
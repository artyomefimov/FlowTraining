package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.ExpectedException
import com.artyomefimov.flowtraining.model.TestObject
import kotlinx.coroutines.flow.*

/**
 * Эммит только 1 положительного элемента либо ошибка [ExpectedException]
 *
 * @param value любое произвольное число
 * @return [Flow] который эммитит значение value если оно положительное,
 * либо ошибку [ExpectedException] если оно отрицательное
 */
fun TestObject.onlyOneElement(value: Int) = flow {
    if (value > 0) {
        emit(value)
    } else {
        throw ExpectedException()
    }
}

/**
 * Преобразование последовательности {@code Observable} в [Flow]
 *
 * @param flow [Flow] произвольная последовательность чисел
 * @return [Flow] который эммитит либо самый первый элемент последовательности
 * flow, либо ошибку [ExpectedException] в случае, если
 * последовательность пустая
 */
fun TestObject.firstElementOfSequence(flow: Flow<Int>) = flow {
    flow.firstOrNull()?.let { emit(it) } ?: throw ExpectedException()
}

/**
 * Сумма всех элементов последовательности
 *
 * @param intFlow [Flow] произвольная последовательность чисел
 * @return [Flow] который эммитит сумму всех элементов, либо 0 если последовательность
 * пустая
 */
fun TestObject.calculateSumOfValues(intFlow: Flow<Int>) = flow {
    val sum = intFlow.reduce { accumulator, value -> accumulator + value }
    emit(sum)
}

/**
 * Преобразование последовательности в список
 *
 * @param intFlow [Flow] произвольная последовательность чисел
 * @return {@link Single} который эммитит [List] со всеми элементами последовательности
 * intFlow
 */
fun TestObject.collectionOfValues(intFlow: Flow<Int>) = flow {
    emit(intFlow.toList())
}

/**
 * Проверка всех элементов на положительность
 *
 * @param intFlow [Flow] произвольная последовательность чисел
 * @return {@link Single} который эммитит true если все элементы последовательности
 * intFlow положительны, false если есть отрицательные элементы
 */
fun TestObject.allElementsArePositive(intFlow: Flow<Int>) = flow {
    val list = intFlow.toList()
    val onlyPositiveList = list.filter { it > 0 }

    emit(list.size == onlyPositiveList.size)
}
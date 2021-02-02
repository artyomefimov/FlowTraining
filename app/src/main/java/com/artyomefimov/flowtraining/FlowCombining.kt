package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.TestObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * Суммирование элементов двух последовательностей.
 *
 * @param flow1 [Flow] с произвольным количеством рандомных чисел
 * @param flow2 [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который эммитит числа, где i-й элемент равен сумме i-го элемента
 * flow1 и i-го элемента flow2.
 */
fun TestObject.summation(flow1: Flow<Int>, flow2: Flow<Int>) = flow {
    val zippedFlow = flow1.zip(
        other = flow2,
        transform = { value1, value2 -> value1 + value2 }
    )
    emit(zippedFlow.toList())
}

/**
 * Поиск элементов по выбранной строке и категории
 *
 * @param searchFlow   Последовательность поисковых строк (в приложении может быть
 * введёнными строками в поисковую строку)
 * @param categoryFlow Последовательность категорий, которые необходимо отобразить
 * @return [Flow]  который эммитит списки элементов, с учётом поисковой строки из
 * searchFlow и выбранной категории из categoryFlow
 */
fun TestObject.requestItems(searchFlow: Flow<String>, categoryFlow: Flow<Int>) = flow {
    val combined = combine(searchFlow, categoryFlow) { searchString, categoryId ->
        "$searchString -> $categoryId"
    }.toList()
    emit(combined)
}

/**
 * Композиция потоков, обращение с несколькими объектами [Flow], как с одним.
 *
 * @param flow1 [Flow] с произвольным количеством рандомных чисел
 * @param flow2 [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который эммитит элементы из flow1 и flow2
 */
@ExperimentalCoroutinesApi
fun TestObject.composition(flow1: Flow<Int>, flow2: Flow<Int>) = flow {
    emit(merge(flow1, flow2).toList())
}

/**
 * Дополнительный элемент перед всеми элементами потока
 *
 * @param firstItem     Первый элемент, который необходимо добавить
 * @param intFlow [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который сначала эммитит элемент firstItem, а потом все
 * элементы последовательности intFlow
 */
fun TestObject.additionalFirstItem(intFlow: Flow<Int>, firstItem: Int) = flow {
    emit(listOf(firstItem) + intFlow.toList())
}
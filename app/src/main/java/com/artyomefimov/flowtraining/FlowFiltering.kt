package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.TestObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Только положительные числа
 *
 * @param intFlow [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] только с положительными числами, отрицательные должны быть
 * отфильтрованы
 */
fun TestObject.onlyPositiveNumbers(intFlow: Flow<Int>) = flow {
    emit(intFlow.toList().filter { it > 0 })
}

/**
 * Эммит только последних значений
 *
 * @param count     Количество последних элементов, которые нужно эммитить
 * @param intFlow [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который эммитит последние значения
 */
fun TestObject.onlyLastValues(intFlow: Flow<Int>, count: Int) = flow {
    emit(intFlow.toList().takeLast(count))
}

/**
 * Эммит только первых значений
 *
 * @param count     Количество первых элементов, которые нужно эммитить
 * @param intFlow [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который эммитит первые значения
 */
fun TestObject.onlyFirstValues(intFlow: Flow<Int>, count: Int) = flow {
    emit(intFlow.take(count).toList())
}

/**
 * Отфильтровать первые значения
 *
 * @param count     Количество первых элементов, которые нужно отфильтровать
 * @param intFlow [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который эммитит значения из intFlow кроме первых
 * count значений
 */
fun TestObject.ignoreFirstValues(intFlow: Flow<Int>, count: Int) = flow {
    emit(intFlow.drop(count).toList())
}

/**
 * Только последний элемент из всех элементов во временном периоде
 *
 * @param period    Период в миллисекундах, за который необходимо произвести выборку
 *                    последнего элемента
 * @param intFlow   [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который эммитит максимум 1 значения за интервал period
 */
@FlowPreview
fun TestObject.onlyLastPerInterval(intFlow: Flow<Int>, period: Long) = flow {
    emit(intFlow.sample(period).toList())
}

/**
 * Ошибка при длительном ожидании элементов
 *
 * @param timeout Время ожидания в миллисекундах
 * @param intFlow [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который эммитит значения intFlow, или выдаёт ошибку,
 * если время ожидания превышает timeout
 */
@ExperimentalCoroutinesApi
fun TestObject.errorIfLongWait(intFlow: Flow<Int>, timeout: Long) = channelFlow {
    val result = mutableListOf<Int>()
    var startTimeOfPeriod = System.currentTimeMillis()
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    intFlow
        .onCompletion { offer(result) }
        .onEach { value ->
            val delta = System.currentTimeMillis() - startTimeOfPeriod
            if (delta >= timeout) {
                offer(result)
                currentCoroutineContext().cancel()
            } else {
                result.add(value)
                startTimeOfPeriod = System.currentTimeMillis()
            }
        }
        .launchIn(scope)
}

/**
 * Значения без повторений
 *
 * @param intFlow [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который эммитит значения intFlow, но без повторяющихся
 * значений
 */
fun TestObject.ignoreDuplicates(intFlow: Flow<Int>) = flow {
    emit(intFlow.toList().distinct())
}

/**
 * Игноритуются повторяющиеся элементы, которые идут подряд
 *
 * @param intFlow [Flow] с произвольным количеством рандомных чисел
 * @return [Flow] который эммитит значения intFlow, но если новое значение
 * повторяет предыдущее, оно пропускается
 */
fun TestObject.onlyChangedValues(intFlow: Flow<Int>) = flow {
    emit(intFlow.distinctUntilChanged().toList())
}
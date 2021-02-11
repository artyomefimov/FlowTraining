package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.ExpectedException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

open class FlowCreating {

    /**
     * Эммит одного элемента
     *
     * @param value - Произвольное число
     * @return [Flow], который эммитит только значение {@code value}
     */
    fun valueToFlow(value: Int) = flow {
        emit(value)
    }

    /**
     * Эммит произвольного количества чисел в [Flow]
     *
     * @param values - Произвольное количество чисел
     * @return [Flow], который эммитит по порядку все числа, переданные в аргументы метода
     */
    fun varargToFlow(vararg values: Int) = values.asFlow()

    /**
     * Эммит элементов списка в [Flow]
     *
     * @param list - Список произвольных строк
     * @return [Flow], который эммитит по порядку все строки из заданного массива
     */
    fun listToFlow(list: List<String>) = flow {
        list.forEach {
            emit(it)
        }
    }

    /**
     * Выполнение метода с длительными вычислениями: {@link #expensiveMethod()}. Необходимо, чтобы метод
     * вызывался только при подписке на Flow
     *
     * @return [Flow] - который эммитит результат выполнения метода
     * {@link #expensiveMethod()}
     */
    fun expensiveMethodResult() = flow {
        emit(expensiveMethod())
    }

    /**
     * Возрастающая последовательность, начинающаяся с нуля с первоначальной задержкой и заданным
     * интервалом
     *
     * @return [Flow] - который эммитит возрастающую последовательность значений,
     * начиная с 0L, пока не произойдёт отписка.
     * Значения начинают эммититься с задержкой {@code initialDelay} миллисекунд и каждый
     * последующий с интервалом {@code period} миллисекунд.
     */
    fun increasingSequenceWithDelays(
        initialDelay: Long,
        period: Long
    ) = flow {
        delay(initialDelay)
        for (i in 0L..Long.MAX_VALUE) {
            emit(i)
            delay(period)
        }
    }

    /**
     * Возращение значения 0L с заданной задержкой
     *
     * @param delay - Задержка
     * @return [Flow] который эммитит только одно значение 0L с указанной
     * задержкой {@code delay}
     */
    fun delayedZero(delay: Long) = flow {
        delay(delay)
        emit(0L)
    }

    /**
     * Последовательный вызов нескольких методов с длительными вычислениями.
     *
     * @param condition - условие, которое необходимо передавать в {@code unstableMethod}
     * @return [Flow] который последовательно эммитит результаты выполнения методов, в
     * следующем порядке:
     * 1. {@link #expensiveMethod()}
     * 2. {@link #alternativeExpensiveMethod()}
     * 3. {@link #unstableMethod(boolean)}
     */
    fun combinationExpensiveMethods(condition: Boolean) = flow {
        emit(expensiveMethod())
        emit(anotherExpensiveMethod())
        emit(unstableMethod(condition))
    }

    /**
     * Без каких либо событий
     *
     * @return [Flow] который не эммитит ни одного элемента и не завершается
     */
    fun withoutAnyEvents() = flow<Int> {
        delay(Long.MAX_VALUE)
    }

    /**
     * Пустая последовательность
     *
     * @return [Flow] который не эммитит значения, а только завершается
     */
    fun onlyComplete() = flow<Int> { }

    /**
     * Только одна ошибка
     *
     * @return [Flow] который не эммитит значения, только бросает [ExpectedException]]
     */
    fun onlyError() = flow<Nothing> {
        throw ExpectedException()
    }

    /**
     * Тяжелый метод, который возвращает [Int] значение после задержки
     */
    open suspend fun expensiveMethod(): Int {
        delay(3000L)
        return Int.MAX_VALUE
    }

    /**
     * Тяжелый метод, который возвращает [Int] значение после задержки
     */
    open suspend fun anotherExpensiveMethod(): Int {
        delay(3000L)
        return Int.MAX_VALUE
    }

    /**
     * Возвращает [Int] значение или [ExpectedException] в зависимости от переданного [condition]
     */
    open fun unstableMethod(condition: Boolean): Int {
        if (condition)
            throw ExpectedException()
        return Int.MAX_VALUE
    }
}
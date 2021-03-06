package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.Entity
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

open class FlowTransforming {

    /**
     * Преобразование чисел в строки
     *
     * @param intFlow - источник
     * @return [Flow] - который эммитит строки,
     * преобразованные из чисел в {@code intObservable}
     */
    fun transformIntToString(intFlow: Flow<Int>) = flow {
        emit(intFlow.map { it.toString() }.toList())
    }

    /**
     * Преобразование intFlow, который эмитит идентификаторы сущностей в сами
     * сущности, которые должны быть получены с помощью метода {@link #requestApiEntity(int)}
     *
     * @param intFlow - идентификаторы сущностей
     * @return [Flow] эммитит сущности, соответствующие идентификаторам из intFlow
     */
    @FlowPreview
    fun getPairById(intFlow: Flow<Int>) = flow {
        val mapped = intFlow.flatMapConcat {
            flowOf(requestApiEntity(it))
        }.toList()
        emit(mapped)
    }

    /**
     * Объединить элементы, полученные из intFlow в списки [List] с максимальным
     * размером bufferSize
     *
     * @param bufferSize      максимальный размер списка элементов
     * @param intFlow  [Flow] с произвольным количеством рандомных чисел
     * @return {@code Observable} который эммитит списки чисел из intFlow
     */
    fun collectsIntsToLists(intFlow: Flow<Int>, bufferSize: Int) = flow {
        val chunked = intFlow.toList().chunked(bufferSize)
        emit(chunked)
    }

    /**
     * Распределение имён из namesFlow по первой букве имени, в отдельные списки [List]
     *
     * @param namesFlow - [Flow] с именами
     * @return [Flow] который эммитит Map<Char, List<String>> - сгруппированный
     * поток имён объединённых первой буквой в имени
     */
    fun distributeNamesByFirstLetter(namesFlow: Flow<String>) = flow {
        val groupedByFirstLetter = namesFlow.toList().groupBy { it[0] }
        emit(groupedByFirstLetter)
    }

    /**
     * Получает [Entity] с переданным [id]
     */
    open fun requestApiEntity(id: Int) = Entity(id, id.toString())
}
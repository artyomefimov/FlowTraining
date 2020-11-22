package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.TestObject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

fun TestObject.transformIntToString(intFlow: Flow<Int>) = flow {
    emit(intFlow.map { it.toString() }.toList())
}

@FlowPreview
fun TestObject.getPairById(intFlow: Flow<Int>) = flow {
    val mapped = intFlow.flatMapConcat {
        flowOf(it to it.toString())
    }.toList()
    emit(mapped)
}

fun TestObject.collectsIntsToLists(intFlow: Flow<Int>, bufferSize: Int) = flow {
    val chunked = intFlow.toList().chunked(bufferSize)
    emit(chunked)
}

fun TestObject.distributeNamesByFirstLetter(namesFlow: Flow<String>) = flow {
    val groupedByFirstLetter = namesFlow.toList().groupBy { it[0] }
    emit(groupedByFirstLetter)
}
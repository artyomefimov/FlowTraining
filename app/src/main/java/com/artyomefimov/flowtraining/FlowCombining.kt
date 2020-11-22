package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.TestObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

fun TestObject.summation(flow1: Flow<Int>, flow2: Flow<Int>) = flow {
    val zippedFlow = flow1.zip(
        other = flow2,
        transform = { value1, value2 -> value1 + value2 }
    )
    emit(zippedFlow.toList())
}

fun TestObject.requestItems(searchFlow: Flow<String>, categoryFlow: Flow<Int>) = flow {
    val combined = combine(searchFlow, categoryFlow) { searchString, categoryId ->
        "$searchString -> $categoryId"
    }.toList()
    emit(combined)
}

@ExperimentalCoroutinesApi
fun TestObject.composition(flow1: Flow<Int>, flow2: Flow<Int>) = flow {
    emit(merge(flow1, flow2).toList())
}

fun TestObject.additionalFirstItem(intFlow: Flow<Int>, firstItem: Int) = flow {
    emit(listOf(firstItem) + intFlow.toList())
}
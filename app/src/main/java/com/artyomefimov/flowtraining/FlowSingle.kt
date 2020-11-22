package com.artyomefimov.flowtraining

import kotlinx.coroutines.flow.*

fun TestObject.onlyOneElement(value: Int) = flow {
    if (value > 0) {
        emit(value)
    } else {
        throw ExpectedException()
    }
}

fun TestObject.firstElementOfSequence(flow: Flow<Int>) = flow {
    flow.firstOrNull()?.let { emit(it) } ?: throw ExpectedException()
}

fun TestObject.calculateSumOfValues(intFlow: Flow<Int>) = flow {
    val sum = intFlow.reduce { accumulator, value -> accumulator + value }
    emit(sum)
}

fun TestObject.collectionOfValues(intFlow: Flow<Int>) = flow {
    emit(intFlow.toList())
}

fun TestObject.allElementsArePositive(intFlow: Flow<Int>) = flow {
    val list = intFlow.toList()
    val onlyPositiveList = list.filter { it > 0 }

    emit(list.size == onlyPositiveList.size)
}
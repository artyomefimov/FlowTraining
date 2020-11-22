package com.artyomefimov.flowtraining

import com.artyomefimov.flowtraining.model.TestObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun TestObject.onlyPositiveNumbers(intFlow: Flow<Int>) = flow {
    emit(intFlow.toList().filter { it > 0 })
}

fun TestObject.onlyLastValues(intFlow: Flow<Int>, count: Int) = flow {
    emit(intFlow.toList().takeLast(count))
}

fun TestObject.onlyFirstValues(intFlow: Flow<Int>, count: Int) = flow {
    emit(intFlow.take(count).toList())
}

fun TestObject.ignoreFirstValues(intFlow: Flow<Int>, count: Int) = flow {
    emit(intFlow.drop(count).toList())
}

@FlowPreview
fun TestObject.onlyLastPerInterval(intFlow: Flow<Int>, period: Long) = flow {
    emit(intFlow.sample(period).toList())
}

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

fun TestObject.ignoreDuplicates(intFlow: Flow<Int>) = flow {
    emit(intFlow.toList().distinct())
}

fun TestObject.onlyChangedValues(intFlow: Flow<Int>) = flow {
    emit(intFlow.distinctUntilChanged().toList())
}
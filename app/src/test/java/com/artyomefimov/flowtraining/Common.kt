package com.artyomefimov.flowtraining

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.junit.Assert

suspend fun <T> Flow<T>.assertValue(value: T) = collect {
    Assert.assertEquals(value, it)
}

suspend fun <T> Flow<T>.assertValues(values: List<T>) =
    Assert.assertEquals(values, toList())

fun <T> Flow<T>.failOnError() = catch { Assert.fail() }
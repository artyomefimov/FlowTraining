package com.artyomefimov.flowtraining.model

open class FlowTransformingEntity {
    open fun requestApiEntity(id: Int) = Entity(id, id.toString())
}

data class Entity(
    val id: Int,
    val name: String
)
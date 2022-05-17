package com.bmatjik.processor.model


internal data class ProcessedField(
    val imports:List<String>,
    val text:String
)
internal fun kspPackage() = "com.bmatjik.ksp"

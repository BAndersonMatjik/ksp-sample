package com.bmatjik.annotations

@Target( AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Listed(val name: String="")

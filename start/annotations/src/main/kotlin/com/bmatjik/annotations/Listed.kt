package com.bmatjik.annotations

@Target( AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Listed(val name: String="")


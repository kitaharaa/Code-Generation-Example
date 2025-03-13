package com.boichuk.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class GenerateUpdateFunctions(val viewStateClassName: String)
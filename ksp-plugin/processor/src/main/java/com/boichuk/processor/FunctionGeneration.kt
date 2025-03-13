package com.boichuk.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import java.io.File

fun main() {
    // TODO vary the package name
    val outputDir = File("app/src/main/java/com/boichuk/codegenerationexample").apply { mkdirs() }
    val updateFile = generateExtensionFunction()
    updateFile.writeTo(outputDir)
}

private fun generateExtensionFunction(): FileSpec {
    val viewState = ClassName("com.boichuk.codegenerationexample" ,"MainViewModel")

    // TODO change name every time
    val viewStatePropertyName = "counter"
    val functionParameterName = "newValue"

    val intUpdateFunction = FunSpec
        .builder("updateCounterState") // TODO the function name: use update${propertyName}State. propertyName should be uppercase!
        .addCode("_viewState.update { it.copy($viewStatePropertyName = $functionParameterName) }")
        .receiver(viewState)
        .addParameter(functionParameterName, INT)
        .build()
    return FileSpec
        .builder("", "ViewStateUpdateFunction")
        .addImport("kotlinx.coroutines.flow", "update")
        .addFunction(intUpdateFunction)
        .build()
}
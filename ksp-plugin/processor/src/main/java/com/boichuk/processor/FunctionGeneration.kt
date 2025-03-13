package com.boichuk.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import java.io.File

fun main() {
    // TODO vary the package name
//    val outputDir = File("app/src/main/java/com/boichuk/codegenerationexample").apply { mkdirs() }
//    val updateFile = generateExtensionFunction()
//    updateFile.writeTo(outputDir)
}

fun generateExtensionFunction(codeGenerator: CodeGenerator) {
    val viewState = ClassName("com.boichuk.codegenerationexample", "MainViewModel")
    val viewStatePropertyName = "counter"
    val functionParameterName = "newValue"

    val intUpdateFunction = FunSpec
        .builder("update${viewStatePropertyName.capitalize()}State")
        .addCode("_viewState.update { it.copy($viewStatePropertyName = $functionParameterName) }")
        .receiver(viewState)
        .addParameter(functionParameterName, INT)
        .build()

    val fileSpec = FileSpec
        .builder("com.boichuk.generated", "ViewStateUpdateFunction")
        .addImport("kotlinx.coroutines.flow", "update")
        .addFunction(intUpdateFunction)
        .build()

    // Generate file using KSP's code generator
    val file = codeGenerator.createNewFile(
        Dependencies(false),
        "app/src/main/java/com/boichuk/codegenerationexample",
        "ViewStateUpdateFunction"
    )

    file.bufferedWriter().use { writer ->
        fileSpec.writeTo(writer)
    }
}
/*

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
}*/

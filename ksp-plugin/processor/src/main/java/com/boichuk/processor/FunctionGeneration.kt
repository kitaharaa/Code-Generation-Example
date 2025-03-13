package com.boichuk.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ksp.writeTo

fun generateExtensionFunction(codeGenerator: CodeGenerator) {
    val packageName = "com.boichuk.codegenerationexample"
    val viewModelName = "MainViewModel"
    val updatePropertyName = "counter"
    val functionParameterName = "newValue"

    val viewState = ClassName(packageName, viewModelName)

    val intUpdateFunction = FunSpec
        .builder("update${updatePropertyName.capitalize()}State")
        .addComment("packageName=$packageName, updatePropertyName=$updatePropertyName")
        .addCode("_viewState.update { it.copy($updatePropertyName = $functionParameterName) }")
        .receiver(viewState)
        .addParameter(functionParameterName, INT)
        .build()

    val fileSpec = FileSpec
        .builder("com.generated.functions", "${viewModelName}ViewStateUpdateFunction")
        .addImport("kotlinx.coroutines.flow", "update")
        .addFunction(intUpdateFunction)
        .build()

    fileSpec.writeTo(codeGenerator, false)
}
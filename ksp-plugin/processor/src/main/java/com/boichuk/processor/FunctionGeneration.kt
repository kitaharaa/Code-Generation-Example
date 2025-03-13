package com.boichuk.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ksp.writeTo

fun generateFile(
    viewModelName: String,
    codeGenerator: CodeGenerator,
    functions: List<FunSpec>,
    logger: KSPLogger,
) {
    val fileSpec = FileSpec
        .builder("com.generated.functions", "${viewModelName}UpdateFunction")
        .addImport("kotlinx.coroutines.flow", "update")
        .apply { functions.forEach {
            logger.warn("$viewModelName: funName=${it.name}")
            addFunction(it) }
        }

    fileSpec.build().writeTo(codeGenerator, false)
}

fun generateFunction(
    viewModelName: String,
    propertyName: String,
    type: String,
    packageName: String
): FunSpec {
    val viewState = ClassName(packageName, viewModelName)
    val className = ClassName.bestGuess(type)
    val functionParameterName = "newValue"

    val updateFunction = FunSpec
        .builder("update${propertyName.capitalize()}State")
        .addComment("packageName=$packageName, updatePropertyName=$propertyName")
        .addCode("_viewState.update { it.copy($propertyName = $functionParameterName) }")
        .receiver(viewState)
        .addParameter(functionParameterName, INT)
//        .addParameter(functionParameterName, className)
        .build()

    return updateFunction
}
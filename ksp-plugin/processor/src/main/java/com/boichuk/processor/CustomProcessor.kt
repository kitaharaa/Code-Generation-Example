package com.boichuk.processor

import com.boichuk.annotation.GenerateUpdateFunctions
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

class CustomProcessor(private val logger: KSPLogger, private val codeGenerator: CodeGenerator) :
    SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols =
            resolver.getSymbolsWithAnnotation(GenerateUpdateFunctions::class.qualifiedName.orEmpty())
                .filterIsInstance<KSClassDeclaration>()
                .filter { it.classKind == ClassKind.CLASS }
                .toSet()

        logger.warn("ClassSymbolsCount=${symbols.count()}")

        symbols.forEach { classDeclaration ->
            val packageName = classDeclaration.containingFile?.packageName?.asString().orEmpty()
            val viewModelName = classDeclaration.simpleName.asString()

            logger.warn("CodeGeneration for $viewModelName in $packageName")

            val generateAnnotation =
                classDeclaration.annotations.filter {
                    val qualifiedName =  it.annotationType.resolve().declaration.qualifiedName?.asString()
                    qualifiedName == GenerateUpdateFunctions::class.qualifiedName
                }

            generateAnnotation.forEach { ann ->
                val viewStateClassName =
                    ann.arguments.firstOrNull()?.value?.toString() ?: return@forEach

                val viewStateClass = classDeclaration.declarations
                    .filterIsInstance<KSClassDeclaration>()
                    .firstOrNull { it.simpleName.asString() == viewStateClassName }

                if (viewStateClass == null) {
                    logger.warn("ViewState class $viewStateClassName not found in $viewModelName")
                    return@forEach
                }

                val generatedFunctions = viewStateClass.getAllProperties().mapNotNull { property ->
                    generateFunction(
                        viewModelName = viewModelName,
                        propertyName = property.simpleName.asString(),
                        type = property.type.resolve().toString(),
                        packageName = packageName
                    )
                }

                logger.warn("$viewModelName: generatedFunctionsCount = ${generatedFunctions.count()}")

                if (generatedFunctions.toList().isNotEmpty()) {
                    generateFile(viewModelName, codeGenerator, generatedFunctions.toList(), logger)
                    logger.warn("$viewModelName: generatedFunctions=${generatedFunctions.count()}")
                } else {
                    logger.warn("$viewModelName: generatedFunctions=empty")
                }
            }
        }

        return symbols.filterNot { it.validate() }.toList()
    }
}
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
import java.io.File

class CustomProcessor(private val logger: KSPLogger, private val codeGenerator: CodeGenerator) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation(GenerateUpdateFunctions::class.qualifiedName.orEmpty())
            .toSet()
        val infoMap = mutableMapOf<String, String>()
        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            if (classDeclaration.classKind == ClassKind.CLASS) {
                val packageName = classDeclaration.containingFile?.packageName?.asString().orEmpty()
                val className =
                    classDeclaration.containingFile?.fileName?.replace(".kt", "").orEmpty()
                logger.warn("CodeGeneration: $className at $packageName")
//                main()
                generateExtensionFunction(codeGenerator)
                println("Annotation is detected: $className")
                // TODO get inner class and create update functions
                infoMap[className] = packageName
            }
        }

        updateTextFile(infoMap)
        // Filter out symbols that are not valid, e.g. annotated on a class
        val ret = symbols.filterNot { it.validate() }.toList()
        return ret
    }

    fun updateTextFile(infoMap: MutableMap<String, String>) {
        val file = File("CodeGenerationInfo.txt")
        val content = "Last update function generation was at ${System.currentTimeMillis()}"
        file.writeText(content + "\n$infoMap")
    }
}
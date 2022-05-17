package com.bmatjik.processor.provider

import com.bmatjik.annotations.Listed
import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo


@AutoService(SymbolProcessorProvider::class)
class FieldToListProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        FieldToListProcessor(environment.logger, environment.options, environment.codeGenerator)

}

class FieldToListProcessor(
    private val logger: KSPLogger, private val options: Map<String, String>, private val codeGenerator: CodeGenerator
) : SymbolProcessor {
    private lateinit var intType: KSType

    override fun process(resolver: Resolver): List<KSAnnotated> {
        intType = resolver.builtIns.intType
        resolver.getSymbolsWithAnnotation(Listed::class.qualifiedName!!)
            .forEach { it.accept(Visitor(), Unit) }
        return emptyList()
    }

    inner class Visitor : KSVisitorVoid() {

        private lateinit var ksType: KSType
        private val summables: MutableList<String> = mutableListOf()
        private lateinit var packageName: String
        private val arrayListOfString = ArrayList::class.parameterizedBy(String::class)
        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            logger.info("Visit Property ${property.simpleName.asString()}")
            summables.add(property.simpleName.asString())
        }
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

            if (classDeclaration.modifiers.none { it == Modifier.DATA }) {
                logger.error("@Listed must target a data class.", classDeclaration)
            }

            val arrayList = classDeclaration.getAllProperties().filter {
                it.extensionReceiver == null
            }.forEach { it.accept(this, Unit) }

            ksType = classDeclaration.asType(emptyList())
            packageName = classDeclaration.packageName.asString()
            if (summables.isEmpty()) {
                return
            }
            FileSpec.builder(classDeclaration.packageName.asString(), "${classDeclaration.simpleName.asString()}Ext")
                .addFunction(
                    FunSpec.builder("sumField").returns(List::class.parameterizedBy(String::class))
                        .receiver(classDeclaration.asType(emptyList()).toTypeName())
                        .addStatement("val result = %T()",arrayListOfString)
                        .apply {
                            summables.forEach {
                                addStatement("result.add(%S)",it)
                            }
                        }
                        .addStatement("return result",summables)
                        .build()
                ).build().writeTo(codeGenerator, false)
        }
    }
}

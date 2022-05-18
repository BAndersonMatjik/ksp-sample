package com.bmatjik.processor.provider

import com.bmatjik.annotations.IgnoreField
import com.bmatjik.annotations.Listed
import com.bmatjik.processor.utils.getAnnotationsByType
import com.bmatjik.processor.utils.isAnnotationPresent
import com.google.auto.service.AutoService
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSTopDownVisitor
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
            .filter { it.validate() && it is KSClassDeclaration }
            .toList()
            .apply {
                logger.info("Class Size ${this.size}")
            }
            .forEach { ks ->
                ks.accept(Visitor(), Unit)
            }
        return emptyList()
    }

    inner class Visitor : KSVisitorVoid() {

        private val summables: MutableList<String> = mutableListOf()
        private lateinit var packageName: String
        private val arrayListOfString = ArrayList::class.parameterizedBy(String::class)
        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            logger.info("Visit Property ${property.simpleName.asString()}")
            summables.add(property.simpleName.asString())
            logger.info("summable size array ==> ${summables.size}")
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

            if (classDeclaration.modifiers.none { it == Modifier.DATA }) {
                logger.error("@Listed must target a data class.", classDeclaration)
                return
            }

            classDeclaration.getAllProperties().filter {
                it.extensionReceiver == null
            }.toList().apply {
                logger.info("visitClassDeclaration ${this.size }")
            }.forEach {
                it.accept(this, Unit) }

            packageName = classDeclaration.packageName.asString()
            if (summables.isEmpty()) {
                return
            }

            val companion = TypeSpec.companionObjectBuilder()
                .addFunction(FunSpec.builder("members").returns(List::class.parameterizedBy(String::class))
                    .addStatement("val result = %T()", arrayListOfString)
                    .apply {
                        summables.forEach {
                            addStatement("result.add(%S)", it)
                        }
                    }
                    .addStatement("return result", summables)
                    .build())
                .build()

            val ext = TypeSpec.classBuilder("${classDeclaration.simpleName.asString()}Ext")
                .addType(companion)
                .build()


            FileSpec.builder(classDeclaration.packageName.asString(), "${classDeclaration.simpleName.asString()}Ext")
                .addType(ext).build().writeTo(codeGenerator, false)
        }
    }
}

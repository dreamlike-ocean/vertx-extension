package top.dreamlike.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import jakarta.ws.rs.Path
import top.dreamlike.ksp.model.ClassData.Companion.parse
import top.dreamlike.ksp.model.FunctionData.Companion.parse
import java.io.OutputStreamWriter

class VertxJaxRsSymbolProcessor(val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    companion object {
        val Object_Method = setOf("equals", "hashCode", "toString", "<init>")
        val Not_Allow_Fun_Modifier = setOf(
            Modifier.PRIVATE,
            Modifier.ACTUAL,
            Modifier.INTERNAL,
            Modifier.PROTECTED,
            Modifier.OPERATOR,
            Modifier.INFIX,
            Modifier.INLINE,
            Modifier.EXTERNAL
        )

        lateinit var logger:KSPLogger

        fun generateProxyClassName(controllerSimple: String) : String {
            return "${controllerSimple}_proxy_"
        }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val allController = resolver
            .getSymbolsWithAnnotation(Path::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
        if (!allController.iterator().hasNext()) return emptyList()

        logger = environment.logger
        allController.forEach { generateController(resolver, it) }

        // 生成的代码无需参加下一轮
        return emptyList()
    }

    fun generateController(resolver: Resolver, controller: KSClassDeclaration) {
        if (controller.classKind != ClassKind.CLASS) {
            return
        }

        val classData = controller.parse()
        environment.logger.warn("parse res: $classData")
        val dependencies = Dependencies(false, controller.containingFile!!)
        val functionStatement = controller.getAllFunctions()
            .filter { !it.modifiers.contains(Modifier.PRIVATE) && !Object_Method.contains(it.qualifiedName!!.getShortName()) }
            .map {it.parse(classData)}
            .mapIndexed { index, it ->
               it.generateRouteHandle(index)
            }
        //单个Controller生成的挂载Router的代码
        val res = functionStatement.joinToString("\n")
        val proxyClassSource = Template.proxyClass(res, classData)
        logger.warn(proxyClassSource)
        environment.codeGenerator
            .createNewFile(dependencies, classData.packagePath, generateProxyClassName(classData.simpleName))
            .use {
                OutputStreamWriter(it).use { writer ->
                    writer.write(proxyClassSource)
                }
            }
    }




}
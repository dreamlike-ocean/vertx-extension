package top.dreamlike

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import jakarta.ws.rs.Path
import top.dreamlike.model.ClassData.Companion.parse
import top.dreamlike.model.FunctionData.Companion.parse

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
        val functionInfo = controller.getAllFunctions()
            .filter { !it.modifiers.contains(Modifier.PRIVATE) && !Object_Method.contains(it.qualifiedName!!.getShortName()) }
            .map {it.parse(classData)}
            .forEach {
                logger.warn("current function : ${it}")
            }

    }

    /**
     * @Path
     * @GET
     * @POST
     * @PUT
     * @DELETE
     * @HEAD
     * @PATCH
     * @OPTIONS
     * @Consumes
     * @Produces
     * @QueryParam
     * @PathParam
     * @HeaderParam
     * @MatrixParam
     * @CookieParam
     * @FormParam
     */
    fun generateFunction(resolver: Resolver, function: KSFunctionDeclaration, rootPath: String): String {
        var simpleName = function.closestClassDeclaration()!!.simpleName

        return ""
    }


}
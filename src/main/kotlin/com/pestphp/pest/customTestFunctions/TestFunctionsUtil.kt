package com.pestphp.pest.customTestFunctions

import com.intellij.microservices.url.parameters.a
import com.intellij.patterns.StringPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.descendantsOfType
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.impl.*
import com.jetbrains.rd.util.string.printToString
import com.pestphp.pest.customExpectations.generators.Method
import com.pestphp.pest.customExpectations.generators.Parameter
import com.jetbrains.php.lang.psi.elements.Parameter as PsiParameter
import com.pestphp.pest.extendName
import com.pestphp.pest.isPestTestFunction
import com.pestphp.pest.isPestTestMethodReference
import com.pestphp.pest.isPestTestReference

fun PsiElement.isPestCustomTestFunction(): Boolean {
    if (this !is FunctionImpl) {
        return false
    }

    val pest = this.descendantsOfType<FunctionReferenceImpl>()
        .firstOrNull { it.isPestTestReference() }

    return pest is FunctionReferenceImpl
}

val PsiFile.customTestFunctions: List<FunctionImpl>
    get() {
        if (this !is PhpFile) return emptyList()

        val element = this.firstChild

        return element.children.filterIsInstance<PhpNamespace>()
                .mapNotNull { it.statements }
                .getOrElse(
                        0
                ) { element }
                .children
                .filterIsInstance<FunctionImpl>()
                .filter { it.isPestCustomTestFunction() }
    }

fun FunctionImpl.toMethod(): Method {
    return Method(
            this.name,
            this.type,
            this.parameters.map { parameter ->
                Parameter(
                        parameter.name,
                        parameter.type,
                        parameter.defaultValuePresentation
                )
            },
    );
}

val FunctionImpl.customFunctionTestTestnamePattern: String?
    get() {
        val pestCall = this.descendantsOfType<FunctionReferenceImpl>()
                .firstOrNull { it.isPestTestReference() } ?: return null

        val firstArgStringPattern = pestCall.getParameter(0)

        return firstArgStringPattern?.getPestCustomTestFunctionPatternPart(this.parameters)
    }

fun PsiElement.getPestCustomTestFunctionPatternPart(parameters: Array<PsiParameter>): String {
    return when (this) {
        is StringLiteralExpressionImpl -> this.getPestCustomTestFunctionPatternPart(parameters)
        is ConcatenationExpressionImpl -> this.getPestCustomTestFunctionPatternPart(parameters)
        is VariableImpl -> this.getPestCustomTestFunctionPatternPart(parameters)
        else -> throw Exception("Pest custom function test name can be generated only for string test name.")
    }
}

fun StringLiteralExpressionImpl.getPestCustomTestFunctionPatternPart(parameters: Array<PsiParameter>): String {
    if (this.isSingleQuote) {
        return this.contents
    }

    val paramsMap = parameters.map { it.name }.toSet()

    val discoveredParamNames = "\\$[a-zA-Z_\\x7f-\\xff][a-zA-Z0-9_\\x7f-\\xff]*".toRegex()
            .findAll(this.contents)
            .map { it.groupValues }
            .flatten()
            .toList()

    var result = this.contents

    for (variable in discoveredParamNames) {
        val variableName = variable.removePrefix("$")

        if (! paramsMap.contains(variableName)) {
            throw Exception("$variableName is not in the list of function parameters")
        }

        result = result.replace("{$variable}", variable)

        result = result.replace(variable, variable.replace('$', ':'))
    }

    return result
}

fun VariableImpl.getPestCustomTestFunctionPatternPart(parameters: Array<PsiParameter>): String {
    val paramsMap = parameters.map { it.name }.toSet()
    if (! paramsMap.contains(this.name)) {
        throw Exception("${this.name} is not in the list of function parameters")
    }
    return ":".plus(this.name)
}

fun ConcatenationExpressionImpl.getPestCustomTestFunctionPatternPart(parameters: Array<PsiParameter>): String {
    return (this.leftOperand?.getPestCustomTestFunctionPatternPart(parameters) ?: "")
            .plus(this.rightOperand?.getPestCustomTestFunctionPatternPart(parameters) ?: "")
}

fun PsiElement.isPestCustomTestFunctionReference(): Boolean {
    if (this !is FunctionReferenceImpl) {
        return false
    }

    val index = FileBasedIndex.getInstance()

    val values = index
            .getValues(
                    CustomTestFunctionsIndex.key,
                    this.canonicalText,
                    GlobalSearchScope.projectScope(this.project)
            );

    return values.count() != 0
}

fun PsiElement.getPestCustomFunctionTestName(parametersList: ParameterList?): String? {
    if (this !is FunctionReferenceImpl) {
        return null
    }

    val index = FileBasedIndex.getInstance()

    val (method, testNamePattern) = index
            .getValues(
                    CustomTestFunctionsIndex.key,
                    this.canonicalText,
                    GlobalSearchScope.projectScope(this.project)
            )[0] ?: return null

    if (parametersList == null) {
        return testNamePattern
    }

    val parametersValues = parametersList.parameters

    var pattern = testNamePattern

    // [fn-parameter-name => parameter-value]
    val namedParamsMap = parametersValues
            .withIndex()
            .associate { (index, param) ->
                val argumentIdentifier = ParameterListImpl.getNameIdentifier(param)

                val argumentName = if (argumentIdentifier != null)
                    argumentIdentifier.text
                    else method.parameters[index]?.name

                if (argumentName == null) {
                    throw Exception("Cannot figure out parameter name")
                }

                Pair(argumentName, (param as StringLiteralExpressionImpl)?.contents)
            }

    for ((key, value) in namedParamsMap) {
        pattern = pattern.replace(":".plus(key), value)
    }

    return pattern
}


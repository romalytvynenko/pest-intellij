package com.pestphp.pest.customTestFunctions

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.descendantsOfType
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpNamespace
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
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
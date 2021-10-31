package com.pestphp.pest.customTestFunctions

import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.customExpects
import com.pestphp.pest.isPestTestReference
import com.pestphp.pest.toMethod

class CustomTestFunctionsDefinitionTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/customTestFunctions"
    }

    fun testCustomTestFunctionsAreFound() {
        val file = myFixture.configureByFile("CustomTestFunctionUsage.php")

        val call = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java).last()

        assertTrue(call.isPestCustomTestFunctionReference())
    }

    fun testNotCustomTestFunctionsArentFound() {
        val file = myFixture.configureByFile("CustomTestFunctionNotUsed.php")

        val call = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java).last()

        assertFalse(call.isPestCustomTestFunctionReference())
    }
}
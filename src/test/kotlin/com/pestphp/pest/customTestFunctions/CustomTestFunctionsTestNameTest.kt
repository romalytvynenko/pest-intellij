package com.pestphp.pest.customTestFunctions

import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.getPestTestName

class CustomTestFunctionsTestNameTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/customTestFunctions"
    }

    fun testCustomTestFunctionGivesCorrectTestNamePattern() {
        val file = myFixture.configureByFile("CustomTestFunction.php")

        val fn = PsiTreeUtil.findChildrenOfType(file, FunctionImpl::class.java).first()

        assertEquals(":expected is correct", fn.customFunctionTestTestnamePattern)
    }

    fun testFunctionWithStringConcatenationGivesCorrectTestNamePattern() {
        val file = myFixture.configureByFile("CustomTestFunctionWithStringConcatenation.php")

        val fn = PsiTreeUtil.findChildrenOfType(file, FunctionImpl::class.java).first()

        assertEquals(":expected is correct", fn.customFunctionTestTestnamePattern)
    }

    fun testFunctionWithMultipleStringConcatenationGivesCorrectTestNamePattern() {
        val file = myFixture.configureByFile("CustomTestFunctionWithMultipleStringConcatenation.php")

        val fn = PsiTreeUtil.findChildrenOfType(file, FunctionImpl::class.java).first()

        assertEquals(":expected is correct :wow", fn.customFunctionTestTestnamePattern)
    }

    fun testFunctionWithCurlyStringBracketsGivesCorrectTestNamePattern() {
        val file = myFixture.configureByFile("CustomTestFunctionWithCurlyStringBrackets.php")

        val fn = PsiTreeUtil.findChildrenOfType(file, FunctionImpl::class.java).first()

        assertEquals(":expected is correct", fn.customFunctionTestTestnamePattern)
    }

    fun testCustomTestFunctionGivesCorrectTestName() {
        val file = myFixture.configureByFile("CustomTestFunctionUsage.php")

        val call = PsiTreeUtil.findChildrenOfType(file, FunctionReferenceImpl::class.java).last()

        assertEquals("some is correct", call.getPestTestName())
    }
}
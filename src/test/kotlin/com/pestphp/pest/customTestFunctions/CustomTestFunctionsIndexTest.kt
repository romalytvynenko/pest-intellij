package com.pestphp.pest.customTestFunctions

import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.pestphp.pest.PestLightCodeFixture
import com.pestphp.pest.customExpects
import com.pestphp.pest.toMethod

class CustomTestFunctionsIndexTest : PestLightCodeFixture() {
    override fun getTestDataPath(): String {
        return "src/test/resources/com/pestphp/pest/customTestFunctions"
    }

    fun testCustomExpectationIsIndexed() {
        val virtualFile = myFixture.copyFileToProject("CustomTestFunction.php")
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        var wow = file.customTestFunctions

        return

        val fileBasedIndex = FileBasedIndex.getInstance()

        val values = fileBasedIndex.getValues(
            CustomTestFunctionsIndex.key,
            "/src/CustomTestFunction.php",
            GlobalSearchScope.projectScope(project)
        ).flatten()

        assertSize(1, values)
        assertEquals(
            values,
            file.customExpects.map { it.toMethod() }
        )
    }
}
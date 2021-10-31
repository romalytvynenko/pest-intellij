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

    fun testCustomTestFunctionsAreIndexed() {
        val virtualFile = myFixture.copyFileToProject("CustomTestFunction.php")
        val file = PsiManager.getInstance(project).findFile(virtualFile)!!

        val fileBasedIndex = FileBasedIndex.getInstance()

        val values = fileBasedIndex.getAllKeys(
            CustomTestFunctionsIndex.key,
            project
        )

        assertSize(1, values)
        assertEquals(
            values.toList(),
            file.customTestFunctions.map { it.name }
        )
    }
}
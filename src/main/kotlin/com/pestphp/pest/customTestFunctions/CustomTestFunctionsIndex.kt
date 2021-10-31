package com.pestphp.pest.customTestFunctions

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.PhpFile
import gnu.trove.THashMap

class CustomTestFunctionsIndex : FileBasedIndexExtension<String, MethodTestnamePair>() {
    companion object {
        val key = ID.create<String, MethodTestnamePair>("php.pest.custom_test_functions")
    }

    override fun getName(): ID<String, MethodTestnamePair> {
        return key
    }

    override fun getVersion(): Int {
        return 2
    }

    override fun getIndexer(): DataIndexer<String, MethodTestnamePair, FileContent> {
        return DataIndexer { inputData ->
            val file = inputData.psiFile

            if (file !is PhpFile) {
                return@DataIndexer mapOf()
            }

            val customTestFunctions = file
                    .customTestFunctions
                    .filter { it.customFunctionTestTestnamePattern != null }
                    .map { Pair(it.toMethod(), it.customFunctionTestTestnamePattern as String) }

            val map = THashMap<String, MethodTestnamePair>()
            for((method, testname) in customTestFunctions) {
                map[method.name] = Pair(method, testname)
            }
            return@DataIndexer map
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<MethodTestnamePair> {
        return MethodTestnamePairExternalizer.INSTANCE
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return object : DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
            override fun acceptInput(file: VirtualFile): Boolean {
                return true
            }
        }
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }
}
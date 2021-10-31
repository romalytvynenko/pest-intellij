package com.pestphp.pest.customTestFunctions

import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.pestphp.pest.customExpectations.externalizers.ListDataExternalizer
import com.pestphp.pest.customExpectations.externalizers.MethodDataExternalizer
import com.pestphp.pest.customExpectations.externalizers.ParameterDataExternalizer
import com.pestphp.pest.customExpectations.externalizers.PhpTypeDataExternalizer
import com.pestphp.pest.customExpectations.generators.Method
import java.io.DataInput
import java.io.DataOutput

typealias MethodTestnamePair = Pair<Method, String>

class MethodTestnamePairExternalizer : DataExternalizer<MethodTestnamePair> {
    companion object {
        val INSTANCE = MethodTestnamePairExternalizer()
    }

    override fun save(out: DataOutput, value: MethodTestnamePair) {
        val (method, testname) = value
        MethodDataExternalizer.INSTANCE.save(out, method)
        EnumeratorStringDescriptor.INSTANCE.save(out, testname)
    }

    override fun read(input: DataInput): MethodTestnamePair {
        val method = MethodDataExternalizer.INSTANCE.read(input)
        val testname = EnumeratorStringDescriptor.INSTANCE.read(input)

        return Pair(method, testname)
    }
}
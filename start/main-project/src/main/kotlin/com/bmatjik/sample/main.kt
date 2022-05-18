package com.bmatjik.sample

import com.bmatjik.annotations.IgnoreField
import com.bmatjik.annotations.Listed

//data class Sample1(@Listed("nik") val nik :String)
@Listed
data class Sample2( val nik :Int, val z:Int)


fun main() {
    // Calling first generated function.
//    functionWithoutArgs()
//
//    // Calling second generated function.
//    myAmazingFunction(
//        arg1 = "sample value",
//        arg2 = listOf(0, 1, null),
//        arg3 = listOf(mapOf("sample key" to 0))
//    )
    Sample2Ext.members()
}
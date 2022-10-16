package com.aad.storyapp.helper

/****************************************************
 * Created by Indra Muliana
 * On Sunday, 16/10/2022 00.00
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

object Log {
    fun d(tag: String, msg: String) {
        println("DEBUG: $tag: $msg")
    }

    fun i(tag: String, msg: String) {
        println("INFO: $tag: $msg")
    }

    fun w(tag: String, msg: String) {
        println("WARN: $tag: $msg")
    }

    fun e(tag: String, msg: String) {
        println("ERROR: $tag: $msg")
    }
}
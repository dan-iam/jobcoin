package com.gemini.storage

interface Storage {

    fun getDirectory(): MutableMap<String, List<String>>

    fun updateDirectory(depositAddress: String, sourceAddresses: List<String>)

    fun getAssociatedAddresses(depositAddress: String): List<String>

}
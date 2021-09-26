package com.gemini.storage

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class FileSystem(private val directoryPath: String? = DEFAULT_DIRECTORY_PATH) : Storage {

    companion object {
        private const val DEFAULT_DIRECTORY_PATH = "/tmp/directory.json"
        private val mapper = jacksonObjectMapper().registerKotlinModule()
    }

    init {
        initializeDirectory()
    }

    override fun updateDirectory(depositAddress: String, sourceAddresses: List<String>) {
        val directory = getDirectory()
        directory[depositAddress] = sourceAddresses
        File(directoryPath).writeText(jacksonObjectMapper().writeValueAsString(directory))
    }

    override fun getAssociatedAddresses(depositAddress: String): List<String> {
        val directory = getDirectory()
        return when (directory.containsKey(depositAddress)) {
            true -> directory[depositAddress]!!
            else -> emptyList()
        }
    }

    override fun getDirectory(): MutableMap<String, List<String>> {
        return when (File(directoryPath).exists()) {
            true -> mapper.readValue(File(directoryPath).readText(Charsets.UTF_8))
            else -> mutableMapOf()
        }
    }

    private fun initializeDirectory() {
        when (File(directoryPath).exists()) {
            true -> if (File(directoryPath).readText(Charsets.UTF_8).isEmpty()) writeNewDirectory()
            else -> writeNewDirectory()
        }
    }

    private fun writeNewDirectory() {
        File(directoryPath).writeText(jacksonObjectMapper().writeValueAsString(emptyMap<String, List<String>>()))
    }
}
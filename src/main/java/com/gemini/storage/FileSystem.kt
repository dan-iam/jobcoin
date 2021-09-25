package com.gemini.storage

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class FileSystem : Storage {

    companion object {
        private const val DEFAULT_DIRECTORY_PATH = "/tmp/directory.json"
        private val mapper = jacksonObjectMapper().registerKotlinModule()
    }

    override fun updateDirectory(depositAddress: String, sourceAddresses: List<String>) {
        val directory = getDirectory()
        directory[depositAddress] = sourceAddresses
        File(DEFAULT_DIRECTORY_PATH).writeText(jacksonObjectMapper().writeValueAsString(directory))
    }

    override fun getAssociatedAddresses(depositAddress: String): List<String> {
        val directory = getDirectory()
        return when (directory.containsKey(depositAddress)) {
            true -> directory[depositAddress]!!
            else -> emptyList()
        }
    }

    override fun getDirectory(): MutableMap<String, List<String>> {
        return when (File(DEFAULT_DIRECTORY_PATH).exists()) {
            true -> mapper.readValue(File(DEFAULT_DIRECTORY_PATH).readText(Charsets.UTF_8))
            else -> mutableMapOf()
        }
    }
}
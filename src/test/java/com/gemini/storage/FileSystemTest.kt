package com.gemini.storage

import org.junit.Test
import java.nio.file.Files

class FileSystemTest {

    @Test
    fun init_creates_empty_dir() {
        val tmpDirPath = Files.createTempFile("directory", ".json")
        val storage = FileSystem(tmpDirPath.toString())
        assert(storage.getDirectory().isEmpty())
    }

    @Test
    fun updateDirectory_success() {
        val tmpDirPath = Files.createTempFile("directory", ".json")
        val storage = FileSystem(tmpDirPath.toString())
        assert(storage.getDirectory().isEmpty())
        storage.updateDirectory("deposit_here", listOf("here", "there", "where"))
        assert(storage.getDirectory().isNotEmpty())
    }

    @Test
    fun getAssociatedAddresses_success() {
        val tmpDirPath = Files.createTempFile("directory", ".json")
        val storage = FileSystem(tmpDirPath.toString())
        assert(storage.getDirectory().isEmpty())
        // verify if depositAddress isn't known returns empty list of associated addresses
        assert(storage.getAssociatedAddresses("THIS_DOESNT_EXIST").isEmpty())
        storage.updateDirectory("deposit_here", listOf("here", "there", "where"))
        // verify addresses are associated correctly
        assert(storage.getAssociatedAddresses("deposit_here") == listOf("here", "there", "where"))
    }
}
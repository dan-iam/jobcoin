package com.gemini.cmd

import com.gemini.client.JobcoinClient
import com.gemini.storage.FileSystem
import kotlinx.cli.ArgParser
import org.junit.Test
import java.nio.file.Files
import java.util.*

// TODO - replace client with mocked instance
class RegisterCmdTest {

    companion object {
        const val BASE_URL = "http://jobcoin.gemini.com/rockslide-scope/api"
    }

    @Test
    fun registerCmd_success() {
        val tmpDirPath = Files.createTempFile("directory", ".json")
        val parser = ArgParser("jobcoin")
        val client = JobcoinClient(BASE_URL)
        val storage = FileSystem(tmpDirPath.toString())
        // verify we have a fresh directory
        assert(storage.getDirectory().isEmpty())

        val registerCmd = RegisterCmd(storage, client)
        parser.subcommands(registerCmd)
        val sourceAddress = UUID.randomUUID().toString().replace("-","")
        val input = listOf("register", "-a", sourceAddress).toTypedArray()
        parser.parse(input)
        // verify address was registered
        assert(storage.getDirectory().isNotEmpty())
    }

    @Test
    fun registerCmd_multiple_addresses_success() {
        val tmpDirPath = Files.createTempFile("directory", ".json")
        val parser = ArgParser("jobcoin")
        val client = JobcoinClient(BASE_URL)
        val storage = FileSystem(tmpDirPath.toString())
        // verify we have a fresh directory
        assert(storage.getDirectory().isEmpty())

        val registerCmd = RegisterCmd(storage, client)
        parser.subcommands(registerCmd)
        val sourceAddress1 = UUID.randomUUID().toString().replace("-","")
        val sourceAddress2 = UUID.randomUUID().toString().replace("-","")
        val input = listOf("register", "-a", "$sourceAddress1,$sourceAddress2").toTypedArray()
        parser.parse(input)
        // verify addresses were registered
        assert(storage.getDirectory().values.contains(listOf(sourceAddress1, sourceAddress2)))
    }

    @Test
    fun registerCmd_used_address_fails() {
        val tmpDirPath = Files.createTempFile("directory", ".json")
        val parser = ArgParser("jobcoin")
        val client = JobcoinClient(BASE_URL)
        val storage = FileSystem(tmpDirPath.toString())
        // verify we have a fresh directory
        assert(storage.getDirectory().isEmpty())

        val registerCmd = RegisterCmd(storage, client)
        parser.subcommands(registerCmd)
        val input = listOf("register", "-a", "TestReceive").toTypedArray()
        parser.parse(input)
        // verify address was NOT registered
        assert(storage.getDirectory().isEmpty())
    }

}
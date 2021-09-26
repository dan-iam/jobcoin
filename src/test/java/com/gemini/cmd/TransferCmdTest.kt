package com.gemini.cmd

import com.gemini.client.JobcoinClient
import com.gemini.client.JobcoinClientTest
import com.gemini.storage.FileSystem
import kotlinx.cli.ArgParser
import org.junit.Test
import java.nio.file.Files
import java.util.*


// TODO - replace client with mocked instance
class TransferCmdTest {

    companion object {
        const val BASE_URL = "http://jobcoin.gemini.com/rockslide-scope/api"
        const val TEST_HOUSE_ADDRESS = "house1"
    }

    private val client = JobcoinClient(JobcoinClientTest.BASE_URL, TEST_HOUSE_ADDRESS)


    @Test
    fun transferCmd_success() {
        val tmpDirPath = Files.createTempFile("directory", ".json")
        val parser = ArgParser("jobcoin")
        val storage = FileSystem(tmpDirPath.toString())
        // verify we have a fresh directory
        assert(storage.getDirectory().isEmpty())

        val transferCmd = TransferCmd(storage, client)
        parser.subcommands(transferCmd)

        // register new address
        val sourceAddress = UUID.randomUUID().toString().replace("-","")
        val depositAddress = UUID.randomUUID().toString().replace("-","")
        storage.updateDirectory(depositAddress, listOf(sourceAddress))

        // verify transfer occurred
        val fromAddressBalanceBefore = client.getAddressInfo("Test").balance.toInt()
        val toAddressBalanceBefore = client.getAddressInfo(sourceAddress).balance.toInt()
        parser.parse(listOf("transfer", "-t", "Test,$depositAddress,1").toTypedArray())

        client.getAddressInfo("Test").balance.let { fromAddressBalanceAfter ->
            assert(fromAddressBalanceAfter.toInt() == (fromAddressBalanceBefore - 1))
        }
        client.getAddressInfo(sourceAddress).balance.let { toAddressBalanceAfter ->
            assert(toAddressBalanceAfter.toInt() == (toAddressBalanceBefore + 1))
        }
    }

    @Test
    fun transferCmd_insufficient_funds() {
        val tmpDirPath = Files.createTempFile("directory", ".json")
        val parser = ArgParser("jobcoin")
        val storage = FileSystem(tmpDirPath.toString())
        // verify we have a fresh directory
        assert(storage.getDirectory().isEmpty())

        val transferCmd = TransferCmd(storage, client)
        parser.subcommands(transferCmd)

        // register new address
        val sourceAddress = UUID.randomUUID().toString().replace("-","")
        val depositAddress = UUID.randomUUID().toString().replace("-","")
        storage.updateDirectory(depositAddress, listOf(sourceAddress))

        // verify transfer occurred
        val fromAddressBalanceBefore = client.getAddressInfo("Test").balance.toInt()
        val toAddressBalanceBefore = client.getAddressInfo(sourceAddress).balance.toInt()
        parser.parse(listOf("transfer", "-t", "Test,$depositAddress,1000000").toTypedArray())

        // verify NO transfer occurred
        client.getAddressInfo("Test").balance.let { fromAddressBalanceAfter ->
            assert(fromAddressBalanceAfter.toInt() == fromAddressBalanceBefore)
        }
        client.getAddressInfo(sourceAddress).balance.let { toAddressBalanceAfter ->
            assert(toAddressBalanceAfter.toInt() == toAddressBalanceBefore)
        }
    }

    @Test
    fun transfer_invalid_fromAddress() {
        val tmpDirPath = Files.createTempFile("directory", ".json")
        val parser = ArgParser("jobcoin")
        val storage = FileSystem(tmpDirPath.toString())
        // verify we have a fresh directory
        assert(storage.getDirectory().isEmpty())

        val transferCmd = TransferCmd(storage, client)
        parser.subcommands(transferCmd)

        // register new address
        val sourceAddress = UUID.randomUUID().toString().replace("-","")
        val depositAddress = UUID.randomUUID().toString().replace("-","")
        storage.updateDirectory(depositAddress, listOf(sourceAddress))

        // verify transfer occurred
        val fromAddressBalanceBefore = client.getAddressInfo("THIS_DOES_NOT_EXIST").balance.toInt()
        val toAddressBalanceBefore = client.getAddressInfo(sourceAddress).balance.toInt()
        parser.parse(listOf("transfer", "-t", "THIS_DOES_NOT_EXIST,$depositAddress,1000000").toTypedArray())

        // verify NO transfer occurred
        client.getAddressInfo("THIS_DOES_NOT_EXIST").balance.let { fromAddressBalanceAfter ->
            assert(fromAddressBalanceAfter.toInt() == fromAddressBalanceBefore)
        }
        client.getAddressInfo(sourceAddress).balance.let { toAddressBalanceAfter ->
            assert(toAddressBalanceAfter.toInt() == toAddressBalanceBefore)
        }
    }
}
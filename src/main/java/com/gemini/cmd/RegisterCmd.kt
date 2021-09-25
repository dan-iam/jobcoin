package com.gemini.cmd

import com.gemini.client.JobcoinClient
import com.gemini.storage.Storage
import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import java.util.*

class RegisterCmd(
    private val storage: Storage,
    private val client: JobcoinClient
) : Subcommand("register", "register addresses") {

    private val source by option(
        type = ArgType.String,
        fullName = "addresses",
        shortName = "a",
        description = "comma separated list of source addresses to register"
    )

    override fun execute() {
        println()
        val sourceAddresses = source.toString().split(",")
        if (validAddresses(sourceAddresses)) {
            val depositAddress = UUID.randomUUID().toString().replace("-","")
            storage.updateDirectory(depositAddress, sourceAddresses)
            println("Successful registration! - Please make all deposits to $depositAddress")
        }
    }

    private fun validAddresses(addresses: List<String>): Boolean {
        var shouldUpdate = true
        val sourceAddresses = source.toString().split(",")
        sourceAddresses.forEach { address ->
            if (client.isUsedAddress(address)) {
                println("Error: invalid address found -- please provide only unused addresses.")
                shouldUpdate = false
            }
        }
        return shouldUpdate
    }

}
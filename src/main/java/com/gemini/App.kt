package com.gemini

import com.gemini.client.JobcoinClient
import com.gemini.cmd.RegisterCmd
import com.gemini.cmd.TransferCmd
import com.gemini.storage.FileSystem
import kotlinx.cli.ArgParser


const val BASE_URL = "http://jobcoin.gemini.com/rockslide-scope/api"

fun main(args : Array<String>) {
    println("~~~~~~~~~~~~~~~~~")
    println("~~ Jobcoin CLI ~~")
    println("~~~~~~~~~~~~~~~~~")
    println()
    while (true) {
        val url = System.getProperty("url")

        val parser = ArgParser("jobcoin")
        val client = JobcoinClient(url ?: BASE_URL)
        val storage = FileSystem()
        val registerCmd = RegisterCmd(storage, client)
        val transferCmd = TransferCmd(storage, client)
        parser.subcommands(registerCmd, transferCmd)

        print("Enter cmd: ")
        val input: Array<String> = readLine()!!.split(" ").toTypedArray()
        parser.parse(input)
        println()
    }
}

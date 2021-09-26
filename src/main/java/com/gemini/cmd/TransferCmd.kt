package com.gemini.cmd

import com.gemini.client.JobcoinClient
import com.gemini.storage.Storage
import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import kotlin.random.Random


class TransferCmd(
    private val storage: Storage,
    private val client: JobcoinClient
) : Subcommand("transfer", "transfer jobcoin from from one address to another.")  {

    private val houseAddress: String = client.houseAddress

    private val transfer by option(
        type = ArgType.String,
        fullName = "transaction",
        shortName = "t",
        description = "comma separated transfer request e.g. <fromAddress>,<toAddress>,<amount>"
    )

    override fun execute() {
        val transferRequest = transfer.toString().split(",")
        val fromAddress = transferRequest[0]
        val toAddress = transferRequest[1]
        val amount = transferRequest[2].toIntOrNull()

        println()
        when {
            (amount == null || amount < 0) -> println("Error: invalid amount")
            client.getAddressInfo(fromAddress).balance.toInt() < amount -> println("Error: insufficient funds")
            else -> {
                val sourceAddresses = storage.getAssociatedAddresses(toAddress)
                when (sourceAddresses.isEmpty()) {
                    true -> println("Error: invalid destination address")
                    else -> {
                        println("transferring $amount from $fromAddress to deposit address $toAddress")
                        val depositResult = client.transfer(Transaction(fromAddress, toAddress, amount.toString()))
                        when (depositResult) {
                            false -> println("Error: unable to process transfer request.")
                            else -> {
                                val transactions = mutableListOf<Transaction>()
                                transactions.add(Transaction(toAddress, houseAddress, amount.toString()))
                                transactions.addAll(distributeTransfer(sourceAddresses, amount))
                                // in reality this would publish to a queue,
                                // where another service would handle processing micro transactions
                                // and any retries/failures
                                // for the sake of the exercies we will complete transactions here
                                publish(transactions, amount)
                                println("Successfully and anonymously transferred $amount Jobcoin!")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun publish(transactions: List<Transaction>, amount: Int) {
        var totalTransferred = 0
        transactions.forEachIndexed { idx, transaction ->
            println("transferring ${transaction.amount} from ${transaction.fromAddress} to ${transaction.toAddress}")
            client.transfer(transaction)
            // first transaction is just moving from depositAddress to houseAddress.
            // No need to account for in reconciliation
            if (idx > 0) totalTransferred += transaction.amount.toInt()
        }
        println()
        println("Verifying transactions...")
        println("Verified: ${amount == totalTransferred}")
        println("Gathering transfer details...")
        println("Requested transfer=$amount Jobcoin Actual transfer=$totalTransferred")
    }

    private fun distributeTransfer(sourceAddresses: List<String>, amount: Int): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        var amountLeft = amount
        var transactionAmount = randomAmount()
        while (amountLeft > transactionAmount) {
            transactions.add(Transaction(houseAddress, sourceAddresses.random(), transactionAmount.toString()))
            amountLeft -= transactionAmount
            transactionAmount = randomAmount()
        }

        if (amountLeft > 0) {
            transactions.add(Transaction(houseAddress, sourceAddresses.random(), amountLeft.toString()))
        }

        return transactions
    }

    private fun List<String>.random(): String {
        return this[Random.nextInt(0, this.size)]
    }

    private fun randomAmount(): Int {
        return Random.nextInt(1, 11)
    }

}

data class Transaction(val fromAddress: String, val toAddress: String, val amount: String)
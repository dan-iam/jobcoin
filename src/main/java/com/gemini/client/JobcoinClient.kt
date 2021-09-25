package com.gemini.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gemini.cmd.Transaction
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response


class JobcoinClient(private val baseUrl: String) {

    companion object {
        const val APPLICATION_JSON = "application/json"

        private val mapper = jacksonObjectMapper()
    }

    private val client = OkHttpClient()

    fun isUsedAddress(address: String): Boolean {
        val addressInfo = getAddressInfo(address)
        return addressInfo.transactions.isNotEmpty()
    }

    fun getAddressInfo(address: String): AddressInfo {
        val request = Request.Builder()
            .url("$baseUrl/addresses/${address}")
            .get()
            .build()

        return try {
            client.executeHttpRequest(request).body.use { response ->
                mapper.readValue(response?.string(), AddressInfo::class.java)
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

    fun transfer(transaction: Transaction) {
        val body = mapper.writeValueAsString(transaction).toRequestBody(APPLICATION_JSON.toMediaTypeOrNull())
        val request = Request.Builder()
            .url("$baseUrl/transactions")
            .post(body)
            .build()
        client.executeHttpRequest(request).use { response ->
            if(!response.isSuccessful) {
                println("Error: unsuccessful transfer!")
            }
        }
    }

    private fun OkHttpClient.executeHttpRequest(request: Request): Response {
        return this.newCall(request).execute()
    }
}

data class AddressInfo(val balance: String, val transactions: List<TransactionInfo>)
data class TransactionInfo(
    val timestamp: String,
    val toAddress: String,
    val fromAddress: String? = "UIGenerated",
    val amount: String
)
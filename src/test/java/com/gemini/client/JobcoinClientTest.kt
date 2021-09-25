package com.gemini.client

import org.junit.Test

// TODO - use mocks!
class JobcoinClientTest {

    companion object {
        const val BASE_URL = "http://jobcoin.gemini.com/rockslide-scope/api"
    }

    private val client = JobcoinClient(BASE_URL)

    @Test
    fun getAddressInfo_valid_address() {
        client.getAddressInfo("Alice").let { info ->
            assert(info.balance == "37.5")
            assert(info.transactions.size == 2)
        }
    }

    @Test
    fun getAddressInfo_invalid_address() {
        client.getAddressInfo("THIS_DOES_NOT_EXIST").let { info ->
            assert(info.balance == "0")
            assert(info.transactions.isEmpty())
        }
    }
}
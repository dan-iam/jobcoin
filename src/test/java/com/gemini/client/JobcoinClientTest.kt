package com.gemini.client

import com.gemini.cmd.Transaction
import org.junit.Test

class JobcoinClientTest {

    companion object {
        const val BASE_URL = "http://jobcoin.gemini.com/rockslide-scope/api"
        const val TEST_HOUSE_ADDRESS = "house1"
    }

    private val client = JobcoinClient(BASE_URL, TEST_HOUSE_ADDRESS)

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

    @Test
    fun isUsedAddress() {
        assert(client.isUsedAddress("Alice") == true)
        assert(client.isUsedAddress("UNUSED") == false)
    }

    @Test
    fun transfer_success() {
        val fromAddressBalanceBefore = client.getAddressInfo("Test6").balance.toInt()
        val toAddressBalanceBefore = client.getAddressInfo("house1").balance.toInt()
        client.transfer(
            Transaction(fromAddress = "Test6", toAddress = "house1", amount = "1")
        )
        // verify transfer occurred
        client.getAddressInfo("Test6").balance.let { fromAddressBalanceAfter ->
            assert(fromAddressBalanceAfter.toInt() == (fromAddressBalanceBefore - 1))
        }
        client.getAddressInfo("house1").balance.let { toAddressBalanceAfter ->
            assert(toAddressBalanceAfter.toInt() == (toAddressBalanceBefore + 1))
        }
    }

    @Test
    fun transfer_insufficient_funds() {
        val fromAddressBalanceBefore = client.getAddressInfo("Test6").balance.toInt()
        val toAddressBalanceBefore = client.getAddressInfo("house1").balance.toInt()
        client.transfer(
            Transaction(fromAddress = "Test6", toAddress = "house1", amount = "100000")
        ).let { response ->
            assert(response == false)
        }
        // verify NO transfer occurred
        client.getAddressInfo("Test6").balance.let { fromAddressBalanceAfter ->
            assert(fromAddressBalanceAfter.toInt() == fromAddressBalanceBefore)
        }
        client.getAddressInfo("house1").balance.let { toAddressBalanceAfter ->
            assert(toAddressBalanceAfter.toInt() == toAddressBalanceBefore)
        }
    }

    @Test
    fun transfer_invalid_fromAddress() {
        val fromAddressBalanceBefore = client.getAddressInfo("THIS_DOES_NOT_EXIST").balance.toInt()
        assert(fromAddressBalanceBefore == 0)
        val toAddressBalanceBefore = client.getAddressInfo("house1").balance.toInt()
        client.transfer(
            Transaction(fromAddress = "THIS_DOES_NOT_EXIST", toAddress = "house1", amount = "1")
        ).let { response ->
            assert(response == false)
        }
        // verify NO transfer occurred
        client.getAddressInfo("THIS_DOES_NOT_EXIST").balance.let { fromAddressBalanceAfter ->
            assert(fromAddressBalanceAfter.toInt() == fromAddressBalanceBefore)
        }
        client.getAddressInfo("house1").balance.let { toAddressBalanceAfter ->
            assert(toAddressBalanceAfter.toInt() == toAddressBalanceBefore)
        }
    }
}
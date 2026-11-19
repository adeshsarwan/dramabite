package com.dramabite.thebes.app.engineBox.interfaces

import com.dramabite.thebes.app.engineBox.model.transaction.TransactionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("transactions")
    fun getTransactions(
        @Query("user_id") userId: String
    ): Call<TransactionResponse>
}
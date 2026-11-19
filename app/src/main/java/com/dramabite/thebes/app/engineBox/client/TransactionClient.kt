package com.dramabite.thebes.app.engineBox.client

import com.dramabite.thebes.app.applicationPreference.ControlPreference
import com.dramabite.thebes.app.engineBox.interfaces.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object TransactionClient {


    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(ControlPreference.getTransactionUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}

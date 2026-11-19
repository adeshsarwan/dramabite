package com.dramabite.thebes.app.engineBox.client;

import android.util.Log;

import com.dramabite.thebes.app.applicationPreference.ControlPreference;
import com.dramabite.thebes.app.engineBox.aauth.AuthInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EngineClient {

    private static Retrofit retrofit;

    public static void resetClient() {
        retrofit = null;
    }

    public static Retrofit getClient() {

        if (retrofit == null) {
            String baseUrl = ControlPreference.getMainBaseUrl();
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                throw new IllegalStateException(
                        "main_base_url empty — host Remote Config not loaded yet"
                );
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}

package com.denisenko.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import okhttp3.OkHttpClient;

@ApplicationScoped
public class HttpClientProducer {

    @Produces
    @ApplicationScoped
    public OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder().build();
    }
}

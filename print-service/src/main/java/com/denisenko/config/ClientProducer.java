package com.denisenko.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class ClientProducer {

    @Produces
    @ApplicationScoped
    public Client produceClient() {
        return ClientBuilder.newClient();
    }
}

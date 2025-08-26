package com.unimedsc.ms_list.config;

import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.kiota.authentication.AzureIdentityAuthenticationProvider;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;

@Configuration
public class GraphClientConfig {

    @Bean
    public GraphServiceClient getGraphClient(@Value("${graph.oauth.tenant-id}") String tenantId,
                                             @Value("${graph.oauth.client-id}") String clientId,
                                             @Value("${graph.oauth.client-secret}") String clientSecret,
                                             @Value("${graph.oauth.scope}") String scope) {

        // Crie o cliente HTTP
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        // Crie a credencial de autenticação com segredo do cliente
        final ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .tenantId(tenantId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

        // Crie o provedor de autenticação Kiota. Passe o escopo e o host permitido.
        AuthenticationProvider authenticationProvider = new AzureIdentityAuthenticationProvider(
                credential,
                new String[] { scope },
                "graph.microsoft.com"
        );

        // Crie o cliente do Graph
        return new GraphServiceClient(authenticationProvider, okHttpClient);
    }
}
package com.eventim.entity;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@AutoService(JpaEntityProviderFactory.class)
public class UserEntityProviderFactory implements JpaEntityProviderFactory {

    public static final String PROVIDER_ID = "custom-user-entity-provider";
    private static final UserEntityProvider INSTANCE = new UserEntityProvider();

    @Override
    public JpaEntityProvider create(KeycloakSession keycloakSession) {
        return INSTANCE;
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}

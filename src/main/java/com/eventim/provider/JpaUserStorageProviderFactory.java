package com.eventim.provider;

import com.google.auto.service.AutoService;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

@AutoService(UserStorageProviderFactory.class)
public class JpaUserStorageProviderFactory implements UserStorageProviderFactory<JpaUserStorageProvider> {
    @Override
    public JpaUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new JpaUserStorageProvider(model, session, session.getProvider(JpaConnectionProvider.class).getEntityManager());
    }

    @Override
    public String getId() {
        return "custom-user-storage-provider";
    }
}

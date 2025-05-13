package com.eventim.entity;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

import java.util.List;

import static com.eventim.entity.UserEntityProviderFactory.PROVIDER_ID;

public class UserEntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        return List.of(UserEntity.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/jpa-changelog.xml";
    }

    @Override
    public String getFactoryId() {
        return PROVIDER_ID;
    }

    @Override
    public void close() {

    }
}

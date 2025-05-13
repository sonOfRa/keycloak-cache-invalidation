package com.eventim.provider;

import com.eventim.entity.UserEntity;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class JpaUserAdapter extends AbstractUserAdapterFederatedStorage {

    private final UserEntity user;

    public JpaUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, UserEntity user) {
        super(session, realm, storageProviderModel);
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String s) {
        user.setUsername(s);
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        user.setEmail(email);
    }

    @Override
    public String getId() {
        return StorageId.keycloakId(
                storageProviderModel, user.getId().toString());
    }

    @Override
    public String getFirstName() {
        return "John";
    }

    @Override
    public String getLastName() {
        return "Doe";
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        final var attributes = super.getAttributes();
        attributes.put(FIRST_NAME, List.of(getFirstName()));
        attributes.put(LAST_NAME, List.of(getLastName()));
        attributes.put(EMAIL, List.of(getEmail()));
        return attributes;
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        final var attributes = getAttributes().get(name);
        if (attributes == null) {
            return Stream.empty();
        }
        return attributes.stream();
    }

    @Override
    public boolean isEmailVerified() {
        return true;
    }
}

package com.eventim.provider;

import com.eventim.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

public class JpaUserStorageProvider implements UserRegistrationProvider, UserLookupProvider, UserStorageProvider, CredentialInputValidator {
    private final ComponentModel componentModel;
    private final KeycloakSession session;
    private final EntityManager entityManager;

    public JpaUserStorageProvider(ComponentModel componentModel, KeycloakSession session, EntityManager entityManager) {
        this.componentModel = componentModel;
        this.session = session;
        this.entityManager = entityManager;
    }

    @Override
    public UserModel addUser(RealmModel realmModel, String username) {
        final var user = new UserEntity();
        user.setUsername(username);
        entityManager.persist(user);
        return new JpaUserAdapter(session, realmModel, componentModel, user);
    }

    @Override
    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        final var user = entityManager.find(UserEntity.class, userModel.getId());
        if (user != null) {
            entityManager.remove(user);
            return true;
        }
        return false;
    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String id) {
        final var userId = new StorageId(id).getExternalId();
        final var user = entityManager.find(UserEntity.class, userId);
        if (user == null) {
            return null;
        }
        return new JpaUserAdapter(session, realmModel, componentModel, user);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String s) {
        final var user = entityManager.createQuery("SELECT u FROM CustomUserEntity u WHERE u.username = :username", UserEntity.class)
                .setParameter("username", s).getResultStream().findFirst().orElse(null);
        if (user == null) {
            return null;
        }
        return new JpaUserAdapter(session, realmModel, componentModel, user);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String s) {
        final var user = entityManager.createQuery("SELECT u FROM CustomUserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", s).getResultStream().findFirst().orElse(null);
        if (user == null) {
            return null;
        }
        return new JpaUserAdapter(session, realmModel, componentModel, user);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return true;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return true;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        return credentialInput.getChallengeResponse().equals("test");
    }
}

# Keycloak Cache Invalidation vulnerability

This repo showcases a vulnerability in Keycloak that can lead to non-existing users being able to log in, when a user federation is used with a lifetime cache setting, and the username is changed in an external system.

## Reproduction Steps
1. Start the included `docker-compose` environment. It already includes the needed federation and configures a realm to use it
2. Use the provided password grant client and user to log in:

```
curl -XPOST http://localhost:8456/realms/test/protocol/openid-connect/token -dgrant_type=password -dusername=test -dpassword=test -dclient_id=password
```

3. Change the user's username in the database, *not* via keycloak
4. Wait 5 seconds (the cache TTL of the federation is one second)
5. Observe that we can still log in with the old username via curl:

```
curl -XPOST http://localhost:8456/realms/test/protocol/openid-connect/token -dgrant_type=password -dusername=test -dpassword=test -dclient_id=password
```

6. Also observe that the access token returned by this login, despite logging in with the *old* username, contains the *new* username:

```json
{
  "exp": 1747169153,
  "iat": 1747168853,
  "jti": "onrtro:bb6b1473-e0e9-448e-892d-4b41365bc407",
  "iss": "http://localhost:8456/realms/test",
  "aud": "account",
  "sub": "f:d6c3bbd6-4f34-4427-8084-510197ba5559:1",
  "typ": "Bearer",
  "azp": "password",
  "sid": "08768f68-7ec1-4f79-af57-7662dfcf68e5",
  "acr": "1",
  "realm_access": {
    "roles": [
      "offline_access",
      "uma_authorization"
    ]
  },
  "resource_access": {
    "account": {
      "roles": [
        "manage-account",
        "manage-account-links",
        "view-profile"
      ]
    }
  },
  "scope": "profile email",
  "email_verified": true,
  "name": "John Doe",
  "preferred_username": "nottest",
  "given_name": "John",
  "family_name": "Doe",
  "email": "test@test.de"
}
```

## Cause
In addition to caching the actual results of user queries, Keycloak also caches the mapping from usernames to IDs.
When keycloak now has to look up that user, it looks up the username -> ID mapping, and fetches the user via its ID from the federated storage.

These cache entries do not honor the MAX_LIFESPAN setting on the user federation provider and can exist indefinitely.
This means that if the username changed but the ID stayed the same, keycloak still finds the user under the old email address and allows authentication to proceed.

In a high traffic setup, they often expire within seconds, but in lower traffic environments, we have observed cache entries being valid for hours and days.

## Possible Fixes
In our setup, we're currently running on 25.0.6, and have fixed the issue by patching UserCacheSession.java to verify that after fetching the user (from cache or federation), that it still has the same username/email.
If it does not, we invalidate the cache entry, and we fetch the user by username/email again via the delegate

The other possibility for a fix would be to somehow ensure that these entries also honor the MAX_LIFESPAN configuration of the federation provider
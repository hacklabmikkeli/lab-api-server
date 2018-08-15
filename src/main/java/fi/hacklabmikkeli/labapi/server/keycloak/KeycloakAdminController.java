package fi.hacklabmikkeli.labapi.server.keycloak;

import java.util.Arrays;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;

import fi.hacklabmikkeli.labapi.server.settings.SystemSettingController;

/**
 * Controller for keycloak administration tasks 
 */
@ApplicationScoped
public class KeycloakAdminController {

  private static final String KEYCLOAK_ADMIN_REALM_SETTING = "keycloak-admin-realm";
  private static final String KEYCLOAK_ADMIN_CLIENT_ID_SETTING = "keycloak-admin-client-id";
  private static final String KEYCLOAK_ADMIN_CLIENT_SECRET_SETTING = "keycloak-admin-client-secret";
  private static final String KEYCLOAK_ADMIN_SERVER_URL_SETTING = "keycloak-admin-server-url";

  @Inject
  private SystemSettingController systemSettingController;

  /**
   * Adds realm role to keycloak user
   * 
   * @param userId id of user that the role will be added
   * @param roleName name of the role
   */
  public void addRealmRole(UUID userId, String roleName) {
    Keycloak client = getClient();
    String realm = systemSettingController.getSettingValue(KEYCLOAK_ADMIN_REALM_SETTING);
    RealmResource realmResource = client.realm(realm);
    UsersResource userRessource = realmResource.users();
    RoleRepresentation realmRole = realmResource.roles().get(roleName).toRepresentation();
    userRessource.get(userId.toString()).roles().realmLevel().add(Arrays.asList(realmRole));
  }

  /**
   * Removes realm role from keycloak user
   * 
   * @param userId id of user that the role will be added
   * @param roleName name of the role
   */
  public void removeRealmRole(UUID userId, String roleName) {
    Keycloak client = getClient();
    String realm = systemSettingController.getSettingValue(KEYCLOAK_ADMIN_REALM_SETTING);
    RealmResource realmResource = client.realm(realm);
    UsersResource userRessource = realmResource.users();
    RoleRepresentation realmRole = realmResource.roles().get(roleName).toRepresentation();
    userRessource.get(userId.toString()).roles().realmLevel().remove(Arrays.asList(realmRole));
  }
  /**
   * Constructs keycloak admin client
   */
  private Keycloak getClient() {
    return KeycloakBuilder.builder()
      .serverUrl(systemSettingController.getSettingValue(KEYCLOAK_ADMIN_SERVER_URL_SETTING))
      .realm(systemSettingController.getSettingValue(KEYCLOAK_ADMIN_REALM_SETTING))
      .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
      .clientId(systemSettingController.getSettingValue(KEYCLOAK_ADMIN_CLIENT_ID_SETTING))
      .clientSecret(systemSettingController.getSettingValue(KEYCLOAK_ADMIN_CLIENT_SECRET_SETTING)).build();
  }

}
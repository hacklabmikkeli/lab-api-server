package fi.hacklabmikkeli.labapi.server.persistence.dao;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import fi.hacklabmikkeli.labapi.server.persistence.model.Door;

/**
 * @author Heikki Kurhinen
 * 
 * DAO class for Door entity
 */
@ApplicationScoped
public class DoorDAO extends AbstractDAO<Door> {

  /**
   * Creates and persists new door entity
   * 
   * @param id id
   * @param name name
   * @param lastPing last ping (can be null)
   * 
   * @return created Door entity
   */
  public Door create(UUID id, String name, OffsetDateTime lastPing) {
    Door door = new Door();

    door.setId(id);
    door.setName(name);
    door.setLastPing(lastPing);

    return persist(door);
  }

  /**
   * Updates doors 
   * 
   * @param door door to update
   * @param name new name
   * @return updated door
   */
  public Door updateName(Door door, String name) {
    door.setName(name);
    return persist(door);
  }

  /**
   * Updates doors last ping 
   * 
   * @param door door to update
   * @param lastPing new last ping
   * @return updated door
   */
  public Door updateLastPing(Door door, OffsetDateTime lastPing) {
    door.setLastPing(lastPing);
    return persist(door);
  }
}

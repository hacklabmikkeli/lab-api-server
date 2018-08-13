package fi.hacklabmikkeli.labapi.server.doors;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.hacklabmikkeli.labapi.server.persistence.dao.DoorActionDAO;
import fi.hacklabmikkeli.labapi.server.persistence.dao.DoorDAO;
import fi.hacklabmikkeli.labapi.server.persistence.model.Door;
import fi.hacklabmikkeli.labapi.server.persistence.model.DoorAction;
import fi.hacklabmikkeli.labapi.server.persistence.model.DoorActionType;

/**
 * Controller for door related operations
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class DoorController {

  @Inject
  private DoorDAO doorDAO;

  @Inject
  private DoorActionDAO doorActionDAO;

  /**
   * Creates new door
   * 
   * @param name name of the door
   * @param lastPing last time ping was received from door 
   * 
   * @return created door
   */
  public Door createDoor(String name, OffsetDateTime lastPing) {
    return doorDAO.create(UUID.randomUUID(), name, lastPing);
  }

  /**
   * Creates door action
   * 
   * @param door Door that action is targetet to
   * @param type Action type
   * @param userId Id of the user who created the action 
   * 
   * @return created door action
   */
  public DoorAction createDoorAction(Door door, DoorActionType type, UUID userId) {
    return doorActionDAO.create(UUID.randomUUID(), door, type, userId);
  }

  /**
   * Finds door with id
   * 
   * @param id door id
   * 
   * @return door or null if not found
   */
  public Door findDoor(UUID id) {
    return doorDAO.findById(id);
  }

  /**
   * Lists doors
   * 
   * @param firstResult first result to return (optional)
   * @param maxResults max number of results to return (optinal)
   * 
   * @return list of doors 
   */
  public List<Door> listDoors(Long firstResult, Long maxResults) {
    return doorDAO.listAll(firstResult, maxResults);
  }

  /**
   * Lists actions of door
   * 
   * @param door door to list the actions from
   * @param firstResult first result to return (optional)
   * @param maxResults max number of results to return (optional)
   * 
   * @return list of door actions from door
   */
  public List<DoorAction> listDoorActions(Door door, Long firstResult, Long maxResults) {
    return doorActionDAO.listByDoor(door, firstResult, maxResults);
  }

  /**
   * Updates door
   * 
   * @param door door to update
   * @param name new name for the door 
   * @param lastPing new last ping for the door 
   */
  public Door updateDoor(Door door, String name, OffsetDateTime lastPing) {
    doorDAO.updateLastPing(door, lastPing);
    doorDAO.updateName(door, name);
    return door;
  }

  /**
   * Updates doors last ping
   * 
   * @param door door to update
   * @param lastPing new last ping for the door 
   */
  public Door updateDoorLastPing(Door door, OffsetDateTime lastPing) {
    doorDAO.updateLastPing(door, lastPing);
    return door;
  }

  /**
   * Deletes door
   * 
   * @param door Door to delete 
   */
  public void deleteDoor(Door door) {
    listDoorActions(door, null, null).stream().forEach(doorActionDAO::delete);
    doorDAO.delete(door);
  }

}

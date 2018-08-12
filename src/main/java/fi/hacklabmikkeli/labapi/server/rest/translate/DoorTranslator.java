package fi.hacklabmikkeli.labapi.server.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import fi.hacklabmikkeli.labapi.server.rest.model.Door;
import fi.hacklabmikkeli.labapi.server.rest.model.DoorAction;
import fi.hacklabmikkeli.labapi.server.rest.model.DoorAction.TypeEnum;

/**
 * Translates Door related entites to REST entities
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class DoorTranslator extends AbstractTranslator {

  /**
   * Translates Door entity to Door REST model
   * 
   * @param doorEntity Door JPA entity
   * @return translated Door REST model
   */
  public Door translateDoor(fi.hacklabmikkeli.labapi.server.persistence.model.Door doorEntity) {
    Door door = new Door();
    door.setId(doorEntity.getId());
    door.setName(doorEntity.getName());
    door.setLastPing(doorEntity.getLastPing());
    return door;
  }

  /**
   * Tranlastes list of Door entities into Door REST model
   *
   * @param doorEntities Door JPA entities 
   *  
   * @return list of translated Door REST models
   */
  public List<Door> translateDoors(List<fi.hacklabmikkeli.labapi.server.persistence.model.Door> doorEntities) {
    return doorEntities.stream().map(this::translateDoor).collect(Collectors.toList());
  }

  /**
   * Translates DoorAction entity into DoorAction REST model
   *
   * @param doorActionEntity DoorAction JPA entity 
   * 
   * @return translated DoorAction REST model
   */
  public DoorAction translateDoorAction(fi.hacklabmikkeli.labapi.server.persistence.model.DoorAction doorActionEntity) {
    DoorAction doorAction = new DoorAction();
    doorAction.setType(translateEnum(TypeEnum.class, doorActionEntity.getType()));
    return doorAction;
  }

}
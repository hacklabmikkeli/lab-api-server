package fi.hacklabmikkeli.labapi.server.rest;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import fi.hacklabmikkeli.labapi.server.doors.DoorController;
import fi.hacklabmikkeli.labapi.server.mqtt.MqttController;
import fi.hacklabmikkeli.labapi.server.persistence.model.DoorActionType;
import fi.hacklabmikkeli.labapi.server.rest.model.Door;
import fi.hacklabmikkeli.labapi.server.rest.model.DoorAction;
import fi.hacklabmikkeli.labapi.server.rest.translate.DoorTranslator;

/**
 * Doors REST service implementation
 * 
 * @author Heikki Kurhinen
 */
@RequestScoped
@Stateful
public class DoorsApiImpl extends AbstractApi implements DoorsApi {

  public static final String DOOR_OPEN_MESSAGE = "1";

  @Inject
  private DoorController doorController;

  @Inject
  private DoorTranslator doorTranslator;

  @Inject
  private MqttController mqttController;

  @Override
  public Response createDoor(@Valid Door payload) throws Exception {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    String name = payload.getName();
    OffsetDateTime lastPing = payload.getLastPing();

    return createOk(doorTranslator.translateDoor(doorController.createDoor(name, lastPing)));
  }

  @Override
  public Response createDoorAction(UUID doorId, @Valid DoorAction payload) throws Exception {
    if (!isSpaceUser()) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Door doorEntity = doorController.findDoor(doorId);
    if (doorEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    switch (payload.getType()) {
      case OPEN:
        return handleDoorOpen(doorEntity);
      default:
        return createBadRequest("Unknown door action");
    }
  }

  @Override
  public Response deleteDoor(UUID doorId) throws Exception {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Door doorEntity = doorController.findDoor(doorId);
    if (doorEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    doorController.deleteDoor(doorEntity);
    return createNoContent();
  }

  @Override
  public Response findDoor(UUID doorId) throws Exception {
    fi.hacklabmikkeli.labapi.server.persistence.model.Door doorEntity = doorController.findDoor(doorId);
    if (doorEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(doorTranslator.translateDoor(doorEntity));
  }

  @Override
  public Response listDoors(Long firstResult, Long maxResults) throws Exception {
    return createOk(doorTranslator.translateDoors(doorController.listDoors(firstResult, maxResults)));
  }

  @Override
  public Response updateDoor(UUID doorId, @Valid Door payload) throws Exception {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Door doorEntity = doorController.findDoor(doorId);
    if (doorEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(doorTranslator.translateDoor(doorController.updateDoor(doorEntity, payload.getName(), payload.getLastPing())));
  }

  /**
   * Handles door opening request
   * 
   * @param doorEntity door entity to open
   * 
   * @return response
   */
  private Response handleDoorOpen(fi.hacklabmikkeli.labapi.server.persistence.model.Door doorEntity) {
    fi.hacklabmikkeli.labapi.server.persistence.model.DoorAction doorActionEntity =  doorController.createDoorAction(doorEntity, DoorActionType.OPEN, getLoggerUserId());
    mqttController.publishMessage(String.format("door/%s", doorEntity.getId()), DOOR_OPEN_MESSAGE);
    return createOk(doorTranslator.translateDoorAction(doorActionEntity));
  }

}
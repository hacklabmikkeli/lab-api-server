package fi.hacklabmikkeli.labapi.server.doors;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;

import fi.hacklabmikkeli.labapi.server.mqtt.LabApiMqttListener;
import fi.hacklabmikkeli.labapi.server.persistence.model.Door;

@ApplicationScoped
public class DoorMqttListener implements LabApiMqttListener {

  public static final String DOOR_PING_TOPIC = "door/ping";

  @Inject
  private DoorController doorController;

  @Inject
  private Logger logger;

  @Override
  public String getTopic() {
    return DOOR_PING_TOPIC;
  }

  @Override
  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    String doorIdString = new String(message.getPayload());
    UUID doorId = UUID.fromString(doorIdString);
    Door door = doorController.findDoor(doorId);
    if (door == null) {
      logger.error("Received ping from unknown door {}", doorIdString);
      return;
    }

    doorController.updateDoorLastPing(door, OffsetDateTime.now());
  }
}
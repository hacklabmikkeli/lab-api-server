package fi.hacklabmikkeli.labapi.server.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

/**
 * Interface describing MQTT listener in LAB api
 * 
 * @author Heikki Kurhinen
 */
public interface LabApiMqttListener extends IMqttMessageListener {

  /**
   * Returns the topic that listener should listen to
   */
  public String getTopic();

}
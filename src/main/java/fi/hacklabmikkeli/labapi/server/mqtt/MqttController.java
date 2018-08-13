package fi.hacklabmikkeli.labapi.server.mqtt;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;

/**
 * Controller for mqtt related operations
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
@Singleton
@Startup
public class MqttController {

  public static final String BROKER_HOST = "localhost";
  public static final Integer BROKER_PORT = 1883;

  @Inject
  private Logger logger;

  @Inject
  @Any
  private Instance<LabApiMqttListener> labApiMqttListeners;

  private IMqttClient client;

  /**
   * Initializes mqtt client and starts listening to topics
   */
  @PostConstruct
  public void init() {
    try {
      client = new MqttClient(String.format("tcp://%s:%d", BROKER_HOST, BROKER_PORT), UUID.randomUUID().toString());
      MqttConnectOptions options = new MqttConnectOptions();
      options.setAutomaticReconnect(true);
      options.setCleanSession(true);
      options.setConnectionTimeout(10);
      client.connect(options);
      labApiMqttListeners.forEach(listener -> {
        try {
          client.subscribe(listener.getTopic(), listener);
        } catch (MqttException e) {
          logger.error("Error subsribing to topic", e);
        }
      });
    } catch (MqttException e) {
      logger.error("Error connecting to MQTT broker", e);
    }
  }

  /**
   * Gracefully shuts down the mqtt client on application shutdown
   */
  @PreDestroy
  public void destroy() {
    if (client.isConnected()) {
      try {
        client.disconnect();
      } catch (MqttException e) {
        logger.error("Error disconnecting mqtt client", e);
      }
    }
  }

  /**
   * Publishes message to mqtt broker
   * 
   * @param topic Topic to publish message into
   * @param message Message to publish
   */
  public void publishMessage(String topic, String message) {
    if (!client.isConnected()) {
      logger.error("Cannot send message because mqtt client is not connected");
      return;
    }

    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
    mqttMessage.setQos(0);
    mqttMessage.setRetained(false);
    try {
      client.publish(topic, mqttMessage);
    } catch (MqttException e) {
      logger.error("Error sending mqtt message", e);
    }
  }


}
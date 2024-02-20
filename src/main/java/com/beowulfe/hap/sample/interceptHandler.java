package com.beowulfe.hap.sample;

import io.moquette.broker.Server;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.*;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.CharsetUtil;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class interceptHandler implements InterceptHandler {

    public double humidityValue;
    public double temperatureValue;
    // todo: add id for hum and temp sens owner so when it disconnects it resets the values

    @Override
    public String getID() {
        return "1";
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
        System.out.println("Client connected: " + msg.getClientID());
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        JsonObject dataObject;
        JsonObject jsonObject;
        JsonReader reader;

        String topic = msg.getTopicName();
        ByteBuffer payloadBuffer = msg.getPayload().nioBuffer();

        // Read the direct buffer into a byte array
        byte[] payloadBytes = new byte[payloadBuffer.remaining()];
        payloadBuffer.get(payloadBytes);

        //Convert the payload byte array to a string using UTF-8 encoding
        String payloadString = new String(payloadBytes, StandardCharsets.UTF_8);

        System.out.println("Message received - Topic: " + topic + ", Payload: " + payloadString);
        switch (topic) {
            case "sensor/AM2315": {
                // Create a JsonReader from the payload string
                reader = Json.createReader(new StringReader(payloadString));
                jsonObject = reader.readObject();
                reader.close();

                // Get the data object from the JSON
                dataObject = jsonObject.getJsonObject("data");

                // Extract t and h values
                temperatureValue = dataObject.getJsonNumber("t").doubleValue();
                humidityValue = dataObject.getJsonNumber("h").doubleValue();

                // making sure that function gets the value
                getHumidityValue();
                getTemperatureValue();
                break;
            }
            default: {
                //TODO: save topics to array
                System.out.println("Undefined topic");
            }
        }

    }

    public double getHumidityValue() {
        return humidityValue;
    }

    public double getTemperatureValue() {
        return temperatureValue;
    }

    @Override
    public Class<?>[] getInterceptedMessageTypes() {
        // Define the types of messages this handler is interested in
        return new Class<?>[]{
                InterceptConnectMessage.class,
                InterceptPublishMessage.class,
                InterceptDisconnectMessage.class,
                InterceptConnectionLostMessage.class,
                InterceptSubscribeMessage.class,
                InterceptUnsubscribeMessage.class,
                InterceptAcknowledgedMessage.class
        };
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        System.out.println("Client disconnected: " + msg.toString());

        // Reseting values to default 0 to let user know sensor is not working
        humidityValue = 0;
        temperatureValue = 0;

        // making sure that function gets the value
        getHumidityValue();
        getTemperatureValue();

    }

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage msg) {
        System.out.printf("Connection lost from: %s, ID: %s \n", msg.getUsername(), msg.getClientID());
        // Reseting values to default 0 to let user know sensor is not working
        humidityValue = 0;
        temperatureValue = 0;

        // making sure that function gets the value
        getHumidityValue();
        getTemperatureValue();
    }

    @Override
    public void onSubscribe(InterceptSubscribeMessage msg) {
        String topic = msg.getTopicFilter();
        System.out.printf("Client %s subscribed to a topic: %s\n", msg.getClientID(), topic);
    }

    public void publish(Server mqttServer, String topic, String content) {
        MqttPublishMessage specialMessage = MqttMessageBuilders.publish()
                .topicName(topic)
                .retained(false)
                // The retained flag in MQTT specifies whether a published message should be stored
                // by the broker and delivered to future subscribers when they subscribe to the topic.
                // When set to true, the broker retains the message and sends it to new subscribers
                // immediately upon subscription, even if the message was published before the subscriber
                // subscribed to the topic.
                .qos(MqttQoS.EXACTLY_ONCE) // Adjust QoS as needed
                .payload(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8))
                .build();
        mqttServer.internalPublish(specialMessage, "ESP32Client");
    }

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
        String topic = msg.getTopicFilter();
        System.out.printf("Client %s unsubscribed from a topic: %s", msg.getClientID(), topic);
    }

    @Override
    public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
        System.out.println(msg);
    }

}

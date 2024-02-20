package com.beowulfe.hap.sample;

import io.github.hapjava.accessories.LightbulbAccessory;
import io.github.hapjava.accessories.optionalcharacteristic.AccessoryWithHardwareRevision;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import io.moquette.broker.Server;

import java.util.concurrent.CompletableFuture;

public class mqttLed implements LightbulbAccessory, AccessoryWithHardwareRevision {

    Server mqttServer;
    interceptHandler mqttDeviceInstance;
    String name, topic;
    int id;
    private boolean powerState = false;
    private HomekitCharacteristicChangeCallback subscribeCallback = null;

    public mqttLed(Server mqttServer, interceptHandler mqttDeviceInstance, String name, String topic, int id) {
        this.mqttServer = mqttServer;
        this.mqttDeviceInstance = mqttDeviceInstance;
        this.name = name;
        this.topic = topic;
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(name);
    }

    @Override
    public void identify() {
        System.out.println("Identifying " + name);
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture(name);
    }

    @Override
    public CompletableFuture<String> getModel() {
        return CompletableFuture.completedFuture("TestSwitch Model");
    }

    @Override
    public CompletableFuture<String> getManufacturer() {
        return CompletableFuture.completedFuture("Test SwitchManufacturer");
    }

    @Override
    public CompletableFuture<String> getFirmwareRevision() {
        return CompletableFuture.completedFuture("Test Switch Firmware");
    }

    @Override
    public CompletableFuture<Boolean> getLightbulbPowerState() {
        return CompletableFuture.completedFuture(powerState);
    }

    @Override
    public CompletableFuture<Void> setLightbulbPowerState(boolean powerState)
            throws Exception {
        this.powerState = powerState;
        if (subscribeCallback != null) {
            subscribeCallback.changed();
        }
        System.out.println("The " + name + " is now " + (powerState ? "on" : "off"));
        mqttDeviceInstance.publish(mqttServer, topic, (powerState ? "ON" : "OFF"));
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeLightbulbPowerState(
            HomekitCharacteristicChangeCallback callback) {
        this.subscribeCallback = callback;
    }

    @Override
    public void unsubscribeLightbulbPowerState() {
        this.subscribeCallback = null;
    }

    @Override
    public CompletableFuture<String> getHardwareRevision() {
        return CompletableFuture.completedFuture("Test Switch Hardware");
    }
}

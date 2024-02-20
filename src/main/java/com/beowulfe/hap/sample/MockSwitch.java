package com.beowulfe.hap.sample;

import io.github.hapjava.accessories.LightbulbAccessory;
import io.github.hapjava.accessories.optionalcharacteristic.AccessoryWithHardwareRevision;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;

import java.util.concurrent.CompletableFuture;

public class MockSwitch implements LightbulbAccessory, AccessoryWithHardwareRevision {
    private static int nextId = 1;
    String name;
    private final int id;
    private boolean powerState = false;
    private HomekitCharacteristicChangeCallback subscribeCallback = null;

    // Constructor without explicit ID (generates one automatically)
    public MockSwitch(String name) {
        this.id = nextId++;
        this.name = name;
    }

    // Constructor with explicit ID
    public MockSwitch(String name, int id) {
        this.id = id;
        this.name = name;
        nextId = Math.max(nextId, id + 1); // Ensure nextId is greater than the provided ID
        System.out.println(nextId); // TODO: Remove
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
        System.out.println("Identifying light");
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture("Test SwitchSN");
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

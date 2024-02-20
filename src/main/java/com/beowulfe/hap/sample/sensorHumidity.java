package com.beowulfe.hap.sample;

import io.github.hapjava.accessories.HumiditySensorAccessory;
import io.github.hapjava.accessories.optionalcharacteristic.AccessoryWithHardwareRevision;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;

import java.util.concurrent.CompletableFuture;

public class sensorHumidity implements HumiditySensorAccessory, AccessoryWithHardwareRevision {
    String name;
    interceptHandler mqttDeviceInstance;
    private final int id;
    private HomekitCharacteristicChangeCallback subscribeCallback = null;

    public sensorHumidity(String name, int id, interceptHandler mqttDeviceInstance) {
        this.name = name;
        this.id = id;
        this.mqttDeviceInstance = mqttDeviceInstance;
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
        System.out.println(name);
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture(name);
    }

    @Override
    public CompletableFuture<String> getModel() {
        return CompletableFuture.completedFuture("TestSensor model");
    }

    @Override
    public CompletableFuture<String> getManufacturer() {
        return CompletableFuture.completedFuture("Test SensorManufacturer");
    }

    @Override
    public CompletableFuture<String> getFirmwareRevision() {
        return CompletableFuture.completedFuture("Test sensor Firmware");
    }

    @Override
    public void subscribeCurrentRelativeHumidity(
            HomekitCharacteristicChangeCallback callback) {
        this.subscribeCallback = callback;
    }

    @Override
    public void unsubscribeCurrentRelativeHumidity() {
        this.subscribeCallback = null;
    }

    @Override
    public CompletableFuture<Double> getCurrentRelativeHumidity() {
        // Create a CompletableFuture to hold the humidity value
        CompletableFuture<Double> futureHumidity = new CompletableFuture<>();

        // Asynchronously fetch the humidity data
        CompletableFuture.supplyAsync(() -> {
            // This lambda function runs asynchronously
            // Inside the lambda, the getHumidityValue() method is called to retrieve the humidity value
            double humidity = mqttDeviceInstance.getHumidityValue();
            return humidity; // Return the retrieved humidity value
        }).thenAccept(humidity -> {
            // This part runs when the asynchronous operation completes
            // The humidity value obtained from the previous step is passed as an argument
            // The thenAccept() method completes the CompletableFuture with the humidity value
            futureHumidity.complete(humidity);
        });

        return futureHumidity; // Return the CompletableFuture immediately
    }


    @Override
    public CompletableFuture<String> getHardwareRevision() {
        return CompletableFuture.completedFuture("Test sensor Hardware");
    }
}

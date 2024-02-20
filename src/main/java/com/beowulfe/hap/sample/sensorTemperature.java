package com.beowulfe.hap.sample;

import io.github.hapjava.accessories.TemperatureSensorAccessory;
import io.github.hapjava.accessories.optionalcharacteristic.AccessoryWithHardwareRevision;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;

import java.util.concurrent.CompletableFuture;

public class sensorTemperature implements TemperatureSensorAccessory, AccessoryWithHardwareRevision {
    String name;
    interceptHandler mqttDeviceInstance;
    private final int id;
    private HomekitCharacteristicChangeCallback subscribeCallback = null;

    public sensorTemperature(String name, int id, interceptHandler mqttDeviceInstance) {
        this.name = name;
        this.id = id;
        this.mqttDeviceInstance = mqttDeviceInstance;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture("sensor #1");
    }

    @Override
    public void identify() {
        System.out.println("Identifying sensor");
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture("Test sensor");
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
    public void subscribeCurrentTemperature(
            HomekitCharacteristicChangeCallback callback) {
        this.subscribeCallback = callback;
    }

    @Override
    public void unsubscribeCurrentTemperature() {
        this.subscribeCallback = null;
    }

    @Override
    public CompletableFuture<Double> getCurrentTemperature() {
        // Create a CompletableFuture to hold the temperature value
        CompletableFuture<Double> futureTemperature = new CompletableFuture<>();

        // Asynchronously fetch the temperature data
        CompletableFuture.supplyAsync(() -> {
            // This lambda function runs asynchronously
            // Inside the lambda, the getTemperatureValue() method is called to retrieve the temperature value
            double temperature = mqttDeviceInstance.getTemperatureValue();
            return temperature; // Return the retrieved temperature value
        }).thenAccept(temperature -> {
            // This part runs when the asynchronous operation completes
            // The temperature value obtained from the previous step is passed as an argument
            // The thenAccept() method completes the CompletableFuture with the temperature value
            futureTemperature.complete(temperature);
        });

        return futureTemperature; // Return the CompletableFuture immediately
    }

    @Override
    public CompletableFuture<String> getHardwareRevision() {
        return CompletableFuture.completedFuture("Test sensor Hardware");
    }
}

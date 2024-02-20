package com.beowulfe.hap.sample;

import io.github.hapjava.server.impl.HomekitRoot;
import io.github.hapjava.server.impl.HomekitServer;
import io.github.hapjava.server.impl.crypto.HAPSetupCodeUtils;
import io.moquette.broker.Server;
import io.moquette.broker.config.MemoryConfig;

import java.io.*;
import java.util.Properties;

public class Main {
    private static final int PORT = 9123;

    public static void main(String[] args) {
        try {
            File authFile = new File("auth-state.bin");
            System.out.println("File exists: " + authFile.exists());
            MockAuthInfo mockAuth;
            if (authFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(authFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                try {
                    System.out.println("Using persisted auth");
                    AuthState authState = (AuthState) objectInputStream.readObject();
                    System.out.println("Loaded AuthState: " + authState.toString());
                    mockAuth = new MockAuthInfo(authState);
                } finally {
                    objectInputStream.close();
                }
            } else {
                mockAuth = new MockAuthInfo();
            }

            // Init Homekit server
            HomekitServer homekitServer = new HomekitServer(PORT);
            // Category: HomekitAccessoryCategories... (Category DISABLED)
            HomekitRoot bridge = homekitServer.createBridge(mockAuth, "Test Bridge", "TestBridge, Inc.", "C6", "12345abc", "1.1", "1.2");

            String setupURI = HAPSetupCodeUtils.getSetupURI(mockAuth.getPin().replace("-", ""), mockAuth.getSetupId(), 2);
            QRtoConsole.printQR(setupURI);


            mockAuth.onChange(state -> {
                try {
                    System.out.println("State has changed! Writing");
                    FileOutputStream fileOutputStream = new FileOutputStream(authFile);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(state);
                    /*  ^^^
                     * Logging: PIN, Mac, Salt, privateKey, SetupId & userKeyMap
                     * -Mit */

                    objectOutputStream.flush();
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            // MQTT server instance
            Server mqttServer = new Server();

            // create properties for mqqt memConfig
            Properties properties = new Properties();
            properties.setProperty("port", "1883");

            // creating custom config in memory
            MemoryConfig memConfig = new MemoryConfig(properties);
            mqttServer.startServer(memConfig);

            System.out.println("Moquette MQTT broker started.");
            // Add a callback to handle client connections, subscriptions, and messages
            interceptHandler mqttDeviceInstance = new interceptHandler();
            mqttServer.addInterceptHandler(mqttDeviceInstance);

            // Add a shutdown hook to gracefully stop the broker
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Stopping Moquette MQTT broker...");
                mqttServer.stopServer();
                System.out.println("Moquette MQTT broker stopped.");
            }));

            /* Ids can be generated automatically or added manually -Mit */
            // TODO: create global ids for devices
            bridge.addAccessory(new MockSwitch("Test", 2));
            //bridge.addAccessory(new MockSwitch("Led"));

            bridge.addAccessory(new mqttLed(mqttServer, mqttDeviceInstance, "LED", "led", 4));
            bridge.addAccessory(new sensorHumidity("Vlaga", 10, mqttDeviceInstance));
            bridge.addAccessory(new sensorTemperature("temperatura", 11, mqttDeviceInstance));

            //bridge.addAccessory(new mqttLed());

            // TODO: Add .properties for setting up Switches...lights...doors etc... and automatically assign required class

            // TODO: PRO TIP CE HOCES DODAT KAJ ISCI V ACCESSORIES MAPI IN NE V SERVICE

            // Starting Apple's homeBridge
            bridge.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Stopping homekit server.");
                homekitServer.stop();
            }));

        /*
            while (true) {
                // Your task here
                System.out.println("Executing task...");
                System.out.println(mqttDeviceInstance.getHumidityValue());
                System.out.println(mqttDeviceInstance.getTemperatureValue());
                //System.out.println(HumidityValue);
                try {
                    // Sleep for 10 seconds
                    Thread.sleep(10000); // 10 seconds = 10,000 milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        */


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

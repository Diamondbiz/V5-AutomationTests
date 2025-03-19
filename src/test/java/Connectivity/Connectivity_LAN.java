package Connectivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Connectivity_LAN {

    public static void main(String[] args) {
        System.out.println("=== LAN Connectivity Test Started ===");

        try {
            // Step 1: Check if LAN is connected
            if (isConnectedToLan()) {
                System.out.println("[INFO] The LAN is connected. Let's start the test!");

                // Test Step 1: Disconnect LAN for 15 seconds
                performLanDisconnectionTest(15);

                // Test Step 2: Disconnect LAN for 30 seconds
                performLanDisconnectionTest(30);

                // Test Step 3: Disconnect LAN for 35 seconds
                performLanDisconnectionTest(35);

                System.out.println("[SUCCESS] Test completed successfully. LAN connection is stable.");
            } else {
                System.out.println("[ERROR] LAN is not connected. Aborting test.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("=== LAN Connectivity Test Ended ===");
    }

    // Method to perform LAN disconnection and reconnection test
    private static void performLanDisconnectionTest(int disconnectDuration) throws IOException, InterruptedException {
        System.out.println("\n=== Test Step: Disconnect LAN for " + disconnectDuration + " seconds ===");

        // Disconnect LAN
        System.out.println("[INFO] LAN disconnected.");
        disconnectLan();
        System.out.println("[INFO] LAN is disconnected. Starting " + disconnectDuration + "-second countdown...");

        // Sample active network interface after disconnection
        String activeInterface = getActiveNetworkInterface();
        System.out.println("[INFO] Active network interface after LAN disconnection: " + activeInterface);

        // Countdown for disconnection duration
        for (int i = disconnectDuration; i > 0; i--) {
            System.out.println("[INFO] Disconnecting - " + i + " seconds remaining...");
            Thread.sleep(1000); // Wait for 1 second
        }

        // Reconnect LAN
        System.out.println("[INFO] Reconnecting LAN...");
        reconnectLan();

        // Wait for LAN to reconnect
        long startTime = System.currentTimeMillis();
        while (!isConnectedToLan()) {
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("[INFO] Waiting for LAN to reconnect... " + elapsedTime + " seconds elapsed");
            Thread.sleep(1000); // Wait for 1 second
        }

        long totalWaitTime = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("[INFO] LAN reconnected. Total wait time: " + totalWaitTime + " seconds.");
        System.out.println("[SUCCESS] LAN reconnected successfully.");

        // Stabilize connection
        System.out.println("[INFO] Waiting for 10 seconds to stabilize connection...");
        for (int i = 10; i > 0; i--) {
            System.out.println("[INFO] Stabilizing - " + i + " seconds remaining...");
            Thread.sleep(1000); // Wait for 1 second
        }
    }

    // Method to check if the device is connected to LAN
    private static boolean isConnectedToLan() throws IOException {
        String command = "adb shell ping -c 1 google.com";
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("1 received")) {
                return true;
            }
        }
        return false;
    }

    // Method to disconnect LAN
    private static void disconnectLan() throws IOException {
        String command = "adb shell ifconfig eth0 down"; // Replace eth0 with the correct interface
        Runtime.getRuntime().exec(command);
    }

    // Method to reconnect LAN
    private static void reconnectLan() throws IOException {
        String command = "adb shell ifconfig eth0 up"; // Replace eth0 with the correct interface
        Runtime.getRuntime().exec(command);
    }

    // Method to get the active network interface
    private static String getActiveNetworkInterface() throws IOException {
        String command = "adb shell netstat -i"; // Use netstat to list network interfaces
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Parse the output to find the active interface
        if (output.toString().contains("wlan0")) {
            return "Wi-Fi (wlan0)";
        } else if (output.toString().contains("eth0")) {
            return "LAN (eth0)";
        } else {
            return "Unknown";
        }
    }
}
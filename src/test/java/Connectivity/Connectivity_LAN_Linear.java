package Connectivity;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Connectivity_LAN_Linear {

    public AndroidDriver driver;
    private static final Logger logger = LogManager.getLogger(Connectivity_LAN_Linear.class);
    private static final String DEVICE_SERIAL = "GZ2407064N340046"; // Replace with your device serial number
    private static final String LAN_INTERFACE = "eth0";
    private static final int DISCONNECTION_DURATION_1 = 15;
    private static final int DISCONNECTION_DURATION_2 = 30;
    private static final int DISCONNECTION_DURATION_3 = 35;
    private static final int STABILIZATION_DURATION = 10;

    @BeforeTest
    void setUp() {
        try {
            UiAutomator2Options options = new UiAutomator2Options()
                    .setDeviceName("HOTStreamerV4-b1d6")
                    .setUdid(DEVICE_SERIAL)
                    .setAppPackage("il.net.hot.hot")
                    .setAppActivity("il.net.hot.hot.TvMainActivity")
                    .setNoReset(true)
                    .setAutoGrantPermissions(true);

            driver = new AndroidDriver(new URL("http://localhost:4724"), options);
            logger.info("Appium session started successfully.");
        } catch (Exception e) {
            logger.error("Failed to start Appium session: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLanConnectivity() {
        try {
            logger.info("=== LAN Connectivity Test Started ===");

            if (!sampleLanConnection(DEVICE_SERIAL)) {
                logger.error("[ERROR] The LAN is disconnected. Stopping the test.");
                return;
            }

            performTestStep("Step 1: Disconnect LAN for 15 seconds", DISCONNECTION_DURATION_1);
            performTestStep("Step 2: Disconnect LAN for 30 seconds", DISCONNECTION_DURATION_2);
            performTestStep("Step 3: Disconnect LAN for 35 seconds", DISCONNECTION_DURATION_3);

            logger.info("=== LAN Connectivity Test Ended ===");
        } catch (Exception e) {
            logger.error("Test failed: " + e.getMessage());
        }
    }

    private void performTestStep(String stepName, int disconnectionDuration) throws Exception {
        logger.info("\n=== " + stepName + " ===");
        disconnectLan(DEVICE_SERIAL);
        if (!sampleLanConnection(DEVICE_SERIAL)) {
            countdownTimer(disconnectionDuration, "Disconnecting");
        }
        reconnectLan(DEVICE_SERIAL);
        waitForLanConnection(DEVICE_SERIAL);
        countdownTimer(STABILIZATION_DURATION, "Stabilizing");
    }

    private static boolean sampleLanConnection(String deviceSerial) throws Exception {
        Process process = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell ifconfig " + LAN_INTERFACE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("UP")) {
                return true;
            }
        }
        return false;
    }

    private static void disconnectLan(String deviceSerial) throws Exception {
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell ifconfig " + LAN_INTERFACE + " down");
        logger.info("[INFO] LAN disconnected.");
    }

    private static void reconnectLan(String deviceSerial) throws Exception {
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell ifconfig " + LAN_INTERFACE + " up");
        logger.info("[INFO] LAN reconnected.");
    }

    private static void waitForLanConnection(String deviceSerial) throws Exception {
        int waitTime = 0;
        while (!sampleLanConnection(deviceSerial)) {
            logger.info("[INFO] Waiting for LAN to reconnect... " + waitTime + " seconds elapsed");
            Thread.sleep(1000);
            waitTime++;
        }
        logger.info("[INFO] LAN reconnected. Total wait time: " + waitTime + " seconds.");
    }

    private static void countdownTimer(int seconds, String action) {
        for (int i = seconds; i >= 0; i--) {
            logger.info("[INFO] " + action + " - " + i + " seconds remaining...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Countdown interrupted: " + e.getMessage());
            }
        }
    }

    @AfterTest
    void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("Appium session ended.");
        }
    }
}
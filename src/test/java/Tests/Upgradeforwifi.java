package Tests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class Upgradeforwifi {

    public AndroidDriver driver;
    public WebDriverWait wait;
    private String videoFileName; // Store the video filename

    // Define paths (Make sure these paths are correct for your system)
    private static final String PROJECT_PATH = System.getProperty("user.dir"); // Gets the project directory
    private static final String SCREENSHOTS_PATH = PROJECT_PATH + "/screenshots";
    private static final String RECORDINGS_PATH = PROJECT_PATH + "/recordings";
    private static final String LOGS_PATH = PROJECT_PATH + "/logs";

    // Define locators for potential UI elements during disconnection and reconnection
    private final By loadingSpinnerLocator = AppiumBy.id("dvLoader"); // USE AppiumBy.id
    private final By hotIconLocator = By.xpath("//android.widget.ImageView[@content-desc=\"HOT Play\"]");


    private static final Logger logger = LogManager.getLogger(Upgradeforwifi.class);


    @BeforeTest
    public void setUp() {
        try {
            // Create directories if they don't exist
            new File(SCREENSHOTS_PATH).mkdirs();
            new File(RECORDINGS_PATH).mkdirs();
            new File(LOGS_PATH).mkdirs();

            // Define Appium capabilities
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", "Android");
            caps.setCapability("appium:automationName", "UiAutomator2");
            caps.setCapability("appium:deviceName", "HOTStreamerV4-b1d6");
            caps.setCapability("appium:udid", "GZ2407064N340046");
            caps.setCapability("appium:appPackage", "il.net.hot.hot");
            caps.setCapability("appium:appActivity", "il.net.hot.hot.TvMainActivity");
            caps.setCapability("appium:platformVersion", "14");
            caps.setCapability("appium:noReset", true);
            caps.setCapability("appium:noSign", true);
            caps.setCapability("appium:autoGrantPermissions", true);
            // caps.setCapability("appium:autoLaunch", true); //  needed if noReset = true
            caps.setCapability("appium:skipUnlock", true);
            caps.setCapability("appium:disableWindowAnimation", false);

            driver = new AndroidDriver(new URL("http://localhost:4724"), caps);
            wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Initialize explicit wait

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }

    @Test
    public void runTest() {
        String deviceSerial = "GZ2407064N340046";

        // No BufferedWriter needed here when using Log4j2

        // Start screen recording and store the filename
        try {
            startScreenRecording(deviceSerial);
        } catch (Exception e) {
            logger.error("Failed to start screen recording", e);
            return; // Exit if recording can't start
        }

        // Test Header
        logger.info("=== Wi-Fi Connectivity Test Started ===");

        // Initial Wi-Fi check
        boolean isWifiConnected;
        try {
            isWifiConnected = sampleWifiConnection(deviceSerial);
        } catch (Exception e) {
            logger.error("Error sampling Wi-Fi connection", e);
            return; // Exit if we can't even check Wi-Fi status
        }

        if (!isWifiConnected) {
            logger.error("The Wi-Fi is disconnected. Stopping the test.");
            return; // Stop if Wi-Fi is initially disconnected
        }
        logger.info("The Wi-Fi is connected. Starting the test!");

        // --- Test Steps (Refactored into a method) ---
        try {
            runWifiTestStep(deviceSerial, 15, "Step 1");
            runWifiTestStep(deviceSerial, 30, "Step 2");
            runWifiTestStep(deviceSerial, 35, "Step 3");
        }catch (Exception e){
            logger.error("Error",e);
            return;
        }

        // Final check
        try {
            if (sampleWifiConnection(deviceSerial)) {
                logger.info("Test completed successfully. Wi-Fi connection is stable.");
            } else {
                logger.error("Test failed. Wi-Fi connection lost.");
            }
        } catch (Exception e) {
            logger.error("Error in final Wi-Fi check", e);
        }

        logger.info("=== Wi-Fi Connectivity Test Ended ===");

    }

    private void runWifiTestStep(String deviceSerial, int disconnectTime, String stepName) throws Exception {
        logger.info("=== Test {}: Disconnect Wi-Fi for {} seconds ===", stepName, disconnectTime);

        disconnectWifi(deviceSerial);
        Thread.sleep(500); // Short wait after disconnecting

        if (sampleWifiConnection(deviceSerial)) {
            logger.warn("Wi-Fi is still connected.");
        } else {
            logger.info("Wi-Fi is disconnected. Starting {}-second countdown...", disconnectTime);
            takeScreenshot("Before_Disconnect_WiFi_" + stepName);
            checkAppBehavior("disconnection"); // No BufferedWriter needed
            countdownTimer(disconnectTime, "Disconnecting");
        }

        logger.info("Reconnecting Wi-Fi...");
        reconnectWifi(deviceSerial);
        waitForWifiConnection(deviceSerial);

        if (sampleWifiConnection(deviceSerial)) {
            logger.info("Wi-Fi reconnected successfully.");
            takeScreenshot("After_Reconnect_WiFi_" + stepName);
            checkAppBehavior("reconnection"); // No BufferedWriter needed
        } else {
            logger.error("Wi-Fi connection lost.");
        }

        logger.info("Waiting for 10 seconds to stabilize connection...");
        countdownTimer(10, "Stabilizing");
    }

    private void checkAppBehavior(String state) {
        try {
            if ("disconnection".equals(state)) {
                // Wait for the loader element ONLY.
                wait.until(ExpectedConditions.visibilityOfElementLocated(loadingSpinnerLocator));

                if (isElementPresent(loadingSpinnerLocator)) {
                    logger.info("Loading spinner displayed during disconnection.");
                } else {
                    // This else block should not be reached if the wait.until is successful.
                    // It's kept for completeness, but the timeout exception will likely occur
                    // before this point if the element is not found.
                    logger.warn("Loader not found, but wait.until did not time out (unexpected).");
                }
            } else if ("reconnection".equals(state)) {
                // Wait for the loader element ONLY.
                wait.until(ExpectedConditions.visibilityOfElementLocated(loadingSpinnerLocator));

                if (isElementPresent(loadingSpinnerLocator)) {
                    logger.info("Loading spinner displayed during Reconnection.");
                } else {
                    // This else block should not be reached if the wait.until is successful.
                    // It's kept for completeness, but the timeout exception will likely occur
                    // before this point if the element is not found.
                    logger.warn("Loader not found, but wait.until did not time out (unexpected).");
                }
            } else {
                logger.warn("Invalid state provided to checkAppBehavior: {}", state);
            }
        } catch (Exception e) {
            logger.error("Exception in checkAppBehavior", e);
        }
    }

    private boolean isElementPresent(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public static boolean sampleWifiConnection(String deviceSerial) throws Exception {
        Process process = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell settings get global wifi_on");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            return "1".equals(line); // Returns true if Wi-Fi is connected (1), false otherwise (0)
        }
    }

    private static void disconnectWifi(String deviceSerial) throws Exception {
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell svc wifi disable");
    }

    private static void reconnectWifi(String deviceSerial) throws Exception {
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell svc wifi enable");
    }


    private static void waitForWifiConnection(String deviceSerial) throws Exception {
        int waitTime = 0;
        while (!sampleWifiConnection(deviceSerial)) {
            System.out.print("\r[INFO] Waiting for Wi-Fi to reconnect... " + waitTime + " seconds elapsed");
            Thread.sleep(1000); // Check every 1 second
            waitTime++;
        }
        System.out.println("\r[INFO] Wi-Fi reconnected. Total wait time: " + waitTime + " seconds.");
    }

    private void takeScreenshot(String testStepName) {
        try {
            // Create a timestamp for the screenshot name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String screenshotName = testStepName + "_" + timeStamp + ".png";

            // Take the screenshot
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // Use Files.move for atomic operation and better error handling:
            Files.move(screenshotFile.toPath(), new File(SCREENSHOTS_PATH + "/" + screenshotName).toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.debug("[INFO] Screenshot taken: " + screenshotName); //DEBUG level
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to take screenshot: " + e.getMessage());
            logger.error("Failed to take screenshot",e);
        }
    }


    private void startScreenRecording(String deviceSerial) throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        videoFileName = "connectivity_test_" + timeStamp + ".mp4"; // Store filename
        Process process = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell screenrecord /sdcard/" + videoFileName);
        // No need to consume the output stream here.  screenrecord runs in the background.

        logger.debug("[INFO] Screen recording started: " + videoFileName); //DEBUG level
    }


    private void stopScreenRecording(String deviceSerial) throws Exception {

        // Stop the screen recording using pkill -2 (SIGINT)
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell pkill -2 screenrecord");
        logger.info("Screen recording stopped.");

        // Wait for the recording to finish (important!)
        Thread.sleep(5000); //  wait 5 seconds.  Adjust as needed.

        // Find the video file (more robust than assuming the filename)
        Process findProcess = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell ls -t /sdcard/connectivity_test_*.mp4");
        String videoPath;
        try (BufferedReader findReader = new BufferedReader(new InputStreamReader(findProcess.getInputStream()))) {
            videoPath = findReader.readLine(); // Get the first (most recent) file
        }

        if (videoPath == null || videoPath.trim().isEmpty()) {
            System.out.println("[ERROR] No video file found on device.");
            logger.error("No video file found on the device.");
            return; // Exit if no video found
        }
        videoPath = videoPath.trim();


        // Pull the recorded video to the local machine
        String localVideoPath = RECORDINGS_PATH + "/" + videoFileName; // Use stored name for local path.
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " pull " + videoPath + " " + localVideoPath);
        System.out.println("[INFO] Screen recording saved to " + localVideoPath);
        logger.info("Screen recording saved to {}", localVideoPath);


        // Delete the video from the device (optional but good practice)
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell rm " + videoPath);
        logger.info("Video file removed from device");
    }


    public static void countdownTimer(int seconds, String action) {
        System.out.print("[INFO] " + action + " - " + seconds + " seconds remaining...");
        for (int i = seconds - 1; i >= 0; i--) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); //  restore interrupted status
                logger.error("Countdown timer interrupted",e);
                return; // Exit the countdown if interrupted
            }
            System.out.print("\r[INFO] " + action + " - " + i + " seconds remaining...");
        }
        System.out.println();
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            try {
                stopScreenRecording(driver.getCapabilities().getCapability("udid").toString());
            } catch (Exception e) {
                logger.error("Error stopping screen recording", e);
            }
            driver.quit();
        }
    }
}
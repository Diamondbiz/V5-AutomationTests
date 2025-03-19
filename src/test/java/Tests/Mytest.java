package Tests;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class Mytest {

    private AndroidDriver driver; // Instance variable for the driver
    private WebDriverWait wait;

    // Define locators (if any are used in this class; otherwise, remove)
    // Example:  public final By hotIconLocator = By.xpath("//android.widget.ImageView[@content-desc=\"HOT Play\"]");


    // Initialization method (replaces @BeforeTest)
    public void initialize(String deviceSerial) {
        try {

            // Define Appium capabilities
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", "Android");
            caps.setCapability("appium:automationName", "UiAutomator2");
            caps.setCapability("appium:deviceName", "HOTStreamerV4-b1d6"); //  Device Name
            caps.setCapability("appium:udid", deviceSerial); // Use the deviceSerial
            caps.setCapability("appium:appPackage", "il.net.hot.hot");
            caps.setCapability("appium:appActivity", "il.net.hot.hot.TvMainActivity");
            caps.setCapability("appium:platformVersion", "14");
            caps.setCapability("appium:noReset", true);
            caps.setCapability("appium:noSign", true);
            caps.setCapability("appium:autoGrantPermissions", true);
            caps.setCapability("appium:autoLaunch", true);
            caps.setCapability("appium:skipUnlock", true);
            caps.setCapability("appium:disableWindowAnimation", false);
            driver = new AndroidDriver(new URL("http://localhost:4724"), caps); // Use the instance variable
            wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Initialize explicit wait
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }

    @Test
    public void runWifiConnectivityTest() {
        String deviceSerial = "GZ2407064N340046"; // Device serial number
        initialize(deviceSerial); // Initialize Appium and set up driver
        try {
            // Start screen recording
            startScreenRecording(deviceSerial);
            // Test Header
            System.out.println("=== Wi-Fi Connectivity Test Started ===");
            // Sample the Wi-Fi connection before executing any commands
            boolean isWifiConnected = isWifiConnected(deviceSerial);
            if (!isWifiConnected) {
                System.out.println("[ERROR] The Wi-Fi is disconnected. Stopping the test.");
                return; // Stop the test if Wi-Fi is not connected
            } else {
                System.out.println("[INFO] The Wi-Fi is connected. Let's start the test!");
            }

            // Test Step 1: Disconnect Wi-Fi for 15 seconds
            System.out.println("\n=== Test Step 1: Disconnect Wi-Fi for 15 seconds ===");
            disconnectWifi(deviceSerial);

            if (isWifiConnected(deviceSerial)) {
                System.out.println("[WARNING] Wi-Fi is still connected.");
            } else {
                System.out.println("[INFO] Wi-Fi is disconnected. Starting 15-second countdown...");
                takeScreenshot("Before_Disconnect_WiFi_Step1"); // Dynamic screenshot name
                countdownTimer(15, "Disconnecting"); // Use the improved countdown timer
            }

            // ... (rest of your test steps, using the improved methods)
            reconnectWifi(deviceSerial);
            waitForWifiReconnection(deviceSerial);
            if(isWifiConnected(deviceSerial)){
                System.out.println("[SUCCESS] Wi-Fi reconnected successfully.");
                takeScreenshot("After_Reconnect_WiFi_Step1");
            } else {
                System.out.println("[ERROR] Wi-Fi connection lost.");
            }

            System.out.println("\n[INFO] Waiting for 10 seconds to stabilize connection...");
            countdownTimer(10, "Stabilizing");
            System.out.println("[INFO] 10 seconds to next Wi-Fi disconnection.");

            // Test Step 2: Disconnect Wi-Fi for 30 seconds
            System.out.println("\n=== Test Step 2: Disconnect Wi-Fi for 30 seconds ===");
            disconnectWifi(deviceSerial);

            if (isWifiConnected(deviceSerial)) {
                System.out.println("[WARNING] Wi-Fi is still connected.");
            } else {
                System.out.println("[INFO] Wi-Fi is disconnected. Starting 30-second countdown...");
                takeScreenshot("Before_Disconnect_WiFi_Step2"); // Dynamic screenshot name
                countdownTimer(30, "Disconnecting");
            }
            reconnectWifi(deviceSerial);
            waitForWifiReconnection(deviceSerial);
            if(isWifiConnected(deviceSerial)){
                System.out.println("[SUCCESS] Wi-Fi reconnected successfully.");
                takeScreenshot("After_Reconnect_WiFi_Step2");
            } else {
                System.out.println("[ERROR] Wi-Fi connection lost.");
            }

            System.out.println("\n[INFO] Waiting for 10 seconds to stabilize connection...");
            countdownTimer(10, "Stabilizing");
            System.out.println("[INFO] 10 seconds to next Wi-Fi disconnection.");

            // Test Step 3: Disconnect Wi-Fi for 35 seconds
            System.out.println("\n=== Test Step 3: Disconnect Wi-Fi for 35 seconds ===");
            disconnectWifi(deviceSerial);
            if (isWifiConnected(deviceSerial)) {
                System.out.println("[WARNING] Wi-Fi is still connected.");
            } else {
                System.out.println("[INFO] Wi-Fi is disconnected. Starting 35-second countdown...");
                takeScreenshot("Before_Disconnect_WiFi_Step3");
                countdownTimer(35, "Disconnecting");
            }
            reconnectWifi(deviceSerial);
            waitForWifiReconnection(deviceSerial); // Wait until Wi-Fi is reconnected

            if (isWifiConnected(deviceSerial)) {
                System.out.println("[SUCCESS] Wi-Fi reconnected successfully.");
                takeScreenshot("After_Reconnect_WiFi_Step3");
            } else {
                System.out.println("[ERROR] Wi-Fi connection lost.");
            }
            // Wait for 10 seconds to stabilize connection
            System.out.println("\n[INFO] Waiting for 10 seconds to stabilize connection...");
            countdownTimer(10, "Stabilizing");
            System.out.println("[INFO] 10 seconds till test ends.");

            // Final check
            if (isWifiConnected(deviceSerial)) {
                System.out.println("\n[SUCCESS] Test completed successfully. Wi-Fi connection is stable.");
            } else {
                System.out.println("\n[ERROR] Test failed. Wi-Fi connection lost.");
            }

            // Test Footer
            System.out.println("\n=== Wi-Fi Connectivity Test Ended ===");

            // Stop screen recording
            stopScreenRecording(deviceSerial);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit(); // Ensure the driver is quit even if there's an exception
            }
        }
    }

    // Improved method to check Wi-Fi connection (more descriptive name)
    private boolean isWifiConnected(String deviceSerial) throws Exception {
        Process process = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell dumpsys wifi | grep 'Wi-Fi is '");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("Wi-Fi is enabled")) {
                return true;
            } else if (line.contains("Wi-Fi is disabled")) {
                return false;
            }
        }
        return false; // Default to false if no status is found
    }

    // Improved method to wait for Wi-Fi reconnection (more descriptive name)
    private void waitForWifiReconnection(String deviceSerial) throws Exception {
        int waitTime = 0;
        final int maxWaitTime = 60; // Set a maximum wait time (e.g., 60 seconds)

        while (!isWifiConnected(deviceSerial) && waitTime < maxWaitTime) {
            System.out.print("\r[INFO] Waiting for Wi-Fi to reconnect... " + waitTime + " seconds elapsed");
            System.out.flush();
            Thread.sleep(1000);
            waitTime++;
        }

        if (waitTime >= maxWaitTime) {
            System.out.println("\n[ERROR] Wi-Fi failed to reconnect within " + maxWaitTime + " seconds.");
        } else {
            System.out.println("\n[INFO] Wi-Fi reconnected. Total wait time: " + waitTime + " seconds.");
        }
    }


    // Improved countdown timer (clearer output, handles interruption)
    public void countdownTimer(int seconds, String action) {
        try {
            for (int i = seconds; i > 0; i--) {
                System.out.print("\r" + action + "... " + i + " seconds remaining"); // \r returns to the beginning of the line
                System.out.flush(); // Flush the output to ensure it's displayed immediately
                Thread.sleep(1000);
            }
            System.out.println("\r" + action + " complete.                   "); // Clear the line after countdown
        } catch (InterruptedException e) {
            System.err.println("\nCountdown interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }

    // Method to disconnect Wi-Fi
    private void disconnectWifi(String deviceSerial) throws Exception {
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell svc wifi disable");
    }

    // Method to reconnect Wi-Fi
    private void reconnectWifi(String deviceSerial) throws Exception {
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell svc wifi enable");
    }

    // Method to take a screenshot
    private void takeScreenshot(String testStepName) {
        try {
            // Create a timestamp for the screenshot name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String screenshotName = testStepName + "_" + timeStamp + ".png";

            // Take the screenshot using the instance variable 'driver'
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(SCREENSHOTS_PATH, screenshotName); // Use File constructor
            if (screenshotFile.renameTo(destinationFile)) {
                System.out.println("[INFO] Screenshot taken: " + destinationFile.getAbsolutePath());
            } else {
                System.err.println("[ERROR] Failed to move screenshot to: " + destinationFile.getAbsolutePath());
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to take screenshot: " + e.getMessage());
        }
    }


    // Method to start screen recording (Now an instance method)
    private void startScreenRecording(String deviceSerial) throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoName = "connectivity_test_" + timeStamp + ".mp4";
        //Using adb shell command.
        Process process = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell screenrecord /sdcard/" + videoName);
        //Don't wait.
        System.out.println("[INFO] Screen recording started: " + videoName);
    }

    // Method to stop screen recording and pull the video (Now an instance method)
    private void stopScreenRecording(String deviceSerial) throws Exception {

        //Find the screenrecord process id, and stop the process
        Process pidProcess = Runtime.getRuntime().exec("adb -s "+ deviceSerial + " shell pgrep -f screenrecord");
        BufferedReader reader = new BufferedReader(new InputStreamReader(pidProcess.getInputStream()));
        String pid = reader.readLine();


        if (pid != null && !pid.isEmpty()) {
            // Stop the screen recording.  Critically, we need to send a SIGINT (signal 2)
            // to the screenrecord process.  Using pkill -2 is the easiest way to do this.
            Process stopProcess = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell kill -2 " + pid);
            stopProcess.waitFor(); // Wait for the process to terminate
            System.out.println("[INFO] Screen recording stopped.");

            //Get the video file name:
            Process videoNameProcess = Runtime.getRuntime().exec("adb -s "+ deviceSerial + " shell ls -t /sdcard/*.mp4");
            BufferedReader videoNameReader = new BufferedReader(new InputStreamReader(videoNameProcess.getInputStream()));
            String videoName = videoNameReader.readLine();


            if (videoName != null && !videoName.isEmpty()){
                // Pull the recorded video to the local machine
                Process pullProcess = Runtime.getRuntime().exec("adb -s " + deviceSerial + " pull " + videoName + " " + RECORDINGS_PATH + "/");
                pullProcess.waitFor(); // Crucial: wait for the pull to complete.
                System.out.println("[INFO] Screen recording saved to " + RECORDINGS_PATH + videoName.substring(videoName.lastIndexOf('/')));
            }
            else {
                System.out.println("[ERROR] No video file found on device.");
            }

        } else {
            System.out.println("[WARNING] No active screen recording process found.");
        }
    }
}
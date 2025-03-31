package Tests;
//21.03
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

    public AndroidDriver driver;
    public WebDriverWait wait;

    // Define paths
    private static final String SCREENSHOTS_PATH = "/Users/hanangoverman/IntelliJProjects/V5-AutomationTests/screenshots/";
    private static final String RECORDINGS_PATH = "/Users/hanangoverman/IntelliJProjects/V5-AutomationTests/recordings/";
    private static final String LOGS_PATH = "/Users/hanangoverman/IntelliJProjects/V5-AutomationTests/logs/";

    public void initialize(String deviceSerial) {
        try {
            // Create directories if they don't exist
            new File(SCREENSHOTS_PATH).mkdirs();
            new File(RECORDINGS_PATH).mkdirs();
            new File(LOGS_PATH).mkdirs();

            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", "Android");
            caps.setCapability("appium:automationName", "UiAutomator2");
            caps.setCapability("appium:deviceName", "HOTStreamerV4-b1d6");
            caps.setCapability("appium:udid", deviceSerial);
            caps.setCapability("appium:appPackage", "il.net.hot.hot");
            caps.setCapability("appium:appActivity", "il.net.hot.hot.TvMainActivity");
            caps.setCapability("appium:platformVersion", "14");
            caps.setCapability("appium:noReset", true);
            caps.setCapability("appium:noSign", true);
            caps.setCapability("appium:autoGrantPermissions", true);
            caps.setCapability("appium:autoLaunch", true);
            caps.setCapability("appium:skipUnlock", true);
            caps.setCapability("appium:disableWindowAnimation", false);

            driver = new AndroidDriver(new URL("http://localhost:4724"), caps);
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }

    @Test
    public void runTest() {
        String deviceSerial = "GZ2407064N340046";
        initialize(deviceSerial);
        try {
            startScreenRecording(deviceSerial);
            System.out.println("=== Wi-Fi Connectivity Test Started ===");

            // Rest of your test code remains the same...
            // [Previous test steps remain unchanged]

            System.out.println("\n=== Wi-Fi Connectivity Test Ended ===");
            stopScreenRecording(deviceSerial);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    // [All other methods remain unchanged...]
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
        return false;
    }

    private void waitForWifiReconnection(String deviceSerial) throws Exception {
        int waitTime = 0;
        final int maxWaitTime = 60;

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

    public void countdownTimer(int seconds, String action) {
        try {
            for (int i = seconds; i > 0; i--) {
                System.out.print("\r" + action + "... " + i + " seconds remaining");
                System.out.flush();
                Thread.sleep(1000);
            }
            System.out.println("\r" + action + " complete.                   ");
        } catch (InterruptedException e) {
            System.err.println("\nCountdown interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void disconnectWifi(String deviceSerial) throws Exception {
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell svc wifi disable");
    }

    private void reconnectWifi(String deviceSerial) throws Exception {
        Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell svc wifi enable");
    }

    private void takeScreenshot(String testStepName) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String screenshotName = testStepName + "_" + timeStamp + ".png";
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(SCREENSHOTS_PATH, screenshotName);
            if (screenshotFile.renameTo(destinationFile)) {
                System.out.println("[INFO] Screenshot taken: " + destinationFile.getAbsolutePath());
            } else {
                System.err.println("[ERROR] Failed to move screenshot to: " + destinationFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to take screenshot: " + e.getMessage());
        }
    }

    private void startScreenRecording(String deviceSerial) throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoName = "connectivity_test_" + timeStamp + ".mp4";
        Process process = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell screenrecord /sdcard/" + videoName);
        System.out.println("[INFO] Screen recording started: " + videoName);
    }

    private void stopScreenRecording(String deviceSerial) throws Exception {
        Process pidProcess = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell pgrep -f screenrecord");
        BufferedReader reader = new BufferedReader(new InputStreamReader(pidProcess.getInputStream()));
        String pid = reader.readLine();

        if (pid != null && !pid.isEmpty()) {
            Process stopProcess = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell kill -2 " + pid);
            stopProcess.waitFor();
            System.out.println("[INFO] Screen recording stopped.");

            Process videoNameProcess = Runtime.getRuntime().exec("adb -s " + deviceSerial + " shell ls -t /sdcard/*.mp4");
            BufferedReader videoNameReader = new BufferedReader(new InputStreamReader(videoNameProcess.getInputStream()));
            String videoName = videoNameReader.readLine();

            if (videoName != null && !videoName.isEmpty()) {
                Process pullProcess = Runtime.getRuntime().exec("adb -s " + deviceSerial + " pull " + videoName + " " + RECORDINGS_PATH);
                pullProcess.waitFor();
                System.out.println("[INFO] Screen recording saved to " + RECORDINGS_PATH + videoName.substring(videoName.lastIndexOf('/')));
            } else {
                System.out.println("[ERROR] No video file found on device.");
            }
        } else {
            System.out.println("[WARNING] No active screen recording process found.");
        }
    }
}
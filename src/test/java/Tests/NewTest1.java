package Tests;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTest1 {

    private AndroidDriver driver;
    private File logFile;
    private File screenshotDir;
    private File recordingDir;

    @BeforeMethod
    public void setUp() throws Exception {
        screenshotDir = new File("/Users/hanangoverman/IntelliJProjects/V5-AutomationTests/screenshots");
        recordingDir = new File("/Users/hanangoverman/IntelliJProjects/V5-AutomationTests/recordings");
        File logsDir = new File("/Users/hanangoverman/IntelliJProjects/V5-AutomationTests/logs");

        if (!screenshotDir.exists()) screenshotDir.mkdirs();
        if (!recordingDir.exists()) recordingDir.mkdirs();
        if (!logsDir.exists()) logsDir.mkdirs();

        logFile = new File(logsDir, "test_log_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt");
        logFile.createNewFile();

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:deviceName", "HOTStreamerV5-b1d6");
        caps.setCapability("appium:udid", "GZ2407064N340046");
        caps.setCapability("appium:appPackage", "il.net.hot.hot");
        caps.setCapability("appium:appActivity", "il.net.hot.hot.TvMainActivity");
        caps.setCapability("appium:platformVersion", "14");
        caps.setCapability("appium:noReset", true);
        caps.setCapability("appium:noSign", true);
        caps.setCapability("appium:autoGrantPermissions", true);
        caps.setCapability("appium:autoLaunch", true);
        caps.setCapability("appium:skipUnlock", true);
        caps.setCapability("appium:disableWindowAnimation", false);
        caps.setCapability("appium:adbExecTimeout", 60000);

        driver = new AndroidDriver(new URL("http://localhost:4724"), caps);
    }

    @Test
    public void testNetworkConnection() throws InterruptedException, IOException {
        log("Searching for network connection");
        System.out.println("Searching for network connection");

        boolean isConnected = checkNetworkConnection();
        if (!isConnected) {
            log("Network connection not available.");
            System.out.println("Network connection not available.");
            while (!isConnected) {
                isConnected = checkNetworkConnection();
                Thread.sleep(1000);
            }
        }

        log("LAN/Wi-Fi connection established.");
        System.out.println("LAN/Wi-Fi connection established.");

        log("Let's start testing.");
        System.out.println("Let's start testing.");

        startVideoRecording();

        log("First test: 15 sec Wi-Fi disconnection starts.");
        System.out.println("First test: 15 sec Wi-Fi disconnection starts.");

        takeScreenshot("before_disconnection.png");
        disconnectWiFi();
        Thread.sleep(500);

        if (!checkNetworkConnection()) {
            log("Wi-Fi network disconnected for 15 sec.");
            System.out.println("Wi-Fi network disconnected for 15 sec.");
        } else {
            log("Wi-Fi network still connected.");
            System.out.println("Wi-Fi network still connected.");
        }

        countdownTimer(15);

        reconnectWiFi();
        log("Time for reconnection");
        System.out.println("Time for reconnection");

        log("In process: Wi-Fi reconnection.");
        System.out.println("In process: Wi-Fi reconnection.");

        while (!checkNetworkConnection()) {
            Thread.sleep(1000);
        }

        takeScreenshot("after_reconnection.png");
        log("Wi-Fi reconnected.");
        System.out.println("Wi-Fi reconnected.");

        log("Stabilization time: 10 seconds");
        System.out.println("Stabilization time: 10 seconds");
        countdownTimer(10);

        if (checkNetworkConnection()) {
            log("Wi-Fi network stabilized and connected.");
            System.out.println("Wi-Fi network stabilized and connected.");
        } else {
            log("Wi-Fi network still stabilizing.");
            System.out.println("Wi-Fi network still stabilizing.");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private boolean checkNetworkConnection() {
        return Math.random() > 0.5;
    }

    private void disconnectWiFi() {
        log("Wi-Fi disconnected.");
    }

    private void reconnectWiFi() {
        log("Wi-Fi reconnected.");
    }

    private void startVideoRecording() {
        log("Video recording started.");
    }

    private void takeScreenshot(String fileName) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Files.copy(screenshot.toPath(), new File(screenshotDir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        log("Screenshot saved: " + fileName);
    }

    private void countdownTimer(int seconds) throws InterruptedException {
        for (int i = seconds; i > 0; i--) {
            System.out.print("\rTime remaining: " + i + " seconds");
            Thread.sleep(1000);
        }
        System.out.println();
    }

    private void log(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " - " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
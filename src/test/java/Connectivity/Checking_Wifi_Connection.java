package Connectivity;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;

public class Checking_Wifi_Connection {

    private AndroidDriver driver;
    private WebDriverWait wait;

    // Define locators
    private final By settings = By.xpath("//android.widget.ImageView[@resource-id=\"com.google.android.tvlauncher:id/button_icon\"]");
    private final By WiFi5Mhz = By.xpath("//android.widget.TextView[@resource-id=\"android:id/summary\" and @text=\"QA BLUE-SKY 5Mhz\"]");
    private final By WiFi24Mhz = By.xpath("//android.widget.TextView[@resource-id=\"android:id/title\" and @text=\"QA BLUE-SKY 2.4Mhz\"]");
    private final By OlegsiPhone = By.xpath("//android.widget.TextView[@resource-id=\"android:id/title\" and @text=\"Oleg’s iPhone\"]");
    private final By Mainscreenuppermenu = By.id("com.google.android.tvlauncher:id/items_container");
    @BeforeTest
    void setUp() {
        try {
            // Define Appium capabilities
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", "Android");
            caps.setCapability("appium:automationName", "UiAutomator2");
            caps.setCapability("appium:deviceName", "HOTStreamerV4-b1d6");
            caps.setCapability("appium:udid", "192.168.1.248:5555");
            caps.setCapability("appium:appPackage", "il.net.hot.hot");
            caps.setCapability("appium:appActivity", "il.net.hot.hot.TvMainActivity");
            caps.setCapability("appium:platformVersion", "14");
            caps.setCapability("appium:noReset", true);
            caps.setCapability("appium:noSign", true);

            driver = new AndroidDriver(new URL("http://localhost:4724"), caps);
            wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Initialize explicit wait
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }

    @Test
    void openAndClickSettings() throws InterruptedException, IOException {
        System.out.println("🔵 Press on POWER ON button...");
        driver.pressKey(new KeyEvent(AndroidKey.POWER));
        // Add a delay to allow the device to turn on
        Thread.sleep(5000); // Wait for 5 seconds


        // Wait for the device to turn on (replace with a specific condition)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Wait up to 30 seconds
        WebElement powerOnIndicator = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("com.example.app:id/power_on_indicator"))
        );
        System.out.println("🔵 V5 streamer is ON...");

        System.out.println("🔵 Press on home button...");
        // Simulate Home button press using UiAutomator2
        driver.executeScript("mobile: pressKey", Collections.singletonMap("keycode", 3));

        // Wait for the home screen to load (replace with a specific condition)
        WebElement homeScreenElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("com.example.app:id/home_screen_element"))
        );
        System.out.println("🔵 HOME page is open...");

        // Perform additional actions (e.g., click on settings)
        WebElement settingsButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("com.example.app:id/settings_button"))
        );
        settingsButton.click();
        System.out.println("🔵 Settings page is open...");

        //  WebElement settings = wait.until(ExpectedConditions.elementToBeClickable(settings));
        //  settings.click();
        //  sleep(500);
        // Loop to press DPAD_UP until the target element is found
        boolean elementFound = false;
        while (!elementFound) {
            try {
                // Check if the target element is present
                WebElement mainScreenUpperMenu = driver.findElement(Mainscreenuppermenu);
                if (mainScreenUpperMenu.isDisplayed()) {
                    elementFound = true; // Stop the loop if the element is found
                    System.out.println("Mainscreenuppermenu found! Stopping navigation.");
                }
            } catch (Exception e) {
                // If the element is not found, press DPAD_UP
                driver.pressKey(new KeyEvent(AndroidKey.DPAD_UP));
                System.out.println("DPAD_UP pressed. Searching for Mainscreenuppermenu...");
            }
        }

        // Quit the driver
        driver.quit();
    }

}

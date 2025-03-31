package Tests;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class Simple {

        public AndroidDriver driver;
        public WebDriverWait wait;

        public void initialize(String deviceSerial) {
            try {

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
                System.out.println("=== Wi-Fi Connectivity Test Started ===");

                // Rest of your test code remains the same...

//todo1

                // [Previous test steps remain unchanged]

                System.out.println("\n=== Wi-Fi Connectivity Test Ended ===");


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }
}
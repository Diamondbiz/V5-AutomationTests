package Login;

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

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static java.lang.Thread.sleep;

public class Full_Login {

    private AndroidDriver driver;
    private WebDriverWait wait;

    // Define locators
    private final By hotIconLocator = By.xpath("//android.widget.ImageView[@content-desc='HOT']");
    private final By phoneFieldLocator = By.xpath("//android.widget.TextView[@resource-id=\"txtUserCellPhone\"]");

    @BeforeTest
    void setUp() {
        try {
            // Define Appium capabilities
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", "Android");
            caps.setCapability("appium:automationName", "UiAutomator2");
            caps.setCapability("appium:deviceName", "HOTStreamerV4-b1d6");
            caps.setCapability("appium:udid", "192.168.1.10:5555");
            caps.setCapability("appium:appPackage", "il.net.hot.hot");
            caps.setCapability("appium:appActivity", "il.net.hot.hot.TvMainActivity");
            caps.setCapability("appium:platformVersion", "14");
            caps.setCapability("appium:noReset", true);
            caps.setCapability("appium:noSign", true);

            driver = new AndroidDriver(new URL("http://localhost:4723"), caps);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Initialize explicit wait
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }

    @Test
    void openAndClickSettings() throws InterruptedException {
        System.out.println("🔵 Waiting for HOT app to load...");
        WebElement hotIcon = wait.until(ExpectedConditions.elementToBeClickable(hotIconLocator));
        hotIcon.click();
        System.out.println("✅ HOT app is open.");
        sleep(10000);

        System.out.println("🔵 Entering user cell phone number...");

        // Wait for the phone input field to be visible
        // WebElement phoneField = wait.until(ExpectedConditions.elementToBeClickable(phoneFieldLocator));

        // Clear existing text
        //  phoneFieldLocator.clear();
        //  Thread.sleep(500); // Small delay to ensure clearing works

        // Enter the phone number
        //phoneField.click();

        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_0));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_5));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_2));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_2));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_5));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_2));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_2));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_3));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_0));
        driver.pressKey(new KeyEvent(AndroidKey.DIGIT_6));

        System.out.println("✅ The phone number is field in.");

        driver.pressKey(new KeyEvent(AndroidKey.ENTER));

        System.out.println("✅ User is signed in.");
    }
}
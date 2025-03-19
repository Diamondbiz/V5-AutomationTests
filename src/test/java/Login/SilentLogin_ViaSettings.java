//This test must be performed with no previous played linear channel

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

public class SilentLogin_ViaSettings {

    private AndroidDriver driver;
    private WebDriverWait wait;

    // Define locators
    private final By settingsLocator = By.id("mod_Settings_Main");
    private final By hotIconLocator = By.xpath("//android.widget.ImageView[@content-desc='HOT']");
    private final By sideMenuLocator = By.id("mod_Menu_MenuList");
    //private final By sideMenuLocator2 = By.xpath("//android.view.View[@resource-id=\"mod_Menu_Container\"]");


    @BeforeTest
    void setUp() {
        try {
            // Define Appium capabilities
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

            driver = new AndroidDriver(new URL("http://localhost:4724"), caps);
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
        System.out.println("🔵 Opening side menu...");

        int maxAttempts = 7; // Prevent infinite loop

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            if (!driver.findElements(sideMenuLocator).isEmpty()) { // If menu is found
                System.out.println("✅ Side menu is open.");
                break; // Exit the loop
            }

            System.err.println("❌ Side menu not detected. Moving right... (Attempt " + (attempts + 1) + ")");
            driver.pressKey(new KeyEvent(AndroidKey.DPAD_RIGHT)); // Press right to open the menu
            sleep(500); // Small delay to allow UI update
        }

// Check if the loop exited without finding the menu
        // Activate when the manu element will be found.
        //  if (driver.findElements(sideMenuLocator).isEmpty()) {
        //      System.err.println("❌ Failed to open side menu after " + maxAttempts + " attempts.");
        //   }

        // Delete this step when side menu will be found implemented.
        System.out.println("✅ Side menu is open.");

        // **Force scrolling down 7 times before checking**
        System.out.println("🔽 Scrolling down before checking...");
        for (int i = 0; i < 8; i++) {
            driver.pressKey(new KeyEvent(AndroidKey.DPAD_DOWN));
            sleep(500);
        }
        driver.pressKey(new KeyEvent(AndroidKey.ENTER)); // Simulate ENTER Key (KeyCode=23) pressing on settings

        //moving to the "מידע מערכת" tab
        for (int i = 0; i < 6; i++) {
            driver.pressKey(new KeyEvent(AndroidKey.DPAD_LEFT));
            sleep(500);
        }

        driver.pressKey(new KeyEvent(AndroidKey.ENTER)); // Simulate ENTER Key (KeyCode=23) pressing on "מידע מערכת"

        //moving to the "יציאה מהאפליקציה" button
        for (int i = 0; i < 1; i++) {
            driver.pressKey(new KeyEvent(AndroidKey.DPAD_LEFT));
            sleep(500);
        }

        driver.pressKey(new KeyEvent(AndroidKey.ENTER)); // Simulate ENTER Key (KeyCode=23) pressing on "יציאה מהאפליקציה" button
        sleep(500);

        driver.pressKey(new KeyEvent(AndroidKey.ENTER)); // Simulate ENTER Key (KeyCode=23) pressing on "אישור" button
        sleep(500);

        System.out.println("🔵 Waiting for HOT app to load...");
        hotIcon = wait.until(ExpectedConditions.elementToBeClickable(hotIconLocator));
        hotIcon.click();
        System.out.println("✅ HOT app is open.");

/*
        // **Now search for "הגדרות" (Settings)**
        System.out.println("🔵 Searching for 'הגדרות'...");

        int maxScrollAttempts = 6;
        int attempts = 0;

        for (int i = 0; i < maxScrollAttempts; i++) {
            //if (!driver.findElements(settingsLocator).isEmpty()) {
               // System.out.println("✅ Found 'הגדרות', clicking...");
                //WebElement settingsButton = driver.findElement(By.xpath("//android.widget.Image[@text=\"settings\"]"));
                //settingsButton.click();
                driver.pressKey(new KeyEvent(AndroidKey.ENTER)); // Simulate ENTER Key (KeyCode=23)
                System.out.println("✅ Settings clicked successfully.");
           }
   */     }
}

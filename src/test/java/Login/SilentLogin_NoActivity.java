package Login;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class SilentLogin_NoActivity {

    private AndroidDriver driver;
    private WebDriverWait wait;

    // Define locators
    private final By settingsLocator = By.xpath("//android.widget.TextView[@text='הגדרות']");
    private final By hotIconLocator = By.xpath("//android.widget.ImageView[@content-desc='HOT']");

    @BeforeTest
    void setUp() {
        try {
            // Define Appium capabilities
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", "Android");
            caps.setCapability("appium:automationName", "UiAutomator2");
            caps.setCapability("appium:deviceName", "HOTStreamerV5-b1d6");
            caps.setCapability("appium:udid", "192.168.1.10:5555");
            caps.setCapability("appium:appPackage", "il.net.hot.hot");
            caps.setCapability("appium:appActivity", "il.net.hot.hot.TvMainActivity");
            caps.setCapability("appium:platformVersion", "14");
            caps.setCapability("appium:noReset", true);
            caps.setCapability("appium:noSign", true);

            driver = new AndroidDriver(new URL("http://localhost:4723"), caps);
            wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Initialize explicit wait
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL", e);
        }
    }

    @Test
    void openAndClickSettings() throws InterruptedException {
        System.out.println("🔵 Waiting for HOT icon...");
        WebElement hotIcon = wait.until(ExpectedConditions.elementToBeClickable(hotIconLocator));

        // **Use W3C PointerInput Tap Instead of TouchAction**
        tapElement(hotIcon);

        System.out.println("🔵 Opening side menu...");
        driver.pressKey(new KeyEvent(AndroidKey.DPAD_RIGHT)); // Open side menu
        sleep(500); // Allow UI to update

        // **Ensure side menu is open before proceeding**
        if (driver.findElements(settingsLocator).isEmpty()) {
            System.err.println("❌ Side menu is not open. Retrying...");
            driver.pressKey(new KeyEvent(AndroidKey.DPAD_RIGHT));
            sleep(500);
        }

        // **Print full UI source for debugging**
        System.out.println("📄 UI Dump Before Scrolling:");
        System.out.println(driver.getPageSource());

        // **Pre-scroll down 8 times to ensure visibility**
        System.out.println("🔽 Scrolling down before checking...");
        for (int i = 0; i < 8; i++) {
            driver.pressKey(new KeyEvent(AndroidKey.DPAD_DOWN));
            sleep(500);
        }

        // **Now search for "הגדרות" (Settings)**
        System.out.println("🔵 Searching for 'הגדרות'...");
        int maxScrollAttempts = 10;

        for (int i = 0; i < maxScrollAttempts; i++) {
            if (!driver.findElements(settingsLocator).isEmpty()) {
                System.out.println("✅ Found 'הגדרות', clicking...");
                WebElement settingsButton = wait.until(ExpectedConditions.elementToBeClickable(settingsLocator));

                // **Use W3C Tap Instead of Click**
                tapElement(settingsButton);

                System.out.println("✅ Settings clicked successfully.");
                return;
            }

            System.out.println("🔽 Scrolling down again...");
            driver.pressKey(new KeyEvent(AndroidKey.DPAD_DOWN));
            sleep(700);

            // **Print UI dump every 3 attempts for debugging**
            if (i % 3 == 0) {
                System.out.println("📄 UI Dump After Scroll Attempt " + (i + 1) + ":");
                System.out.println(driver.getPageSource());
            }
        }

        System.err.println("❌ Could not find 'הגדרות' before menu closed.");
    }

    // **🔹 W3C Touch Tap Function (Replaces TouchAction)**
    private void tapElement(WebElement element) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ofMillis(0),
                        PointerInput.Origin.viewport(),
                        element.getLocation().getX(),
                        element.getLocation().getY()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(tap));
    }

    @AfterTest
    void tearDown() {
        if (driver != null) {
            System.out.println("🛑 Quitting driver...");
            driver.quit();
            System.out.println("✅ Driver quit successfully.");
        }
    }
}

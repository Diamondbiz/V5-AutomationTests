package Tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);
    private static final String SCREENSHOTS_PATH = System.getProperty("user.dir") + "/screenshots";

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test Failed: {}", result.getName()); // ERROR level

        // Get the driver instance from the test class
        Object currentClass = result.getInstance();
        if (currentClass instanceof Upgradeforwifi) { //  your test class
            Upgradeforwifi testClass = (Upgradeforwifi) currentClass;

            if (testClass.driver != null) {
                try {
                    // Create a timestamp for the screenshot name
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String screenshotName = "FAILURE_" + result.getName() + "_" + timeStamp + ".png";

                    // Take the screenshot
                    File screenshotFile = ((TakesScreenshot) testClass.driver).getScreenshotAs(OutputType.FILE);

                    // Use Files.move for atomic operation and better error handling:
                    Files.move(screenshotFile.toPath(), new File(SCREENSHOTS_PATH + "/" + screenshotName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("[INFO] Screenshot taken for failed test: " + screenshotName); // Use System.out for immediate console output
                    logger.info("Screenshot taken for failed test: {}", screenshotName); //INFO for the log file
                } catch (IOException e) {
                    logger.error("Failed to take screenshot on test failure", e);
                }
            } else {
                logger.error("Driver is null. Cannot take screenshot.");
            }
        }
    }
    // Other ITestListener methods can be implemented (onTestStart, onTestSuccess, etc.),
    // but are often left empty if not needed.
}
package com.lambdatest;

//import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
//import org.openqa.selenium.devtools.v103.emulation.Emulation;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v96.emulation.Emulation;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DevToolsGeoLocationTestsnew {

    private WebDriver driver;
    private WebDriverWait webDriverWait;
    private Actions actions;

    String username = System.getenv("LT_USERNAME") == null ? "" : System.getenv("LT_USERNAME");
    String accessKey = System.getenv("LT_ACCESS_KEY") == null ? "" : System.getenv("LT_ACCESS_KEY");
    public String gridURL = "@hub.lambdatest.com/wd/hub";



    @BeforeEach
    public void setUp() throws MalformedURLException {
        ChromeOptions browserOptions = new ChromeOptions();
        Map prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values.geolocation", 1);


        browserOptions.setExperimentalOption("prefs", prefs);

        browserOptions.setPlatformName("Windows 10");
        browserOptions.setBrowserVersion("100.0");
        HashMap<String, Object> ltOptions = new HashMap<String, Object>();
       

        ltOptions.put("selenium_version", "4.0.0");
        ltOptions.put("seCdp",true);
        ltOptions.put("w3c", true);
        ltOptions.put("build","Junit + CDP Coordinates test");
        browserOptions.setCapability("LT:Options", ltOptions);

        driver = new RemoteWebDriver(new URL("https://" + username + ":" + accessKey + gridURL), browserOptions);
        driver.manage().window().maximize();
        Augmenter augmenter = new Augmenter();
        driver = augmenter.augment(driver);


    }

    @Test
    public void navigatetourl(){
        driver.navigate().to("https://www.gps-coordinates.net/");

    }

    @ParameterizedTest(name = "{index}. verify location from = {0} to {2}")
    @CsvSource(value = {
            "28.6235812,77.3665402,'Sector 62, Dadri, Gautam Buddha Nagar, Uttar Pradesh, 110092, India'",
            "30.7333,76.7794,'Junction 27, Sector 17, Ward 3, Chandigarh, India'",
            "26.8854479,75.6504722,'Mukandpura, Jaipur Tehsil, Jaipur, Rajasthan, 302013, India'",
            "28.527582,77.0688996,'Kapashera Tehsil, South West Delhi, Delhi, 110061, India'",
            "30.7333,76.7794,'Junction 27, Sector 17, Ward 3, Chandigarh, India'",



    })
    public void verifyDistance(String latitude, String longitude,String Location_address) throws InterruptedException {

        ((RemoteWebDriver) driver).executeScript("lambda-name=verify_location");
        driver.manage().window().maximize();
        DevTools devTools = ((HasDevTools) driver).getDevTools();
        devTools.createSession();
        devTools.send(Emulation.setGeolocationOverride(Optional.of(Float.parseFloat(latitude)),
                Optional.of(Float.parseFloat(longitude)),
                Optional.of(1)));


        driver.navigate().to("https://browserleaks.com/geo");


            Thread.sleep(4000);
        WebElement map = driver.findElement(By.id("reverse"));
        Assertions.assertTrue(map.isDisplayed());

        String location_name = map.getText();
       // System.out.println(location_name);

      //  System.out.println(Location_address);
        Assert.assertEquals(Location_address, location_name);
    }






    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

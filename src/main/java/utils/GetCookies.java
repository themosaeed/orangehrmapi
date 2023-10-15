package utils;
import com.shaft.driver.SHAFT;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;

public class GetCookies {
    SHAFT.GUI.WebDriver driver;
    public static final String SELENIUM_URL=System.getProperty("seleniumUrl");

    //elements
    public static By username(){return By.xpath("//input[@name=\"username\"]");}
    public static By password(){return By.xpath("//input[@name=\"password\"]");}
    public static By loginButton(){return By.xpath("//button[@type=\"submit\"]");}

    public String getCookiesUsingSelenium(){
        driver = new SHAFT.GUI.WebDriver();
        driver
                .browser()
                .navigateToURL(SELENIUM_URL);
        driver
                .element()
                .type(username(),"Admin")
                .type(password(),"admin123")
                .click(loginButton());

        Cookie cookie = driver.browser().getCookie("orangehrm");
        driver.quit();
        return (String) cookie.toString().subSequence(0,42);
    }


}

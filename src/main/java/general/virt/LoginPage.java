package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by rest on 3/7/14.
 */
public class LoginPage extends Page {
    public LoginPage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    public LoginPage openVirtUrl(){
        driver.get(getParameter("url"));
        return new LoginPage(driver);
    }

    public MainPage login(){
        driver.findElement(By.id("login")).click();
        driver.findElement(By.id("username")).sendKeys(getLogin());
        driver.findElement(By.id("userpass")).sendKeys(getPasswd());
        driver.findElement(By.xpath("//i[text()='Войти']")).click();
        driver.get("http://virtonomica.ru/vera/main/company/view/"+getParameter("myId")+"/unit_list");

        return new MainPage(driver);
    }

}

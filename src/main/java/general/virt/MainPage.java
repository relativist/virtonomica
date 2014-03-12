package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by rest on 3/7/14.
 */
public class MainPage extends Page {
    public MainPage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    public MainPage selectWareHouse(){
        driver.findElement(By.xpath("//a[contains(@title,'Склад')]")).click();
        return new MainPage(driver);
    }

    public MainPage selectStore(){
        driver.findElement(By.xpath("//a[contains(@title,'Магазин')]")).click();
        return new MainPage(driver);
    }

    public MainPage selectPlant(){
        driver.findElement(By.xpath("//a[contains(@title,'Завод')]")).click();
        return new MainPage(driver);
    }

    public MainPage selectOffice(){
        driver.findElement(By.xpath("//a[contains(@title,'Офис')]")).click();
        return new MainPage(driver);
    }

    public MainPage selectRestrun(){
        driver.findElement(By.xpath("//a[contains(@title,'Ресторан')]")).click();
        return new MainPage(driver);
    }

    public MainPage selectFerm(){
        driver.findElement(By.xpath("//a[contains(@title,'Склад')]")).click();
        return new MainPage(driver);
    }

    public MainPage selectPlantation(){
        driver.findElement(By.xpath("//a[contains(@title,'Плантация')]")).click();
        return new MainPage(driver);
    }

    public MainPage selectAll(){
        driver.findElement(By.xpath("//a[contains(text(),'Все')]")).click();
        return new MainPage(driver);
    }

    public PlantPage selectPlantByUnitId(String unitID){
        driver.get("http://virtonomica.ru/vera/main/unit/view/"+unitID);
        return new PlantPage(driver);
    }

    public List getListAllUnit(){
        List<WebElement> assetList = new ArrayList();
        List<String> myList = new ArrayList();
        Collections.addAll(assetList, driver.findElements(By.xpath("//table[@class='unit-list']//tr//td[3]/a")).toArray(new WebElement[]{}));

        for(int i=0; i<assetList.size(); i++){
            myList.add(assetList.get(i).getAttribute("href"));
        }
        return myList;
    }

}

package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
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

    public MainPage createNewDivision(String type1,String type2,String type3,String type4,String type5,String type6){
        String mainUrl = driver.getCurrentUrl();
        driver.findElement(By.xpath("//a[contains(text(),'Создать подразделение')]")).click();
        driver.findElement(By.xpath("//tr[td[contains(text(),'"+type1+"')]]/td[1]/input")).click();
        driver.findElement(By.xpath("//input[contains(@value,'Продолжить')]")).click();
        driver.findElement(By.xpath("//td[following-sibling::td[2][label[text()='"+type2+"']]]/input")).click();
        driver.findElement(By.xpath("//input[contains(@value,'Продолжить')]")).click();
        driver.findElement(By.xpath("//tr[td[contains(text(),'"+type3+"')]]/td/input")).click();
        driver.findElement(By.xpath("//input[contains(@value,'Продолжить')]")).click();
        driver.findElement(By.xpath("//tr[td[contains(text(),'"+type4+"')]]/td/input")).click();
        driver.findElement(By.xpath("//input[contains(@value,'Продолжить')]")).click();
        if(!type6.equals("")){
            driver.findElement(By.xpath("//tr[td[contains(text(),'"+type6+"')]]/td/input")).click();
            driver.findElement(By.xpath("//input[contains(@value,'Продолжить')]")).click();
        }
        driver.findElement(By.xpath("//tr[td[contains(text(),'"+type5+"')]]/td/input")).click();
        driver.findElement(By.xpath("//input[contains(@value,'Продолжить')]")).click();
        driver.findElement(By.xpath("//input[contains(@value,'Продолжить')]")).click();
        driver.findElement(By.xpath("//input[contains(@value,'Создать подразделение')]")).click();
        driver.get(mainUrl);
        return new MainPage(driver);
    }

    //Сначала ставим товар потом перебираем по городам.
    public MainPage getAnalyzeMarket(String productName, String typeProduct,int session) throws InterruptedException {

        logMe("-----------------------------------------------");
        logMe(productName);
        logMe("-----------------------------------------------");

        String [] countries = {
//                "Азербайджан",
//                "Армения",
                "Болгария",
                "Великобритания",
                "Венесуэла",
//                "Германия",
                "Греция",
//                "Казахстан",
                "Куба",
                "Латвия",
                "Литва",
                "Нидерланды",
                "Норвегия",
//                "Республика Беларусь",
                "Россия",
//                "Узбекистан",
//                "Украина",
                "Финляндия",
                "Франция",
                "Эстония"
        };

        String mainUrl = driver.getCurrentUrl();
        driver.get("http://virtonomica.ru/vera/main/globalreport/marketing/by_trade_at_cities");

        Select s1 = new Select(driver.findElement(By.id("__product_category_list")));
        s1.selectByVisibleText(typeProduct);

        waitForElement("//img[@alt='"+productName+"']");
        waitForElementVisible("//img[@alt='" + productName + "']");

        driver.findElement(By.xpath("//img[@alt='"+productName+"']")).click();
        int counter = 0;


        // COUNTRY
        Select c1 = new Select(driver.findElements(By.xpath("//*[@id='mainContent']/fieldset/table[2]//select")).get(0));
        for(int i=0; i<countries.length; i++){
            c1.selectByVisibleText(countries[i]);
            // REGION
            Select c2 = new Select(driver.findElements(By.xpath("//*[@id='mainContent']/fieldset/table[2]//select")).get(1));
            for(int j=1; j<c2.getOptions().size(); j++){
                c2.selectByIndex(j);
                Select c3 = new Select(driver.findElements(By.xpath("//*[@id='mainContent']/fieldset/table[2]//select")).get(2));
                //CITY
                for(int k=1; k<c3.getOptions().size();k++){
                    c3.selectByIndex(k);
                    getMarketData(session,productName);
                    logMe("Completed: " + counter);
                    counter++;
                    c3 = new Select(driver.findElements(By.xpath("//*[@id='mainContent']/fieldset/table[2]//select")).get(2));
                }
                c2 = new Select(driver.findElements(By.xpath("//*[@id='mainContent']/fieldset/table[2]//select")).get(1));
            }
            c1 = new Select(driver.findElements(By.xpath("//*[@id='mainContent']/fieldset/table[2]//select")).get(0));
        }

        driver.get(mainUrl);
        return new MainPage(driver);
    }

    protected void getMarketData(int session,String productName){
        //местные поставцищики
        //tr[td[text()='Местные поставщики']]/td[5]
        String localSales = "";
        if(driver.findElements(By.xpath("//tr[td[text()='Местные поставщики']]/td[5]")).size()>0)
            localSales = driver.findElement(By.xpath("//tr[td[text()='Местные поставщики']]/td[5]")).getText().split(" ")[0];
        else localSales = "0";

        //развитие рынка
        //tr[td[text()='Индекс развития рынка:']]/td[3]/b
        //String marketIndex = driver.findElement(By.xpath("//tr[td[text()='Индекс развития рынка:']]/td[3]/b")).getText().trim();

        //Объем рынка:
        //tr[td[text()='Объем рынка:']]/td[5]/b
        String marketVolume = driver.findElement(By.xpath("//tr[td[text()='Объем рынка:']]/td[5]/b")).getText().replaceAll(" ","").replaceAll("\\D","");

        //Количество продавцов:
        //tr[td[text()='Количество продавцов:']]/td[7]
        String numSales = driver.findElement(By.xpath("//tr[td[text()='Количество продавцов:']]/td[7]")).getText().trim();

        //tr[th[text()='Цена']]/td[2]
        String price = driver.findElement(By.xpath("//tr[th[text()='Цена']]/td[2]")).getText().replace("$","").replaceAll(" ", "");
        //tr[th[text()='Качество']]/td[2]
        String quality = driver.findElement(By.xpath("//tr[th[text()='Качество']]/td[2]")).getText();
        //tr[th[text()='Бренд']]/td[2]
        String brend = driver.findElement(By.xpath("//tr[th[text()='Бренд']]/td[2]")).getText();

        Select c1 = new Select(driver.findElements(By.xpath("//*[@id='mainContent']/fieldset/table[2]//select")).get(0));
        String country = c1.getFirstSelectedOption().getText();
        logMe(country);

        Select c2 = new Select(driver.findElements(By.xpath("//*[@id='mainContent']/fieldset/table[2]//select")).get(1));
        String region = c2.getFirstSelectedOption().getText();
        logMe(region);
        Select c3 = new Select(driver.findElements(By.xpath("//*[@id='mainContent']/fieldset/table[2]//select")).get(2));
        String city = c3.getFirstSelectedOption().getText();
        logMe(city);



        logMe(productName);
        logMe(localSales);
        //logMe(marketIndex);
        logMe(marketVolume);
        logMe(numSales);
        logMe(price);
        logMe(quality);
        logMe(brend);
        logMe(" ");

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:market.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO MARKET (SESSION,COUNTRY,REGION,CITY,LOCALSALES,VOLUME,NUMSALES,PRICE,QA,BRAND) " +
                    "VALUES (" +
                    session +
                    ",'"+productName +"'"+
                    ",'"+country +"'"+
                    ",'"+region +"'"+
                    ",'"+city +"'"+
                    ","+localSales +
                    ","+marketVolume +
                    ","+numSales +
                    ","+price +
                    ","+quality +
                    ","+brend +
                    ");";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");




    }

}

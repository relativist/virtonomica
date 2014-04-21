package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by rest on 3/7/14.
 */
public class StorePage extends Page {
    public StorePage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    public ArrayList<String> getCurrentTypesDepFromSalesRoom(){
        ArrayList<String> departments = new ArrayList<String>();
        for(int i=0; i< driver.findElements(By.xpath("//tr/td[@class='title']")).size(); i++){
            departments.add(driver.findElements(By.xpath("//tr/td[@class='title']")).get(i).getText().split(" Обзор")[0]);
        }
        return departments;
    }


    public StorePage autoBuyProducts(){
        ArrayList<String> mySellProducts = getMyProductsToSell();
        driver.findElement(By.xpath("//a[text()='Торговый зал']")).click();


        return new StorePage(driver);
    }



    public StorePage educate(){
        if(isStuding() && isNeedtoEducate()){
            logMe("Обучаю персонал");
            String currentUrl = driver.getCurrentUrl();
            String UnitId = getUnitIdByUrl(currentUrl);
            driver.get("http://virtonomica.ru/vera/window/unit/employees/education/"+UnitId);
            driver.findElement(By.xpath("//input[@value='Обучить']")).click();
            driver.get(currentUrl);
        }
        return new StorePage(driver);
    }



    public StorePage finans(){
        driver.findElement(By.xpath("//a[text()='Финансовый отчёт']")).click();

        String profit ="";
        if(driver.findElements(By.xpath("/tr[td[text()='Прибыль']]/td[2]/span")).size()>0)
            profit = driver.findElement(By.xpath("//tr[td[text()='Прибыль']]/td[2]/span")).getText().replaceAll(" ", "").replaceAll("\\$", "");
        else
            profit = driver.findElement(By.xpath("//tr[td[text()='Прибыль']]/td[2]")).getText().replaceAll(" ", "").replaceAll("\\$", "");
        String result = "";
        if(Double.valueOf(profit)>0)
            result="GOOD";
        else {result = "BAD";
            if(Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Прибыль']]/td[3]/span")).getText().replaceAll(" ","").replaceAll("\\$", ""))<0
                    && Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Прибыль']]/td[4]/span")).getText().replaceAll(" ","").replaceAll("\\$", ""))<0
                    && Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Прибыль']]/td[5]/span")).getText().replaceAll(" ","").replaceAll("\\$", ""))<0
                    )
                result="VERY BAD";
        } 
        logMe(result+" profit: "+profit);
        driver.findElement(By.xpath("//a[text()='Магазин']")).click();
        return new StorePage(driver);
    }


    public StorePage advertising(Double fame,Double spentMoney){
        driver.findElement(By.xpath("//a[text()='Маркетинг и Реклама']")).click();
        String localFame = driver.findElement(By.xpath("//tr[td[text()='Известность']]/td[2]")).getText();
        //if(Double.valueOf(localFame)<fame) Условие надо продумать!!!

        driver.findElement(By.xpath("//a[text()='Магазин']")).click();
        return new StorePage(driver);
    }

    private void deselectAllAdvetising(){

        driver.findElement(By.xpath("//tr[td/label[text()='Интернет']]/td/input")).click();
        driver.findElement(By.xpath("//tr[td/label[text()='Печатные издания']]/td/input")).click();
        driver.findElement(By.xpath("//tr[td/label[text()='Наружная реклама']]/td/input")).click();
        driver.findElement(By.xpath("//tr[td/label[text()='Радио']]/td/input")).click();
        driver.findElement(By.xpath("//tr[td/label[text()='Телевидение']]/td/input")).click();

    }

//    1. store==0
//        delete from sales and supply
//    2. price == 0
//        price = basicCost*1.30
//        sell
//    3. store/saled>2.5
//        1. price<= basicCost
//             offer=0 && delete product from offers.
//             price=basic
//        else
//             offer=0
//             price=price*0.9
//    else:
//         price<= basicCost
//             offer=0 && delete product from offers.
//             price=basic
//         else
//             offer=saled*1.10
//             price=price*1.10
//
//    4. market>20
//        price=price*1.10


    public StorePage trading() throws InterruptedException {

        if(isDepProcessed(driver.getCurrentUrl())){
            logMe("Already processed");
            return new StorePage(driver);
        }


        driver.findElement(By.xpath("//a[text()='Торговый зал']")).click();
        String store = new String();
        String price = new String();
        String saled = new String();
        String basicCost = new String();
        String market = new String();
        String offer = new String();
        String avgPrice = new String();
        String retailerStore = new String();
        String result = new String();
        String productName = new String();
        boolean action=false;

//      1. store==0
//           delete from sales and supply
        for(int i=0; i<driver.findElements(By.xpath("//tr[@class='odd' or @class='even']")).size();i++){
            store = driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[6]")).get(i).getText().replaceAll(" ","");
            if(Double.valueOf(store)==0){
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[2]/input")).get(i).click();
                action=true;
            }
        }

        //Если ничего не продается, то нафиг такой магазин нужен??!? - Удаляем
        if(driver.findElements(By.xpath("//input[@value='Ликвидировать остатки товара']")).size()>0)
            driver.findElement(By.xpath("//input[@value='Ликвидировать остатки товара']")).click();
        else{
            driver.findElement(By.xpath("//a[text()='Магазин']")).click();
            String currentUrl = driver.getCurrentUrl();
            String UnitId = getUnitIdByUrl(currentUrl);
            driver.get("http://virtonomica.ru/vera/window/unit/close/"+UnitId);
            driver.findElement(By.xpath("//input[@value='Закрыть предприятие']")).click();
            driver.switchTo().alert().accept();
            driver.findElement(By.xpath("//a[text()='Магазин']")).click();
            return new StorePage(driver);
        }

        if(action)
        driver.switchTo().alert().accept();

        // ВТОРОЙ ЗАХОД. ибо с первого не проходит!
        action=false;
        for(int i=0; i<driver.findElements(By.xpath("//tr[@class='odd' or @class='even']")).size();i++){
            store = driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[6]")).get(i).getText().replaceAll(" ","");
            if(Double.valueOf(store)==0){
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[2]/input")).get(i).click();
                action=true;
            }
        }
        if(driver.findElements(By.xpath("//input[@value='Ликвидировать остатки товара']")).size()>0)
            driver.findElement(By.xpath("//input[@value='Ликвидировать остатки товара']")).click();
        if(action)
        driver.switchTo().alert().accept();

//    2. price == 0
//    price = avgPrice*1.30
//
//    3. store/saled>2.5
//        1. price<= basicCost
//             offer=0 && delete product from offers.
//             price=basic
//        else
//             offer=0
//             price=price*0.9
//    else:
//         price<= basicCost
//             offer=0 && delete product from offers.
//             price=basic
//         else
//             offer=saled*1.10
//             price=price*1.10

        for(int i=0; i<driver.findElements(By.xpath("//tr[@class='odd' or @class='even']")).size();i++){
            result="";
            store = driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[6]")).get(i).getText().replaceAll(" ","");
            saled = driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[4]")).get(i).getText().replaceAll(" ","");
            basicCost = driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[9]")).get(i).getText().replaceAll(" ", "").replaceAll("\\$", "");
            avgPrice = driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[12]")).get(i).getText().replaceAll(" ", "").replaceAll("\\$", "");
            price = driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).getAttribute("value");
            market =driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[11]")).get(i).getText().split(" ")[0];
            productName =driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[3]")).get(i).getAttribute("title").split(" \\(кликните")[0];

            //2
            if(price.equals("0.00")){
                price = String.valueOf(Double.valueOf(avgPrice)*1.30);
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
            }

            //3
            if(Double.valueOf(store)/Double.valueOf(saled)>2.5){
                if(Double.valueOf(price)<Double.valueOf(basicCost)){
                    price = basicCost;
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                    result+="Продажа ниже себестоимости; ";
                }
                else {
                    price = String.valueOf(Double.valueOf(price)*0.9);
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                }
            }
            else{
                if(Double.valueOf(price)<Double.valueOf(basicCost)){
                    price = basicCost;
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                    result+="Продажа ниже себестоимости; ";
                }
                else {
                    price = String.valueOf(Double.valueOf(price)*1.1);
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                }
            }
//          4. market>20
//               price=price*1.10
            if(Double.valueOf(market)>20){
                price = String.valueOf(Double.valueOf(price)*1.1);
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                result+="Рынок забит; ";
            }

            recordDepartment(productName,result);
        }
        if(driver.findElements(By.xpath("//input[@value='Установить цены']")).size()!=0)
            driver.findElement(By.xpath("//input[@value='Установить цены']")).click();


//    1. store==0
//        delete from sales and supply
//    2. price == 0
//        price = basicCost*1.30
//        sell
//    3. store/saled>2.5
//        1. price<= basicCost
//             offer=offer*0.8.
//             price=basic
//        else
//             offer=offer*0.8.
//             price=price*0.9
//    else:
//         price<= basicCost
//             offer=offer*0.8.
//             price=basic
//         else
//             offer=saled*1.10
//             price=price*1.10
//
//    4. market>20
//        price=price*1.10



        driver.findElement(By.xpath("//a[text()='Снабжение']")).click();

//      delete second offer!
        if(driver.findElements(By.xpath("//tr[contains(@id,'product_sub_row')]/td[7]/input")).size()>0){
            waitForElement("//tr[contains(@id,'product_sub_row')]/td[7]/input");
            waitForElementVisible("//tr[contains(@id,'product_sub_row')]/td[7]/input");
            for(int i=0; i<driver.findElements(By.xpath("//tr[contains(@id,'product_sub_row')]/td[7]/input")).size();i++){
                driver.findElements(By.xpath("//tr[contains(@id,'product_sub_row')]/td[7]/input")).get(i).click();
            }
            driver.findElement(By.xpath("//input[@value='Разорвать выбранные контракты']")).click();
            driver.switchTo().alert().accept();
        }



//      1. store==0
//      удаляем дублируемые саплаерные заказы
//      delete from sales and supply
        //Thread.sleep(15000);
        int rows = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]")).size();
        waitForElement("//tr[contains(@id,'product_row')]/td[10]/input["+rows+"]");
        for(int i=0; i< rows;i++){
            store = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[1]//tr[1]/td[2]")).get(i).getText().replaceAll(" ","");
            if(Double.valueOf(store)==0)
                Thread.sleep(100);
                try{
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[10]/input")).get(i).click();
                }   catch (WebDriverException e){
                    System.out.println(e.getMessage());
                    //System.out.println(driver.getPageSource());
                }
        }
        if(driver.findElements(By.xpath("//input[@value='Разорвать выбранные контракты']")).size()>0){
            driver.findElement(By.xpath("//input[@value='Разорвать выбранные контракты']")).click();
            driver.switchTo().alert().accept();
        }


        //3
        for(int i=0; i<driver.findElements(By.xpath("//tr[contains(@id,'product_row')]")).size();i++){
            result="";
            saled = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[1]//tr[5]/td[2]")).get(i).getText().replaceAll(" ","");
            basicCost = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[8]//tr[1]/td[2]")).get(i).getText().replaceAll(" ", "").replaceAll("\\$", "");
            store = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[1]//tr[1]/td[2]")).get(i).getText().replaceAll(" ","");
            retailerStore = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[9]//tr[3]/td[2]")).get(i).getText().replaceAll(" ","");
            offer = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).getAttribute("value");
            productName=driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/th//td/img")).get(i).getAttribute("alt");


            //3
            if(Double.valueOf(store)/Double.valueOf(saled)>2.5){
                if(Double.valueOf(store)/Double.valueOf(saled)>10){
                    offer = "0";
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).sendKeys(offer);
                    result+="Overcroud "+productName+" "+store+"; ";
                }
                else {
                    offer = String.valueOf(Double.valueOf(offer)*0.8);
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).sendKeys(offer);
                    result+="SuperOvercroud "+productName+" "+store+"; ";
                }

            }
            else{
                offer = String.valueOf(Double.valueOf(saled)*1.1);
                driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).sendKeys(offer);
            }

            if(Double.valueOf(retailerStore)/Double.valueOf(saled)<3){
                logMe("Warning! "+productName+" МАЛО!");
                result+="Мало "+productName+" "+retailerStore+"; ";
            }
            recordDepartment(productName, result);
        }

        if(driver.findElements(By.xpath("//input[@value='Изменить']")).size()>0){
            driver.findElement(By.xpath("//input[@value='Изменить']")).click();
        }






        //Записываем в базу о прохождении продразделения.
        //recordDepartment("product","ok");
        driver.findElement(By.xpath("//a[text()='Магазин']")).click();
        return new StorePage(driver);
    }

    protected void recordDepartment(String product,String result){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:store.db");
            c.setAutoCommit(false);
            //System.out.println("Opened database successfully");

            int session = Integer.valueOf(formattedDate("MMdd"));
            String depName =  driver.findElement(By.xpath("//div[@id='headerInfo']/h1")).getText();
            String depUrl = driver.getCurrentUrl();


            stmt = c.createStatement();
            String sql = "INSERT INTO MARKET (SESSION,DEPNAME,DEPURL,RESULT,PRODUCT) " +
                    "VALUES (" +
                    session +
                    ",'"+depName +"'"+
                    ",'"+depUrl +"'"+
                    ",'"+result +"'"+
                    ",'"+product +"'"+
                    ");";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        //System.out.println("Records created successfully");
    } 
    public boolean isDepProcessed(String dep){
        Connection c = null;
        Statement stmt = null;
        boolean result=false;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:store.db");
            c.setAutoCommit(false);
            //System.out.println("Opened database successfully");

            int session = Integer.valueOf(formattedDate("MMdd"));
            //String depName =  driver.findElement(By.xpath("//div[@id='headerInfo']/h1")).getText();
            String depUrl = driver.getCurrentUrl();


            stmt = c.createStatement();
            String sql = "select count(*) from market where session="+session+" and depurl like '"+dep+"%';";
            ResultSet rs =  stmt.executeQuery(sql);

            while ( rs.next() ) {
                int id = rs.getInt("count(*)");
                if(id>0)
                    result=true;
                else
                    result=false;
            }

            rs.close();
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        //logMe("result is "+result);
        return result;
    }

    public StorePage setAutoQaSlave() throws InterruptedException {
        new SalaryPage(driver).autoSetSalaryAndQaFormula();
        //new SalaryPage(driver).autoSetSalaryAndQa();
        return new StorePage(driver);
    }



    private boolean isNeedtoEducate(){
        String salarySlave = driver.findElement(By.xpath("//tr[td[text()='Зарплата одного сотрудника']]/td[2]")).getText().replaceAll(" ","").split("\\$")[0].replaceAll(" ","");
        String salaryTown  = driver.findElement(By.xpath("//tr[td[text()='Зарплата одного сотрудника']]/td[2]")).getText().split("городу ")[1].replaceAll(" ","").replaceAll("\\$\\)", "").replaceAll(" ","");
        //logMe(salarySlave);
        //logMe(salaryTown);
        if (Double.valueOf(salarySlave) > Double.valueOf(salaryTown)*0.3)
            return true;
        else return false;
    }

    private boolean isStuding(){
        if(driver.findElements(By.xpath("//a[text()='Обучение персонала']")).size()>0)
            return true;
        else return false;
    }

    public boolean isSlaveOnVacation(){
        if(driver.findElements(By.xpath("//a[text()='Возвращение персонала из отпусков']")).size()>0)
            return true;
        else return false;
    }

    public StorePage getInfo(){
        logMe("INFO:");
        String qtyEq = driver.findElement(By.xpath("//tr[td[text()='Количество оборудования']]/td[2]")).getText().split(" ед. ")[0].replaceAll(" ","");
        String qaEq = driver.findElement(By.xpath("//tr[td[text()='Качество оборудования']]/td[2]")).getText().split(" ")[0];
        String wearEq = driver.findElement(By.xpath("//tr[td[text()='Износ оборудования']]/td[2]//td[2]")).getText().split(" ")[0];
        String qaEqneed = driver.findElement(By.xpath("//tr[td[text()='Качество оборудования']]/td[2]")).getText().split("технологии ")[1].replaceAll("\\)","");

        String qtySlave = driver.findElement(By.xpath("//tr[td[text()='Количество рабочих']]/td[2]")).getText().replaceAll(" ","").split("\\(")[0];
        String qaSlave = driver.findElement(By.xpath("//tr[td[text()='Уровень квалификации сотрудников']]/td[2]")).getText().split(" ")[0];
        String qaSlaveneed = driver.findElement(By.xpath("//tr[td[text()='Уровень квалификации сотрудников']]/td[2]")).getText().split("технологии ")[1].replaceAll("\\)","");

        String technologyLevel = driver.findElement(By.xpath("//tr[td[text()='Уровень технологии']]/td[2]")).getText();
        String playerSkill = driver.findElement(By.xpath("//tr[td[text()='Квалификация игрока']]/td[2]")).getText().replaceAll("\\D","");
        String totalSlaveGlobal = driver.findElement(By.xpath("//tr[td[contains(text(),'Суммарное количество подчинённых')]]/td[2]")).getText().replaceAll(" ","");


        logMe("максимальное качество оборудования " + String.valueOf(calcEqQualMax(Double.valueOf(qaSlave))));
        logMe("Максимальная технология "+String.valueOf(calcTechMax(Double.valueOf(playerSkill))));
        logMe("Максимальное количество рабов на заводе "+String.valueOf(calcPersonalTop1(Double.valueOf(playerSkill), Double.valueOf(qaSlave)))); 
        logMe("Максимальная обученность рабов "+String.valueOf(calcQualTop1(Double.valueOf(playerSkill),Double.valueOf(qtySlave))));
        logMe("Максимальная количество рабов вообще "+String.valueOf(calcPersonalTop3(Double.valueOf(playerSkill))));

        return new StorePage(driver);
    }

}

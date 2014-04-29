package general.virt;

import general.Page;
import org.openqa.selenium.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

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
            //logMe("Dep local: "+driver.findElements(By.xpath("//tr/td[@class='title']")).get(i).getText().split(" Обзор")[0]);
        }
        return departments;
    }

    // продается ли этот продукт на странице торговый зал ?
    public boolean isThisProductSellOnSellPage(String productTitle) throws InterruptedException {
        Thread.sleep(1000);
        for(WebElement i:driver.findElements(By.xpath("//tr[@class=' product_row']/th//tr/td[1]/img"))){
            if(productTitle.equals(i.getAttribute("alt")))
                return true;
        }

        return false;
    }

    //есть ли этот отдел в странице торгового зала?
    public boolean isDepToSell(String depTitle, ArrayList<String> departments){
        for(String dep: departments){
            if(dep.equals(depTitle))
                return true;
        }
        return false;
    }

    //возвращает строку продукта для продажи с его данными для фильра
    public String getProductDataFromCompanyConfig(String depTitle, ArrayList<String> departments){
        for(String dep: departments){
            if(dep.split(";")[0].equals(depTitle))
                return dep;
        }
        logMe("Unable to find: "+depTitle);
        assertTrue(false);
        return "";
    }

    //возвращает максимальнодопустимое количество отделов в магазине. ( по торговой площади, из конфига )
    public int getStoreDepMaxSize(){
        return Integer.valueOf(getParameter("StoreSize"
                + driver.findElement(By.xpath("//tr[td[text()='Торговая площадь']]/td[2]")).getText().replaceAll(" ", "").split("м")[0]));
    }

    //возвращает максимальнодопустимое количество отделов в магазине. ( по торговой площади, из конфига )
    public int getStoreDepSize(){
        return Integer.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество отделов']]/td[2]")).getText());
    }

    //автоматическая закупка в магазине при заданном отделе
    public StorePage autoBuyWithDep(String department) throws InterruptedException {
        int maxDepSize = getStoreDepMaxSize();
        ArrayList<String> companyDepSellProducts = getMyProductsDepToSell(); // продукция компании по отделам [0] - название отдела
        driver.findElement(By.xpath("//a[text()='Торговый зал']")).click();
        ArrayList <String> currentTypesDep = getCurrentTypesDepFromSalesRoom();
        ArrayList <String> companyProductsToSell = getMyProductsToSell(); // продукция компании с кретерием закупок.[0] - название продукта
        int depCount = Integer.valueOf(currentTypesDep.size());
        driver.findElement(By.xpath("//a[text()='Снабжение']")).click();


        //идем в снабжение и по выясненым отделам закупаем продукты. (смотрим на кретерий в конфиге)
        //если встречаются в снабжении продукты пропускаем идем дальше
        //если размер
        String productInfo=new String();
        org.openqa.selenium.support.ui.Select s = null;
        for(String companyProduct: companyDepSellProducts){
            if(department.equals(companyProduct.split(";")[0])){
            //if(isDepToSell(companyProduct.split(";")[0],currentTypesDep)) {
                logMe("покупаем продукцию: " + companyProduct.split(";")[0]);
                for(int i=1; i<companyProduct.split(";").length;i++){


                    productInfo = getProductDataFromCompanyConfig(companyProduct.split(";")[i],companyProductsToSell);


                    //этого продукта не должно быть на странице снабжения.
                    if(isThisProductSellOnSellPage(productInfo.split(";")[0]))
                        continue;

                    logMe(productInfo);
                    s = new org.openqa.selenium.support.ui.Select(driver.findElement(By.name("productCategory")));
                    s.selectByVisibleText(companyProduct.split(";")[0]);
                    Thread.sleep(500);
                    driver.findElement(By.xpath("//span[label/img[@alt='"+productInfo.split(";")[0]+"']]/input")).click();


                    String handle1 = driver.getWindowHandle();
                    driver.findElement(By.xpath("//input[@value='Добавить поставщика']")).click();
                    Set<String> handles=driver.getWindowHandles();
                    Iterator<String> it =handles.iterator();
                    while (it.hasNext()) {
                        String popupHandle = it.next().toString();
                        if (!popupHandle.contains(handle1)) {
                            driver.switchTo().window(popupHandle);
                            //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                        }
                    }
                    //#myPro#=title;volume;localsales;qa;brand;price;base_value_to_buy
                    //параметры: бренд прайс качество
                    Double price=Double.valueOf(driver.findElement(By.xpath("//table[@class='right_corner']//tr[2]/td[1]")).getText().split(":")[1].replaceAll(" ","").replaceAll("\\$",""));
                    Double qa=Double.valueOf(driver.findElement(By.xpath("//table[@class='right_corner']//tr[2]/td[2]")).getText().split(" ")[1].replaceAll(" ","").replaceAll("\\$",""));
                    Double brand=Double.valueOf(driver.findElement(By.xpath("//table[@class='right_corner']//tr[2]/td[3]")).getText().split(" ")[1].replaceAll(" ","").replaceAll("\\$",""));
                    Double confPrice = Double.valueOf(productInfo.split(";")[5]);
                    Double confQa = Double.valueOf(productInfo.split(";")[3]);
                    Double confBrand = Double.valueOf(productInfo.split(";")[4]);
                    String valueToSet = productInfo.split(";")[6];

                    logMe(""+price+">="+confPrice);
                    logMe(""+qa+"<"+confQa);
                    logMe(""+brand+"<"+confBrand);

                    logMe(valueToSet);

                    //если наши параметры подходят для покупки в этом городе - покупаем
                    if(price>=confPrice && confBrand>brand && confQa>qa) {
                        logMe("Ура, продукт подошел!");

                        driver.findElement(By.xpath("//a[text()='Свои']")).click();
                        if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() != 0)
                            if (driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).isDisplayed())
                                driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();

                        //если нету своих - то ничего не покупаем
                        if (driver.findElements(By.xpath("//tr[td[text()='Cвободно']]/td[2]/a[2]/img")).size() != 0) {

                            //сортировка
                            Thread.sleep(500);
                            driver.findElement(By.xpath("//tr[td[text()='Cвободно']]/td[2]/a[2]/img")).click();

                            String goodId = driver.findElement(By.xpath("//table//tr[@class='friendlyHighLight']/td[@class='choose']/span")).getAttribute("id");
                            ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).sendKeys(valueToSet);
                            ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");

                            driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                            driver.switchTo().window(handle1);
                            logMe("Закупились " + productInfo.split(";")[0]);
                        }
                        else {
                            logMe("Продукта нет!");
                            driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                            driver.switchTo().window(handle1);
                        }
                    }
                    else {
                        logMe("Продукт не подошел!");
                        driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                        driver.switchTo().window(handle1);
                    }
                }
            }
        }


        // Идем в зал продаж и ставим цены. на 30% дороже чем средняя цена по городу
        boolean action=false;
        Thread.sleep(3000);
        waitForElementVisible("//a[text()='Торговый зал']");
        waitForElement("//a[text()='Торговый зал']");
        driver.findElement(By.xpath("//a[text()='Торговый зал']")).click();
        for(int i=0; i<driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).size();i++){
            String salePrice=driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).getAttribute("value");
            if(salePrice.equals("0.00")){
                Double setupvalue = Double.valueOf(driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[12]")).get(i).getText().replaceAll(" ","").replaceAll("\\$",""));
                setupvalue=setupvalue*1.3;
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(String.valueOf(setupvalue));
                action=true;
            }
        }
        if(action) {
//            waitForElement("//input[@value='Установить цены']");
//            waitForElementVisible("//input[@value='Установить цены']");
//            driver.findElement(By.xpath("//input[@value='Установить цены']")).click();
            driver.findElement(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).sendKeys(Keys.RETURN);
        }

        driver.findElement(By.xpath("//a[text()='Магазин']")).click();






        return new StorePage(driver);
    }

    //автоматическая закупка в магазинах
    public StorePage autoBuyProducts() throws InterruptedException {
        int maxDepSize = Integer.valueOf(getParameter("StoreSize"
                + driver.findElement(By.xpath("//tr[td[text()='Торговая площадь']]/td[2]")).getText().replaceAll(" ", "").split("м")[0]));
        ArrayList<String> companyDepSellProducts = getMyProductsDepToSell();
        driver.findElement(By.xpath("//a[text()='Торговый зал']")).click();
        ArrayList <String> currentTypesDep = getCurrentTypesDepFromSalesRoom();
        ArrayList <String> companyProductsToSell = getMyProductsToSell();
        int depCount = Integer.valueOf(currentTypesDep.size());
        driver.findElement(By.xpath("//a[text()='Снабжение']")).click();


        //идем в снабжение и по выясненым отделам закупаем продукты. (смотрим на кретерий в конфиге)
        //если встречаются в снабжении продукты пропускаем идем дальше
        //если размер
        String productInfo=new String();
        org.openqa.selenium.support.ui.Select s = null;
        for(String companyProduct: companyDepSellProducts){
            if(isDepToSell(companyProduct.split(";")[0],currentTypesDep)) {
                logMe("покупаем продукцию: " + companyProduct.split(";")[0]);
                for(int i=1; i<companyProduct.split(";").length;i++){


                    productInfo = getProductDataFromCompanyConfig(companyProduct.split(";")[i],companyProductsToSell);


                    //этого продукта не должно быть на странице снабжения.
                    if(isThisProductSellOnSellPage(productInfo.split(";")[0]))
                        continue;

                    logMe(productInfo);
                    s = new org.openqa.selenium.support.ui.Select(driver.findElement(By.name("productCategory")));
                    s.selectByVisibleText(companyProduct.split(";")[0]);
                    Thread.sleep(500);
                    driver.findElement(By.xpath("//span[label/img[@alt='"+productInfo.split(";")[0]+"']]/input")).click();


                    String handle1 = driver.getWindowHandle();
                    driver.findElement(By.xpath("//input[@value='Добавить поставщика']")).click();
                    Set<String> handles=driver.getWindowHandles();
                    Iterator<String> it =handles.iterator();
                    while (it.hasNext()) {
                        String popupHandle = it.next().toString();
                        if (!popupHandle.contains(handle1)) {
                            driver.switchTo().window(popupHandle);
                            //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                        }
                    }
                    //#myPro#=title;volume;localsales;qa;brand;price;base_value_to_buy
                    //параметры: бренд прайс качество
                    Double price=Double.valueOf(driver.findElement(By.xpath("//table[@class='right_corner']//tr[2]/td[1]")).getText().split(":")[1].replaceAll(" ","").replaceAll("\\$",""));
                    Double qa=Double.valueOf(driver.findElement(By.xpath("//table[@class='right_corner']//tr[2]/td[2]")).getText().split(" ")[1].replaceAll(" ","").replaceAll("\\$",""));
                    Double brand=Double.valueOf(driver.findElement(By.xpath("//table[@class='right_corner']//tr[2]/td[3]")).getText().split(" ")[1].replaceAll(" ","").replaceAll("\\$",""));
                    Double confPrice = Double.valueOf(productInfo.split(";")[5]);
                    Double confQa = Double.valueOf(productInfo.split(";")[3]);
                    Double confBrand = Double.valueOf(productInfo.split(";")[4]);
                    String valueToSet = productInfo.split(";")[6];

                    logMe(""+price+">="+confPrice);
                    logMe(""+qa+"<"+confQa);
                    logMe(""+brand+"<"+confBrand);

                    logMe(valueToSet);

                    //если наши параметры подходят для покупки в этом городе - покупаем
                    if(price>=confPrice && confBrand>brand && confQa>qa) {
                        logMe("Ура, продукт подошел!");

                        driver.findElement(By.xpath("//a[text()='Свои']")).click();
                        if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() != 0)
                            if (driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).isDisplayed())
                                driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();

                        //если нету своих - то ничего не покупаем
                        if (driver.findElements(By.xpath("//tr[td[text()='Cвободно']]/td[2]/a[2]/img")).size() != 0) {

                            //сортировка
                            Thread.sleep(500);
                            driver.findElement(By.xpath("//tr[td[text()='Cвободно']]/td[2]/a[2]/img")).click();

                            String goodId = driver.findElement(By.xpath("//table//tr[@class='friendlyHighLight']/td[@class='choose']/span")).getAttribute("id");
                            ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).sendKeys(valueToSet);
                            ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");

                            driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                            driver.switchTo().window(handle1);
                            logMe("Закупились " + productInfo.split(";")[0]);
                        }
                        else {
                            logMe("Продукта нет!");
                            driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                            driver.switchTo().window(handle1);
                        }
                    }
                    else {
                        logMe("Продукт не подошел!");
                        driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                        driver.switchTo().window(handle1);
                    }
                }
            }
        }

        // второй заход, допокупаем отделы продуктов если позволяет магазин
        for(String companyProduct: companyDepSellProducts){
            //если размер магазина больше или равен дозволенного размера магазинов - прекращаем закупку!
            if(depCount>=maxDepSize)
                break;

            if(!isDepToSell(companyProduct.split(";")[0],currentTypesDep)) {
                logMe("Докупаем продукцию: " + companyProduct.split(";")[0]);

                for(int i=1; i<companyProduct.split(";").length;i++){


                    productInfo = getProductDataFromCompanyConfig(companyProduct.split(";")[i],companyProductsToSell);


                    //этого продукта не должно быть на странице снабжения.
                    if(isThisProductSellOnSellPage(productInfo.split(";")[0]))
                        continue;

                    logMe(productInfo);
                    s = new org.openqa.selenium.support.ui.Select(driver.findElement(By.name("productCategory")));
                    s.selectByVisibleText(companyProduct.split(";")[0]);
                    Thread.sleep(500);
                    driver.findElement(By.xpath("//span[label/img[@alt='"+productInfo.split(";")[0]+"']]/input")).click();


                    String handle1 = driver.getWindowHandle();
                    driver.findElement(By.xpath("//input[@value='Добавить поставщика']")).click();
                    Set<String> handles=driver.getWindowHandles();
                    Iterator<String> it =handles.iterator();
                    while (it.hasNext()) {
                        String popupHandle = it.next().toString();
                        if (!popupHandle.contains(handle1)) {
                            driver.switchTo().window(popupHandle);
                            //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                        }
                    }
                    //#myPro#=title;volume;localsales;qa;brand;price;base_value_to_buy
                    //параметры: бренд прайс качество
                    Double price=Double.valueOf(driver.findElement(By.xpath("//table[@class='right_corner']//tr[2]/td[1]")).getText().split(":")[1].replaceAll(" ","").replaceAll("\\$",""));
                    Double qa=Double.valueOf(driver.findElement(By.xpath("//table[@class='right_corner']//tr[2]/td[2]")).getText().split(" ")[1].replaceAll(" ","").replaceAll("\\$",""));
                    Double brand=Double.valueOf(driver.findElement(By.xpath("//table[@class='right_corner']//tr[2]/td[3]")).getText().split(" ")[1].replaceAll(" ","").replaceAll("\\$",""));
                    Double confPrice = Double.valueOf(productInfo.split(";")[5]);
                    Double confQa = Double.valueOf(productInfo.split(";")[3]);
                    Double confBrand = Double.valueOf(productInfo.split(";")[4]);
                    String valueToSet = productInfo.split(";")[6];

                    logMe(""+price+">="+confPrice);
                    logMe(""+qa+"<"+confQa);
                    logMe(""+brand+"<"+confBrand);

                    logMe(valueToSet);

                    //если наши параметры подходят для покупки в этом городе - покупаем
                    if(price>=confPrice && confBrand>brand && confQa>qa){
                        logMe("Ура, продукт подошел!");

                        driver.findElement(By.xpath("//a[text()='Свои']")).click();
                        if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() !=0)
                            if (driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).isDisplayed() )
                                driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();
                        if(driver.findElements(By.xpath("//tr[td[text()='Cвободно']]/td[2]/a[2]/img")).size()!=0){
                            Thread.sleep(500);
                            //сортировка
                            driver.findElement(By.xpath("//tr[td[text()='Cвободно']]/td[2]/a[2]/img")).click();

                            String goodId = driver.findElement(By.xpath("//table//tr[@class='friendlyHighLight']/td[@class='choose']/span")).getAttribute("id");
                            ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).sendKeys(valueToSet);
                            ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");

                            driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                            driver.switchTo().window(handle1);
                            logMe("Закупились "+productInfo.split(";")[0]);
                        }
                        else {
                            logMe("Продукта не нашли!!");
                            driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                            driver.switchTo().window(handle1);
                        }

                    }
                    else {
                        logMe("Продукт не подошел!");
                        driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                        driver.switchTo().window(handle1);
                    }
                }
                depCount++;
            }
        }




        // Идем в зал продаж и ставим цены. на 30% дороже чем средняя цена по городу
        boolean action=false;
        Thread.sleep(3000);
        waitForElementVisible("//a[text()='Торговый зал']");
        waitForElement("//a[text()='Торговый зал']");
        driver.findElement(By.xpath("//a[text()='Торговый зал']")).click();
        for(int i=0; i<driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).size();i++){
            String salePrice=driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).getAttribute("value");
            if(salePrice.equals("0.00")){
                Double setupvalue = Double.valueOf(driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[12]")).get(i).getText().replaceAll(" ","").replaceAll("\\$",""));
                setupvalue=setupvalue*1.3;
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(String.valueOf(setupvalue));
                action=true;
            }
        }
        if(action) {
//            waitForElement("//input[@value='Установить цены']");
//            waitForElementVisible("//input[@value='Установить цены']");
//            driver.findElement(By.xpath("//input[@value='Установить цены']")).click();
              driver.findElement(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).sendKeys(Keys.RETURN);
        }

        driver.findElement(By.xpath("//a[text()='Магазин']")).click();






        return new StorePage(driver);
    }

    public StorePage goToTradingRoom() throws InterruptedException {
        waitForElementVisible("//a[text()='Торговый зал']");
        waitForElement("//a[text()='Торговый зал']");
        driver.findElement(By.xpath("//a[text()='Торговый зал']")).click();
        return new StorePage(driver);
    }

    public StorePage goToMainStorePage() throws InterruptedException {
        waitForElementVisible("//a[text()='Магазин']");
        waitForElement("//a[text()='Магазин']");
        driver.findElement(By.xpath("//a[text()='Магазин']")).click();
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

//        if(isDepProcessed(driver.getCurrentUrl())){
//            logMe("Already processed");
//            return new StorePage(driver);
//        }


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
        //удаляем товар из зала продаж который по нулям. не продается.на складе ноль.
        for(int i=0; i<driver.findElements(By.xpath("//tr[@class='odd' or @class='even']")).size();i++){
            store = driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[6]")).get(i).getText().replaceAll(" ","");
            if(Double.valueOf(store)==0){
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[2]/input")).get(i).click();
                action=true;
            }
        }


        //Если ничего не продается, то нафиг такой магазин нужен??!? - Удаляем
//        if(driver.findElements(By.xpath("//input[@value='Ликвидировать остатки товара']")).size()>0)
//            driver.findElement(By.xpath("//input[@value='Ликвидировать остатки товара']")).click();
//        else{
//            driver.findElement(By.xpath("//a[text()='Магазин']")).click();
//            String currentUrl = driver.getCurrentUrl();
//            String UnitId = getUnitIdByUrl(currentUrl);
//            driver.get("http://virtonomica.ru/vera/window/unit/close/"+UnitId);
//            driver.findElement(By.xpath("//input[@value='Закрыть предприятие']")).click();
//            driver.switchTo().alert().accept();
//            driver.findElement(By.xpath("//a[text()='Магазин']")).click();
//            return new StorePage(driver);
//        }

        if(action) {
            if(driver.findElements(By.xpath("//input[@value='Ликвидировать остатки товара']")).size()>0)
                driver.findElement(By.xpath("//input[@value='Ликвидировать остатки товара']")).click();
            driver.switchTo().alert().accept();
        }

        // ВТОРОЙ ЗАХОД. ибо с первого не проходит!
        action=false;
        for(int i=0; i<driver.findElements(By.xpath("//tr[@class='odd' or @class='even']")).size();i++){
            store = driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[6]")).get(i).getText().replaceAll(" ","");
            if(Double.valueOf(store)==0){
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[2]/input")).get(i).click();
                action=true;
            }
        }
        if(action) {
            if(driver.findElements(By.xpath("//input[@value='Ликвидировать остатки товара']")).size()>0)
                driver.findElement(By.xpath("//input[@value='Ликвидировать остатки товара']")).click();
            driver.switchTo().alert().accept();
        }

//    2. price == 0 если забыли проставить цену - назначаем среднюю цену по городу +30%
//    price = avgPrice*1.30
//
//    3. store/saled>3 // если продажи отстают от снабжения
//        1. price<= basicCost // товар продается ниже себестоимости // товар в конф фале должен быть не помечен как для низкой продажи
//             offer=0.7 * offer // оффер уменьшаем на 30%
//             price=price // цену не меняем!
//        else
//             offer=offer*0.7
//             price=price*0.9 // цену делаем меньше
//    else: // если с продажами все нормально
//         price<= basicCost // товар в конф фале должен быть не помечен как для низкой продажи
//             offer=0.7
//             price=price // поправочка!!! можно продавать ниже себестоимости. // цену не меняем!
//         else //продажи идут нормально но не шибко хорошо.
//             offer=saled*0,9
//             price=price*0,9
//         if saled ==  store или saled > offer // продажи очень хорошо идут увеличиваем оффер//
//              offer=saled*1.50
//              price=price*1.10
//          if рынок забит > 30%
//              price=price*1.3

        //сейчас мы в тоговом зале
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
            if(Double.valueOf(store)/Double.valueOf(saled)>3){
                if(Double.valueOf(price)<Double.valueOf(basicCost) && !getParameter("ShopGoodsLessSelfCost").contains(productName)){
                    price = basicCost;
//                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
//                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
//                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
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
                if(Double.valueOf(price)<Double.valueOf(basicCost) && !getParameter("ShopGoodsLessSelfCost").contains(productName)){
                    if(saled.equals(store)){
                        price = String.valueOf(Double.valueOf(price)*1.1);
                        driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                        driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                        driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                    }
                    else {
                        price = basicCost;
//                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
//                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
//                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                        result += "Продажа ниже себестоимости; ";
                    }
                }
                else {
                    price = String.valueOf(Double.valueOf(price)*1.1);
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                }

                if(saled.equals(store)){
                    price = String.valueOf(Double.valueOf(price)*1.1);
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                }
            }
//          4. market>20
//               price=price*1.10
            if(Double.valueOf(market)>30){
                price = String.valueOf(Double.valueOf(price)*1.2);
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).clear();
                driver.findElements(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).get(i).sendKeys(price);
                result+="Рынок забит; ";
            }

            recordDepartment(productName,result);
        }

        if(driver.findElements(By.xpath("//input[@value='Установить цены']")).size()!=0) {
//            driver.findElement(By.xpath("//input[@value='Установить цены']")).click();
//            waitForElement("//input[@class='button160']");
//            waitForElementVisible("//input[@class='button160']");
//            driver.findElement(By.xpath("//input[@class='button160']")).click();
            driver.findElement(By.xpath("//tr[@class='odd' or @class='even']/td[10]/input")).sendKeys(Keys.ENTER);
        }



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
            logMe("удалили второй офер");
        }

//      1. store==0
//      удаляем дублируемые саплаерные заказы
        //Thread.sleep(15000);
        int rows = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]")).size();
        waitForElement("//tr[contains(@id,'product_row')]/td[10]/input["+rows+"]");
        for(int i=0; i< rows;i++){
            store = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[1]//tr[1]/td[2]")).get(i).getText().replaceAll(" ","");
            String supplierStore = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[9]//tr[3]/td[2]")).get(i).getText().replaceAll(" ", "").replaceAll("\\$", "");
            if(Double.valueOf(store)==0 && Double.valueOf(supplierStore)!=0) {
                Thread.sleep(100);
                try {
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[10]/input")).get(i).click();
                    logMe("удалили того саплаера если у нас на складе ноль и у него на складе ноль.");
                } catch (WebDriverException e) {
                    System.out.println(e.getMessage());
                    //System.out.println(driver.getPageSource());
                }
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

            //если новый продукт: офер не ноль, на складе поставщика не ноль, у нас на складе ноль, продажи = ноль
            if(Double.valueOf(offer)>1 && Double.valueOf(retailerStore)>1 && Double.valueOf(store)<1 && Double.valueOf(saled)<1){
                continue;
            }


            //3
            if(Double.valueOf(store)/Double.valueOf(saled)>=3){
                if(Double.valueOf(store)/Double.valueOf(saled)>=10){
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

                if(offer.equals("0"))
                    offer=String.valueOf(Double.valueOf(saled)*0.8);

                if(Double.valueOf(saled) == Double.valueOf(store) || Double.valueOf(saled) >= Double.valueOf(offer) ){
                    offer = String.valueOf(Double.valueOf(saled)*1.5);
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).sendKeys(offer);
                }
                else {
                    offer = String.valueOf(Double.valueOf(saled) * 1.1);
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[5]/input")).get(i).sendKeys(offer);
                }
            }

            if(Double.valueOf(retailerStore)/Double.valueOf(saled)<3){
                logMe("Warning! " + productName + " МАЛО!");
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


}

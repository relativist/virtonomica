package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by rest on 3/7/14.
 */
public class WareHousePage extends Page {
    public WareHousePage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    private Integer sleepTimer=1000;

    public boolean isMyProduct(ArrayList<String> wProducts,String product){
        for(int i=0; i<wProducts.size();i++){
            if(wProducts.get(i).split(";")[0].equals(product))
                if(wProducts.get(i).split(";")[3].equals("my"))
                    return true;
        }
        return false;
    }

    public String getPriceFromConfFileByName(ArrayList<String> wProducts,String productTitle){
        for(int i=0; i<wProducts.size();i++){
            if(wProducts.get(i).split(";")[0].equals(productTitle))
                return wProducts.get(i).split(";")[1];
        }
        logMe("Не могу найти продукт в конф файле: "+productTitle);
        assertTrue(false);
        return "";
    }
    public String getQaFromConfFileByName(ArrayList<String> wProducts,String productTitle){
        for(int i=0; i<wProducts.size();i++){
            if(wProducts.get(i).split(";")[0].equals(productTitle))
                return wProducts.get(i).split(";")[2];
        }
        logMe("Не могу найти продукт в конф файле: "+productTitle);
        assertTrue(false);
        return "";
    }

    public boolean isConfProduct(ArrayList<String> wProducts,String product){
        for(int i=0; i<wProducts.size();i++){
            if(wProducts.get(i).split(";")[0].equals(product)) {
                return true;
            }
            //else logMe(wProducts.get(i).split(";")[0]+" не "+ product);
        }
        return false;
    }

    // возвращает лучшую позицию в окне саплаеров товаров.
    public int getBestLineFromSupplyWindow(){
        ArrayList<String> koff = new ArrayList<String>();
        String price = "";
        String quality = "";
        for(int i=0; i<driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']")).size(); i++){
            price = driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']/td[9]")).get(i).getText().replaceAll("\\$","").replaceAll(" ","");
            quality = driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']/td[10]")).get(i).getText().replaceAll("\\$","").replaceAll(" ","");
            koff.add(String.valueOf(Double.valueOf(price) / Double.valueOf(quality) + ";" + i));
        }
        return getMinValue(koff);
    }

    // возвращает лучшую позицию в окне саплаеров товаров. "позиция; коэфф"
    public String getBestLineAndKoeffFromSupplyWindow(String productOffer){
        ArrayList<String> koff = new ArrayList<String>();
        String price = "";
        String quality = "";
        String qty = "";
        driver.findElement(By.xpath("//a[text()='Все']")).click();
        for(int i=0; i<driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']")).size(); i++){

            //ограничение на красные ограничители
            if (driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']["+(i+1)+"]/td[4]/span")).size()>0){
                qty = driver.findElement(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']["+(i+1)+"]/td[4]/span")).getText().replaceAll(" ","").split(":")[1];
            }
            else qty = productOffer;


            price = driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']/td[9]")).get(i).getText().replaceAll("\\$","").replaceAll(" ","");

            //если кнасное ограничение меньше оффера то ставим качество = 1
            if(Double.valueOf(qty)<Double.valueOf(productOffer))
                quality = "1";
            else
                quality = driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']/td[10]")).get(i).getText().replaceAll("\\$","").replaceAll(" ","");

            koff.add(String.valueOf(Double.valueOf(price) / Double.valueOf(quality) + ";" + i));
            logMe("getBestLineAndKoeffFromSupplyWindow:");
            logMe(price+" "+quality+" "+i);
            logMe(productOffer+" "+qty);
        }

        int minvaluePosition = getMinValue(koff);
        logMe("minimal position: "+minvaluePosition);
        String returnKoeff = "";
        for(int couner=0; couner<koff.size();couner++){
            if(Integer.valueOf(koff.get(couner).split(";")[1]) == minvaluePosition)
                returnKoeff = koff.get(couner).split(";")[0];
        }
        String returnValue= String.valueOf(minvaluePosition)+";"+returnKoeff;
        logMe("returned : "+minvaluePosition + " "+returnKoeff);
        return returnValue;
    }

    public void doesWeHaveBetterProduct(ArrayList<String> myProducts,ArrayList<String> wProducts) throws InterruptedException {
        //    Step 2.7
        //    главная страница
        //    снабжение только своих продуктов.
        int i = 1;//счетчик парентов
        int j = 1;//счетчик детей
        boolean isNeedEraseOffer=false;
        String productStore ="";
        String productOffer="";
        String productTitle="";
        String supplierStore="";
        String supplierTotalStore="";
        boolean result=false;

        for(int counter=0; counter<myProducts.size(); counter++){
            i=1;
            j=1;
            List<WebElement> family = null;
            List<WebElement> all = driver.findElements(By.xpath("//table//tr[@class='p_title' or @class='odd' or @class='even']"));
            for(WebElement el:all){
                if(el.getAttribute("class").equals("p_title")){
                    //обновляем данные для парента!
                    productTitle = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div/strong")).getText();
                    // на складе
                    productStore = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).getText().replaceAll(" ", "");
                    // отгрузки
                    if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).size()!=0)
                        productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).getText().replaceAll(" ", "");
                    else productOffer="0";
                    // закупаем продукт наш. чтоб на заводах ничего не оставалось. все храним на складах.
                    if(myProducts.get(counter).equals(productTitle)){
                        logMe("Закупаем чужой продукт! " + productTitle);
                        String handle1 = driver.getWindowHandle();
                        driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div[2]/a[2]/img")).click();
                        Set<String> handles=driver.getWindowHandles();
                        Iterator<String> it =handles.iterator();
                        while (it.hasNext()) {
                            String popupHandle = it.next().toString();
                            if (!popupHandle.contains(handle1)) {
                                driver.switchTo().window(popupHandle);
                                //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                            }
                        }

                        driver.findElement(By.xpath("//a[text()='Свои']")).click();
                        if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() !=0)
                            if (driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).isDisplayed() )
                                driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();

                        //Задаем фильтр нашего поиска продуктов.
                        driver.findElement(By.id("filterLegend")).click();
                        driver.findElement(By.id("total_price_isset")).click();
                        driver.findElement(By.id("free_for_buy_isset")).click();
                        driver.findElement(By.id("quality_isset")).click();

                        driver.findElement(By.name("total_price[to]")).clear();
                        driver.findElement(By.name("total_price[to]")).clear();
                        driver.findElement(By.name("total_price[to]")).sendKeys(getPriceFromConfFileByName(wProducts,productTitle));

                        driver.findElement(By.name("quality[from]")).clear();
                        driver.findElement(By.name("quality[from]")).clear();
                        driver.findElement(By.name("quality[from]")).sendKeys(getQaFromConfFileByName(wProducts,productTitle));

                        driver.findElement(By.xpath("//input[@class='button160']")).click();
                        //getBestLineFromSupplyWindow();

                        //String goodId = driver.findElements(By.xpath("//*[@id='mainTable']//tr[@class='odd' or @class='even']/td[@class='choose']/span")).get(getBestLineFromSupplyWindow()).getAttribute("id");
                        String goodId = "";
                        String quantity = "";
                        logMe("всего нашли продуктов для покупки: "+ driver.findElements(By.xpath("//table//tr[@class='friendlyHighLight']")).size());
                        for(int d=0; d<driver.findElements(By.xpath("//table//tr[@class='friendlyHighLight']")).size(); d++){
                            logMe("продукт закупаем "+d);
                            goodId = driver.findElements(By.xpath("//table//tr[@class='friendlyHighLight']/td[@class='choose']/span")).get(d).getAttribute("id");
                            quantity = driver.findElements(By.xpath("//table//tr[@class='friendlyHighLight']/td[5]")).get(d).getText().replaceAll("\\$","").replaceAll(" ","");
                            ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");
                            //logMe("кликнули закупить");
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).sendKeys(quantity);
                            ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");
                            //logMe("закупили");
                        }


                        driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                        driver.switchTo().window(handle1);
                        Thread.sleep(sleepTimer);
                        break;
                    }
                    //logMe(productTitle+" "+productStore+" "+productOffer+" "+isNeedEraseOffer);
                    i++;
                }
                else{
                    //апдейтим данные для чайлда и применяем правила:
                    //    если товара*3 > отгрузок - обнуляем заказы
                    supplierStore= driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[9]//span")).getText().replaceAll(" ","");
                    if(supplierStore.contains("из")){
                        //logMe("Contains!");
                        supplierStore=supplierStore.split("из")[1];
                    }
                    //logMe(j+" child "+supplierStore);
                    supplierTotalStore=supplierStore.split("\\W")[1];
                    supplierStore=supplierStore.split("\\W")[0];


                    //обнуляем или не обнуляем

                    j++;
                }
            }
            Thread.sleep(sleepTimer);
        }

        Thread.sleep(sleepTimer);
    }

    //функция снабжения чужими продуктами в окне выбора саплаеров чужих. т.е. используется эта фу если мы  нашли у чужих саплаеров продукт лучше чем у нас.
    public void supplyTheirProducts(ArrayList<String> myProducts,ArrayList<String> wProducts) throws InterruptedException {
        //    Step 2.5
        //    главная страница
        //    снабжение только своих продуктов.
        int i = 1;//счетчик парентов
        int j = 1;//счетчик детей
        boolean isNeedEraseOffer=false;
        String productStore ="";
        String productOffer="";
        String productTitle="";
        String supplierStore="";
        String supplierTotalStore="";
        boolean result=false;

        for(int counter=0; counter<myProducts.size(); counter++){
            i=1;
            j=1;
            List<WebElement> family = null;
            List<WebElement> all = driver.findElements(By.xpath("//table//tr[@class='p_title' or @class='odd' or @class='even']"));
            for(WebElement el:all){
                if(el.getAttribute("class").equals("p_title")){
                    //обновляем данные для парента!
                    productTitle = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div/strong")).getText();
                    // на складе
                    productStore = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).getText().replaceAll(" ", "");
                    // отгрузки
                    //productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).getText().replaceAll(" ", "");

                    // закупаем продукт наш. чтоб на заводах ничего не оставалось. все храним на складах.
                    if(myProducts.get(counter).split(";")[0].equals(productTitle)){
                        logMe("Закупаем чужой продукт!" + productTitle);
                        productOffer = String.valueOf(Double.valueOf(myProducts.get(counter).split(";")[1])*1.1);
                        logMe("Для чужого продукта "+productTitle+"  закупаем "+productOffer);
                        String handle1 = driver.getWindowHandle();
                        driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div[2]/a[2]/img")).click();
                        Set<String> handles=driver.getWindowHandles();
                        Iterator<String> it =handles.iterator();
                        while (it.hasNext()) {
                            String popupHandle = it.next().toString();
                            if (!popupHandle.contains(handle1)) {
                                driver.switchTo().window(popupHandle);
                                //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                            }
                        }

                        driver.findElement(By.xpath("//a[text()='Все']")).click();
                        if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() !=0)
                            if (driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).isDisplayed() )
                                driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();

                        //Задаем фильтр нашего поиска продуктов.
                        driver.findElement(By.id("filterLegend")).click();
                        driver.findElement(By.id("total_price_isset")).click();
                        driver.findElement(By.id("free_for_buy_isset")).click();
                        driver.findElement(By.id("quality_isset")).click();

                        driver.findElement(By.name("total_price[to]")).clear();
                        driver.findElement(By.name("total_price[to]")).clear();
                        driver.findElement(By.name("total_price[to]")).sendKeys(getPriceFromConfFileByName(wProducts,productTitle));

                        driver.findElement(By.name("quality[from]")).clear();
                        driver.findElement(By.name("quality[from]")).clear();
                        driver.findElement(By.name("quality[from]")).sendKeys(getQaFromConfFileByName(wProducts,productTitle));

                        driver.findElement(By.name("free_for_buy[from]")).clear();
                        driver.findElement(By.name("free_for_buy[from]")).clear();
                        driver.findElement(By.name("free_for_buy[from]")).sendKeys(productOffer);



                        driver.findElement(By.xpath("//input[@class='button160']")).click();
                        //getBestLineFromSupplyWindow();
                        String goodId ="0";

                        if(driver.findElements(By.xpath("//*[@id='mainTable']//tr[@class='odd' or @class='even']/td[@class='choose']/span")).size()!=0) {
                            //goodId = driver.findElements(By.xpath("//*[@id='mainTable']//tr[@class='odd' or @class='even']/td[@class='choose']/span")).get(getBestLineFromSupplyWindow()).getAttribute("id");
                            String selectedNumber = String.valueOf(Integer.valueOf(getBestLineAndKoeffFromSupplyWindow(productOffer).split(";")[0])+1);
                            goodId = driver.findElement(By.xpath("//*[@id='mainTable']//tr[@class='odd' or @class='even']["+selectedNumber+"]/td[@class='choose']/span")).getAttribute("id");
                            //goodId = getBestLineAndKoeffFromSupplyWindow(productOffer).split(";")[0];
                            logMe("goodId = "+goodId);
                        }
                        else{
                            logMe("ERRORRRRRR По заданному кретерию не найден ни один саплаер!!!");
                            continue;
                        }
                        //String goodId = "";
                        String quantity = "";





                        ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");
                        //logMe("кликнули закупить");
                        driver.findElement(By.id("amountInput")).clear();
                        driver.findElement(By.id("amountInput")).clear();
                        driver.findElement(By.id("amountInput")).sendKeys(productOffer);
                        ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");
                        //logMe("закупили");



                        driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                        driver.switchTo().window(handle1);
                        Thread.sleep(sleepTimer);
                        break;
                    }
                    //logMe(productTitle+" "+productStore+" "+productOffer+" "+isNeedEraseOffer);
                    i++;
                }
                else{
                    //апдейтим данные для чайлда и применяем правила:
                    //    если товара*3 > отгрузок - обнуляем заказы
                    supplierStore= driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[9]//span")).getText().replaceAll(" ","");
                    if(supplierStore.contains("из")){
                        //logMe("Contains!");
                        supplierStore=supplierStore.split("из")[1];
                    }
                    //logMe(j+" child "+supplierStore);
                    supplierTotalStore=supplierStore.split("\\W")[1];
                    supplierStore=supplierStore.split("\\W")[0];


                    //обнуляем или не обнуляем

                    j++;
                }
            }
            Thread.sleep(sleepTimer);
        }

        Thread.sleep(sleepTimer);
    }

    public String getDepParameter(){
        return "";
    }

    //функция снабжения своими продуктами. т.е. ищем все продукты свои в поставщиках и закупаем все что можно. обход - прошлый цикл меняется дерево. поэтому сделали так.
    public void supplyOurProducts(ArrayList<String> myProducts,ArrayList<String> wProducts) throws InterruptedException {
        //    Step 2.5
        //    главная страница
        //    снабжение только своих продуктов.
        logMe("2.5  Пытаемся закупить у своих саплаеров все что имеется!");
        int i = 1;//счетчик парентов
        int j = 1;//счетчик детей
        boolean isNeedEraseOffer=false;
        String productStore ="";
        String productOffer="";
        String productTitle="";
        String supplierStore="";
        String supplierTotalStore="";
        boolean result=false;

        for(int counter=0; counter<myProducts.size(); counter++){
            i=1;
            j=1;
            List<WebElement> family = null;
            List<WebElement> all = driver.findElements(By.xpath("//table//tr[@class='p_title' or @class='odd' or @class='even']"));
            for(WebElement el:all){
                if(el.getAttribute("class").equals("p_title")){
                    //обновляем данные для парента!
                    productTitle = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div/strong")).getText();
                    // на складе
                    productStore = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).getText().replaceAll(" ", "");
                    // отгрузки
                    //productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).getText().replaceAll(" ", "");
                    // закупаем продукт наш. чтоб на заводах ничего не оставалось. все храним на складах.
                    if(myProducts.get(counter).equals(productTitle)){
                        logMe("Закупаем НАШ продукт!" + productTitle);
                        String handle1 = driver.getWindowHandle();
                        driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div[2]/a[2]/img")).click();
                        Set<String> handles=driver.getWindowHandles();
                        Iterator<String> it =handles.iterator();
                        while (it.hasNext()) {
                            String popupHandle = it.next().toString();
                            if (!popupHandle.contains(handle1)) {
                                driver.switchTo().window(popupHandle);
                                //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                            }
                        }

                        driver.findElement(By.xpath("//a[text()='Свои']")).click();
                        if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() !=0)
                            if (driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).isDisplayed() )
                                driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();

                        //Задаем фильтр нашего поиска продуктов.
                        driver.findElement(By.id("filterLegend")).click();
                        driver.findElement(By.id("total_price_isset")).click();
                        driver.findElement(By.id("free_for_buy_isset")).click();
                        driver.findElement(By.id("quality_isset")).click();

                        driver.findElement(By.name("total_price[to]")).clear();
                        driver.findElement(By.name("total_price[to]")).clear();
                        driver.findElement(By.name("total_price[to]")).sendKeys(getPriceFromConfFileByName(wProducts,productTitle));

                        driver.findElement(By.name("quality[from]")).clear();
                        driver.findElement(By.name("quality[from]")).clear();
                        driver.findElement(By.name("quality[from]")).sendKeys(getQaFromConfFileByName(wProducts,productTitle));

                        driver.findElement(By.xpath("//input[@class='button160']")).click();
                        //getBestLineFromSupplyWindow();

                        //String goodId = driver.findElements(By.xpath("//*[@id='mainTable']//tr[@class='odd' or @class='even']/td[@class='choose']/span")).get(getBestLineFromSupplyWindow()).getAttribute("id");
                        String goodId = "";
                        String quantity = "";
                        logMe("всего нашли продуктов для покупки: "+ driver.findElements(By.xpath("//table//tr[@class='friendlyHighLight']")).size());
                        for(int d=0; d<driver.findElements(By.xpath("//table//tr[@class='friendlyHighLight']")).size(); d++){
                            logMe("продукт закупаем "+d);
                            goodId = driver.findElements(By.xpath("//table//tr[@class='friendlyHighLight']/td[@class='choose']/span")).get(d).getAttribute("id");
                            quantity = driver.findElements(By.xpath("//table//tr[@class='friendlyHighLight']/td[5]")).get(d).getText().replaceAll("\\$","").replaceAll(" ","");
                            ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");
                            //logMe("кликнули закупить");
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).clear();
                            driver.findElement(By.id("amountInput")).sendKeys(quantity);
                            ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");
                            //logMe("закупили");
                        }


                        driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                        driver.switchTo().window(handle1);
                        Thread.sleep(sleepTimer);
                        break;
                    }
                    //logMe(productTitle+" "+productStore+" "+productOffer+" "+isNeedEraseOffer);
                    i++;
                }
                else{
                    //апдейтим данные для чайлда и применяем правила:
                    //    если товара*3 > отгрузок - обнуляем заказы
                    supplierStore= driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[9]//span")).getText().replaceAll(" ","");
                    if(supplierStore.contains("из")){
                        //logMe("Contains!");
                        supplierStore=supplierStore.split("из")[1];
                    }
                    //logMe(j+" child "+supplierStore);
                    supplierTotalStore=supplierStore.split("\\W")[1];
                    supplierStore=supplierStore.split("\\W")[0];


                    //обнуляем или не обнуляем

                    j++;
                }
            }
            Thread.sleep(sleepTimer);
        }

        Thread.sleep(sleepTimer);
    }
    /*
    1. если на складе больше в два раза чем требуется. обнуляем оффер. ждем
    2. если у поставщика меньше чем два моих требования - бить тревогу
    3. если на складе меньше двух требования и больше одного - перезаказать сумму
    */
    public WareHousePage supply() throws InterruptedException {
        //Step1
        driver.findElement(By.xpath("//a[text()='Снабжение']")).click();
        int i = 1;//счетчик парентов
        int j = 1;//счетчик детей
        String productStore ="";
        String productOffer="";
        String productTitle="";
        String supplierStore="";
        String supplierTotalStore="";
        boolean action=false;

        ArrayList<String> wProducts = getWproducts();
        boolean result = true;

        List<WebElement> family = null;
        List<WebElement> all = driver.findElements(By.xpath("//table//tr[@class='p_title' or @class='odd' or @class='even']"));

        logMe("1 . проверяем базу продуктов. (все ли продукты которые на складе содеражтся в конфиге)");
        logMe("удаляем тех саплаеров , у кого меньше на складе чем наш оффер");
        for(WebElement el:all){
            if(el.getAttribute("class").equals("p_title")){
                //j=1;
                //обновляем данные для парента!
                productTitle = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div/strong")).getText();
                // на складе
                if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).size()>0)
                    productStore = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).getText().replaceAll(" ", "");
                else
                    productStore="0";
                // отгрузки
                if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).size()!=0)
                    productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).getText().replaceAll(" ", "");
                else productOffer="0";
                result=isConfProduct(wProducts,productTitle);
                //logMe("result = "+result);
                if(!result){
                    logMe("ОШИБКА!!!! НУЖНО ВНЕСТИ В БАЗУ НАШ ПРОДУКТ!");
                    new HelpPage(driver).recordReport(driver.getCurrentUrl(),"WAREHOUSE. нужно внести в базу продукт: "+productTitle);
                    logMe(productTitle);
                    assertTrue(false);
                }


                //logMe(productTitle+" "+productStore+" "+productOffer);
                i++;
            }
            else{
                //апдейтим данные для чайлда и применяем правила:
                //ищем поставщиков у кого на складах меньше чем мне требуется - удаляем.
                //проставляем галки и удаляем
                //supplierStore= driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[9]//span")).getText().replaceAll(" ","").split("\\W")[0];
                supplierStore= driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[9]//span")).getText().replaceAll(" ","");
                if(supplierStore.contains("из")){
                    //logMe("Contains!");
                    supplierStore=supplierStore.split("из")[1];
                }
                supplierTotalStore=supplierStore.split("\\W")[1];
                supplierStore=supplierStore.split("\\W")[0];

                //logMe(j+" child "+supplierStore);

                //проставляем галки и удаляем
                if(Double.valueOf(supplierStore)<Double.valueOf(productOffer) && !isMyProduct(wProducts,productTitle)){
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[1]/input")).click();
                    action=true;
                }
                // если у саплаера ноль, зачем он нужен?
                if(Double.valueOf(supplierStore)==0 && !isMyProduct(wProducts,productTitle)){
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[1]/input")).click();
                    action=true;
                }
                j++;
            }
        }
        if(action){
            driver.findElement(By.xpath("//input[@value='Разорвать выбранные контракты']")).click();
            driver.switchTo().alert().accept();
            action=false;
        }



        //    Step 2
        //    главная страница
        //    снабжение
        //    если товара*3 > отгрузок - обнуляем заказы
        //    главная страница
        i = 1;//счетчик парентов
        j = 1;//счетчик детей
        family = null;
        logMe("2. если товара больше на складе чем заказов - обнуляем заказы.");
        logMe("если наш продукт - закупаем всё");
        all = driver.findElements(By.xpath("//table//tr[@class='p_title' or @class='odd' or @class='even']"));
        boolean isNeedEraseOffer=false;
        ArrayList<String> myProducts= new ArrayList<String>();
        for(WebElement el:all){
            if(el.getAttribute("class").equals("p_title")){

                //обновляем данные для парента!
                productTitle = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div/strong")).getText();
                // на складе
                if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).size()>0)
                    productStore = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).getText().replaceAll(" ", "");
                else
                    productStore="0.0";
                // отгрузки
                if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).size()!=0)
                    productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).getText().replaceAll(" ", "");
                else productOffer="0";

                isNeedEraseOffer=false;
                if(Double.valueOf(productStore)>3*Double.valueOf(productOffer) && !isMyProduct(wProducts,productTitle)) {
                    logMe("Будем удалять продукт "+ productTitle + "потому что на складе  больше товара чем заказов в 3 раза.");
                    logMe(productStore+">3*"+productOffer);
                    isNeedEraseOffer = true;
                }
                if(isMyProduct(wProducts,productTitle)) {
                    logMe("мой продукт"+ productTitle);
                    myProducts.add(productTitle);
                }

                //logMe(productTitle+" "+productStore+" "+productOffer+" "+isNeedEraseOffer);
                i++;
            }
            else{
                //апдейтим данные для чайлда и применяем правила:
                //    если товара*3 > отгрузок - обнуляем заказы
                supplierStore= driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[9]//span")).getText().replaceAll(" ","");
                if(supplierStore.contains("из")){
                    //logMe("Contains!");
                    supplierStore=supplierStore.split("из")[1];
                }
                //logMe(j+" child "+supplierStore);
                supplierTotalStore=supplierStore.split("\\W")[1];
                supplierStore=supplierStore.split("\\W")[0];


                //обнуляем или не обнуляем
                result = isMyProduct(wProducts,productTitle);
                //logMe(result+" my"+productTitle);
                if(isNeedEraseOffer && !result){
                    logMe("Обнуляем заказ! "+productTitle);
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).sendKeys("0");
                    action=true;
                }
                if(result){
                    logMe("наш продукт" + productTitle + "закупаем все что имеется");
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).sendKeys(supplierTotalStore);
                    action=true;
                }
                j++;
            }
        }

        if(action){
            driver.findElement(By.xpath("//input[@value='Изменить']")).click();
            //driver.switchTo().alert().accept();
            action=false;
        }

        //Включить!Открыть!!!
        //supplyOurProducts(myProducts,wProducts);

        driver.findElement(By.xpath("//a[text()='Склад']")).click();

        //    Step 3
        //    главная страница
        //    на главной странице смотрим товар если на складе товара меньше чем отгрузок, закупаемся:
        //    идем в снабжение
        //    ищем в договорах продукт если есть:
        //    берем лучший коэффициент текущих договоров продукта: цена / кач (смотрим в конф файл, если нет продукта - пропускаем продукт - сигналим.)
        //    ищем в поставщиках по заданному кретерию(цена.кач.колич.).
        //    сравниваем с нашим лучшим  коэфф
        //    нашли лучше - закупаем все унего. остальные обнуляем.
        //
        //    не нашли - закупаемся у текущих.
        //
        //
        //    если нет договора о продукте:
        //    заключаем договор по кретерию (цена кач колич). покупаем.
        logMe("3. Смотрим на общую картину. закупаем товары которых не хватает ( офер больше склада )");
        ArrayList<String> productsToBuy = new ArrayList<String>();
        boolean isNeedToBuy=false;

        for(int counter=1; counter<=driver.findElements(By.xpath("//tr[@class='odd' or @class='even']")).size(); counter++ ){
            productTitle= driver.findElement(By.xpath("//tr[@class='odd' or @class='even']["+counter+"]//img")).getAttribute("alt");
            productStore = driver.findElement(By.xpath("//tr[@class='odd' or @class='even']["+counter+"]/td[2]")).getText().replaceAll(" ", "");
            productOffer = driver.findElement(By.xpath("//tr[@class='odd' or @class='even']["+counter+"]/td[6]")).getText().replaceAll(" ", "");
            if(Double.valueOf(productStore)<Double.valueOf(productOffer)*2){
                logMe(productTitle+" закупаем. Нехватка товара!");
                productsToBuy.add(productTitle+";"+productOffer);
                new HelpPage(driver).recordReport(driver.getCurrentUrl(),"WAREHOUSE. Закупаем: "+productTitle);
                isNeedToBuy=true;
            }
        }
        if(isNeedToBuy){
            ArrayList<String> productsToBuyExternal = new ArrayList<String>(productsToBuy);
            driver.findElement(By.xpath("//a[text()='Снабжение']")).click();
            ArrayList<String> otherHasBetterProducts = new ArrayList<String>();

            for(int counter=0; counter<productsToBuy.size(); counter++){
                i = 1;//счетчик парентов
                j = 1;//счетчик детей
                int k=0; //лучший саплаер!
                family = null;
                logMe("Закупаем "+productsToBuy.get(counter));
                boolean isOtherHasBetterProduct = false;
                all = driver.findElements(By.xpath("//table//tr[@class='p_title' or @class='odd' or @class='even']"));

                for(WebElement el:all){
                    if(el.getAttribute("class").equals("p_title")){

                        //обновляем данные для парента!
                        productTitle = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div/strong")).getText();
                        // на складе
                        productStore = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).getText().replaceAll(" ", "");
                        // отгрузки
                        if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).size()!=0)
                            productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).getText().replaceAll(" ", "");
                        else productOffer="0";

                        isOtherHasBetterProduct=false;

                        if(productsToBuy.get(counter).split(";")[0].equals(productTitle)){
                            logMe("Нашли наш продукт. нужно найти лучшего поставщика");
                            //logMe("тут падает, размер: "+productsToBuyExternal.size()+"");
                            //if(productsToBuyExternal.size()>1)
                            logMe("перечисляем массив тех продуктов в случае необходимости которых нужно закупить в нижних иконках :");
                            int numtoDell=0;
                            for(int c1=0; c1<productsToBuyExternal.size(); c1++){
                                if(productsToBuyExternal.get(c1).equals(productTitle))
                                    numtoDell=c1;
                                logMe(productsToBuyExternal.get(c1));
                            }
                            logMe("удаляем продукт из массива с номером "+numtoDell);
                                productsToBuyExternal.remove(numtoDell);

                            k=Integer.valueOf(getTheBestLocalSupplier(productTitle).split(";")[0]);

                            // здесь выясняем , есть ли продукт лучше коэфф чем у нас есть ?!? если есть - пометим что нужно докупить этот продукт в следущем цикле. кладем в массив.
                            logMe("ищем лучший продукт по коэфф чем мы имеем!!!");
                            String handle1 = driver.getWindowHandle();
                            driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div[2]/a[2]/img")).click();
                            Set<String> handles=driver.getWindowHandles();
                            Iterator<String> it =handles.iterator();
                            while (it.hasNext()) {
                                String popupHandle = it.next().toString();
                                if (!popupHandle.contains(handle1)) {
                                    driver.switchTo().window(popupHandle);
                                    //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                                }
                            }
                            //logMe("у продукта "+productTitle+"кликаем СВОИ");
                            //driver.findElement(By.xpath("//a[text()='Свои']")).click();
                            if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() !=0)
                                if (driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).isDisplayed() )
                                    driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();

                            //Задаем фильтр нашего поиска продуктов.
                            driver.findElement(By.id("filterLegend")).click();
                            driver.findElement(By.id("total_price_isset")).click();
                            driver.findElement(By.id("free_for_buy_isset")).click();
                            driver.findElement(By.id("quality_isset")).click();

                            driver.findElement(By.name("total_price[to]")).clear();
                            driver.findElement(By.name("total_price[to]")).clear();
                            driver.findElement(By.name("total_price[to]")).sendKeys(getPriceFromConfFileByName(wProducts,productTitle));

                            driver.findElement(By.name("quality[from]")).clear();
                            driver.findElement(By.name("quality[from]")).clear();
                            driver.findElement(By.name("quality[from]")).sendKeys(getQaFromConfFileByName(wProducts,productTitle));

                            driver.findElement(By.xpath("//input[@class='button160']")).click();
                            String bestOtherValue = getBestLineAndKoeffFromSupplyWindow(productOffer);


                            driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                            driver.switchTo().window(handle1);
                            Thread.sleep(sleepTimer);


                            logMe("Сравниваем продукт у нас и у них "+productTitle);
                            String ourBestKoeff = getTheBestLocalSupplier(productTitle).split(";")[1];
                            if(Double.valueOf(ourBestKoeff)<Double.valueOf(bestOtherValue.split(";")[1])){
                                logMe("Лучшего нашего продукта " +productTitle+" нигде нет!");
                                isOtherHasBetterProduct=false;
                            }else{
                                logMe("У других компаний есть продукт "+productTitle+" лучше чем у нас!");
                                isOtherHasBetterProduct=true;
                                otherHasBetterProducts.add(productTitle+";"+productOffer);
                                logMe("для продукта "+productTitle+" кладем в массив оффер "+productOffer);
                            }


                        }

                        //logMe(productTitle+" "+productStore+" "+productOffer+" "+isNeedEraseOffer);
                        i++;
                    }
                    else{
                        //апдейтим данные для чайлда и применяем правила:
                        //    покупаем у лучшего поставщика нужное количество.
                        supplierStore= driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[9]//span")).getText().replaceAll(" ","");
                        if(supplierStore.contains("из")){
                            //logMe("Contains!");
                            supplierStore=supplierStore.split("из")[1];
                        }
                        //logMe(j+" child "+supplierStore);
                        supplierTotalStore=supplierStore.split("\\W")[1];
                        supplierStore=supplierStore.split("\\W")[0];

                        //добрались до лучшего саплаера
                        if(j==k && !isOtherHasBetterProduct){
                            driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                            driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                            driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).sendKeys(Double.valueOf(productsToBuy.get(counter).split(";")[1])*1.2+"");
                            action=true;
                            isOtherHasBetterProduct=false;
                        }
                        if(isOtherHasBetterProduct){
                            logMe("обнуляем продукт "+productTitle+" потому что нашли лучше саплаера");
                            driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                            driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                            driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).sendKeys("0");
                            action=true;
                        }

                        j++;
                    }
                }
            }

            if(action){
                driver.findElement(By.xpath("//input[@value='Изменить']")).click();
                //driver.switchTo().alert().accept();
                action=false;
            }

            //если есть продукты которые нужно закупить, но есть сторонние поставщики у которых этот продукт лучше чем у нас - закупаем у сторонних.
            if(otherHasBetterProducts.size()>0){
                supplyTheirProducts(otherHasBetterProducts,wProducts);
            }


            // если требуется продукт которого нету в списке поставщиков продукто на кладке снабжение. закупаем продукт из нижних иконочек квадратных.
            if(productsToBuyExternal.size()>0){
                for(int counter=0; counter< productsToBuyExternal.size(); counter++){
                    logMe("Дополнительно Закупаем "+productsToBuyExternal.get(counter));
                    String handle1 = driver.getWindowHandle();
                    Thread.sleep(sleepTimer);
                    driver.findElement(By.xpath("//img[@alt='"+productsToBuyExternal.get(counter).split(";")[0]+"']")).click();
                    Set<String> handles=driver.getWindowHandles();
                    Iterator<String> it =handles.iterator();
                    while (it.hasNext()) {
                        String popupHandle = it.next().toString();
                        if (!popupHandle.contains(handle1)) {
                            driver.switchTo().window(popupHandle);
                            //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                        }
                    }
                    driver.findElement(By.xpath("//a[text()='Все']")).click();
                    if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() !=0)
                        if (driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).isDisplayed() )
                            driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();

                    //Задаем фильтр нашего поиска продуктов.
                    driver.findElement(By.id("filterLegend")).click();
                    driver.findElement(By.id("total_price_isset")).click();
                    driver.findElement(By.id("free_for_buy_isset")).click();
                    driver.findElement(By.id("quality_isset")).click();

                    driver.findElement(By.name("total_price[to]")).clear();
                    driver.findElement(By.name("total_price[to]")).clear();
                    driver.findElement(By.name("total_price[to]")).sendKeys(getPriceFromConfFileByName(wProducts,productsToBuyExternal.get(counter).split(";")[0]));

                    driver.findElement(By.name("quality[from]")).clear();
                    driver.findElement(By.name("quality[from]")).clear();
                    driver.findElement(By.name("quality[from]")).sendKeys(getQaFromConfFileByName(wProducts, productsToBuyExternal.get(counter).split(";")[0]));

                    driver.findElement(By.name("free_for_buy[from]")).clear();
                    driver.findElement(By.name("free_for_buy[from]")).clear();
                    driver.findElement(By.name("free_for_buy[from]")).sendKeys(String.valueOf(productsToBuyExternal.get(counter).split(";")[1]));

                    driver.findElement(By.xpath("//input[@class='button160']")).click();
                    //getBestLineFromSupplyWindow();

                    String goodId = driver.findElements(By.xpath("//*[@id='mainTable']//tr[@class='odd' or @class='even']/td[@class='choose']/span")).get(getBestLineFromSupplyWindow()).getAttribute("id");
                    ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");

                    driver.findElement(By.id("amountInput")).sendKeys(Double.valueOf(productsToBuyExternal.get(counter).split(";")[1])*1.2+"");
                    ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");

                    driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                    driver.switchTo().window(handle1);
                }
            }
        }

        //driver.findElement(By.xpath("//a[text()='Склад']")).click();
        return new WareHousePage(driver);
    }

    //возвращаем "номер позиции;коэфф" лучшего продукта
    public String getTheBestLocalSupplier(String myProductTitle){
        int i = 1;//счетчик парентов
        int j = 1;//счетчик детей
        String productStore ="";
        String productOffer="";
        String productTitle="";
        String supplierStore="";
        String supplierPrice="";
        String supplierQuality="";
        String supplierTotalStore="";
        boolean action=false;
        List<WebElement> family = null;
        List<WebElement> all = driver.findElements(By.xpath("//table//tr[@class='p_title' or @class='odd' or @class='even']"));
        ArrayList<String> koeff = new ArrayList<String>();

        for(WebElement el:all){
            if(el.getAttribute("class").equals("p_title")){

                //обновляем данные для парента!
                productTitle = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div/strong")).getText();
                // на складе
                productStore = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).getText().replaceAll(" ", "");
                // отгрузки
                if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).size()!=0)
                    productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//td[contains(text(),'Отгрузки')]/following-sibling::td")).getText().replaceAll(" ", "");
                else productOffer="0";

                if(myProductTitle.equals(productTitle)){
                    action=true;
                }
                else{
                    action=false;
                }

                i++;
            }
            else{
                //апдейтим данные для чайлда и применяем правила:
                //    покупаем у лучшего поставщика нужное количество.
                supplierStore= driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[9]//span")).getText().replaceAll(" ","");
                if(supplierStore.contains("из")){
                    //logMe("Contains!");
                    supplierStore=supplierStore.split("из")[1];
                }
                supplierTotalStore=supplierStore.split("\\W")[1];
                supplierStore=supplierStore.split("\\W")[0];

                if(action){
                    supplierPrice=driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[4]")).getText().trim().replaceAll(" ", "").replaceAll("\\$","");
                    if(supplierPrice.contains("/"))
                        supplierPrice=supplierPrice.split("/")[1];
                    supplierQuality=driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[6]")).getText().trim().replaceAll(" ", "").replaceAll("\\$","");
                    logMe("suppPrice: "+supplierPrice);
                    logMe("suppQa: "+supplierQuality);
                    koeff.add(String.valueOf((Double.valueOf(supplierPrice) / Double.valueOf(supplierQuality))) + ";" + j);
                }

                j++;
            }
        }
        int minvaluePosition = getMinValue(koeff);
        String returnKoeff = "";
        for(int couner=0; couner<koeff.size();couner++){
            if(Integer.valueOf(koeff.get(couner).split(";")[1]) == minvaluePosition)
                returnKoeff = koeff.get(couner).split(";")[0];
        }
        String returnValue= String.valueOf(minvaluePosition)+";"+returnKoeff;
        return returnValue;
    }

    //из массива саплаеров получаем айдишник паплаера лучшего! формат(коэфф;айди)
    public int getMinValue(ArrayList<String> p){

        ArrayList<Double> num = new ArrayList<Double>();

        Double best=100000.0;
        for(int counter=0; counter<p.size(); counter++){
            num.add(Double.valueOf(p.get(counter).split(";")[0]));
        }
        for(int counter=0; counter<num.size(); counter++){
            if(num.get(counter)<best)
                best=num.get(counter);
        }
        for(int counter=0; counter<p.size(); counter++){
            if(p.get(counter).split(";")[0].equals(String.valueOf(best))){
                //logMe("best "+p.get(counter).split(";")[1]);
                return Integer.valueOf(p.get(counter).split(";")[1]);
            }
        }
        assertTrue(false);
        return 500;
    }

    public WareHousePage supplyProductsWithSuppliers(){
        String need = new String();
        String title = new String();
        //если нет поставщика данного товара, то идем и закупаем из своих. если нет своих - тревога.
        for(int i =0; i<driver.findElements(By.xpath("//tr[contains(@id,'product_row')]//a[@title]")).size();i++){
            need = driver.findElements(By.xpath("//tr[td[contains(text(),'Требуется')]]/td[2]")).get(i).getText().replaceAll(" ", "");
            title = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]//a[@title]")).get(i).getAttribute("title");
            //ищем товар с пустым поставщиком
            if(driver.findElements(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td")).size()<7){
                String handle1 = driver.getWindowHandle();
                driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/th//td[2]//img")).get(i).click();
                Set<String> handles=driver.getWindowHandles();
                Iterator<String> it =handles.iterator();
                while (it.hasNext()) {
                    String popupHandle = it.next().toString();
                    if (!popupHandle.contains(handle1)) {
                        driver.switchTo().window(popupHandle);
                        //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                    }
                }
                driver.findElement(By.xpath("//a[text()='Свои']")).click();
                if(driver.findElements(By.xpath("//table[@class='list main_table']/tbody/tr/td[11]/span")).size()==0){
                    logMe("У нас нет подходящего саплаера для продукта "+title);
                    driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                    driver.switchTo().window(handle1);
                    continue;
                }

                String goodId = driver.findElement(By.xpath("//table[@class='list main_table']/tbody/tr/td[11]/span")).getAttribute("id");
                ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");

                driver.findElement(By.id("amountInput")).sendKeys(String.valueOf(Double.valueOf(need)*1.2+""));
                ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");
                driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                driver.switchTo().window(handle1);
            }

        }
        driver.navigate().refresh();
        return new WareHousePage(driver);
    }

//продавать по себестоимости и только своим.
    public WareHousePage sales() throws InterruptedException {
        Thread.sleep(1000);
        String wareHouseFixedPrice=getParameter("WareHouseFixedPrice");
        logMe("param is : "+wareHouseFixedPrice);
        waitForElement("//a[text()='Сбыт']");
        waitForElementVisible("//a[text()='Сбыт']");
        driver.findElement(By.xpath("//a[text()='Сбыт']")).click();
        for(int i=0; i<driver.findElements(By.xpath("//table[@class='grid']//tr[@class]")).size(); i ++){
            String selfCost = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[4]//tr[td[contains(text(),'Себестоимость')]]/td[2]")).get(i).getText();
            if(selfCost.equals("Не известна")||selfCost.equals("---"))
                continue;
            selfCost=selfCost.replaceAll(" ","").replaceAll("\\$","");
            String priceToSell = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).getAttribute("value");
            Double sCost=Double.valueOf(selfCost);
            Double pToSell=Double.valueOf(priceToSell);

            String productTitle = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[3]//img")).get(i).getAttribute("alt");
            logMe(productTitle);
            if(productTitle.equals("Ликер"))
                logMe(productTitle);

            if((sCost+sCost*0.1)>=pToSell && (pToSell+pToSell*0.1)>=sCost) {
                logMe("prices not changed for "+productTitle);
                continue;
            }




            //если в настройках нет продукта у которого фиксированной цены нет, меняем цену на себестоимость. иначе - ничего не трогаем. оставляем как есть у этого продукта.
            if(!wareHouseFixedPrice.contains(productTitle)){
                driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
                driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
                driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).sendKeys(selfCost);

                Select s1 = new Select(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/select")).get(i));
                s1.selectByVisibleText("Только своей компании");
            }{
                new HelpPage(driver).recordReport(driver.getCurrentUrl(),"WAREHOUSE SALE. зафиксирована цена: "+productTitle);
            }
        }
        driver.findElement(By.xpath("//input[@value='Сохранить изменения']")).click();
        driver.findElement(By.xpath("//a[text()='Склад']")).click();
        return new WareHousePage(driver);
    }

    public WareHousePage setAutoQaSlave() throws InterruptedException {
        new SalaryPage(driver).autoSetSalaryAndQa();
        return new WareHousePage(driver);
    }



    private boolean isNeedtoEducate(){
        String salarySlave = driver.findElement(By.xpath("//tr[td[text()='Зарплата рабочих']]/td[2]")).getText().split("\\$")[0].replaceAll(" ","");
        String salaryTown  = driver.findElement(By.xpath("//tr[td[text()='Зарплата рабочих']]/td[2]")).getText().split("городу ")[1].replaceAll("\\$\\)","").replaceAll(" ", "");
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


}

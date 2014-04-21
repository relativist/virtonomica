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
            if(wProducts.get(i).split(";")[0].equals(product))
                return true;
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
    public String getBestLineAndKoeffFromSupplyWindow(){
        ArrayList<String> koff = new ArrayList<String>();
        String price = "";
        String quality = "";
        driver.findElement(By.xpath("//a[text()='Все']")).click();
        for(int i=0; i<driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']")).size(); i++){
            price = driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']/td[9]")).get(i).getText().replaceAll("\\$","").replaceAll(" ","");
            quality = driver.findElements(By.xpath("//table[@class='list main_table']//tr[@class='odd' or @class='even']/td[10]")).get(i).getText().replaceAll("\\$","").replaceAll(" ","");
            koff.add(String.valueOf(Double.valueOf(price) / Double.valueOf(quality) + ";" + i));
        }

        int minvaluePosition = getMinValue(koff);
        String returnKoeff = "";
        for(int couner=0; couner<koff.size();couner++){
            if(Integer.valueOf(koff.get(couner).split(";")[1]) == minvaluePosition)
                returnKoeff = koff.get(couner).split(";")[0];
        }
        String returnValue= String.valueOf(minvaluePosition)+";"+returnKoeff;
        return returnValue;
    }

    public void doesWeHaveBetterProduct(ArrayList<String> myProducts,ArrayList<String> wProducts) throws InterruptedException {
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
                    if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).size()==0)
                        productOffer="0";
                    else
                        productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).getText().replaceAll(" ", "");
                    // закупаем продукт наш. чтоб на заводах ничего не оставалось. все храним на складах.
                    if(myProducts.get(counter).equals(productTitle)){
                        logMe("Закупаем наш продукт!" + productTitle);
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
                    if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).size()==0)
                        productOffer="0";
                    else
                        productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).getText().replaceAll(" ", "");
                    // закупаем продукт наш. чтоб на заводах ничего не оставалось. все храним на складах.
                    if(myProducts.get(counter).split(";")[0].equals(productTitle)){
                        logMe("Закупаем наш продукт!" + productTitle);
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

                        driver.findElement(By.xpath("//input[@class='button160']")).click();
                        //getBestLineFromSupplyWindow();

                        String goodId = driver.findElements(By.xpath("//*[@id='mainTable']//tr[@class='odd' or @class='even']/td[@class='choose']/span")).get(getBestLineFromSupplyWindow()).getAttribute("id");
                        //String goodId = "";
                        String quantity = "";





                        ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");
                        //logMe("кликнули закупить");
                        driver.findElement(By.id("amountInput")).clear();
                        driver.findElement(By.id("amountInput")).clear();
                        driver.findElement(By.id("amountInput")).sendKeys(myProducts.get(counter).split(";")[1]);
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

    //функция снабжения своими продуктами. т.е. ищем все продукты свои в поставщиках и закупаем все что можно. обход - прошлый цикл меняется дерево. поэтому сделали так.
    public void supplyOurProducts(ArrayList<String> myProducts,ArrayList<String> wProducts) throws InterruptedException {
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
                    if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).size()==0)
                        productOffer="0";
                    else
                        productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).getText().replaceAll(" ", "");
                    // закупаем продукт наш. чтоб на заводах ничего не оставалось. все храним на складах.
                    if(myProducts.get(counter).equals(productTitle)){
                        logMe("Закупаем наш продукт!" + productTitle);
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
                if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).size()==0)
                    productOffer="0";
                else
                    productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).getText().replaceAll(" ", "");
                result=isConfProduct(wProducts,productTitle);
                //logMe("result = "+result);
                if(!result){
                    logMe("ОШИБКА!!!! НУЖНО ВНЕСТИ В БАЗУ НАШ ПРОДУКТ!");
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
                    productStore="0";
                // отгрузки
                if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).size()==0)
                    productOffer="0";
                else
                    productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).getText().replaceAll(" ", "");

                isNeedEraseOffer=false;
                if(Double.valueOf(productStore)>3*Double.valueOf(productOffer))
                    isNeedEraseOffer=true;
                if(isMyProduct(wProducts,productTitle))
                    myProducts.add(productTitle);

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
                    //logMe("delete data!");
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).clear();
                    driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).sendKeys("0");
                    action=true;
                }
                if(result){
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


        supplyOurProducts(myProducts,wProducts);

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
        ArrayList<String> productsToBuy = new ArrayList<String>();
        boolean isNeedToBuy=false;

        for(int counter=1; counter<=driver.findElements(By.xpath("//tr[@class='odd' or @class='even']")).size(); counter++ ){
            productTitle= driver.findElement(By.xpath("//tr[@class='odd' or @class='even']["+counter+"]//img")).getAttribute("alt");
            productStore = driver.findElement(By.xpath("//tr[@class='odd' or @class='even']["+counter+"]/td[2]")).getText().replaceAll(" ", "");
            productOffer = driver.findElement(By.xpath("//tr[@class='odd' or @class='even']["+counter+"]/td[6]")).getText().replaceAll(" ", "");
            if(Double.valueOf(productStore)<Double.valueOf(productOffer)){
                logMe(productTitle+" закупаем");
                productsToBuy.add(productTitle+";"+productOffer);
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
                boolean isOtherHasBetterProduct = false;
                all = driver.findElements(By.xpath("//table//tr[@class='p_title' or @class='odd' or @class='even']"));

                for(WebElement el:all){
                    if(el.getAttribute("class").equals("p_title")){

                        //обновляем данные для парента!
                        productTitle = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//div/strong")).getText();
                        // на складе
                        productStore = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).getText().replaceAll(" ", "");
                        // отгрузки
                        if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).size()==0)
                            productOffer="0";
                        else
                            productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).getText().replaceAll(" ", "");

                        if(productsToBuy.get(counter).split(";")[0].equals(productTitle)){
                            logMe("Нашли наш продукт. нужно найти лучшего поставщика");
                            productsToBuyExternal.remove(counter);
                            k=Integer.valueOf(getTheBestSupplier(productTitle).split(";")[0]);

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
                            String bestOtherValue = getBestLineAndKoeffFromSupplyWindow();


                            driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                            driver.switchTo().window(handle1);
                            Thread.sleep(sleepTimer);

                            logMe("Сравниваем продукт у нас и у них "+productTitle);
                            if(Double.valueOf(getTheBestSupplier(productTitle).split(";")[1])<Double.valueOf(bestOtherValue.split(";")[1])){
                                logMe("Лучшего нашего продукта " +productTitle+" нигде нет!");
                                isOtherHasBetterProduct=false;
                            }else{
                                logMe("У других компаний есть продукт "+productTitle+" лучше чем у нас!");
                                isOtherHasBetterProduct=true;
                                otherHasBetterProducts.add(productTitle+";"+productOffer);
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
                            driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[2]/input[1]")).sendKeys(productsToBuy.get(counter).split(";")[1]);
                            action=true;
                        }
                        if(isOtherHasBetterProduct){
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

                    driver.findElement(By.id("amountInput")).sendKeys(String.valueOf(productsToBuyExternal.get(counter).split(";")[1]));
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
    public String getTheBestSupplier(String myProductTitle){
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
                if(driver.findElements(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).size()==0)
                    productOffer="0";
                else
                    productOffer = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[3]/td[2]")).getText().replaceAll(" ", "");

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
                    supplierQuality=driver.findElement(By.xpath("//table//tr[@class='odd' or @class='even']["+j+"]/td[6]")).getText().trim().replaceAll(" ", "").replaceAll("\\$","");
                    koeff.add(String.valueOf(Double.valueOf(supplierPrice) / Double.valueOf(supplierQuality)) + ";" + j);
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

                driver.findElement(By.id("amountInput")).sendKeys(String.valueOf(need));
                ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");
                driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
                driver.switchTo().window(handle1);
            }

        }
        driver.navigate().refresh();
        return new WareHousePage(driver);
    }

//продавать по себестоимости и только своим.
    public WareHousePage sales(){

        driver.findElement(By.xpath("//a[text()='Сбыт']")).click();
        for(int i=0; i<driver.findElements(By.xpath("//table[@class='grid']//tr[@class]")).size(); i ++){
            String selfCost = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[4]//tr[td[contains(text(),'Себестоимость')]]/td[2]")).get(i).getText();
            if(selfCost.equals("Не известна")||selfCost.equals("---"))
                continue;
            selfCost=selfCost.replaceAll(" ","").replaceAll("\\$","");
            String priceToSell = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).getAttribute("value");
            Double sCost=Double.valueOf(selfCost);
            Double pToSell=Double.valueOf(priceToSell);
            if((sCost+sCost*0.1)>=pToSell && (pToSell+pToSell*0.1)>=sCost) {
                logMe("prices not changed.");
                continue;
            }
            driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
            driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
            driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).sendKeys(selfCost);

            Select s1 = new Select(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/select")).get(i));
            s1.selectByVisibleText("Только своей компании");
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

    public WareHousePage getInfo(){
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

        return new WareHousePage(driver);
    }

}

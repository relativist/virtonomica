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

    public WareHousePage educate(){
        if(isStuding() && isNeedtoEducate()){
            logMe("Обучаю персонал");
            String currentUrl = driver.getCurrentUrl();
            String UnitId = getUnitIdByUrl(currentUrl);
            driver.get("http://virtonomica.ru/vera/window/unit/employees/education/"+UnitId);
            if(driver.findElements(By.xpath("//input[@value='Обучить']")).size()>0)
                driver.findElement(By.xpath("//input[@value='Обучить']")).click();
            driver.get(currentUrl);
        }
        return new WareHousePage(driver);
    }

    public List<WebElement> getFamily(int numberOfParent){
        List<WebElement> family = null;
        List<WebElement> all = driver.findElements(By.xpath("//table//tr[@class='p_title' or @class='odd' or @class='even']"));
        int i = 0;
        for(WebElement el:all){
            if(el.getAttribute("class").equals("p_title")){
                i++;
            }
            while(i==numberOfParent){
                family.add(el);
            }
        }
        return family;
    }

    public boolean isMyProduct(ArrayList<String> wProducts,String product){
        for(int i=0; i<wProducts.size();i++){
            if(wProducts.get(i).split(";")[0].equals(product))
                if(wProducts.get(i).split(";")[3].equals("my"))
                    return true;
        }
        return false;
    }

    public boolean isConfProduct(ArrayList<String> wProducts,String product){
        for(int i=0; i<wProducts.size();i++){
            if(wProducts.get(i).split(";")[0].equals(product))
                return true;
        }
        return false;
    }

//    Step 1
//    главная страница
//    снабжение
//    ищем поставщиков у кого на складах меньше чем мне требуется - удаляем.
//            главная страница
//    главная страница
//    Step 2
//    главная страница
//    снабжение
//    если товара*3 > отгрузок - обнуляем заказы
//    главная страница
//
//    Step 3
//    главная страница
//    на главной странице смотрим товар если на складе товара меньше чем отгрузок*1.5 а отгрузки по контрактам не ноль, закупаемся:
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

    public WareHousePage supply(){
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
                productStore = driver.findElement(By.xpath("//table//tr[@class='p_title']["+i+"]//tbody//tr[1]/td[2]")).getText().replaceAll(" ", "");
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

                isNeedEraseOffer=false;
                if(Double.valueOf(productStore)>3*Double.valueOf(productOffer))
                    isNeedEraseOffer=true;
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

        for(int counter=1; counter<driver.findElements(By.xpath("//tr[@class='odd' or @class='even']")).size(); counter++ ){
            productTitle= driver.findElement(By.xpath("//tr[@class='odd' or @class='even']["+counter+"]//img")).getText().replaceAll(" ","");
            productStore = driver.findElement(By.xpath("//tr[@class='odd' or @class='even']["+counter+"]/td[2]")).getText().replaceAll(" ", "");
            productOffer = driver.findElement(By.xpath("//tr[@class='odd' or @class='even']["+counter+"]/td[6]")).getText().replaceAll(" ", "");
            if(Double.valueOf(productStore)<Double.valueOf(productOffer)){
                logMe(productTitle+" закупаем");
                logMe("ERROR!!!");
            }
        }



        //driver.findElement(By.xpath("//a[text()='Склад']")).click();
        return new WareHousePage(driver);
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


    public WareHousePage sales(){

//        driver.findElement(By.xpath("//a[text()='Финансовый отчёт']")).click();
//        String balance = driver.findElement(By.xpath("//tr[td[text()='Прибыль']]//td[2]")).getText().replaceAll(" ", "").replaceAll("\\$", "");
//        logMe("balance = "+balance);
//
//        driver.findElement(By.xpath("//a[text()='Сбыт']")).click();
//        String generalSalePrice = "0";
//        boolean debet = true; //все хорошо. положительный баланс
//        boolean highdebet = false; //все хорошо. положительный баланс , но не на много!
//        boolean changeAnyPrice = true;
//        String settablePrice = "0.0";
//        if(Float.valueOf(balance)>0){
//            debet=true;
//            if(Float.valueOf(balance)>5000000)
//                highdebet=true;
//            else
//                highdebet=false;
//        }
//        else debet = false;
//        if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]")).size()>1)
//            for(int i=0; i<driver.findElements(By.xpath("//table[@class='grid']//tr[@class]")).size(); i ++){
//                settablePrice = "0.0";
//
//                //продаваемая цена
//                if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[5]//tr[3]/td[2]")).get(i).getText().equals("---") ||
//                        driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[5]//tr[3]/td[2]")).get(i).getText().equals("Не известна"))
//                    continue;
//
//                //кому продавать
//                Select s1 = new Select(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[9]/select")).get(i));
//                logMe("selected: "+s1.getFirstSelectedOption().getText());
//                if(s1.getFirstSelectedOption().getText().equals("Не продавать"))
//                    s1.selectByVisibleText("Только своей компании");
//                else if(!s1.getFirstSelectedOption().getText().equals("Не продавать"))
//                    changeAnyPrice=false;
//
//
//                generalSalePrice = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).getText().replaceAll(" ","").replaceAll("\\$","");
//                if(debet && highdebet && changeAnyPrice) //уменьшаем на 5%
//                    settablePrice = String.valueOf(Float.valueOf(generalSalePrice)*0.95);
//                else if(!debet) //увеличиваем на 10%
//                    settablePrice = String.valueOf(Float.valueOf(generalSalePrice)*1.10);
//
//                logMe("Recomended Pice to set up is : "+ settablePrice);
//                driver.findElement(By.xpath("//input[@value='Сохранить изменения']")).click();
//            }
//        else{
//            for(int i=0; i<driver.findElements(By.xpath("//table[@class='grid']//tr[@class]")).size(); i ++){
//                settablePrice = "0";
//
//                //продаваемая цена
//                if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[4]//tr[3]/td[2]")).get(i).getText().equals("---") ||
//                        driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[4]//tr[3]/td[2]")).get(i).getText().equals("Не известна"))
//                    continue;
//
//                //кому продавать
//                Select s1 = new Select(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/select")).get(i));
//                logMe("selected: "+s1.getFirstSelectedOption().getText());
//                if(s1.getFirstSelectedOption().getText().equals("Не продавать"))
//                    s1.selectByVisibleText("Только своей компании");
//                else if(!s1.getFirstSelectedOption().getText().equals("Не продавать"))
//                    changeAnyPrice=false;
//
//
//                generalSalePrice = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).getAttribute("value").replaceAll(" ","").replaceAll("\\$","");
//                logMe("generalSalePrice = "+generalSalePrice);
//                if(debet && highdebet && changeAnyPrice){ //уменьшаем на 5%
//                    settablePrice = String.valueOf(Float.valueOf(generalSalePrice)*0.95);
//                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
//                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).sendKeys(settablePrice);
//                }
//                else if(!debet){ //увеличиваем на 10%
//                    settablePrice = String.valueOf(Float.valueOf(generalSalePrice)*1.10);
//                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
//                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).sendKeys(settablePrice);
//                }
//                else{
//                    settablePrice = generalSalePrice;
//                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
//                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).sendKeys(settablePrice);
//                }
//
//
//                logMe("Recomended Pice to set up is : "+ settablePrice);
//                driver.findElement(By.xpath("//input[@value='Сохранить изменения']")).click();
//            }
//        }


        driver.findElement(By.xpath("//a[text()='Сбыт']")).click();
        for(int i=0; i<driver.findElements(By.xpath("//table[@class='grid']//tr[@class]")).size(); i ++){
            String selfCost = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[5]//tr[td[contains(text(),'Себестоимость')]]/td[2]")).get(i).getText();
            if(selfCost.equals("Не известна")||selfCost.equals("---"))
                continue;
            selfCost=selfCost.replaceAll(" ","").replaceAll("\\$","");
            String priceToSell = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).getAttribute("value");
            Double sCost=Double.valueOf(selfCost);
            Double pToSell=Double.valueOf(priceToSell);
//            logMe(sCost+"");
//            logMe(pToSell+"");
//            logMe((sCost+sCost*1.05)+">"+pToSell);
//            logMe((pToSell+pToSell*1.05)+">"+sCost);
            if((sCost+sCost*0.1)>=pToSell && (pToSell+pToSell*0.1)>=sCost) {
                logMe("prices not changed.");
                continue;
            }
            driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).clear();
            driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).clear();
            driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).sendKeys(selfCost);

            Select s1 = new Select(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[9]/select")).get(i));
            s1.selectByVisibleText("Только своей компании");
        }
        driver.findElement(By.xpath("//input[@value='Сохранить изменения']")).click();
        driver.findElement(By.xpath("//a[text()='Завод']")).click();
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

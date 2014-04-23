package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by rest on 3/7/14.
 */
public class PlantPage extends Page {
    public PlantPage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    public PlantPage educate(){
        if(isStuding() && isNeedtoEducate()){
            logMe("Обучаю персонал");
            String currentUrl = driver.getCurrentUrl();
            String UnitId = getUnitIdByUrl(currentUrl);
            driver.get("http://virtonomica.ru/vera/window/unit/employees/education/"+UnitId);
            if(driver.findElements(By.xpath("//input[@value='Обучить']")).size()>0)
                driver.findElement(By.xpath("//input[@value='Обучить']")).click();
            driver.get(currentUrl);
        }
        return new PlantPage(driver);
    }


    /*
    1. если на складе больше в два раза чем требуется. обнуляем оффер. ждем
    2. если у поставщика меньше чем два моих требования - бить тревогу
    3. если на складе меньше двух требования и больше одного - перезаказать сумму
    */
    public PlantPage supply(){
        driver.findElement(By.xpath("//a[text()='Снабжение']")).click();
        String title="";
        String need="";
        String have="";
        String offer="";
        String sklad="";
        String error = "";
        boolean change = false;
        boolean isNeedToFindSuppliers = false;

        supplyProductsWithSuppliers();
        //если у поставщика склад пустой. проходим по поставщикам и удаляем
        for(int i =0; i<driver.findElements(By.xpath("//tr[contains(@id,'product_row')]//a[@title]")).size();i++){
            if(driver.findElements(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td")).size()>6){
                title = driver.findElement(By.xpath("//tr[contains(@id,'product_row')][" + (i + 1) + "]//a[@title]")).getAttribute("title");
                sklad = driver.findElement(By.xpath("//tr[contains(@id,'product_row')][" + (i + 1) + "]/td[7]//tr[2]/td[2]")).getText().replaceAll(" ", "");
                if(sklad.equals("Неогр."))
                    sklad="100000000";
                if(Integer.valueOf(sklad)==0){
                    change=true;
                    logMe("Удалили ненужного поставщика "+title);
                    driver.findElement(By.xpath("//tr[contains(@id,'product_row')][" + (i + 1) + "]/td[8]/input")).click();
                }
            }
        }
        if(change) {
            driver.findElement(By.xpath("//input[@value='Разорвать выбранные контракты']")).click();
            driver.switchTo().alert().accept();
            change=false;
            isNeedToFindSuppliers=true;
        }

        if(isNeedToFindSuppliers) {
            supplyProductsWithSuppliers();
            isNeedToFindSuppliers=false;
        }




        // корректировка заказов!
        for(int i =0; i<driver.findElements(By.xpath("//tr[contains(@id,'product_row')]//a[@title]")).size();i++){
            title = driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]//a[@title]")).getAttribute("title");
            if(driver.findElements(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td")).size()>6){
                error = "";
                title = driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]//a[@title]")).getAttribute("title");
                //logMe("//tr[td[contains(text(),'Требуется')]]["+(i+1)+"]/td[2]");
                need = driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[1]//td[2]")).getText().replaceAll(" ", "");
                have = driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[2]//td[2]")).getText().replaceAll(" ", "");
                offer = driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[4]//input")).getAttribute("value").replaceAll(" ", "");
                sklad = driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[7]//tr[2]/td[2]")).getText().replaceAll(" ", "");

                if(Integer.valueOf(have)>2*Integer.valueOf(need)){
                    if(!offer.equals("0")){
                        driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[4]//input")).clear();
                        driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[4]//input")).sendKeys("0");
                    }
                    continue;
                }
                if(sklad.equals("Неогр."))
                    sklad="100000000";
                if(Integer.valueOf(sklad)<2*Integer.valueOf(need))
                    error+=" Поставщик обосрётся.";

                if(Integer.valueOf(have)<2*Integer.valueOf(need)){
                    driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[4]//input")).clear();
                    driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[4]//input")).sendKeys(need);
                    change=true;
                }



                if(!error.equals(""))
                    logMe(title+"\t\t"+error);
            }
            else {
                //logMe("Нет поставщика "+title);
            }




        }

        if(change){
            driver.findElement(By.name("applyChanges")).click();
            change=false;
        }

        driver.findElement(By.xpath("//a[text()='Завод']")).click();
        return new PlantPage(driver);
    }

    public PlantPage supplyProductsWithSuppliers(){
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
        return new PlantPage(driver);
    }

    // продавать по ценам себестоимости и только своим.
    public PlantPage sales(){

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
            String selfCost="";
            if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[5]//tr[td[contains(text(),'Себестоимость')]]/td[2]")).size()==0)
                selfCost = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[4]//tr[td[contains(text(),'Себестоимость')]]/td[2]")).get(i).getText();
            else
                selfCost = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[5]//tr[td[contains(text(),'Себестоимость')]]/td[2]")).get(i).getText();
            if(selfCost.equals("Не известна")||selfCost.equals("---"))
                continue;
            selfCost=selfCost.replaceAll(" ","").replaceAll("\\$","");
            String priceToSell="";
            if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).size()==0)
                priceToSell = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).getAttribute("value");
            else
                priceToSell = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).getAttribute("value");
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
            if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).size()==0){
                driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
                driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
                driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).sendKeys(selfCost);
            }
            else {
                driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).clear();
                driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).clear();
                driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).sendKeys(selfCost);
            }
            Select s1 = null;
            if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[9]/select")).size()==0)
                s1 = new Select(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/select")).get(i));
            else
                s1 = new Select(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[9]/select")).get(i));
            s1.selectByVisibleText("Только своей компании");
        }
        driver.findElement(By.xpath("//input[@value='Сохранить изменения']")).click();
        driver.findElement(By.xpath("//a[text()='Завод']")).click();
        return new PlantPage(driver);
    }

    public PlantPage setAutoQaSlave() throws InterruptedException {
        //new SalaryPage(driver).autoSetSalaryAndQa();
        new SalaryPage(driver).autoSetSalaryAndQaFormula();
        return new PlantPage(driver);
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

    public PlantPage getInfo(){
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

        return new PlantPage(driver);
    }

}

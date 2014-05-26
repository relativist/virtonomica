package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;

/**
 * Created by rest on 3/7/14.
 */
public class OfficePage extends Page {
    public OfficePage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    public OfficePage educate(){
        if(isStuding() && isNeedtoEducate()){
            logMe("Обучаю персонал");
            String currentUrl = driver.getCurrentUrl();
            String UnitId = getUnitIdByUrl(currentUrl);
            driver.get("http://virtonomica.ru/vera/window/unit/employees/education/"+UnitId);
            driver.findElement(By.xpath("//input[@value='Обучить']")).click();
            driver.get(currentUrl);
        }
        return new OfficePage(driver);
    }


    /*
    1. если на складе больше в два раза чем требуется. обнуляем оффер. ждем
    2. если у поставщика меньше чем два моих требования - бить тревогу
    3. если на складе меньше двух требования и больше одного - перезаказать сумму
    */
    public OfficePage supply(){
        driver.findElement(By.xpath("//a[text()='Снабжение']")).click();
        String title="";
        String need="";
        String have="";
        String offer="";
        String sklad="";
        String error = "";
        boolean change = false;
        for(int i =0; i<driver.findElements(By.xpath("//tr[contains(@id,'product_row')]//a[@title]")).size();i++){

            if(driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[7]//tr[2]/td[2]")).size()!=
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]")).size()){
                logMe("Нет Одного из поставщиков!");
                break;
            }

            error = "";
            title = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]//a[@title]")).get(i).getAttribute("title");
            need = driver.findElements(By.xpath("//tr[td[contains(text(),'Требуется')]]/td[2]")).get(i).getText().replaceAll(" ", "");
            have = driver.findElements(By.xpath("//tr[td[contains(text(),'Количество')]]/td[2]")).get(i).getText().replaceAll(" ","");
            offer = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[4]//input")).get(i).getAttribute("value").replaceAll(" ", "");
            sklad = driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[7]//tr[2]/td[2]")).get(i).getText().replaceAll(" ", "");

            if(Integer.valueOf(have)>2*Integer.valueOf(need)){
                if(!offer.equals("0")){
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[4]//input")).get(i).clear();
                    driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[4]//input")).get(i).sendKeys("0");
                }
                continue;
            }

            if(Integer.valueOf(sklad)<2*Integer.valueOf(need))
                error+=" Поставщик обосрался.";

            if(Integer.valueOf(have)<2*Integer.valueOf(need)){
                driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[4]//input")).get(i).clear();
                driver.findElements(By.xpath("//tr[contains(@id,'product_row')]/td[4]//input")).get(i).sendKeys(need);
                change=true;
            }



            if(!error.equals(""))
                logMe(title+"\t\t"+error);

        }

        if(change){
            driver.findElement(By.name("applyChanges")).click();
            change=false;
        }

        driver.findElement(By.xpath("//a[text()='Завод']")).click();
        return new OfficePage(driver);
    }


    public OfficePage sales(){

        driver.findElement(By.xpath("//a[text()='Финансовый отчёт']")).click();
        String balance = driver.findElement(By.xpath("//tr[td[text()='Прибыль']]//td[2]")).getText().replaceAll(" ", "").replaceAll("\\$", "");
        logMe("balance = "+balance);

        driver.findElement(By.xpath("//a[text()='Сбыт']")).click();
        String generalSalePrice = "0";
        boolean debet = true; //все хорошо. положительный баланс
        boolean highdebet = false; //все хорошо. положительный баланс , но не на много!
        boolean changeAnyPrice = true;
        String settablePrice = "0.0";
        if(Float.valueOf(balance)>0){
            debet=true;
            if(Float.valueOf(balance)>5000000)
                highdebet=true;
            else
                highdebet=false;
        }
        else debet = false;
        if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]")).size()>1)
            for(int i=0; i<driver.findElements(By.xpath("//table[@class='grid']//tr[@class]")).size(); i ++){
                settablePrice = "0.0";

                //продаваемая цена
                if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[5]//tr[3]/td[2]")).get(i).getText().equals("---") ||
                        driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[5]//tr[3]/td[2]")).get(i).getText().equals("Не известна"))
                    continue;

                //кому продавать
                Select s1 = new Select(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[9]/select")).get(i));
                logMe("selected: "+s1.getFirstSelectedOption().getText());
                if(s1.getFirstSelectedOption().getText().equals("Не продавать"))
                    s1.selectByVisibleText("Только своей компании");
                else if(!s1.getFirstSelectedOption().getText().equals("Не продавать"))
                    changeAnyPrice=false;


                generalSalePrice = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/input")).get(i).getText().replaceAll(" ","").replaceAll("\\$","");
                if(debet && highdebet && changeAnyPrice) //уменьшаем на 5%
                    settablePrice = String.valueOf(Float.valueOf(generalSalePrice)*0.95);
                else if(!debet) //увеличиваем на 10%
                    settablePrice = String.valueOf(Float.valueOf(generalSalePrice)*1.10);

                logMe("Recomended Pice to set up is : "+ settablePrice);
                driver.findElement(By.xpath("//input[@value='Сохранить изменения']")).click();
            }
        else{
            for(int i=0; i<driver.findElements(By.xpath("//table[@class='grid']//tr[@class]")).size(); i ++){
                settablePrice = "0";

                //продаваемая цена
                if(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[4]//tr[3]/td[2]")).get(i).getText().equals("---") ||
                        driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[4]//tr[3]/td[2]")).get(i).getText().equals("Не известна"))
                    continue;

                //кому продавать
                Select s1 = new Select(driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[8]/select")).get(i));
                logMe("selected: "+s1.getFirstSelectedOption().getText());
                if(s1.getFirstSelectedOption().getText().equals("Не продавать"))
                    s1.selectByVisibleText("Только своей компании");
                else if(!s1.getFirstSelectedOption().getText().equals("Не продавать"))
                    changeAnyPrice=false;


                generalSalePrice = driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).getAttribute("value").replaceAll(" ","").replaceAll("\\$","");
                logMe("generalSalePrice = "+generalSalePrice);
                if(debet && highdebet && changeAnyPrice){ //уменьшаем на 5%
                    settablePrice = String.valueOf(Float.valueOf(generalSalePrice)*0.95);
                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).sendKeys(settablePrice);
                }
                else if(!debet){ //увеличиваем на 10%
                    settablePrice = String.valueOf(Float.valueOf(generalSalePrice)*1.10);
                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).sendKeys(settablePrice);
                }
                else{
                    settablePrice = generalSalePrice;
                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).clear();
                    driver.findElements(By.xpath("//table[@class='grid']//tr[@class]/td[7]/input")).get(i).sendKeys(settablePrice);
                }


                logMe("Recomended Pice to set up is : "+ settablePrice);
                driver.findElement(By.xpath("//input[@value='Сохранить изменения']")).click();
            }
        }

        driver.findElement(By.xpath("//a[text()='Завод']")).click();
        return new OfficePage(driver);
    }

    public OfficePage setAutoQaSlave() throws InterruptedException {
        new SalaryPage(driver).autoSetSalaryAndQaFormula();
        return new OfficePage(driver);
    }



    public OfficePage checkOfficeLoad(){
        Double load = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Уровень управленческой нагрузки']]/td[2]//td[2]")).getText().split(" % ")[0]);
        logMe("Загрузка офиса: "+load);
        if(load > 80 ){
            logMe("TOO HUGE LOAD OFFICE!!!");
            new HelpPage(driver).recordReport(driver.getCurrentUrl(),"Загрузка офиса :"+load);
        }

        return new OfficePage(driver);
    }



    private boolean isNeedtoEducate(){
        String salarySlave = driver.findElement(By.xpath("//tr[td[text()='Зарплата одного сотрудника']]/td[2]")).getText().split("\\$")[0].replaceAll(" ","");
        String salaryTown  = driver.findElement(By.xpath("//tr[td[text()='Зарплата одного сотрудника']]/td[2]")).getText().split("городу: ")[1].replaceAll("\\$\\)","").replaceAll(" ","");
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

    public OfficePage createAdvertisingAdv() throws InterruptedException {
//        заходим в подразделения
//        составляем массив из товар;город по всем подразделениям типа Магазин.
//
//        идем в реклама
//        составляем второй массив товар;город
//
//        запоминаем текущий урл
//
//        1. удаляем лишнюю рекламу
//        второй сравниваем с первым массимвом. (перебираем второй массив)
//        если в первом не окажится элемента - кладем в третий массив.
//
//                функция нахождения элемента в массиве - булевая.
//
//                перебираем третий массив -
//                товар - идем в товар
//        город - кликаем на галку города
//
//        Остановить или изменить рекламную компанию.
//
//        2. добавляем новую рекламу
//        первый сравниваем со вторым (перебираем первый массив)
//        если во втором не окажится элемента - кладем в четвертый массив
//
//        функция нахождения элемента в массиве - булевая.
//
//                перебираем четвертый массив -
//                товар - идем в товар
//        город - кликаем на галку города
//
//        Начать или изменить рекламную компанию.
        ArrayList<String> first = new ArrayList<String>();
        ArrayList<String> second = new ArrayList<String>();
        ArrayList<String> third = new ArrayList<String>();
        ArrayList<String> firth = new ArrayList<String>();
        driver.findElement(By.xpath("//a[text()='Подразделения']")).click();

        // Step 1
        for(int i=0; i<driver.findElements(By.xpath("//tr[@class='odd' or @class = 'even']/td[2]/a[contains(text(),'tender')]")).size();i++){
            String cityName = driver.findElement(By.xpath("//tr[@class='odd' or @class = 'even'][td[2]/a[contains(text(),'tender')]]["+(i+1)+"]/td[1]")).getText();
            for(int j=0; j<driver.findElements(By.xpath("//tr[@class='odd' or @class = 'even'][td[2]/a[contains(text(),'tender')]]["+(i+1)+"]/td[4]//img")).size();j++){
                String productName = driver.findElements(By.xpath("//tr[@class='odd' or @class = 'even'][td[2]/a[contains(text(),'tender')]]["+(i+1)+"]/td[4]//img")).get(j).getAttribute("alt");
                first.add(productName+";"+cityName.trim());
                logMe(productName+";"+cityName.trim());
            }
        }
        logMe("");
        // Step 2
        driver.findElement(By.xpath("//a[text()='Реклама']")).click();
        for(int i=0; i<driver.findElements(By.xpath("//tr[contains(@class,'hand')]")).size();i++){
            String productName = driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).getAttribute("alt");
            //logMe("productName = "+productName);
            if(driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[2]")).get(i).getText().length()==0){
                //logMe("пропускаем");
                continue;
            }
            //logMe("cities : "+driver.findElement(By.xpath("//tr[contains(@class,'hand')]["+(i+1)+"]/td[2]")).getText());
            for(String city: driver.findElement(By.xpath("//tr[contains(@class,'hand')]["+(i+1)+"]/td[2]")).getText().split(",")){
                second.add(productName+";"+city.trim());
                logMe(productName+";"+city.trim());
            }
        }
        logMe("");

        // Step 3 Лишняя реклама
        for(int i=0; i<second.size();i++){
            String item = second.get(i);
            if(!isContains(first,item)){
                third.add(second.get(i));
                logMe("лишний : "+ second.get(i));
            }
        }
        logMe("");

        // Step 4 Добавление рекламы
        for(int i=0; i<first.size();i++){
            String item = first.get(i);
            if(!isContains(second,first.get(i))){
                firth.add(first.get(i));
                logMe("добавляем : "+ first.get(i));
            }
        }
        logMe("");

        // Step 5 Настоящее удаление рекламы.
        for(int p=0; p<third.size();p++){
            for(int i=0; i<driver.findElements(By.xpath("//tr[contains(@class,'hand')]")).size();i++){
                String productName = driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).getAttribute("alt");
                //logMe("productName = "+productName);
                if(driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[2]")).get(i).getText().length()==0){
                    //logMe("пропускаем");
                    continue;
                }

                //встретили на ненулевой продукт, заходим в него и отменяем рекламу!
                if(productName.equals(third.get(p).split(";")[0])){
                    logMe("кликнули.");
                    driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).click();

                    //удаляем ставя галки
                    String myCitytoDell = third.get(p).split(";")[1].trim();
                    logMe("удалили город: "+myCitytoDell);
                    String myXpath = "//tr[td[2]/label[text()='" + myCitytoDell + "']]/td[1]/input";
                    waitForElement(myXpath);
                    waitForElementVisible("//tr[td[2]/label[text()='" + myCitytoDell + "']]/td[1]/input");
                    Thread.sleep(1000);
                    driver.findElement(By.xpath("//tr[td[2]/label[text()='"+myCitytoDell+"']]/td[1]/input")).click();

                    //кликаем сохранить
                    Thread.sleep(1000);
                    if(driver.findElements(By.xpath("//input[contains(@value,'Изменить условия рекламной кампании') and @disabled]")).size()>0){
                        driver.findElement(By.xpath("//input[contains(@value,'Остановить рекламную кампанию')]")).click();
                    }
                    else driver.findElement(By.xpath("//input[contains(@value,'Изменить условия рекламной кампании')]")).click();
                }
            }
        }


        // Step 6 Настоящее добавление рекламы.
        for(int p=0; p<firth.size();p++){
            for(int i=0; i<driver.findElements(By.xpath("//tr[contains(@class,'hand')]")).size();i++){
                String productName = driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).getAttribute("alt");
                //logMe("productName = "+productName);
//                if(driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[2]")).get(i).getText().length()==0){
//                    //logMe("пропускаем");
//                    continue;
//                }

                //встретили на ненулевой продукт, заходим в него и отменяем рекламу!
                if(productName.equals(firth.get(p).split(";")[0])){
                    //logMe("кликнули.");
                    driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).click();

                    //удаляем ставя галки
                    String myCitytoDell = firth.get(p).split(";")[1].trim();
                    logMe("Добавили город: "+myCitytoDell);
                    String myXpath = "//tr[td[2]/label[text()='" + myCitytoDell + "']]/td[1]/input";
                    waitForElement(myXpath);
                    waitForElementVisible("//tr[td[2]/label[text()='" + myCitytoDell + "']]/td[1]/input");
                    Thread.sleep(1000);
                    driver.findElement(By.xpath("//tr[td[2]/label[text()='"+myCitytoDell+"']]/td[1]/input")).click();

                    //кликаем сохранить
                    Thread.sleep(1000);
                    if(driver.findElements(By.xpath("//input[contains(@value,'Изменить условия рекламной кампании') and not(@disabled)]")).size()>0){
                        driver.findElement(By.xpath("//input[contains(@value,'Изменить условия рекламной кампании')]")).click();
                    }
                    else if(driver.findElements(By.xpath("//input[contains(@value,'Начать рекламную кампанию') and @disabled]")).size()>0){
                        driver.findElement(By.xpath("//tr[td[2]/label[text()='Телевидение']]/td[1]/input")).click();
                        Thread.sleep(500);
                        driver.findElement(By.xpath("//input[contains(@value,'Начать рекламную кампанию')]")).click();
                    }
                    else{
                        logMe("Непредвиденная хрень");
                        assertTrue(false);
                    }
                }
            }
        }
        driver.findElement(By.xpath("//a[text()='Офис']")).click();


        return new OfficePage(driver);
    }

    public OfficePage createAdvertising() throws InterruptedException {
//        заходим в подразделения
//        составляем массив из товар;город по всем подразделениям типа Магазин.
//
//        идем в реклама
//        составляем второй массив товар;город
//
//        запоминаем текущий урл
//
//        1. удаляем лишнюю рекламу
//        второй сравниваем с первым массимвом. (перебираем второй массив)
//        если в первом не окажится элемента - кладем в третий массив.
//
//                функция нахождения элемента в массиве - булевая.
//
//                перебираем третий массив -
//                товар - идем в товар
//        город - кликаем на галку города
//
//        Остановить или изменить рекламную компанию.
//
//        2. добавляем новую рекламу
//        первый сравниваем со вторым (перебираем первый массив)
//        если во втором не окажится элемента - кладем в четвертый массив
//
//        функция нахождения элемента в массиве - булевая.
//
//                перебираем четвертый массив -
//                товар - идем в товар
//        город - кликаем на галку города
//
//        Начать или изменить рекламную компанию.
        ArrayList<String> first = new ArrayList<String>();
        ArrayList<String> second = new ArrayList<String>();
        ArrayList<String> third = new ArrayList<String>();
        ArrayList<String> firth = new ArrayList<String>();
        driver.findElement(By.xpath("//a[text()='Подразделения']")).click();

        // Step 1
        for(int i=0; i<driver.findElements(By.xpath("//tr[@class='odd' or @class = 'even']/td[2]/a[contains(text(),'Магазин')]")).size();i++){
            String cityName = driver.findElement(By.xpath("//tr[@class='odd' or @class = 'even'][td[2]/a[contains(text(),'Магазин')]]["+(i+1)+"]/td[1]")).getText();
            for(int j=0; j<driver.findElements(By.xpath("//tr[@class='odd' or @class = 'even'][td[2]/a[contains(text(),'Магазин')]]["+(i+1)+"]/td[4]//img")).size();j++){
                String productName = driver.findElements(By.xpath("//tr[@class='odd' or @class = 'even'][td[2]/a[contains(text(),'Магазин')]]["+(i+1)+"]/td[4]//img")).get(j).getAttribute("alt");
                first.add(productName+";"+cityName.trim());
                logMe(productName+";"+cityName.trim());
            }
        }
        logMe("");
        // Step 2
        driver.findElement(By.xpath("//a[text()='Реклама']")).click();
        for(int i=0; i<driver.findElements(By.xpath("//tr[contains(@class,'hand')]")).size();i++){
            String productName = driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).getAttribute("alt");
            //logMe("productName = "+productName);
            if(driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[2]")).get(i).getText().length()==0){
                //logMe("пропускаем");
                continue;
            }
            //logMe("cities : "+driver.findElement(By.xpath("//tr[contains(@class,'hand')]["+(i+1)+"]/td[2]")).getText());
            for(String city: driver.findElement(By.xpath("//tr[contains(@class,'hand')]["+(i+1)+"]/td[2]")).getText().split(",")){
                second.add(productName+";"+city.trim());
                logMe(productName+";"+city.trim());
            }
        }
        logMe("");

        // Step 3 Лишняя реклама
        for(int i=0; i<second.size();i++){
            String item = second.get(i);
            if(!isContains(first,item)){
                third.add(second.get(i));
                logMe("лишний : "+ second.get(i));
            }
        }
        logMe("");

        // Step 4 Добавление рекламы
        for(int i=0; i<first.size();i++){
            String item = first.get(i);
            if(!isContains(second,first.get(i))){
                firth.add(first.get(i));
                logMe("добавляем : "+ first.get(i));
            }
        }
        logMe("");

        // Step 5 Настоящее удаление рекламы.
        for(int p=0; p<third.size();p++){
            for(int i=0; i<driver.findElements(By.xpath("//tr[contains(@class,'hand')]")).size();i++){
                String productName = driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).getAttribute("alt");
                //logMe("productName = "+productName);
                if(driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[2]")).get(i).getText().length()==0){
                    //logMe("пропускаем");
                    continue;
                }

                //встретили на ненулевой продукт, заходим в него и отменяем рекламу!
                if(productName.equals(third.get(p).split(";")[0])){
                    logMe("кликнули.");
                    driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).click();

                    //удаляем ставя галки
                    String myCitytoDell = third.get(p).split(";")[1].trim();
                    logMe("удалили город: "+myCitytoDell);
                    String myXpath = "//tr[td[2]/label[text()='" + myCitytoDell + "']]/td[1]/input";
                    waitForElement(myXpath);
                    waitForElementVisible("//tr[td[2]/label[text()='" + myCitytoDell + "']]/td[1]/input");
                    Thread.sleep(1000);
                    driver.findElement(By.xpath("//tr[td[2]/label[text()='"+myCitytoDell+"']]/td[1]/input")).click();

                    //кликаем сохранить
                    Thread.sleep(1000);
                    if(driver.findElements(By.xpath("//input[contains(@value,'Изменить условия рекламной кампании') and @disabled]")).size()>0){
                        driver.findElement(By.xpath("//input[contains(@value,'Остановить рекламную кампанию')]")).click();
                    }
                    else driver.findElement(By.xpath("//input[contains(@value,'Изменить условия рекламной кампании')]")).click();
                }
            }
        }


        // Step 6 Настоящее добавление рекламы.
        for(int p=0; p<firth.size();p++){
            for(int i=0; i<driver.findElements(By.xpath("//tr[contains(@class,'hand')]")).size();i++){
                String productName = driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).getAttribute("alt");
                //logMe("productName = "+productName);
//                if(driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[2]")).get(i).getText().length()==0){
//                    //logMe("пропускаем");
//                    continue;
//                }

                //встретили на ненулевой продукт, заходим в него и отменяем рекламу!
                if(productName.equals(firth.get(p).split(";")[0])){
                    //logMe("кликнули.");
                    driver.findElements(By.xpath("//tr[contains(@class,'hand')]/td[1]/img")).get(i).click();

                    //удаляем ставя галки
                    String myCitytoDell = firth.get(p).split(";")[1].trim();
                    logMe("Добавили город: "+myCitytoDell);
                    String myXpath = "//tr[td[2]/label[text()='" + myCitytoDell + "']]/td[1]/input";
                    waitForElement(myXpath);
                    waitForElementVisible("//tr[td[2]/label[text()='" + myCitytoDell + "']]/td[1]/input");
                    Thread.sleep(1000);
                    driver.findElement(By.xpath("//tr[td[2]/label[text()='"+myCitytoDell+"']]/td[1]/input")).click();

                    //кликаем сохранить
                    Thread.sleep(1000);
                    if(driver.findElements(By.xpath("//input[contains(@value,'Изменить условия рекламной кампании') and not(@disabled)]")).size()>0){
                        driver.findElement(By.xpath("//input[contains(@value,'Изменить условия рекламной кампании')]")).click();
                    }
                    else if(driver.findElements(By.xpath("//input[contains(@value,'Начать рекламную кампанию') and @disabled]")).size()>0){
                        driver.findElement(By.xpath("//tr[td[2]/label[text()='Радио']]/td[1]/input")).click();
                        Thread.sleep(500);
                        driver.findElement(By.xpath("//input[contains(@value,'Начать рекламную кампанию')]")).click();
                    }
                    else{
                        logMe("Непредвиденная хрень");
                        assertTrue(false);
                    }
                }
            }
        }
        driver.findElement(By.xpath("//a[text()='Офис']")).click();


        return new OfficePage(driver);
    }

    public boolean isContains(ArrayList<String> mass , String string){
        for(int i=0; i<mass.size(); i++){
            String item = mass.get(i);
            if(mass.get(i).equals(string))
                return true;
        }
        return false;
    }

}

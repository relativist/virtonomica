package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by rest on 3/7/14.
 */
public class SalaryPage extends Page {
    public SalaryPage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    //сейчас мы все еще находимся на странице ЮНИТА
    // ставим зп такую чтоб квалификация рабов была максимальная и держала топ-1 на 100%
    public SalaryPage autoSetSalaryAndQa() throws InterruptedException {
        String localPage = driver.getCurrentUrl();
        String unitId = getUnitIdByUrl(localPage);
        double qaPlayer = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Квалификация игрока']]/td[2]")).getText().replaceAll("\\D", ""));
        double pageQtySlave = 0.0;
        if(driver.findElements(By.xpath("//tr[td[text()='Количество сотрудников']]/td[2]")).size()>0)
            pageQtySlave = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество сотрудников']]/td[2]")).getText().replaceAll(" ","").split("\\(")[0]);
        else
            pageQtySlave = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество рабочих']]/td[2]")).getText().replaceAll(" ","").split("\\(")[0]);

        if(pageQtySlave==0){
            logMe("No Slave - no Problem!");
            return new SalaryPage(driver);
        }

        double brilliantQA = calcQualTop1(qaPlayer,pageQtySlave);
        double salary =0.0;
        //logMe("Стремимся к "+ brilliantQA);

        //если все ок - уходим, нам нечего тут делать!
        String qaSlave = driver.findElement(By.xpath("//tr[td[text()='Уровень квалификации сотрудников']]/td[2]")).getText().split(" ")[0];
        if(Double.valueOf(qaSlave) == brilliantQA){
            logMe("OK! Nothing to do here!");
            driver.get(localPage);
            return new SalaryPage(driver);
        }

        // Иначе идем и устанавливаем нужную квалу!
        driver.get("http://virtonomica.ru/vera/window/unit/employees/engage/"+unitId);
        WebElement qtySlave = driver.findElement(By.id("quantity"));
        WebElement salarySlave = driver.findElement(By.id("salary"));
        WebElement anoterElement = driver.findElement(By.xpath("//th[contains(text(),'Зарплата')]"));
        WebElement waitElement = driver.findElement(By.xpath("//*[@alt='loading indicator' and @class = 'invisible']"));
        WebElement slaveQaLocal = driver.findElement(By.id("apprisedEmployeeLevel"));
        double slaveQaNeed = Double.valueOf(driver.findElement(By.xpath("//span[contains(text(),'в среднем по городу')]")).getText().split("требуется ")[1].replaceAll("\\)",""));

        double prevQa=0.00;
        boolean tooHugeToContimue=false; //если при увеличении цены квала уже не меняется - выходить из этого цикла
        int counter=0;
        logMe("brilliant: "+brilliantQA);
        // если текущая квала больше идеальной уменьшаем ЗП
        if(Double.valueOf(slaveQaLocal.getText())>brilliantQA){
            //logMe("Уменьшаем ЗП!");
            while(Double.valueOf(slaveQaLocal.getText())!=brilliantQA){
                if(!slaveQaLocal.getText().equals("0"))
                    if(Double.valueOf(slaveQaLocal.getText())>brilliantQA){
                        if(Double.valueOf(slaveQaLocal.getText())==brilliantQA)
                            break;
                        salary = Double.valueOf(salarySlave.getAttribute("value"));
                        salary-=5.0;
                        salarySlave.clear();
                        salarySlave.sendKeys(String.valueOf(salary));
                        //logMe("(-1) salary: "+salary+" qa: "+slaveQaLocal.getText());
                        anoterElement.click();
                        waitForElement("//*[@class = 'invisible']");
                        counter=0;
                    }
                    else{
                        if(Double.valueOf(slaveQaLocal.getText())==brilliantQA)
                            break;
                        salary = Double.valueOf(salarySlave.getAttribute("value"));
                        salary+=0.1;
                        salarySlave.clear();
                        salarySlave.sendKeys(String.valueOf(salary));
                        //logMe("(+0.1) salary: " + salary + " qa: " + slaveQaLocal.getText());
                        anoterElement.click();
                        waitForElement("//*[@class = 'invisible']");
                        counter=0;
                    }
                else {
                    //logMe("Wait because ZERO!");
                    anoterElement.click();
                    Thread.sleep(1000);
                    counter++;

                    if(counter>3){
                        //logMe("FUCK");
                        salarySlave.clear();
                        salarySlave.sendKeys(String.valueOf(salary));
                        //logMe("salary: "+salary+" qa: "+slaveQaLocal.getText());
                        anoterElement.click();
                        waitForElement("//*[@class = 'invisible']");
                        counter=0;
                    }
                }
            }
            driver.findElement(By.xpath("//input[@type='submit']")).click();
        }// если текущая квала меньше идеальной увеличиваем ЗП
        else{
            //logMe("Увеличиваем ЗП!");
            while(Double.valueOf(slaveQaLocal.getText())!=brilliantQA || !tooHugeToContimue){
                if(!(slaveQaLocal.getText().equals("0")))
                    if(Double.valueOf(slaveQaLocal.getText())<brilliantQA){
                        //logMe("current QA: "+slaveQaLocal.getText()+" prevQa: "+prevQa+" brilliant: "+brilliantQA);
                        if(Double.valueOf(slaveQaLocal.getText())==brilliantQA)
                            break;

                        if(Double.valueOf(slaveQaLocal.getText())==prevQa)
                            tooHugeToContimue=true;
                        prevQa = Double.valueOf(slaveQaLocal.getText());
                        salary = Double.valueOf(salarySlave.getAttribute("value"));
                        salary+=5.0;
                        salarySlave.clear();
                        salarySlave.sendKeys(String.valueOf(salary));
                        //logMe("(+1) salary: "+salary+" qa: "+slaveQaLocal.getText());
                        anoterElement.click();
                        waitForElement("//*[@class = 'invisible']");
                        counter=0;
                    }
                    else{
                        if(Double.valueOf(slaveQaLocal.getText())==brilliantQA)
                            break;
                        salary = Double.valueOf(salarySlave.getAttribute("value"));
                        salary-=0.1;
                        salarySlave.clear();
                        salarySlave.sendKeys(String.valueOf(salary));
                        //logMe("(-0.1) salary: " + salary + " qa: " + slaveQaLocal.getText());
                        anoterElement.click();
                        waitForElement("//*[@class = 'invisible']");
                        counter=0;
                    }
                else {
                    //logMe("Wait because ZERO!");
                    anoterElement.click();
                    Thread.sleep(1000);
                    counter++;

                    if(counter>3){
                        //logMe("FUCK");
                        salarySlave.clear();
                        salarySlave.sendKeys(String.valueOf(salary));
                        //logMe("salary: "+salary+" qa: "+slaveQaLocal.getText());
                        anoterElement.click();
                        waitForElement("//*[@class = 'invisible']");
                        counter=0;
                    }
                }
            }
            driver.findElement(By.xpath("//input[@type='submit']")).click();
        }
        driver.get(localPage);
     return new SalaryPage(driver);
    }

    public SalaryPage autoSetSalaryAndQaFormula() throws InterruptedException {

        Double Qreq=15.73;    // Квалификация которую мы хотим добиться+
        Double Q=10.79;      // текущая квалификация рабочих
        Double Sreq=0.0;     // зп которую будем считать!
        Double S=61.50;     // текущая зп
        Double Savg=49.28;  // средняя зп по городу
        Double Qavg=4.75;  // Средняя квалификация по городу

        //logMe("Поставьте зарплату: "+Sreq);

        String localPage = driver.getCurrentUrl();
        String unitId = getUnitIdByUrl(localPage);
        double qaPlayer = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Квалификация игрока']]/td[2]")).getText().replaceAll("\\D", ""));
        double pageQtySlave = 0.0;
        if(driver.findElements(By.xpath("//tr[td[text()='Количество сотрудников']]/td[2]")).size()>0)
            pageQtySlave = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество сотрудников']]/td[2]")).getText().replaceAll(" ","").split("\\(")[0]);
        else if(driver.findElements(By.xpath("//tr[td[text()='Количество рабочих']]/td[2]")).size()>0)
            pageQtySlave = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество рабочих']]/td[2]")).getText().replaceAll(" ","").split("\\(")[0]);
        else
            pageQtySlave = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество работников']]/td[2]")).getText().replaceAll(" ","").split("\\(")[0].replaceAll("ед.",""));

        if(pageQtySlave==0){
            logMe("No Slave - no Problem!");
            return new SalaryPage(driver);
        }
        logMe(qaPlayer+" "+pageQtySlave);
        double brilliantQA = calcQualTop1(qaPlayer,pageQtySlave);
        Qreq=brilliantQA;
        double salary =0.0;
        //logMe("Стремимся к "+ brilliantQA);

        //если все ок - уходим, нам нечего тут делать!
        String qaSlave = "";
        if(driver.findElements(By.xpath("//tr[td[text()='Уровень квалификации работников']]/td[2]")).size()>0)
            qaSlave = driver.findElement(By.xpath("//tr[td[text()='Уровень квалификации работников']]/td[2]")).getText().split(" ")[0];
        else
            qaSlave = driver.findElement(By.xpath("//tr[td[text()='Уровень квалификации сотрудников']]/td[2]")).getText().split(" ")[0];
        Q=Double.valueOf(qaSlave);
        if(Double.valueOf(qaSlave) == brilliantQA){
            logMe("OK! Nothing to do here!");
            driver.get(localPage);
            return new SalaryPage(driver);
        }
        // текущая зп
        if(driver.findElements(By.xpath("//tr[td[text()='Зарплата одного сотрудника']]/td[2]")).size()>0)
            S = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Зарплата одного сотрудника']]/td[2]")).getText().split("\\$")[0].replaceAll(" ",""));
        else if(driver.findElements(By.xpath("//tr[td[text()='Зарплата рабочих']]/td[2]")).size()>0)
            S = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Зарплата рабочих']]/td[2]")).getText().split("\\$")[0].replaceAll(" ",""));
        else if(driver.findElements(By.xpath("//tr[td[text()='Зарплата работников']]/td[2]")).size()>0)
            S = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Зарплата работников']]/td[2]")).getText().split("\\$")[0].replaceAll(" ",""));

        // средняя зп по городу
        if(driver.findElements(By.xpath("//tr[td[text()='Зарплата одного сотрудника']]/td[2]")).size()>0)
            Savg=Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Зарплата одного сотрудника']]/td[2]")).getText().replaceAll(":","").split("в среднем по городу ")[1].replaceAll("\\)","").replaceAll("\\$","").replaceAll(" ",""));
        else if(driver.findElements(By.xpath("//tr[td[text()='Зарплата рабочих']]/td[2]")).size()>0)
            Savg=Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Зарплата рабочих']]/td[2]")).getText().replaceAll(":","").split("в среднем по городу ")[1].replaceAll("\\)","").replaceAll("\\$","").replaceAll(" ",""));
        else if(driver.findElements(By.xpath("//tr[td[text()='Зарплата работников']]/td[2]")).size()>0)
            Savg=Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Зарплата работников']]/td[2]")).getText().replaceAll(":","").split("в среднем по городу ")[1].replaceAll("\\)","").replaceAll("\\$","").replaceAll(" ",""));
        else assertTrue(false);

        // Средняя квалификация по городу
        if(driver.findElements(By.xpath("//tr[td[text()='Уровень квалификации сотрудников']]/td[2]")).size()>0)
            Qavg=Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Уровень квалификации сотрудников']]/td[2]"))
                .getText().split(",")[0].replaceAll(":","").split("по городу ")[1].replaceAll("\\)", "").replaceAll("\\$", "").replaceAll(" ", ""));
        else
            Qavg=Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Уровень квалификации работников']]/td[2]"))
                    .getText().split(",")[0].replaceAll(":","").split("по городу ")[1].replaceAll("\\)", "").replaceAll("\\$", "").replaceAll(" ", ""));

        // АЛГОРИТМ
        Double b=0.0;
        Double k = S/Savg;
        Double k1 = k*k;
        if(k <= 1){
            b = (Q/k1);
            Sreq = Math.sqrt(Qreq/b)*Savg;
            // если зарплата превысила среднею
            if( Sreq/Savg > 1){
                b = b / Qavg;
                b = 2 * Math.pow(0.5, b);
                Sreq = (Math.pow(2, Qreq/Qavg)*b - 1)*Savg;
            }
        } else {
            b = (k+1)/Math.pow(2, Q/Qavg);
            Sreq = (Math.pow(2, Qreq/Qavg)*b - 1)*Savg;
            // если зарплата стала меньше средней;
            if(Sreq/Savg < 1){
                b = Qavg * Math.log(b/2)/Math.log(0.5);
                Sreq = Math.sqrt(Qreq/b)*Savg;
            }
        }
        // блокировка от потери обученности
        if (Sreq/Savg <= 0.80){
            Sreq = Math.floor(0.80 * Savg) + 1;
        }

        //logMe(" Поставить "+ Sreq);
        // АЛГОРИТМ end

        // Иначе идем и устанавливаем нужную квалу!
        driver.get("http://virtonomica.ru/vera/window/unit/employees/engage/"+unitId);
        WebElement qtySlave = driver.findElement(By.id("quantity"));
        WebElement salarySlave = driver.findElement(By.id("salary"));
        WebElement anoterElement = driver.findElement(By.xpath("//th[contains(text(),'Зарплата')]"));
        WebElement waitElement = driver.findElement(By.xpath("//*[@alt='loading indicator' and @class = 'invisible']"));
        WebElement slaveQaLocal = driver.findElement(By.id("apprisedEmployeeLevel"));
        double slaveQaNeed = Double.valueOf(driver.findElement(By.xpath("//span[contains(text(),'в среднем по городу')]")).getText().split("требуется ")[1].replaceAll("\\)",""));

        double prevQa=0.00;
        boolean tooHugeToContimue=false; //если при увеличении цены квала уже не меняется - выходить из этого цикла
        int counter=0;
        logMe("brilliant: "+brilliantQA);
        // если текущая квала больше идеальной уменьшаем ЗП

        //logMe("Уменьшаем ЗП!");
        while(Double.valueOf(slaveQaLocal.getText())!=brilliantQA) {
            if (!slaveQaLocal.getText().equals("0")){
                if (Double.valueOf(slaveQaLocal.getText()) == brilliantQA)
                    break;
                salarySlave.clear();
                salarySlave.sendKeys(String.valueOf(Sreq));
                anoterElement.click();
                waitForElement("//*[@class = 'invisible']");
                counter++;
                if(counter>2){
                    break;
                }
            }
            else {
                //logMe("Wait because ZERO!");
                anoterElement.click();
                Thread.sleep(1000);
                counter++;

                if(counter>3){
                    //logMe("FUCK");
                    salarySlave.clear();
                    salarySlave.sendKeys(String.valueOf(Sreq));
                    anoterElement.click();
                    waitForElement("//*[@class = 'invisible']");
                    counter=0;
                    break;
                }
            }
        }
        driver.findElement(By.xpath("//input[@type='submit']")).click();


        driver.get(localPage);
        return new SalaryPage(driver);
    }
}

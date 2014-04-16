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
}

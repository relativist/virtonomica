package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
            driver.findElement(By.xpath("//input[@value='Обучить']")).click();
            driver.get(currentUrl);
        }
        return new PlantPage(driver);
    }

    public PlantPage supply(){
        driver.findElement(By.xpath("//a[text()='Снабжение']"));
        for(int i =0; i<driver.findElements(By.xpath("//tr[contains(@id,'product_row')]//a[@title]")).size();i++){
            logMe("Title: "+driver.findElements(By.xpath("//tr[contains(@id,'product_row')]//a[@title]")).get(i).getAttribute("title"));
            logMe("Need: " + driver.findElement(By.xpath("//tr[contains(@id,'product_row')]/td//td[2]")).getText().replaceAll(" ", ""));
            logMe("Have: "+driver.findElement(By.xpath("//tr[contains(@id,'product_row')]/td[2]//td[2]")).getText().replaceAll(" ",""));

            logMe("Offer: "+driver.findElement(By.xpath("//tr[contains(@id,'product_row')]/td[2]//td[2]")).getText().replaceAll(" ",""));


        }

        driver.findElement(By.xpath("//a[text()='Завод']"));
        return new PlantPage(driver);
    }


    public PlantPage setAutoQaSlave() throws InterruptedException {
        new SalaryPage(driver).autoSetSalaryAndQa();
        return new PlantPage(driver);
    }



    private boolean isNeedtoEducate(){
        String salarySlave = driver.findElement(By.xpath("//tr[td[text()='Зарплата рабочих']]/td[2]")).getText().split("\\$")[0];
        String salaryTown  = driver.findElement(By.xpath("//tr[td[text()='Зарплата рабочих']]/td[2]")).getText().split("городу ")[1].replaceAll("\\$\\)","");
        logMe(salarySlave);
        logMe(salaryTown);
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

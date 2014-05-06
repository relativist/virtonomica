package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by rest on 3/7/14.
 */
public class AnimalFermPage extends Page {
    public AnimalFermPage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    public AnimalFermPage educate(){
        if(isStuding() && isNeedtoEducate()){
            logMe("Обучаю персонал");
            String currentUrl = driver.getCurrentUrl();
            String UnitId = getUnitIdByUrl(currentUrl);
            driver.get("http://virtonomica.ru/vera/window/unit/employees/education/"+UnitId);
            if(driver.findElements(By.xpath("//input[@value='Обучить']")).size()>0)
                driver.findElement(By.xpath("//input[@value='Обучить']")).click();
            driver.get(currentUrl);
        }
        return new AnimalFermPage(driver);
    }

    public boolean isDepProcessed(String dep){
        Connection c = null;
        Statement stmt = null;
        boolean result=false;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:plant.db");
            c.setAutoCommit(false);
            //System.out.println("Opened database successfully");

            int session = Integer.valueOf(formattedDate("MMdd"));
            stmt = c.createStatement();
            String sql = "select count(*) from plant where session="+session+" and depurl='"+dep+"';";
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

    public void recordDepartment(String departmentURL){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:plant.db");
            c.setAutoCommit(false);
            //System.out.println("Opened database successfully");

            int session = Integer.valueOf(formattedDate("MMdd"));
            String depName =  driver.findElement(By.xpath("//div[@id='headerInfo']/h1")).getText();
            String depUrl = driver.getCurrentUrl();


            stmt = c.createStatement();
            String sql = "INSERT INTO PLANT (SESSION,DEPURL) " +
                    "VALUES (" +
                    session +
                    ",'"+depUrl +"'"+
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

   public AnimalFermPage finans(){
       driver.findElement(By.xpath("//a[text()='Финансовый отчёт']")).click();
       double profit = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Прибыль']]/td[2]")).getText().replaceAll(" ","").replaceAll("\\$",""));

       if(profit>0){
           new HelpPage(driver).recordReport(driver.getCurrentUrl(),"Profit = "+profit);
           logMe("Profit = "+profit);
       }
       driver.findElement(By.xpath("//a[text()='Ферма']")).click();
       return new AnimalFermPage(driver);
   }


    /*
    1. если на складе больше в три раза чем требуется. обнуляем оффер. ждем
    2. если у поставщика меньше чем два моих требования - бить тревогу
    3. если на складе меньше двух требования и больше одного - перезаказать сумму
    */
    public AnimalFermPage supply(){
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

                if(Integer.valueOf(have)>3*Integer.valueOf(need)){
                    if(!offer.equals("0")){
                        driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[4]//input")).clear();
                        driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[4]//input")).sendKeys("0");
                        change=true;
                    }
                    continue;
                }
                if(sklad.equals("Неогр."))
                    sklad="100000000";
                if(Integer.valueOf(sklad)<2*Integer.valueOf(need)) {
                    error += " Поставщик обосрётся.";
                    new HelpPage(driver).recordReport(driver.getCurrentUrl(),"Завод. поставщик имеет мало товара на складе: "+title);
                }

                if(Integer.valueOf(have)<2*Integer.valueOf(need)){
                    driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[4]//input")).clear();
                    driver.findElement(By.xpath("//tr[contains(@id,'product_row')]["+(i+1)+"]/td[4]//input")).sendKeys(Double.valueOf(need)*1.05+"");
                    change=true;
                }



                if(!error.equals(""))
                    logMe(title+"\t\t"+error);
            }
            else {
                logMe("ERROR          Нет поставщика "+title);
                new HelpPage(driver).recordReport(driver.getCurrentUrl(),"Завод. Нет поставщика :"+title);
            }




        }

        if(change){
            driver.findElement(By.name("applyChanges")).click();
            change=false;
        }

        driver.findElement(By.xpath("//a[text()='Ферма']")).click();
        return new AnimalFermPage(driver);
    }

    public AnimalFermPage supplyProductsWithSuppliers(){
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
                    new HelpPage(driver).recordReport(driver.getCurrentUrl(),"У нас нет подходящего саплаера для продукта "+title);
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
        return new AnimalFermPage(driver);
    }

    public AnimalFermPage setAutoQaSlave() throws InterruptedException {
        //new SalaryPage(driver).autoSetSalaryAndQa();
        new SalaryPage(driver).autoSetSalaryAndQaFormula();
        return new AnimalFermPage(driver);
    }



    private boolean isNeedtoEducate(){
        String salarySlave = driver.findElement(By.xpath("//tr[td[text()='Зарплата работников']]/td[2]")).getText().split("\\$")[0].replaceAll(" ","");
        String salaryTown  = driver.findElement(By.xpath("//tr[td[text()='Зарплата работников']]/td[2]")).getText().split("городу ")[1].replaceAll("\\$\\)","").replaceAll(" ", "");
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

    public AnimalFermPage getInfo(){
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

        return new AnimalFermPage(driver);
    }

}

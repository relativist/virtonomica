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
public class SciencePage extends Page {
    public SciencePage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    public SciencePage educate(){
        if(isStuding() && !isNeedtoEducate()){
            logMe("Обучаю персонал");
            String currentUrl = driver.getCurrentUrl();
            String UnitId = getUnitIdByUrl(currentUrl);
            driver.get("http://virtonomica.ru/vera/window/unit/employees/education/"+UnitId);
            if(driver.findElements(By.xpath("//input[@value='Обучить']")).size()>0)
                driver.findElement(By.xpath("//input[@value='Обучить']")).click();
            driver.get(currentUrl);
        }
        return new SciencePage(driver);
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

    public SciencePage setAutoQaSlave() throws InterruptedException {
        new SalaryPage(driver).autoSetSalaryAndQaFormula();
        return new SciencePage(driver);
    }

    private boolean isNeedtoEducate(){
        String salarySlave = driver.findElement(By.xpath("//tr[td[text()='Зарплата учёных']]/td[2]")).getText().split("\\$")[0].replaceAll(" ","");
        String salaryTown  = driver.findElement(By.xpath("//tr[td[text()='Зарплата учёных']]/td[2]")).getText().split("городу ")[1].replaceAll("\\$\\)","").replaceAll(" ", "");
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


    public SciencePage research(){
        driver.findElement(By.xpath("//a[text()='Исследования']")).click();


        //идут ли исследования:
        if(driver.findElements(By.xpath("//a[text()='Остановить проект']")).size()>0){

        }

        driver.findElement(By.xpath("//a[text()='Лаборатория']")).click();
        return new SciencePage(driver);
    }

    //закупаем максимум оборудования
    public SciencePage autoBuyEq(String qaEq){
        double eqHave = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество оборудования']]/td[2]")).getText().split(":")[0].replaceAll("\\D", ""));
        double eqNeed = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество оборудования']]/td[2]")).getText().split(":")[1].replaceAll("\\D",""));
        logMe(eqHave+" "+eqNeed);
        if(eqHave == 0.0){
            String handle1 = driver.getWindowHandle();
            driver.findElement(By.xpath("//a[text()='Оборудование']")).click();
            Set<String> handles=driver.getWindowHandles();
            Iterator<String> it =handles.iterator();
            while (it.hasNext()) {
                String popupHandle = it.next().toString();
                if (!popupHandle.contains(handle1)) {
                    driver.switchTo().window(popupHandle);
                    //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                }
            }

            if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() !=0)
                if (driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).isDisplayed() )
                    driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();

            //Задаем фильтр нашего поиска продуктов.
            driver.findElement(By.id("filterLegend")).click();
            driver.findElement(By.id("quality_isset")).click();
            driver.findElement(By.id("quantity_isset")).click();

            driver.findElement(By.name("quality[from]")).clear();
            driver.findElement(By.name("quality[from]")).clear();
            driver.findElement(By.name("quality[from]")).sendKeys(qaEq);

            driver.findElement(By.name("quantity[from]")).clear();
            driver.findElement(By.name("quantity[from]")).clear();
            driver.findElement(By.name("quantity[from]")).sendKeys(String.valueOf(eqNeed));

            driver.findElement(By.xpath("//input[@class='button160']")).click();

            String goodId = driver.findElement(By.xpath("//table[@class='list main_table']/tbody/tr/td[9]/span")).getAttribute("id");
            ((JavascriptExecutor) driver).executeScript("document.getElementById(" + goodId + ").click();");

            driver.findElement(By.id("amountInput")).sendKeys(String.valueOf(eqNeed));
            ((JavascriptExecutor) driver).executeScript("document.getElementById('submitLink').click();");
            driver.findElement(By.xpath("//span[text()='Закрыть окно']")).click();
            driver.switchTo().window(handle1);
        }
        return new SciencePage(driver);
    }

    public int repairIt(){
        return new RepairPage(driver).repairIt();
    }

    //нанимаем максимум людей
    public SciencePage autoBuyEmployee() throws InterruptedException {
        waitForElement("//tr[td[text()='Количество учёных']]/td[2]");
        double eqHave = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество учёных']]/td[2]")).getText().split(":")[0].replaceAll("\\D", ""));
        double eqNeed = Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Количество учёных']]/td[2]")).getText().split(":")[1].replaceAll("\\D",""));
        logMe(eqHave+" "+eqNeed);
        if(eqHave == 0.0){
            String handle1 = driver.getWindowHandle();
            driver.findElement(By.xpath("//a[text()='Сотрудники и зарплата']")).click();
            Set<String> handles=driver.getWindowHandles();
            Iterator<String> it =handles.iterator();
            while (it.hasNext()) {
                String popupHandle = it.next().toString();
                if (!popupHandle.contains(handle1)) {
                    driver.switchTo().window(popupHandle);
                    //System.out.println("Pop Up Title: " + driver.switchTo().window(popupHandle).getTitle());
                }
            }

            driver.findElement(By.name("unitEmployeesData[quantity]")).sendKeys(String .valueOf(eqNeed));
            waitForElement("//*[@class = 'invisible']");
            driver.findElement(By.xpath("//input[@class='button160']")).click();
            driver.switchTo().alert().accept();
            driver.switchTo().window(handle1);
        }


        return new SciencePage(driver);
    }

}

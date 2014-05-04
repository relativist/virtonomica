package help;


import general.virt.HelpPage;
import general.virt.LoginPage;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class GetFitnesMarket extends HelpPage {

    public boolean isMyProduct(ArrayList<String> wProducts,String product){
        for(int i=0; i<wProducts.size();i++){
            if(wProducts.get(i).split(";")[0].equals(product))
                if(wProducts.get(i).split(";")[3].equals("my"))
                    return true;
        }
        return false;
    }

//    @Override
//    protected void setUp() throws Exception {
//
//    }

    @Override
    protected void tearDown() throws Exception {

    }

    @Test
    public void test() throws Throwable {
        new LoginPage(driver).openVirtUrl().login();
        driver.get("http://virtonomica.ru/vera/main/globalreport/marketing/by_service/348207");
        //Select s1 = new Select(driver.findElement(By.id("__product_category_list")));
        String outputString="";

//        Диагностический центр
//        Поликлиника
//        Стоматологическая клиника
//        Центр народной медицины
//        Больница

        File file = new File("fitnes.db");
        if(!file.exists()) {
            logMe("creating new database table!");
            new CreateDB().createFitnesDb();
        }

        String health ="";
        String tanz ="";
        String fitnes ="";
        String city = "";
        String price = "";
        String volume ="";
        String count ="";
        int counter=1;

        //table/tbody/tr[td[text()='Больница']]/td[5]

        int seconds=1000;
        String secondString=new String();
        String thirdString=new String();

        Select s1 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[1]/select")));
        for(int i=1; i<s1.getOptions().size(); i++){
            s1.selectByIndex(i);
            s1 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[1]/select")));
            Thread.sleep(seconds);
            outputString=s1.getFirstSelectedOption().getText()+";";
            Select s2 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[3]/select")));
            for(int j=1; j<s2.getOptions().size(); j++){
                s2.selectByIndex(j);
                s2 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[3]/select")));
                Thread.sleep(seconds);
                Select s3 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[5]/select")));

                secondString="";
                secondString+=outputString+s2.getFirstSelectedOption().getText()+";";
                for(int k=1; k<s3.getOptions().size(); k++){
                    s3.selectByIndex(k);
                    Thread.sleep(seconds);
                    s3 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[5]/select")));
                    city=s3.getFirstSelectedOption().getText();
                    thirdString=secondString+city+";";

                    if(driver.findElements(By.xpath("//table/tbody/tr[td[text()='Группы здоровья']]/td[5]")).size()==0)
                        health="0";
                    else
                        health = driver.findElement(By.xpath("//table/tbody/tr[td[text()='Группы здоровья']]/td[5]")).getText().split(" ")[0];

                    if(driver.findElements(By.xpath("//table/tbody/tr[td[text()='Танцы']]/td[5]")).size()==0)
                        tanz="0";
                    else
                        tanz = driver.findElement(By.xpath("//table/tbody/tr[td[text()='Танцы']]/td[5]")).getText().split(" ")[0];

                    if(driver.findElements(By.xpath("//table/tbody/tr[td[text()='Фитнес']]/td[5]")).size()==0)
                        fitnes="0";
                    else
                        fitnes = driver.findElement(By.xpath("//table/tbody/tr[td[text()='Фитнес']]/td[5]")).getText().split(" ")[0];

                    price = driver.findElement(By.xpath("//td[contains(text(),'Цена')]/b")).getText().replaceAll(" ","").replaceAll("\\$","");
                    volume = driver.findElement(By.xpath("//td[contains(text(),'Объем рынка')]/b")).getText().replaceAll(" ","").replaceAll("\\$","");
                    count = driver.findElement(By.xpath("//td[contains(text(),'Кол-во подразделений')]/b")).getText().replaceAll(" ","").replaceAll("\\$","");


//                    logMe(bol);
//                    logMe(dc);
//                    logMe(pol);
//                    logMe(stomk);
//                    logMe(cnm);

                    logMe(counter+")"+thirdString);
                    counter++;


                    //logMe(thirdString);

                    recordFitnes(city,health,tanz,fitnes,price,volume,count);

                    //assertTrue(false);
                }
                s2 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[3]/select")));
            }
            s1 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[1]/select")));
        }
    }

}


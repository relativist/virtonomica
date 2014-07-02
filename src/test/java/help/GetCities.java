package help;


import general.Page;
import general.virt.LoginPage;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class GetCities extends Page {

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
        driver.get("http://virtonomica.ru/vera/main/globalreport/marketing/by_trade_at_cities");
        Select s1 = new Select(driver.findElement(By.id("__product_category_list")));
        String outputString="";
//        for(int i=0; i<9; i++){
//            s1.selectByIndex(i);
//            outputString=s1.getFirstSelectedOption().getText();
//            for(int j=0; j<driver.findElements(By.xpath("//*[@id='__products_list']/span//img")).size();j ++){
//                outputString+=";"+driver.findElements(By.xpath("//*[@id='__products_list']/span//img")).get(j).getAttribute("title");
//            }
//            s1 = new Select(driver.findElement(By.id("__product_category_list")));
//            logMe(outputString);
//
//        }
        int seconds=1000;
        String secondString=new String();
        String thirdString=new String();

        s1 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[1]/select")));
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
                    thirdString=secondString+s3.getFirstSelectedOption().getText()+";";
                    logMe(thirdString);
                }
                s2 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[3]/select")));
            }
            s1 = new Select(driver.findElement(By.xpath("//fieldset/table[2]//td[1]/select")));
        }
    }
}


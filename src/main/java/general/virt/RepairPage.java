package general.virt;

import general.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;

/**
 * Created by rest on 3/7/14.
 */
public class RepairPage extends Page {
    public RepairPage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }
    protected   String version="1.0";
    protected  Double fix=0.2;
    protected Double eqNeed=0.0;

    protected void doRepair(int line, Double num){
        String SelecterId=driver.findElements(By.xpath("//*[@id='mainTable']/tbody/tr[@class]//td[9]//span")).get(line).getAttribute("id");
        //driver.execute_script("document.getElementById(" + SelecterId + ").click()")
        ((JavascriptExecutor) driver).executeScript("document.getElementById(" + SelecterId + ").click();");

        try {
            logMe("try to send "+num);
            driver.findElement(By.id("amountInput")).clear();
            logMe("send " + num + " success");
            driver.findElement(By.id("amountInput")).sendKeys(String.valueOf(num));
            //driver.execute_script('document.getElementById("repair_button").click()')
            ((JavascriptExecutor) driver).executeScript("document.getElementById('repair_button').click();");
        }catch (WebDriverException e){
            //System.out.println(e.getMessage());
            logMe("fuck");
        }

    }



    public int repairIt(){
        String DepartmentType="";
        //logMe("repair verion: " + version);
        Double EQ=0.0;
        double toRepair=0.0;

        if(driver.findElement(By.xpath("//div[@class='officePlace']")).getText().split(" ")[0].equals("Ресторан")){
            DepartmentType="Restaurant";
            EQ=Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Качество оборудования']]/td[2]")).getText());
            eqNeed=EQ;
            toRepair=Integer.valueOf(driver.findElement(By.xpath("//tr[td[text()='Износ оборудования']]/td[2]//td[2]")).getText().split(" % ")[1].split("\\+")[0].replaceAll("\\D+",""))+1;
            fix=0.2;
        }
        else if (driver.findElement(By.xpath("//div[@class='officePlace']")).getText().split(" ")[0].equals("Офис")){
            DepartmentType="Office";
            EQ=Double.valueOf(driver.findElement(By.xpath("//tr[td[text()='Качество компьютеров']]/td[2]")).getText());
            eqNeed=EQ;
            toRepair=Integer.valueOf(driver.findElement(By.xpath("//tr[td[text()='Износ компьютеров']]/td[2]//td[2]")).getText().split(" % ")[1].split("\\+")[0].replaceAll("\\D+",""))+1;
            fix=0.2;
        }
        else{
            String tempo1 = driver.findElement(By.xpath("//tr[td[text()='Качество оборудования']]/td[2]")).getText();
            fix=0.2;
            eqNeed= Double.valueOf(tempo1.replaceAll(".*требуется по технологии ","").replaceAll("\\)",""));
            if(driver.findElements(By.xpath("//tr[td[text()='Износ оборудования']]/td[2]//td[2]")).size()==0){
                logMe("Нет оборудования");
                return 0;
            }
            String tempos= driver.findElement(By.xpath("//tr[td[text()='Износ оборудования']]/td[2]//td[2]")).getText();
            //logMe(tempos);
            tempos=tempos.split(" % ")[1];
            //logMe(tempos);
            tempos=tempos.split("\\+")[0];
            //logMe(tempos);
            tempos=tempos.replaceAll("\\D+","");
            //logMe(tempos);

            toRepair=Integer.valueOf(driver.findElement(By.xpath("//tr[td[text()='Износ оборудования']]/td[2]//td[2]")).getText().split(" % ")[1].split("\\+")[0].replaceAll("\\D+",""))+1;
            //toRepair=Integer.valueOf(tempos)+1;
        }
        logMe("NEED: " + eqNeed);
        //logMe("ToRepair: " + toRepair);

        String page=getUnitIdByUrl(driver.getCurrentUrl());

        //logMe(" " + page);
        driver.get("http://virtonomica.ru/vera/window/unit/equipment/"+page);

        if (driver.findElements(By.xpath("//a[contains(text(),'Отменить фильтр')]")).size() !=0)
            driver.findElement(By.xpath("//a[contains(text(),'Отменить фильтр')]")).click();

        //Задаем фильтр нашего поиска оборудования.
        driver.findElement(By.id("filterLegend")).click();
        driver.findElement(By.id("quality_isset")).click();

        driver.findElement(By.name("quality[from]")).clear();
        driver.findElement(By.name("quality[from]")).sendKeys(String.valueOf(eqNeed-3));
        driver.findElement(By.name("quality[to]")).clear();
        driver.findElement(By.name("quality[to]")).sendKeys(String.valueOf(eqNeed+3));

        if(DepartmentType.equals("Office")){
            driver.findElement(By.name("quantity[isset]")).click();
            driver.findElement(By.name("quantity[from]")).clear();
            driver.findElement(By.name("quantity[from]")).sendKeys(String.valueOf(toRepair));
        }


        driver.findElement(By.xpath("//input[@class='button160']")).click();

        ArrayList<Double> mass= new ArrayList<Double>();
        for(int i=0; i<driver.findElements(By.xpath("//*[@id='mainTable']/tbody/tr[@class]//td[8]")).size(); i++)
            mass.add(Double.valueOf(driver.findElements(By.xpath("//*[@id='mainTable']/tbody/tr[@class]//td[8]")).get(i).getText()));

        driver.findElement(By.id("tabRepair")).click();

        //# ОСНОВНАЯ ЧАСТЬ ПОЧИНКИ ОБОРУДОВАНИЯ

        logMe("ToRepair: "+toRepair);

        if(toRepair<3){
            logMe("ERROR! No NEED REPAIR! WTF?!?");
            driver.get("http://virtonomica.ru/vera/main/unit/view/"+page);
            return 0;
        }


        int alone=0;
        int first=0;
        int second=0;
        int third=0;
        int threeNumber=0;

        logMe("Need: " + eqNeed);

        double found1=0.0;
        int meet=0;

        alone=-1;

        for(int i=0; i<mass.size(); i++){
            if (eqNeed-0.1 < mass.get(i)&& mass.get(i) < eqNeed+fix && mass.get(i)>=eqNeed){
                logMe("1. Repair with "+String.valueOf(mass.get(i)));
                alone=i;
                doRepair(alone,toRepair);
                break;
            }
        }

        Double finalOFtwo=0.0;
        Double best=0.0;
        Double final1=0.0;
        Double final2=0.0;
        int repairIid0=0;
        int repairJid0=0;
        Double diver=0.0;

        if(alone==-1){
            finalOFtwo=0.0;
            found1=0.0;
            meet=0;
            best=100.0;
            final1=0.0;
            final2=0.0;
            repairIid0=0;
            repairJid0=0;
            Double element=0.0;
            for(int i=0; i<mass.size(); i++){
                element=mass.get(i);
                for(int j=0; j<mass.size(); j++){
                    if(j<=i)
                        continue;

                    diver=eqNeed-(mass.get(i)+mass.get(j))/2;
                    if(diver<0)
                        diver=-diver;

                    if(best>diver && (mass.get(i)+mass.get(j))/2 >= eqNeed){
                        best=diver;
                        final1=mass.get(i);
                        final2=mass.get(j);
                        repairIid0=i;
                        repairJid0=j;
                    }
                }
            }
            logMe("2. Repair with "+String.valueOf((final1+final2)/2)+" "+final2+" "+final1);
            finalOFtwo=(final1+final2)/2;


            Double alpha=0.0;
            found1=0.0;
            meet=0;
            best=100.0;
            final1=0.0;
            final2=0.0;
            int repairIid1=0;
            int repairJid1=0;
            for(int i=0; i<mass.size(); i++){
                element=mass.get(i);
                for(int j=0; j<mass.size(); j++){
                    if(j<=i)
                        continue;
                    diver=eqNeed-(mass.get(i)*2+mass.get(j))/3;
                    if(diver<0)
                        diver=-diver;

                    if(best>diver && (mass.get(i)*2+mass.get(j))/3 >= eqNeed){
                        best=diver;
                        final1=mass.get(i)*2;
                        final2=mass.get(j);
                        repairIid1=i;
                        repairJid1=j;
                    }
                }
            }
            logMe("3. Repair with "+String.valueOf((final1+final2)/3)+" "+final2+" "+final1);
            alpha=(final1+final2)/3;

            Double betta=0.0;
            found1=0.0;
            meet=0;
            best=100.0;
            final1=0.0;
            final2=0.0;
            int repairIid2=0;
            int repairJid2=0;
            for(int i=0; i<mass.size(); i++){
                element=mass.get(i);
                for(int j=0; j<mass.size(); j++){
                    if(j<=i)
                        continue;

                    diver=eqNeed-(mass.get(i)+mass.get(j)*2)/3;
                    if(diver<0)
                        diver=-diver;

                    if(best>diver && (mass.get(i)+mass.get(j)*2)/3 >= eqNeed){
                        best=diver;
                        final1=mass.get(i);
                        final2=mass.get(j)*2;
                        repairIid2=i;
                        repairJid2=j;
                    }
                }
            }
            logMe("3. Repair with "+String.valueOf((final1+final2)/3)+" "+final2+" "+final1);
            betta=(final1+final2)/3;


            Double tetta=0.0;

            best=100.0;
            final1=0.0;
            final2=0.0;
            Double final3=0.0;
            int repairIid3=0;
            int repairJid3=0;
            int repairKid3=0;
            for(int i=0; i<mass.size(); i++){
                element=mass.get(i);
                for(int j=0; j<mass.size(); j++){
                    if(j<=i)
                        continue;
                    for(int k=0; k<mass.size(); k++){
                        if(k<=j)
                            continue;
                        diver=eqNeed-(mass.get(i)+mass.get(j)+mass.get(k))/3;
                        if(diver<0)
                            diver=-diver;
                        if(best>diver && (mass.get(i)+mass.get(j)+mass.get(k))/3 >= eqNeed){
                            if(best==0)
                                break;
                            best=diver;
                            final1=mass.get(i);
                            final2=mass.get(j);
                            final3=mass.get(k);
                            repairIid3=i;
                            repairJid3=j;
                            repairKid3=k;
                        }
                    }
                }
            }
            logMe("4. Repair with "+String.valueOf((final1+final2+final3)/3)+" "+final2+" "+final1+" "+final3);
            tetta=(final1+final2+final3)/3;

            //#========================= LOGIC
            Double finalOFthree=0.0;

            if(alpha<betta){
                logMe("will be alpha");
                first=repairIid1;
                second=repairJid1;
                finalOFthree=alpha;
                threeNumber=1;
            }
            else{
                logMe("will be betta");
                first=repairIid2;
                second=repairJid2;
                finalOFthree=betta;
                threeNumber=2;
            }

            if(finalOFthree>tetta){
                logMe("will be tetta");
                first=repairIid3;
                second=repairJid3;
                third=repairKid3;
                finalOFthree=tetta;
                threeNumber=3;
            }

            if(eqNeed*1.2<finalOFthree){
                finalOFthree=0.0;
                logMe("FOO3");
            }

            if(eqNeed*1.2<finalOFtwo){
                finalOFtwo=0.0;
                logMe("FOO2");
            }
            //#//////////////////////////___REPAIRING !!!!!!!!!!!!

            if(finalOFtwo==0 && finalOFthree==0) //# NO ONE
                logMe("ERROR I CANT REPAIR THIS SHIT !!!");

            else if(finalOFtwo!=0 && finalOFthree==0) {//#ONLY 2
                if(toRepair%2==0){
                    logMe("Good only 2");
                    logMe(repairIid0 + " " + String.valueOf(toRepair / 2));
                    logMe(repairJid0 + " " + String.valueOf(toRepair / 2));
                    doRepair(repairIid0,(toRepair)/2);
                    doRepair(repairJid0,(toRepair)/2);
                }
                else{
                    logMe("have to 2..");
                    logMe(repairIid0+" "+String.valueOf((toRepair - 1) / 2));
                    logMe(repairJid0+" "+ String.valueOf((toRepair - 1) / 2));
                    doRepair(repairIid0,(toRepair-1)/2);
                }
            }

            else if(finalOFtwo==0 && finalOFthree!=0){//#ONLY 3
                if(toRepair%3==0){
                    logMe("Good only 3");
                    if(threeNumber == 1){
                        logMe(first + " " + String.valueOf(toRepair / 3 * 2));
                        logMe(second + " " + String.valueOf(toRepair / 3));
                        doRepair(first, toRepair / 3 * 2);
                        doRepair(second,toRepair/3);
                    }
                      
                    if(threeNumber == 2){
                        logMe(String.valueOf(first) + " " + String.valueOf(toRepair / 3));
                        logMe(String.valueOf(second) + " " + String.valueOf(toRepair / 3 * 2));
                        doRepair(second, toRepair / 3 * 2);
                        doRepair(first, toRepair / 3);
                    }

                    if(threeNumber == 3){
                        logMe(String.valueOf(first) + " " + String.valueOf(toRepair / 3));
                        logMe(String.valueOf(second) + " " + String.valueOf(toRepair / 3));
                        logMe(String.valueOf(third) + " " + String.valueOf(toRepair / 3));
                        doRepair(first, toRepair / 3);
                        doRepair(second, toRepair / 3);
                        doRepair(third, toRepair / 3);
                    }
                }
                else{
                    logMe("have to 3..");
                    if((toRepair-1)%3==0){
                        if(threeNumber == 1){
                            logMe(String.valueOf(first) + " " + String.valueOf((toRepair - 1) / 3 * 2));
                            logMe(String.valueOf(second) + " " + String.valueOf((toRepair - 1) / 3));
                            doRepair(second, (toRepair - 1) / 3);
                            doRepair(first, (toRepair - 1) / 3 * 2);
                        }

                        if(threeNumber == 2){
                            logMe(String.valueOf(first) + " " + String.valueOf((toRepair - 1) / 3));
                            logMe(String.valueOf(second) + " " + String.valueOf((toRepair - 1) / 3 * 2));
                            doRepair(second, (toRepair - 1) / 3 * 2);
                            doRepair(first, (toRepair - 1) / 3);
                        }

                        if(threeNumber == 3){
                            logMe(String.valueOf(first) + " " + String.valueOf((toRepair - 1) / 3));
                            logMe(String.valueOf(second) + " " + String.valueOf((toRepair - 1) / 3));
                            logMe(String.valueOf(third) + " " + String.valueOf((toRepair - 1) / 3));
                            doRepair(first, (toRepair - 1) / 3);
                            doRepair(second, (toRepair - 1) / 3);
                            doRepair(third, (toRepair - 1) / 3);
                        }

                    }

                    else{
                        if(threeNumber == 1){
                            logMe(String.valueOf(first) + " " + String.valueOf((toRepair - 2) / 3 * 2));
                            logMe(String.valueOf(second) + " " + String.valueOf((toRepair - 2) / 3));
                            doRepair(second, (toRepair - 2) / 3);
                            doRepair(first, (toRepair - 2) / 3 * 2);
                        }

                        if(threeNumber == 2){
                            logMe(String.valueOf(first) + " " + String.valueOf((toRepair - 2) / 3));
                            logMe(String.valueOf(second) + " " + String.valueOf((toRepair - 2) / 3 * 2));
                            doRepair(second, (toRepair - 2) / 3 * 2);
                            doRepair(first, (toRepair - 2) / 3);
                        }
                        if(threeNumber == 3){
                            logMe(String.valueOf(first) + " " + String.valueOf((toRepair - 2) / 3));
                            logMe(String.valueOf(second) + " " + String.valueOf((toRepair - 2) / 3));
                            logMe(String.valueOf(third) + " " + String.valueOf((toRepair - 2) / 3));
                            doRepair(first, (toRepair - 2) / 3);
                            doRepair(second, (toRepair - 2) / 3);
                            doRepair(third, (toRepair - 2) / 3);
                        }
                    }
                }
            }

            else if(finalOFtwo!=0 && finalOFthree!=0){//#BOTH
                if((toRepair%3==0) && (toRepair%2==0)){
                    logMe("Good 3 and 2");
                    logMe(String.valueOf(repairIid0) + " " + String.valueOf(toRepair / 2));
                    logMe(String.valueOf(repairJid0) + " " + String.valueOf(toRepair / 2));
                    Double perItem=0.00;
                    perItem=toRepair/2;
                    doRepair(repairIid0,perItem);
                    doRepair(repairJid0,perItem);
                }
                else if((toRepair%3!=0) && (toRepair%2==0)){
                    logMe("Good  2 not 3");
                    logMe(String.valueOf(repairIid0) + " " + String.valueOf(toRepair / 2));
                    logMe(String.valueOf(repairJid0) + " " + String.valueOf(toRepair / 2));
                    Double perItem=toRepair/2;
                    doRepair(repairIid0,perItem);
                    doRepair(repairJid0,perItem);
                }
                else if((toRepair%3==0) && (toRepair%2!=0)){
                    logMe("Good 3 not 2");
                    if(threeNumber == 1){
                        logMe(String.valueOf(first) + " " + String.valueOf(toRepair / 3 * 2));
                        logMe(String.valueOf(second) + " " + String.valueOf(toRepair / 3));
                        doRepair(first,toRepair/3*2);
                        doRepair(second,toRepair/3);
                    }

                    if(threeNumber == 2){
                        logMe(String.valueOf(first) + " " + String.valueOf(toRepair / 3));
                        logMe(String.valueOf(second) + " " + String.valueOf(toRepair / 3 * 2));
                        doRepair(second,toRepair/3*2);
                        doRepair(first,toRepair/3);
                    }

                    if(threeNumber == 3){
                        logMe(String.valueOf(first) + " " + String.valueOf(toRepair / 3));
                        logMe(String.valueOf(second) + " " + String.valueOf(toRepair / 3));
                        logMe(String.valueOf(third) + " " + String.valueOf(toRepair / 3));
                        doRepair(first,toRepair/3);
                        doRepair(second,toRepair/3);
                        doRepair(third,toRepair/3);
                    }

                }
                else{
                    logMe("Oh...will be 2 ...");
                    logMe(String.valueOf(repairIid0) + " " + String.valueOf((toRepair - 1) / 2));
                    logMe(String.valueOf(repairJid0) + " " + String.valueOf((toRepair - 1) / 2));
                    doRepair(repairIid0,(toRepair-1)/2);
                    doRepair(repairJid0,(toRepair-1)/2);
                }


            }  


        }
        driver.get("http://virtonomica.ru/vera/main/unit/view/"+page);







        return 0;
    }
}



//------------------------------------------------------------------

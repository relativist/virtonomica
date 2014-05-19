package autotest;


import general.Page;
import general.virt.*;
import help.CreateDB;
import org.junit.Test;

import java.io.File;
import java.util.List;


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class OfficeIT extends Page {

//    @Override
//    protected void setUp() throws Exception {
//
//    }
//
    @Override
    protected void tearDown() throws Exception {

    }

    // ТОП-1
    // квала рабочих увеличивается пока максимальное количество рабов дозволеное на предприятии будет едва выше текущего.
    // при этом происходит обучение персонала
    // если ТопРабочих < Рабочих, уменьшить зп пока ТопРабочих ~= Рабочих  [количество]


    @Test
    public void test() throws Throwable {

        File file = new File("plant.db");
        if(!file.exists()) {
            logMe("creating new database table!");
            new CreateDB().createPlant();
        }

        file = new File("report.db");
        if(!file.exists()) {
            logMe("creating new database table!");
            new CreateDB().createReport();
        }
        boolean processed=false;
        List<String> list = new LoginPage(driver).openVirtUrl().login().selectOffice().getListAllUnit();
        logMe("go");

        String currentUrl = new String();
        for(int i=0; i< list.size(); i++){
            currentUrl = list.get(i);
            logMe(i+" of "+list.size()+" )"+currentUrl);

            if(new PlantPage(driver).isDepProcessed(currentUrl)){
                logMe("Already processed");
                continue;
            }

            logMe(currentUrl);
            driver.get(currentUrl);

            //top-3
            if(!processed){
                new HelpPage(driver).recordReport("Office",new StorePage(driver).getTop3Report());
                processed=true;
            }


            new OfficePage(driver).setAutoQaSlave().educate().checkOfficeLoad().createAdvertising().repairIt();

            new PlantPage(driver).recordDepartment(currentUrl);
        }
    }
}
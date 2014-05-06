package autotest;


import general.Page;
import general.virt.*;
import help.CreateDB;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class OrchardIT extends Page {

//    @Override
//    protected void setUp() throws Exception {
//
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//
//    }



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
        List<String> list = new LoginPage(driver).openVirtUrl().login().selectOrchard().getListAllUnit();
        logMe("go");


        String currentUrl = new String();
        for(int i=0; i< list.size(); i++){
            currentUrl = list.get(i);
            logMe(i+" of "+list.size()+" )"+currentUrl);

            if(new PlantPage(driver).isDepProcessed(currentUrl)){
                logMe("Already processed");
                continue;
            }

            driver.get(currentUrl);

            //top-3
            if(!processed){
                new HelpPage(driver).recordReport("Orchard",new StorePage(driver).getTop3Report());
                processed=true;
            }

            if(!new AnimalFermPage(driver).isSlaveOnVacation()) {
                new AnimalFermPage(driver).repairIt();
                new AnimalFermPage(driver).setAutoQaSlave().educate();
            }
            else {
                new HelpPage(driver).recordReport(currentUrl,"Orchard в отпуске");
            }
            new PlantPage(driver).recordDepartment(currentUrl);

        }


    }





}
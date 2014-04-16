package autotest;


import general.Page;
import general.virt.LoginPage;
import general.virt.PlantPage;
import org.junit.Test;

import java.util.List;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class PlantRepairIT extends Page {

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
        List<String> list = new LoginPage(driver).openVirtUrl().login().selectPlant().getListAllUnit();
        logMe("go");

        String currenUrl = new String();
        for(int i=0; i< list.size(); i++){
            currenUrl = list.get(i);
            logMe(currenUrl);
            driver.get(currenUrl);
            new PlantPage(driver).repairIt();

        }


    }





}
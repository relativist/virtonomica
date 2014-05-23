package help;


import general.Page;
import general.virt.LoginPage;
import general.virt.OfficePage;
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

public class Gnoms extends Page {

//    @Override
//    protected void setUp() throws Exception {
//
//    }
//
    @Override
    protected void tearDown() throws Exception {

    }

    @Test
    public void test() throws Throwable {

        File file = new File("gnome.db");
        if(!file.exists()) {
            logMe("creating new database table!");
            new CreateDB().createPlant();
        }

        List<String> list = new LoginPage(driver).openVirtUrl().login().selectOffice().getListAllUnit();
        logMe("go");

        String currentUrl = new String();
        for(int i=0; i< list.size(); i++){
            currentUrl = list.get(i);
            logMe(i+" of "+list.size()+" )"+currentUrl);
            logMe(currentUrl);
            driver.get(currentUrl);
            new OfficePage(driver).createAdvertising();
        }
    }





}
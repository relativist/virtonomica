package autotest;


import general.Page;
import general.virt.HelpPage;
import general.virt.LoginPage;
import general.virt.StorePage;
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

public class Store2tradingIT extends Page {

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

        int session = Integer.valueOf(formattedDate("MMdd"));

        File file = new File("store.db");
        if(!file.exists()) {
            logMe("creating new database table!");
            new CreateDB().createStore();
        }
        file = new File("report.db");
        if(!file.exists()) {
            logMe("creating new database table!");
            new CreateDB().createReport();
        }

        List<String> list = new LoginPage(driver).openVirtUrl().login().selectStore().getListAllUnit();
        logMe("go");
        boolean processed = false;

        String currenUrl = new String();
        for(int i=0; i< list.size(); i++){
            currenUrl = list.get(i);
            logMe(currenUrl);
            if(new StorePage(driver).isDepProcessed(currenUrl)){
                logMe("Already processed");
                continue;
            }
            driver.get(currenUrl);

            //top-3
            if(!processed){
                new HelpPage(driver).recordReport("Store",new StorePage(driver).getTop3Report());
                processed=true;
            }

            new StorePage(driver).statusStore().setAutoQaSlave().educate().trading();
        }
    }
}
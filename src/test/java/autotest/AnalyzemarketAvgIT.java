package autotest;


import general.Page;
import general.virt.HelpPage;
import general.virt.LoginPage;
import help.CreateDB;
import org.junit.Test;

import java.io.File;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class AnalyzemarketAvgIT extends Page {

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

        File file = new File("marketAvg.db");
        if(!file.exists()) {
            logMe("creating new database table!");
            new CreateDB().createMarketAvg();
        }

    logMe("today session: "+session);

    new HelpPage(driver).deleteBase("marketAvg.db","MARKET");

    new LoginPage(driver).openVirtUrl().login()
            .getAnalyzeMarketAvg(session)

    ;

    }

}
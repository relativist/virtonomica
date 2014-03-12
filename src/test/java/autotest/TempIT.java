package autotest;


import general.Page;
import general.virt.LoginPage;
import general.virt.PlantPage;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class TempIT extends Page {

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
        List<String> list = new LoginPage(driver).openVirtUrl().login().selectPlant().getListAllUnit();
        logMe("go");

        String currenUrl = new String();
        for(int i=0; i< list.size(); i++){
            currenUrl = list.get(i);
            logMe(currenUrl);
            driver.get(currenUrl);
            new PlantPage(driver).setAutoQaSlave().educate().supply();

        }

//        new LoginPage(driver)
//                .openVirtUrl()
//                .login()
//                .selectPlant()
//                .selectPlantByUnitId("5180845").supply();


    }





}
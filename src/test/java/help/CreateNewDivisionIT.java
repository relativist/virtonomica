package help;


import general.Page;
import general.virt.HelpPage;
import general.virt.LoginPage;
import org.junit.Test;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class CreateNewDivisionIT extends Page {

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

    new LoginPage(driver).openVirtUrl().login();
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
//        new HelpPage(driver).createFerm("Бобруйск","Птицеферма","Разведение кур","5 корпусов");
        for(int i=0; i<16; i++) {
            logMe(""+i);
            new HelpPage(driver).createScienceLaboratory("Рига");
        }


//            .createPlant("Завод","Ликероводочный завод","Узбекистан","Нукус","1000 рабочих мест","Ликер")
//            .createPlant("Завод","Ликероводочный завод","Узбекистан","Нукус","1000 рабочих мест","Ликер");
//        new HelpPage(driver).createRestorun("Рига");



    }





}
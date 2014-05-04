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
        new HelpPage(driver).createRestorun("Роттердам");
        new HelpPage(driver).createRestorun("Утрехт");
        new HelpPage(driver).createRestorun("Рига");
        new HelpPage(driver).createRestorun("Бремен");
        new HelpPage(driver).createRestorun("Тампере");
        new HelpPage(driver).createRestorun("Тампере");
        new HelpPage(driver).createRestorun("Эспоо");
        new HelpPage(driver).createRestorun("Эспоо");
        new HelpPage(driver).createRestorun("Рованиеми");
        new HelpPage(driver).createRestorun("Рованиеми");
        new HelpPage(driver).createRestorun("Гронинген");
        new HelpPage(driver).createRestorun("Гронинген");
        new HelpPage(driver).createRestorun("Варна");
        new HelpPage(driver).createRestorun("Варна");
        new HelpPage(driver).createRestorun("Рига");


//            .createPlant("Завод","Ликероводочный завод","Узбекистан","Нукус","1000 рабочих мест","Ликер")
//            .createPlant("Завод","Ликероводочный завод","Узбекистан","Нукус","1000 рабочих мест","Ликер");




    }





}
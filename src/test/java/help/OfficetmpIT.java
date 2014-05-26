package help;


import general.Page;
import general.virt.LoginPage;
import general.virt.OfficePage;
import org.junit.Test;


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class OfficetmpIT extends Page {

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
        new LoginPage(driver).openVirtUrl().login().selectPlantByUnitId("5512861");


        new OfficePage(driver).createAdvertisingAdv();



    }
}
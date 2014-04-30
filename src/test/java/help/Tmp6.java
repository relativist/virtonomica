package help;


import general.Page;
import general.virt.LoginPage;
import general.virt.StorePage;
import org.junit.Test;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class Tmp6 extends Page {

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
//
//        String currenUrl = new String();
//        for(int i=0; i< list.size(); i++){
//            currenUrl = list.get(i);
//            logMe(currenUrl);
//            driver.get(currenUrl);
//            new StorePage(driver).autoBuyProducts();
//        }


        new LoginPage(driver)
                .openVirtUrl()
                .login()
                .selectPlantByUnitId("5487758");
        new StorePage(driver).trading();


    }





}
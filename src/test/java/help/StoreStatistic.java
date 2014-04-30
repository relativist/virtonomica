package help;


import general.Page;
import general.virt.LoginPage;
import general.virt.StorePage;
import org.junit.Test;

import java.util.List;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class StoreStatistic extends Page {

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
        List<String> list = new LoginPage(driver).openVirtUrl().login().selectStore().getListAllUnit();
        logMe("go");
        logMe("ID"+"\t\t\t"+"Рабы"+"\t\t\t"+"Размер"+"\t\t\t\t"+"Отделы"+"\t\t\t"+"Посы"+"\t\t\t"+"Изв"+"\t\t\t\t\t"+"Население"+"\t\t\t"+"Статус мага");
        String currenUrl = new String();
        for(int i=0; i< list.size(); i++){
            currenUrl = list.get(i);
            driver.get(currenUrl);
            new StorePage(driver).getStoreInfo();
        }
    }



//
//        String currenUrl = new String();
//        for(int i=0; i< list.size(); i++){
//            currenUrl = list.get(i);
//            logMe(currenUrl);
//            driver.get(currenUrl);
//            new StorePage(driver).autoBuyProducts();
//        }


//        new LoginPage(driver)
//                .openVirtUrl()
//                .login()
//                .selectPlantByUnitId("5487740");
//        logMe("Рабы"+"\t\t\t"+"Размер"+"\t\t\t\t"+"Отделы"+"\t\t\t"+"Посы"+"\t\t\t"+"Изв"+"\t\t\t\t\t"+"Население"+"\t\t\t"+"Статус мага");
//        new StorePage(driver).getStoreInfo();
//
//
//    }





}
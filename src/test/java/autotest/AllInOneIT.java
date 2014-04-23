package autotest;


import general.Page;
import org.junit.Test;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class AllInOneIT extends Page {

//    @Override
//    protected void setUp() throws Exception {
//
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//
//    }

    // ТОП-1
    // квала рабочих увеличивается пока максимальное количество рабов дозволеное на предприятии будет едва выше текущего.
    // при этом происходит обучение персонала
    // если ТопРабочих < Рабочих, уменьшить зп пока ТопРабочих ~= Рабочих  [количество]


    @Test
    public void test() throws Throwable {

        new PlantRepairIT().test();
        new Top1OfficeIT().test();
        new Top1PlantIT().test();
        new Store1QAEducateIT().test();
        new WarehouseSupply().test();

    }

}
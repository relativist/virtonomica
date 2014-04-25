package help;


import general.Page;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class Tmp5 extends Page {

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {

    }

    // ТОП-1
    // квала рабочих увеличивается пока максимальное количество рабов дозволеное на предприятии будет едва выше текущего.
    // при этом происходит обучение персонала
    // если ТопРабочих < Рабочих, уменьшить зп пока ТопРабочих ~= Рабочих  [количество]


    @Test
    public void test() throws Throwable {

        //new HelpPage().generateStoreBaseToBuild();
        ArrayList<String>s = new ArrayList<String>();
        s.add("1");
        s.add("2");
        s.add("3");
        s.remove(2);
        s.remove(1);
        s.remove(0);
        logMe(s.toString());

    }





}
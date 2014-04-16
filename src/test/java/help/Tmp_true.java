package help;


import general.Page;
import org.junit.Test;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class Tmp_true extends Page {

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {

    }

    @Test
    public void test() throws Throwable {

//        int session = Integer.valueOf(formattedDate("MMdd"));
//
//
//        File file = new File("market.db");
//        if(!file.exists()) {
//            //Создаем его.
//            //file.createNewFile();
//            new CreateDB().main();
//        }

        String s = "4.26 % (106+1 ед.)";
        String d = s.split(" % ")[1].split("\\+")[0].replaceAll("\\D+","");
        logMe(d);

    }





}
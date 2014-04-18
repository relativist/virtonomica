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

public class Tempo extends Page {

    public boolean isMyProduct(ArrayList<String> wProducts,String product){
        for(int i=0; i<wProducts.size();i++){
            if(wProducts.get(i).split(";")[0].equals(product))
                if(wProducts.get(i).split(";")[3].equals("my"))
                    return true;
        }
        return false;
    }

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {

    }

    @Test
    public void test() throws Throwable {
        ArrayList<String> products = getWproducts();

//        for(int i=0; i<products.size(); i++)
//            logMe(products.get(i));

        logMe(""+isMyProduct(products,"Натуральные лекарственные компоненты"));

    }

    //





}
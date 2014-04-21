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
        //ArrayList<String> products = getWproducts();
        ArrayList<String> p = new ArrayList<String>();
        ArrayList<Double> num = new ArrayList<Double>();
        p.add("4.23;1");
        p.add("1.23;2");
        p.add("5.23;3");
        p.add("3.23;4");
        Double best=0.0;
        for(int counter=0; counter<p.size(); counter++){
            num.add(Double.valueOf(p.get(counter).split(";")[0]));
        }
        for(int counter=0; counter<num.size(); counter++){
            if(num.get(counter)>best)
                best=num.get(counter);
        }
        for(int counter=0; counter<p.size(); counter++){
            if(p.get(counter).split(";")[0].equals(String.valueOf(best))){
                logMe("best "+p.get(counter).split(";")[1]);
            }
        }
        logMe("Shit!");





//        for(int i=0; i<products.size(); i++)
//            logMe(products.get(i));

        //logMe(""+isMyProduct(products,"Натуральные лекарственные компоненты"));

    }

    //





}
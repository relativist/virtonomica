package help;


import general.Page;
import general.virt.LoginPage;
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

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {

    }

    @Test
    public void test() throws Throwable {

        // нужно научиться находить максимальное количество оборудования при максимальном ТОП-1
        double qaPlayer=27;
        double qtySlave=180;
        double qaSlave=5.67;


        double maxQa=0.0;
        double maxSlave=0.0;


        double result=100;
        String type = "workshop";

        maxQa = calcQualTop1(qaPlayer,qtySlave,type);
        maxSlave = calcPersonalTop1(qaPlayer, qaSlave, type);
        logMe("Max QA:"+String.valueOf(maxQa)+" \tSlave: "+qtySlave+"\tMax Slave: "+String.valueOf(maxSlave));

        for(;qtySlave>0; qtySlave--){
            qaSlave=maxQa;
            maxQa = calcQualTop1(qaPlayer,qtySlave,type);
            maxSlave = calcPersonalTop1(qaPlayer, qaSlave, type);
            logMe("Max QA:"+String.valueOf(maxQa)+" \tSlave: "+qtySlave+"\tMax Slave: "+String.valueOf(maxSlave));
            if(qtySlave*100/maxSlave > 5)
                break;
        }

    }





}
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

        Double Qreq=15.73;    // Квалификация которую мы хотим добиться
        Double Q=10.79;      // текущая квалификация
        Double Sreq=0.0;     // зп которую будем считать!
        Double S=61.50;     // текущая зп
        Double Savg=49.28;  // средняя зп по городу
        Double Qavg=4.75;  // Средняя квалификация по городу


        Double b=0.0;
        Double k = S/Savg;
        Double k1 = k*k;
        if(k <= 1){
            b = (Q/k1);
            Sreq = Math.sqrt(Qreq/b)*Savg;
            // если зарплата превысила среднею
            if( Sreq/Savg > 1){
                b = b / Qavg;
                b = 2 * Math.pow(0.5, b);
                Sreq = (Math.pow(2, Qreq/Qavg)*b - 1)*Savg;
            }
        } else {
            b = (k+1)/Math.pow(2, Q/Qavg);
            Sreq = (Math.pow(2, Qreq/Qavg)*b - 1)*Savg;
            // если зарплата стала меньше средней;
            if(Sreq/Savg < 1){
                b = Qavg * Math.log(b/2)/Math.log(0.5);
                Sreq = Math.sqrt(Qreq/b)*Savg;
            }
        }
        // блокировка от потери обученности
        if (Sreq/Savg <= 0.80){
            Sreq = Math.floor(0.80 * Savg) + 1;
        }

        logMe("Поставьте зарплату: "+Sreq);

    }









}
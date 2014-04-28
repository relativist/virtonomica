package help;


import general.Page;
import general.virt.HelpPage;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Created by a.sitnikov on 18.02.14.
 */


/*
    Descriprion
    Simple Soap import product and verify them to correct import to MPO ( SP )
*/

public class InitDBStoreToBuild extends Page {

//    @Override
//    protected void setUp() throws Exception {
//
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//
//    }



    @Test
    public void test() throws Throwable {

        File file = new File("storeBuild.db");
        if(!file.exists()) {
            logMe("creating new database table!");
            new CreateDB().createStoreBuild();
        }

        new HelpPage().deleteBaseStoreBuild();

        LinkedHashSet shopWithDep = new HelpPage().generateStoreBaseToBuild();

        for(Iterator iter = shopWithDep.iterator(); iter.hasNext();) {
            final String content = (String) iter.next();
            System.out.println(content);
            new HelpPage().recordToBaseStoreBuild(content.split(";")[0],content.split(";")[1]);
        }



    }





}
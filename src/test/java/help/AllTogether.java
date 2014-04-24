package help;


import autotest.OfficeIT;
import autotest.PlantIT;
import autotest.Store1QAEducateIT;
import autotest.Store2tradingIT;
import general.virt.WareHousePage;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

    @RunWith(Suite.class)
    @Suite.SuiteClasses({
            OfficeIT.class,
            PlantIT.class,
            Store1QAEducateIT.class,
            Store2tradingIT.class,
            WareHousePage.class
    })
    public class AllTogether {

    }

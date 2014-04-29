package autotest;

import general.virt.HelpPage;
import general.virt.SendVirtoMail;
import org.junit.Test;

public class SendReportIT extends SendVirtoMail{

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {

    }

    @Test
    public void test() throws Throwable {
        String message = new HelpPage().getTodayReport();
        logMe(message);
        new SendVirtoMail().send(message);

    }





}
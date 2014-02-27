package general;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

/**
 * Created by a.sitnikov on 18.02.14.
 */
public class Page extends TestCase {
    protected WebDriver driver = null;

    //other param
    public final int TIMEOUT_IN_SECONDS = 15;
    public final int TIMEOUT_IN_SECONDS_WAIT_START_PROCESS = 90;
    public final int TIMEOUT_IN_SECONDS_PROCESSES = 230;
    public final int IMPLICITY_WAIT_IN_SECONDS = 12;
    protected static Boolean logging = Boolean.valueOf(getParameter("logging"));

    //db parameters
    protected static String url_ora = getParameter("url_ora");
    protected static String username_ora = getParameter("username_ora");
    protected static String pwd_ora = getParameter("pwd_ora");

    //sftp parameters
    protected static String sftphost = getParameter("sftphost");
    protected static String sftphost2 = getParameter("sftphost2");
    protected static String work_place = getParameter("work_place");
    protected static String catalina_home = getParameter("catalina_home");
    protected static String ftp_adapter_dir = getParameter("ftp_adapter_dir");
    protected static String ftp_adapter_dir2 = getParameter("ftp_adapter_dir2");
    protected static String USER = getParameter("USER");
    protected static String sftpPassword = getParameter("sftpPassword");

    //mp param
    protected static String MPhttpPrefix = getParameter("httpPrefix");


    protected Page() {
        super();
    }

    protected Page(WebDriver driver) {
        super();
        this.driver = driver;
    }

    public static String getParameter(String name) {
        return Configuration.getInstance().getParameter(name);
    }




    @Before
    protected void setUp() throws Exception {
        System.out.println("SetUp driver");
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        driver = new ChromeDriver();
    }

    @After
    protected void tearDown() throws Exception {
        System.out.println("TearDown driver");
        //the next strings is commented just for tests to keep window opened
        driver.manage().deleteAllCookies();
        driver.quit();
    }
}



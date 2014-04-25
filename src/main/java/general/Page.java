package general;

import general.virt.RepairPage;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    public static String getLogin() {
        return Passwd.getInstance().getLogin();
    }
    public static String getPasswd() {
        return Passwd.getInstance().getPasswd();
    }

    public static ArrayList<String> getWproducts() {
        return Configuration.getInstance().getWProducts();
    }

    public static ArrayList<String> getMyProductsToSell() {
        return Configuration.getInstance().getMyProductsToSell();
    }

    public static ArrayList<String> getMyProductsDepToSell() {
        return Configuration.getInstance().getMyProductsDepToSell();
    }

    public void logMe(String s){
        System.out.println(s);
    }

    public String getUnitIdByUrl(String url){
        return url.replaceAll(".*view/","");
    }

    // получаем тип подразделения из иконки наверху
    private String getTypeUnit(){
        return  driver.findElement(By.xpath("//*[@id='unitImage']/img")).getAttribute("src").replaceAll(".*units/","").split("_")[0];
    }

    ///////////////////////////////////////////////////////////////////////////
    //function calcEqQualMax(q)
    // qp - квалификация персонала
    //
    //вычисляет максимальное качество оборудования/животных для заданной квалификации персонала
    /////////////////////////////////////////////////////////////////////////////
    public double calcEqQualMax(double qp){
        return Math.floor(100*Math.pow(qp, 1.5))/100 ;
    }

    ///////////////////////////////////////////////////////////////////////////
    //function calcPersonalTop1(q, qp,type)
    // q - квалификация игрока
    // qp -  квалификация персонала
    //вычисляет максимальное кол-во работающих с заданной квалификацией на предприятиии для заданной квалификации игрока (топ-1)
    /////////////////////////////////////////////////////////////////////////////
    public double calcPersonalTop1(double q,double qp){
        return Math.floor(0.2*getK(getTypeUnit())*14*q*q/Math.pow(1.4, qp));
    }//end calcPersonalTop1()

    public double calcPersonalTop1(double q,double qp,String type){
        return Math.floor(0.2*getK(type)*14*q*q/Math.pow(1.4, qp));
    }//end calcPersonalTop1()

    ///////////////////////////////////////////////////////////////////////////
    //function calcTechMax(q)
    // q - квалификация игрока
    //
    //вычисляет максимальный уровень технологии для заданной квалификации игрока
    /////////////////////////////////////////////////////////////////////////////
    public double calcTechMax(double q){
        return Math.floor(10*Math.pow(q/0.0064, 1.0/3.0))/10 ;
    }//end calcTechMax()

    ///////////////////////////////////////////////////////////////////////////
    //function calcPersonalTop3(q, type)
    // q - квалификация игрока
    //
    //вычисляет максимальное кол-во работающих на предприятиях отрасли для заданной квалификации игрока (топ-3)
    /////////////////////////////////////////////////////////////////////////////
    public double calcPersonalTop3(double q){
        return ((2*q*q + 6*q)*getK(getTypeUnit()));
    }//end calcPersonalTop3()

    ///////////////////////////////////////////////////////////////////////////
    //function calcQualTop1(q, p, type)
    // q - квалификация игрока
    // p -  численность персонала
    //вычисляет максимальное квалификацию работающих при заданных их численности и квалификации игрока (обратна calcPersonalTop1())
    /////////////////////////////////////////////////////////////////////////////
    public double calcQualTop1(double q,double p){
        if(p==0) return 0.00;
        return Math.rint(100.0 * Math.log(0.2*14*getK(getTypeUnit())*q*q/p)/Math.log(1.4)- 1) / 100.0 ;
    }//end calcQualTop1()
    public double calcQualTop1(double q,double p,String type){
        if(p==0) return 0.00;
        return Math.rint(100.0 * Math.log(0.2*14*getK(type)*q*q/p)/Math.log(1.4)- 1) / 100.0 ;
    }//end calcQualTop1()


    ///////////////////////////////////////////////////////////////////////////
    //getK(type)
    //возвращает к для расчётов нагрузки по типу
    ///////////////////////////////////////////////////////////////////////////
    private double getK(String type)
    {
        //logMe("type: "+type);
        if (type.equals("shop") || type.equals("restaurant") || type.equals("lab")) {
            return 5;
        } else if (type.equals("workshop") || type.equals("mill") || type.equals("sawmill")) {
            return 50;
        } else if (type.equals("animalfarm")) {
            return 7.5;
        } else if (type.equals("medicine") || type.equals("fishingbase")) {
            return 12.5;
        } else if (type.equals("orchard") || type.equals("farm")) {
            return 20;
        } else if (type.equals("mine")) {
            return 100;
        } else if (type.equals("office")) {
            return 1;
        } else if (type.equals("service")) {
            return 1.5;
        } else if (type.equals("energy")) {
            return 75.0;
        } else {
            return 0;
        }
    }//end getType()

    public void waitForElement(String xpath) throws InterruptedException {
        for(int i=0; i<TIMEOUT_IN_SECONDS; i++){
            if(driver.findElements(By.xpath(xpath)).size()>0){
                //logMe("FOUND!");
                break;
            }
            //logMe("WAIT");
            Thread.sleep(1000);
        }
    }

    public void waitForElementVisible(String xpath) throws InterruptedException {
        for(int i=0; i<TIMEOUT_IN_SECONDS; i++){
            if(driver.findElement(By.xpath(xpath)).isDisplayed()){
                break;
            }
            //logMe("WAIT");
            Thread.sleep(1000);
        }
    }

    public String formattedDate(String format) throws IOException {
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }

    public int repairIt(){
        return new RepairPage(driver).repairIt();
    }





    @Before
    protected void setUp() throws Exception {
//        System.out.println("SetUp driver");
//        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
//        driver = new ChromeDriver();

        DesiredCapabilities capability = DesiredCapabilities.chrome();
        driver = new RemoteWebDriver(new URL("http://alpina.ixtens.local:5555/wd/hub"), capability);
    }

    @After
    protected void tearDown() throws Exception {
        System.out.println("TearDown driver");
        //the next strings is commented just for tests to keep window opened
        driver.manage().deleteAllCookies();
        driver.quit();
    }
}



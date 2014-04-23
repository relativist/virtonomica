package general.virt;

import general.Page;
import org.openqa.selenium.WebDriver;

import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by rest on 3/7/14.
 */
public class HelpPage extends Page {
    public HelpPage(WebDriver driver_out) {
        super();
        driver = driver_out;
    }

    public HelpPage() {
        super();
    }

    //возвращает строку продукта для продажи с его данными для фильра
    public String getProductDataFromCompanyConfig(String depTitle, ArrayList<String> departments){
        for(String dep: departments){
            if(dep.split(";")[0].equals(depTitle))
                return dep;
        }
        logMe("Unable to find: "+depTitle);
        assertTrue(false);
        return "";
    }

    //вернет департамент по продукт тайтлу
    public String getDepByProTitle(String productTitle, ArrayList<String> departments){
        for(String dep: departments){
            for(int i=1; i<dep.split(";").length; i++)
                if(dep.split(";")[i].equals(productTitle))
                    return dep.split(";")[0];
        }
        logMe("Could not find: "+productTitle);
        assertTrue(false);
        return "";
    }

    public boolean generateStoreBaseToBuild() throws ClassNotFoundException, SQLException {
        Connection c = null;
        Statement stmt = null;
        boolean result=false;
        ArrayList<String> dbProducts = new ArrayList<String>();

        // Create a hash map
        Hashtable balance = new Hashtable();
        Enumeration names;
        String str;
        double bal;

        // продукты у которых брать отдел.
        ArrayList<String> companyProductDepToSell = getMyProductsDepToSell();

        // продукты у которых брать данные для запроса.
        ArrayList<String> companyProductToSell = getMyProductsToSell();

        // Добываем уникальные продукты из базы. вообще которые у нас есть.

        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:market.db");
        c.setAutoCommit(false);

        stmt = c.createStatement();
        String sql = "select  distinct product from market;";
        ResultSet rs =  stmt.executeQuery(sql);

        while ( rs.next() ) {
            String product = rs.getString("product");
            dbProducts.add(product);
        }


        //добываем города по продукту которые нас устраивают (фильтр из конфигурации)
        for(String dbProduct: dbProducts){
            //logMe(dbProduct+" "+getDepByProTitle(dbProduct,companyProductDepToSell)+" "+getProductDataFromCompanyConfig(dbProduct,companyProductToSell));
            //title;volume;localsales;qa;brand;price;base_value_to_buy
            String productData=getProductDataFromCompanyConfig(dbProduct,companyProductToSell);
            String productName=productData.split(";")[0];
            String productVolume=productData.split(";")[1];
            String productLocal=productData.split(";")[2];
            String productQa=productData.split(";")[3];
            String productBrand=productData.split(";")[4];
            String productPrice=productData.split(";")[5];

            c = null;
            stmt = null;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:market.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            sql = "select city from market where product='"+productName+"' and volume > "+productVolume+" and localsales>"+productLocal+" and qa<"+productQa+" and brand<"+productBrand+" and price > "+productPrice+" and region!='Франция' order by price desc;";
            rs =  stmt.executeQuery(sql);

            while ( rs.next() ) {
                String city = rs.getString("city");
                String key = city+";"+getDepByProTitle(dbProduct,companyProductDepToSell);
                int value = 0;
                if(balance.containsKey(key)){
                    value = Integer.valueOf((Integer) balance.get(key));
                    value ++;
                }

                //logMe("key: "+key+" value: "+value);
                balance.put(key,value);
            }

            rs.close();
            stmt.close();
            c.commit();
            c.close();

        }

        System.out.println();
        System.out.println();

        // проходимся по хэшу и показываем тех у кого больше двух вхождений.
        names = balance.keys();
        while(names.hasMoreElements()) {

            str = (String) names.nextElement();
            if((Integer)balance.get(str)>2)
                System.out.println(str + ": " +balance.get(str));
        }
        System.out.println();


        return result;
    }

}

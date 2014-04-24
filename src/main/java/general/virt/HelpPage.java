package general.virt;

import general.Page;
import org.openqa.selenium.WebDriver;

import java.sql.*;
import java.util.*;

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
        LinkedHashSet shopWithDep = new LinkedHashSet();
        LinkedHashSet uniqueCity = new LinkedHashSet();

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
            logMe("-----------------------"+dbProduct+" = "+getDepByProTitle(dbProduct,companyProductDepToSell)+"-----------------------");
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
                //String key = city+";"+getDepByProTitle(dbProduct,companyProductDepToSell);
                String key = city;
                int value = 1;
                //logMe(city+";"+getDepByProTitle(dbProduct,companyProductDepToSell));
                logMe(city);
                if(balance.containsKey(key)){
                    value = Integer.valueOf((Integer) balance.get(key));
                    value +=1;
                }

                balance.put(key,value);

//                if((Integer)balance.get(key)>2){
//                    shopWithDep.add(city+";"+getDepByProTitle(dbProduct,companyProductDepToSell));
//                }
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
        int sizeOfShops=0;
        while(names.hasMoreElements()) {
            str = (String) names.nextElement();
            if((Integer)balance.get(str)>3) {
                logMe(str + ": " + balance.get(str));
                uniqueCity.add(str);

                for(String dbProduct: dbProducts){
                    //logMe("-----------------------"+dbProduct+" = "+getDepByProTitle(dbProduct,companyProductDepToSell)+"-----------------------");
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
                        //String key = city+";"+getDepByProTitle(dbProduct,companyProductDepToSell);
                        String key = city;
                        int value = 1;
                        if(str.equals(city)){
                            shopWithDep.add(city+";"+getDepByProTitle(dbProduct,companyProductDepToSell));
                        }
                    }

                    rs.close();
                    stmt.close();
                    c.commit();
                    c.close();

                }
            }
        }
        logMe("Size = "+uniqueCity.size());
        System.out.println();


        System.out.println();
        System.out.println();


        for(Iterator iter = shopWithDep.iterator(); iter.hasNext();) {
            final String content = (String) iter.next();
            System.out.println(content);
        }

        return result;
    }

    public void createStore(String cityName){
        //открываем файл с городами
        //ищем наш город, берем эту строку
        //
        //логинимся
        //тыкаем создать подразделение
        //строку сплитуем по ; и поехали создавать:

        //если наткнулись на элемент который disabled:
        //  пишем что не можем создать потому что в этой местности нет офиса

        //размер максимальный

        //в завимимости от размера - количество рабов.
        //реклама.

    }

    public void createAllStores(String cityName){
//        открываем базу с магазинами и отделами которые еще не построены ( не закуплены ) . кладем в массив.
//                перебираем массив.
//                встречаем город = идем на главную страницу всех магазинов = ищем город
//
//        нашли:
//        заходим в магазин если позволяет добавить отдел, добавляем
//        ставим в базе пометку что закуплено
//
//        не нашли:
//        создаем магазин
//        заходим внутрь и закупаемся нужным отделом.
//        ставим в базе пометку что закуплено
    }


}

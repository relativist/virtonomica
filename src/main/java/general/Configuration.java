package general;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Application configuration
 */
public class Configuration {
    private static Configuration instance;
    private static final String CONF = "./configuration";

    private Properties properties = new Properties();

    private Configuration() {
        try {
            //properties.load(new FileInputStream(CONF));
            properties.load(new InputStreamReader(new FileInputStream(CONF), "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public String getParameter(String name) {
        return (String) properties.get(name);
    }

    public ArrayList<String> getWProducts() {
        int count = Integer.valueOf((String) properties.get("wCount"));
        ArrayList<String> products = new ArrayList<String>();
        String tmp ="";
        for(int i=1; i<=count; i++){

            tmp=String.valueOf(properties.get("wp"+i));
            if(tmp == (null))
                continue;
            products.add(tmp);
        }
        return products;
    }

    public ArrayList<String> getMyProductsToSell() {
        int count = Integer.valueOf((String) properties.get("myProSize"));
        ArrayList<String> products = new ArrayList<String>();
        String tmp ="";
        for(int i=1; i<=count; i++){

            tmp=String.valueOf(properties.get("myPro"+i));
            if(tmp == (null))
                continue;
            products.add(tmp);
        }
        return products;
    }
}

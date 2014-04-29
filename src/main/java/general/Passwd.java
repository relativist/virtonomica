package general;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Application configuration
 */
public class Passwd {
    private static Passwd instance;
    private static final String CONF = "./passFile";

    private Properties properties = new Properties();

    private Passwd() {
        try {
            //properties.load(new FileInputStream(CONF));
            properties.load(new InputStreamReader(new FileInputStream(CONF), "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized Passwd getInstance() {
        if (instance == null) {
            instance = new Passwd();
        }
        return instance;
    }

    public String getLogin() {
        return (String) properties.get("login");
    }

    public String getMailLogin() {
        return (String) properties.get("mailLogin");
    }

    public String getMailPasswd() {
        return (String) properties.get("mailPasswd");
    }

    public String getPasswd() {
        return (String) properties.get("passwd");
    }

}

package general;

import java.io.FileInputStream;
import java.io.IOException;
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
            properties.load(new FileInputStream(CONF));
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
}

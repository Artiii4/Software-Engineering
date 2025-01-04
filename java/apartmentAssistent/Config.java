package apartmentAssistent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, couldn't find config.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(properties);
        return properties;
    }
}

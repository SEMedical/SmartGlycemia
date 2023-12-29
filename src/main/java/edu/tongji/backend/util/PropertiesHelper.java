package edu.tongji.backend.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {
    public static String GetKey(String secretName) {
        InputStream inputStream = PropertiesHelper.class.getClassLoader().getResourceAsStream("secrets.properties");

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取指定 key 对应的值
        String value = properties.getProperty(secretName);

        return value;
    }
}

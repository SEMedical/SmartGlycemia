package edu.tongji.backend.util;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */




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

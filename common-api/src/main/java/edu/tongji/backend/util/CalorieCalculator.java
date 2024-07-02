package edu.tongji.backend.util;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 All contributors of the project
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





import java.util.HashMap;

public class CalorieCalculator {
    //定义一个哈希集合，它的键是运动的名称，值是每小时消耗的卡路里
    private static HashMap<String,Double> METMap=new HashMap<>();
    public static int getCalorie(String key, double weight,int duration) {
        if (METMap.isEmpty())
            initMETMap();
        double MET = METMap.get(key);
        double calorie =duration* MET * weight  / 60.0;
        return (int) Math.ceil(calorie);
    }

    private static void initMETMap() {
        METMap.put("jogging", 8.8);//慢跑
        METMap.put("ropeskipping", 10.5);//跳绳
        METMap.put("yoga", 3.3);//瑜伽
        METMap.put("walking", 2.0);//散步

    }
}

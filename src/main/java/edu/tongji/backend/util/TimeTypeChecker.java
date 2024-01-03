package edu.tongji.backend.util;

public class TimeTypeChecker {
    public enum TimeType {
         MONTH,WEEK, DAY
    }
    public static int check(String type) {
        for(int i = 0; i < TimeType.values().length; i++) {
            if(TimeType.values()[i].toString().equals(type.toUpperCase())) {
                return i+1;//YEAR, MONTH, DAY分别对应1,2,3
            }
        }
        return 0;
    }
}

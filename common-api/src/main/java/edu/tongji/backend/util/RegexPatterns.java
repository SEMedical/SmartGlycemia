package edu.tongji.backend.util;

public abstract class RegexPatterns {
    public static final String IP_REGEX="\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    public static final String EMAIL_REGEX="\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    public static final String PHONE_REGEX="^1[3|4|5|8][0-9]\\d{8}$";
    public static final String DATE_REGEX="^\\d\\d\\d\\d-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01])";
    public static final String DATETIME_REGEX = "^\\d\\d\\d\\d-(0?[1-9]|1[0-2])-(0?[1-9]|[12][0-9]|3[01])T(00|[0-9]|1[0-9]|2[0-3]):([0-9]|[0-5][0-9]):([0-9]|[0-5][0-9])$";
}

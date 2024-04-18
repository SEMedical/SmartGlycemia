package edu.tongji.backend.util;

public abstract class RegexPatterns {
    public static final String IP_REGEX="\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    public static final String EMAIL_REGEX="\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    public static final String PHONE_REGEX="^1[3|4|5|8][0-9]\\d{8}$";

}

package edu.tongji.backend.util;




public class RegexUtils {
    public static boolean isPhoneInvaild(String phone){
        return !phone.matches(RegexPatterns.PHONE_REGEX);
    }
    public static boolean isEmailInvalid(String email){
        return !email.matches(RegexPatterns.EMAIL_REGEX);
    }
    public static boolean isDateTimeInvalid(String date){
        return !date.matches(RegexPatterns.DATETIME_REGEX);
    }
    public static boolean isDateInvalid(String date){
        return !date.matches(RegexPatterns.DATE_REGEX);
    }
}

package edu.tongji.backend.util;




public class RegexUtils {
    public static boolean isPhoneInvaild(String phone){
        return !phone.matches(RegexPatterns.PHONE_REGEX);
    }
    public static boolean isEmailInvalid(String email){
        return !email.matches(RegexPatterns.EMAIL_REGEX);
    }
}

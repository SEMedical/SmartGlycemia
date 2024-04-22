package edu.tongji.backend.util;

import edu.tongji.backend.dto.UserDTO;

public class TokenHolder {
    private static final ThreadLocal<String> tl=new ThreadLocal<>();

    public static void saveToken(String token){
        tl.set(token);
    }
    public static String getToken(){
        return tl.get();
    }
    public static void removeToken(){
        tl.remove();
    }
}
